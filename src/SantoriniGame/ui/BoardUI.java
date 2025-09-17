package SantoriniGame.ui;

import SantoriniGame.model.board.Board;
import SantoriniGame.model.board.BoardTile;
import SantoriniGame.model.player.Worker;
import SantoriniGame.model.board.TileUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * BoardUI class - Responsible for rendering the game board and handling visual interactions.
 * Following the Single Responsibility Principle, this class handles only UI-related aspects of the board.
 */
public class BoardUI {
    // UI Constants
    private static final Color DEFAULT_BG_COLOR = new Color(200, 200, 220);
    private static final Color MOVE_HIGHLIGHT_COLOR = Color.GREEN;
    private static final Color BUILD_HIGHLIGHT_COLOR = Color.YELLOW;
    private static final Color SELECTED_WORKER_COLOR = Color.RED;
    private static final Color WORKER_HIGHLIGHT_COLOR = Color.BLUE;

    // Team colors for worker display
    private static final String[] TEAM_COLORS = new String[] {
            "#FF0000", // Red
            "#0000FF", // Blue
            "#00FF00", // Lime (unused)
            "#C0C0C0", // Silver (unused)
    };

    // UI Components
    private final JPanel boardPanel;
    private final TileUI[][] tileBtns;
    private final int boardSize;
    private final Board board;

    // Tracking highlighted tiles
    private final List<Point> highlightedTiles = new ArrayList<>();

    /**
     * Creates a new BoardUI instance.
     *
     * @param boardSize Size of the board
     * @param board Reference to the game board model
     * @param tileClickListener Listener for tile click events
     */
    public BoardUI(int boardSize, Board board, ActionListener tileClickListener) {
        this.boardSize = boardSize;
        this.board = board;

        // Initialize board panel
        boardPanel = new JPanel(new GridLayout(boardSize, boardSize));
        boardPanel.setBackground(DEFAULT_BG_COLOR);

        // Initialize tile buttons
        tileBtns = new TileUI[boardSize][boardSize];

        // Create tile views
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                final int finalX = x;
                final int finalY = y;

                // Create tile view with click handler that passes coordinates to the listener
                TileUI tileBtn = new TileUI(e -> {
                    if (tileClickListener != null) {
                        // Create a new ActionEvent with coordinates as action command
                        ActionEvent newEvent = new ActionEvent(
                                e.getSource(),
                                ActionEvent.ACTION_PERFORMED,
                                finalX + "," + finalY
                        );
                        tileClickListener.actionPerformed(newEvent);
                    }
                });

                tileBtns[x][y] = tileBtn;
                boardPanel.add(tileBtn);
            }
        }
    }

    /**
     * Gets the board panel component for adding to a container.
     *
     * @return The JPanel containing the board
     */
    public JPanel getBoardPanel() {
        return boardPanel;
    }

    /**
     * Updates the entire board UI to reflect the current game state.
     */
    public void updateBoardUI() {
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                BoardTile tile = board.getTile(x, y);
                TileUI btn = tileBtns[x][y];

                // Reset tile to default state first
                if (!isHighlighted(x, y)) {
                    btn.resetBgColour();
                    btn.resetBorder();
                }

                // Show worker if present
                if (tile.getOccupant() != null) {
                    Worker worker = tile.getOccupant();

                    // Determine worker color based on team
                    String teamColor = TEAM_COLORS[worker.getTeam().getTeamId()];
                    String textColor = "#FFFFFF";

                    // Display worker
                    btn.showWorker(worker, teamColor, textColor);
                } else {
                    // Show building level
                    btn.showBuilding(tile);
                }
            }
        }

        // Force repaint
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    /**
     * Highlights the current player's workers to indicate they can be selected.
     *
     * @param currentTeamId The ID of the current player's team
     */
    public void highlightPlayerWorkers(int currentTeamId) {
        // Clear any existing highlights first
        clearHighlights();

        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                BoardTile tile = board.getTile(x, y);
                if (tile.getOccupant() != null && tile.getOccupant().getTeam().getTeamId() == currentTeamId) {
                    // Apply worker highlight
                    tileBtns[x][y].highlightPlayerWorkers();

                    // Add a thicker border to make it more visible (similar to move/build highlights)
                    tileBtns[x][y].setBorder(BorderFactory.createLineBorder(WORKER_HIGHLIGHT_COLOR, 4));

                    // Track the highlighted tile
                    highlightedTiles.add(new Point(x, y));
                }
            }
        }

        // Force UI update
        refreshHighlights();
    }

    /**
     * Highlights valid move locations for the selected worker.
     *
     * @param validMoves List of valid move locations
     */
    public void highlightValidMoves(List<BoardTile> validMoves) {
        // Clear any existing highlights first
        clearHighlights();

        if (validMoves.isEmpty()) {
            return;
        }

        // Highlight valid move tiles
        for (BoardTile tile : validMoves) {
            int x = tile.getX();
            int y = tile.getY();

            // Apply green background for move highlights
            tileBtns[x][y].highlightMove();

            // Add a thicker border to make it more visible
            tileBtns[x][y].setBorder(BorderFactory.createLineBorder(MOVE_HIGHLIGHT_COLOR, 4));

            // Track the highlighted tile
            highlightedTiles.add(new Point(x, y));
        }

        // Force UI update
        refreshHighlights();
    }

    /**
     * Highlights valid build locations for the selected worker.
     *
     * @param validBuilds List of valid build locations
     */
    public void highlightValidBuilds(List<BoardTile> validBuilds) {
        // Clear any existing highlights first
        clearHighlights();

        if (validBuilds.isEmpty()) {
            return;
        }

        // Highlight valid build tiles
        for (BoardTile tile : validBuilds) {
            int x = tile.getX();
            int y = tile.getY();

            // Apply yellow background for build highlights
            tileBtns[x][y].highlightBuild();

            // Add a thicker border to make it more visible
            tileBtns[x][y].setBorder(BorderFactory.createLineBorder(BUILD_HIGHLIGHT_COLOR, 4));

            // Track the highlighted tile
            highlightedTiles.add(new Point(x, y));
        }

        // Force UI update
        refreshHighlights();
    }

    /**
     * Highlights the currently selected worker.
     *
     * @param worker The selected worker
     */
    public void highlightSelectedWorker(Worker worker) {
        if (worker == null) return;

        // Highlight the worker's tile
        tileBtns[worker.getX()][worker.getY()].highlightSelected();
    }

    /**
     * Clears all highlighted tiles.
     */
    public void clearHighlights() {
        for (Point p : highlightedTiles) {
            // Reset both background and border
            tileBtns[p.x][p.y].resetBgColour();
            tileBtns[p.x][p.y].resetBorder();
            tileBtns[p.x][p.y].repaint();
        }
        highlightedTiles.clear();
        boardPanel.repaint();
    }

    /**
     * Checks if a tile is highlighted.
     *
     * @param x X-coordinate of the tile
     * @param y Y-coordinate of the tile
     * @return true if the tile is highlighted, false otherwise
     */
    public boolean isHighlighted(int x, int y) {
        for (Point p : highlightedTiles) {
            if (p.x == x && p.y == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Forces a refresh of the highlighted tiles.
     */
    private void refreshHighlights() {
        SwingUtilities.invokeLater(() -> {
            for (Point p : highlightedTiles) {
                tileBtns[p.x][p.y].invalidate();
                tileBtns[p.x][p.y].repaint();
            }
            boardPanel.revalidate();
            boardPanel.repaint();
        });
    }
}
