package SantoriniGame.ui;

import SantoriniGame.god.God;
import SantoriniGame.god.GodFactory;
import SantoriniGame.controller.GodSelectionPresenter;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PlayerBoardSelection class - Handles player count and board size selection.
 * Simplified to focus only on UI selection without game logic constants.
 */
public class PlayerBoardSelection extends JFrame {

    // Selection state
    private int selectedPlayers = 2;
    private String selectedBoardSize = "5x5";

    // Button tracking maps
    private Map<Integer, JButton> playerButtons = new HashMap<>();
    private Map<String, JButton> boardButtons = new HashMap<>();

    /**
     * Constructor that creates the player and board selection screen.
     */
    public PlayerBoardSelection() {
        setTitle("Player and Board Selection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 900);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // Top title
        JLabel topNote = new JLabel("Game Setup", SwingConstants.CENTER);
        topNote.setFont(new Font("SansSerif", Font.PLAIN, 55));
        topNote.setBorder(BorderFactory.createEmptyBorder(10, 0, 2, 0));
        add(topNote, BorderLayout.NORTH);

        // Center panel - 2 columns for players and board
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 100, 0));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 20, 100));

        // Create the two selection panels
        JPanel playerPanel = createPlayerSelectionPanel();
        JPanel boardPanel = createBoardSelectionPanel();

        centerPanel.add(playerPanel);
        centerPanel.add(boardPanel);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with navigation buttons
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // Initialize button selection state
        updatePlayerButtonSelection();
        updateBoardButtonSelection();

        setVisible(true);
    }

    /**
     * Creates the player count selection panel.
     *
     * @return Configured player selection panel
     */
    private JPanel createPlayerSelectionPanel() {
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
        playerPanel.setBackground(Color.WHITE);

        JLabel playerLabel = new JLabel("Players:");
        playerLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerPanel.add(playerLabel);
        playerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel playerButtonsPanel = new JPanel(new GridLayout(2, 2, 30, 30));
        playerButtonsPanel.setOpaque(false);

        // Create player number buttons (2-4 players)
        for (int i = 2; i <= 4; i++) {
            JButton btn = createStyledButton(String.valueOf(i), 100, 80);
            btn.setFont(new Font("SansSerif", Font.BOLD, 40));
            int playerCount = i;

            btn.addActionListener(e -> {
                selectedPlayers = playerCount;
                updatePlayerButtonSelection();
            });

            playerButtons.put(i, btn);
            playerButtonsPanel.add(btn);
        }

        // Add empty slot for visual balance (2x2 grid)
        playerButtonsPanel.add(Box.createGlue());

        playerPanel.add(playerButtonsPanel);
        return playerPanel;
    }

    /**
     * Creates the board size selection panel.
     *
     * @return Configured board selection panel
     */
    private JPanel createBoardSelectionPanel() {
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new BoxLayout(boardPanel, BoxLayout.Y_AXIS));
        boardPanel.setBackground(Color.WHITE);

        JLabel boardLabel = new JLabel("Board Size:");
        boardLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        boardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        boardPanel.add(boardLabel);
        boardPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel boardButtonsPanel = new JPanel(new GridLayout(2, 1, 30, 30));
        boardButtonsPanel.setOpaque(false);

        // Create board size buttons
        String[] boardSizes = {"5x5", "7x7"};
        for (String size : boardSizes) {
            JButton btn = createStyledButton(size, 120, 80);
            btn.setFont(new Font("SansSerif", Font.BOLD, 40));

            btn.addActionListener(e -> {
                selectedBoardSize = size;
                updateBoardButtonSelection();
            });

            boardButtons.put(size, btn);
            boardButtonsPanel.add(btn);
        }

        boardPanel.add(boardButtonsPanel);
        return boardPanel;
    }

    /**
     * Creates the bottom navigation panel.
     *
     * @return Configured bottom panel
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        JButton backButton = createStyledButton("Back", 300, 60);
        JButton confirmButton = createStyledButton("Confirm", 300, 60);

        backButton.addActionListener(e -> {
            // Return to main menu
            new SantoriniMainMenu();
            this.dispose();
        });

        confirmButton.addActionListener(e -> {
            // Get all available gods using the GodFactory
            List<God> allGods = GodFactory.getInstance().createAllGods();

            // Setup the God Selection screen - no timer parameter needed
            GodSelectionModel model = new GodSelectionModel(selectedPlayers, allGods);
            GodSelection view = new GodSelection(selectedPlayers);
            new GodSelectionPresenter(
                    model,
                    view,
                    Integer.parseInt(selectedBoardSize.split("x")[0])
                    // No timer parameter - GodSelectionPresenter will use SantoriniGame's default
            );

            // Close this window
            this.dispose();
        });

        bottomPanel.add(backButton, BorderLayout.WEST);
        bottomPanel.add(confirmButton, BorderLayout.EAST);

        return bottomPanel;
    }

    /**
     * Creates a styled button with consistent appearance.
     *
     * @param text   Button text
     * @param width  Button width
     * @param height Button height
     * @return Configured JButton
     */
    private JButton createStyledButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 24));
        button.setPreferredSize(new Dimension(width, height));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        return button;
    }

    /**
     * Updates the visual state of player buttons based on current selection.
     */
    private void updatePlayerButtonSelection() {
        for (Map.Entry<Integer, JButton> entry : playerButtons.entrySet()) {
            if (entry.getKey() == selectedPlayers) {
                // Selected button - bold border and different background
                entry.getValue().setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
                entry.getValue().setBackground(new Color(220, 240, 220)); // Light green
            } else {
                // Unselected buttons - normal border and white background
                entry.getValue().setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                entry.getValue().setBackground(Color.WHITE);
            }
        }
    }

    /**
     * Updates the visual state of board size buttons based on current selection.
     */
    private void updateBoardButtonSelection() {
        for (Map.Entry<String, JButton> entry : boardButtons.entrySet()) {
            if (entry.getKey().equals(selectedBoardSize)) {
                // Selected button - bold border and different background
                entry.getValue().setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
                entry.getValue().setBackground(new Color(220, 240, 220)); // Light green
            } else {
                // Unselected buttons - normal border and white background
                entry.getValue().setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                entry.getValue().setBackground(Color.WHITE);
            }
        }
    }
}