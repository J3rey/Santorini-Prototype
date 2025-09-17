package SantoriniGame.ui;

import SantoriniGame.model.player.Player;
import SantoriniGame.model.player.Team;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Victory screen displayed when a team wins the game.
 * Shows winning team information and game statistics.
 */
public class VictoryScreen extends JFrame {

    private final Team winningTeam;
    // Game statistics
    private final int domesBuilt;
    private final int workerMoves;
    private final int godPowerActivations;

    /**
     * Creates a new victory screen.
     * @param winningTeam the team that won the game
     * @param domesBuilt number of domes built during the game
     * @param workerMoves number of worker moves performed
     * @param godPowerActivations number of god power activations
     */
    public VictoryScreen(Team winningTeam, int domesBuilt, int workerMoves, int godPowerActivations) {
        this.winningTeam = winningTeam;
        this.domesBuilt = domesBuilt;
        this.workerMoves = workerMoves;
        this.godPowerActivations = godPowerActivations;

        initializeUI();
    }

    /**
     * Initializes the user interface.
     */
    private void initializeUI() {
        // Set up the frame
        setTitle("Santorini - Victory");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 900);
        setLocationRelativeTo(null); // Center the window
        setLayout(new BorderLayout(20, 20));
        getContentPane().setBackground(Color.WHITE);

        // Main panel with border
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        mainPanel.setBackground(Color.WHITE);

        // "Victory!" text in the center
        JLabel victoryLabel = new JLabel("Victory!", SwingConstants.CENTER);
        victoryLabel.setFont(new Font("SansSerif", Font.BOLD, 72));
        mainPanel.add(victoryLabel, BorderLayout.NORTH);

        // Center panel containing God image and stats
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setBackground(Color.WHITE);

        // Make tabs to show each player's god in the team
        JTabbedPane godPanel = new JTabbedPane();
        for (Player player : winningTeam.getTeamPlayers()) {
            String godPower = player.getSelectedGod().getName();
            String winnerName = player.getDisplayName();

            // Show image of player's god that they used
            JPanel godImagePanel = new JPanel();
            godImagePanel.setBackground(Color.WHITE);
            JLabel godImageLabel = getGodImage(player);
            godImagePanel.add(godImageLabel);

            godPanel.addTab(godPower + " (" + winnerName + ")", godImagePanel);
        }
        godPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        godPanel.setBackground(Color.WHITE);

        godPanel.setVisible(true);

        // Right side - Final stats with scroll pane
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        statsPanel.setBackground(Color.WHITE);

        JLabel statsTitle = new JLabel("Final Stats", SwingConstants.CENTER);
        statsTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        statsTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JTextArea statsTextArea = new JTextArea();
        statsTextArea.setEditable(false);
        statsTextArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        statsTextArea.setText(generateStatsText());
        statsTextArea.setBackground(new Color(240, 240, 255)); // Light blue background

        JScrollPane scrollPane = new JScrollPane(statsTextArea);

        statsPanel.add(statsTitle, BorderLayout.NORTH);
        statsPanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(godPanel);
        centerPanel.add(statsPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Button panel at the bottom
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton returnButton = createButton("Return to Main", e -> returnToMainMenu());
        JButton quitButton = createButton("Quit", e -> System.exit(0));
        JButton playAgainButton = createButton("Play Again", e -> startNewGame());

        buttonPanel.add(returnButton);
        buttonPanel.add(quitButton);
        buttonPanel.add(playAgainButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame with some padding
        add(mainPanel, BorderLayout.CENTER);

        // Set visible
        setVisible(true);
    }

    /**
     * Gets the god image for a winning player.
     * @param winningPlayer the player whose god image to display
     * @return label containing the god image
     */
    private JLabel getGodImage(Player winningPlayer) {
        // This is a placeholder for the god image
        // In a real implementation, you would load an actual image
        JLabel placeholder = new JLabel();
        placeholder.setBackground(Color.WHITE);
        placeholder.setOpaque(true);

        // Create a simple colored circle as a placeholder
        Icon placeholderIcon = new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.RED);
                g2d.fillOval(x + 50, y + 50, 100, 100);
                g2d.dispose();
            }

            @Override
            public int getIconWidth() {
                return 200;
            }

            @Override
            public int getIconHeight() {
                return 200;
            }
        };

        // Gets the god image from the player
        ImageIcon godImage = winningPlayer.getSelectedGod().getImageIcon();

        placeholder.setIcon(godImage);
        return placeholder;
    }

    /**
     * Creates a styled button with action listener.
     * @param text the button text
     * @param action the action to perform when clicked
     * @return the created button
     */
    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 18));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        button.addActionListener(action);
        return button;
    }

    /**
     * Generates the statistics text for display.
     * @return formatted statistics string
     */
    private String generateStatsText() {
        StringBuilder stats = new StringBuilder();
        for (Player player : winningTeam.getTeamPlayers()) {
            String winningPlayerName = player.getDisplayName();
            String godPower = player.getSelectedGod().getName();

            stats.append("Player: ").append(winningPlayerName).append("\n\n");
            stats.append("God Power: ").append(godPower).append("\n\n");
        }
        stats.append("Game Statistics:\n");
        stats.append("---------------\n\n");
        stats.append("Domes Built: ").append(domesBuilt).append("\n\n");
        stats.append("Worker Moves: ").append(workerMoves).append("\n\n");
        stats.append("God Power Activations: ").append(godPowerActivations).append("\n\n");

        return stats.toString();
    }

    /**
     * Returns to the main menu.
     */
    private void returnToMainMenu() {
        dispose();
        new SantoriniMainMenu();
    }

    /**
     * Starts a new game.
     */
    private void startNewGame() {
        dispose();
        new PlayerBoardSelection();
    }
}