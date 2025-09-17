package SantoriniGame.model.board;

import SantoriniGame.model.player.Worker;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * TileUI class. Extends JButton. Represents a View object for a given BoardTile.
 */
public class TileUI extends JButton {
    private static final Dimension PREF_DIMENSION = new Dimension(80, 80);
    private static final Color DEFAULT_BG_COLOUR = new Color(200, 200, 220);
    private static final Border DEFAULT_BORDER = BorderFactory.createLineBorder(Color.BLACK);

    private static final Border SELECTED_BORDER = BorderFactory.createLineBorder(Color.RED, 3);
    private static final Border PLAYER_WORKER_BORDER = BorderFactory.createLineBorder(Color.YELLOW, 2);

    public static final Color MOVE_HIGHLIGHT = Color.GREEN;
    public static final Color BUILD_HIGHLIGHT = Color.YELLOW;

    /**
     * Creates a new TileUI with specified action listener.
     * @param al the action listener for tile clicks
     */
    public TileUI(ActionListener al) {
        super();

        setPreferredSize(PREF_DIMENSION);
        setBackground(DEFAULT_BG_COLOUR);
        setBorder(DEFAULT_BORDER);

        addActionListener(al);
    }

    /**
     * Shows the given worker on this tile.
     * @param worker the worker to represent
     * @param teamColour background colour for worker
     * @param textColour text colour for worker
     */
    public void showWorker(Worker worker, String teamColour, String textColour) {
        setText("<html><div style='text-align: center; font-size: 20px;'>" +
                "<div style='background-color: " + teamColour + "; color: " + textColour +
                "; border-radius: 50%; display: inline-block; width: 30px; height: 30px; line-height: 30px;'>" +
                worker.getGender().getDisplayString() + "</div></div></html>");
    }

    /**
     * Shows the building on this tile.
     * @param tile the tile with the building
     */
    public void showBuilding(BoardTile tile) {
        String levelLabel = tile.hasDome() ? "D" : String.valueOf(tile.getLevel());

        // If tile as dome or >= 1 layer, display, else, empty
        if (tile.getLevel() > 0 || tile.hasDome()) {
            setText("<html><div style='text-align: center; font-size: 16px;'>" + levelLabel + "</div></html>");
        } else {
            setText("");
        }
    }

    /**
     * Highlights this tile for move actions.
     */
    public void highlightMove() {
        setBackground(MOVE_HIGHLIGHT);
    }

    /**
     * Highlights this tile for build actions.
     */
    public void highlightBuild() {
        setBackground(BUILD_HIGHLIGHT);
    }

    /**
     * Highlights this tile as selected.
     */
    public void highlightSelected() {
        setBorder(SELECTED_BORDER);
    }

    /**
     * Highlights this tile for player workers.
     */
    public void highlightPlayerWorkers() {
        setBorder(PLAYER_WORKER_BORDER);
    }

    /**
     * Resets background colour to default.
     */
    public void resetBgColour() {
        setBackground(DEFAULT_BG_COLOUR);
    }

    /**
     * Resets border to default.
     */
    public void resetBorder() {
        setBorder(DEFAULT_BORDER);
    }
}