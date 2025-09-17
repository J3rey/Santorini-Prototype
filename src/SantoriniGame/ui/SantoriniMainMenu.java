package SantoriniGame.ui;

import SantoriniGame.tutorial.TutorialUI;

import javax.swing.*;
import java.awt.*;

/**
 * Main menu screen for the Santorini game.
 * Provides navigation to different game modes and options.
 */
public class SantoriniMainMenu extends JFrame {

    /**
     * Creates and displays the main menu.
     */
    public SantoriniMainMenu() {
        setTitle("Santorini");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 900);
        setLocationRelativeTo(null); // centre the window
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // Title - reduced top margin and font size
        JLabel titleLabel = new JLabel("Santorini", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 120)); // Reduced from 150
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Reduced margins
        add(titleLabel, BorderLayout.NORTH);

        // Button panel (center)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.WHITE);

        String[] labels = {"Start game", "Tutorial", "How to play", "Settings", "Quit"};

        for (String label : labels) {
            JButton button = new JButton(label);
            button.setFont(new Font("SansSerif", Font.PLAIN, 45)); // Further reduced from 60
            button.setBackground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(600, 85)); // Much smaller width and height
            button.setPreferredSize(new Dimension(600, 85)); // Much smaller width and height

            // Add actions
            if (label.equals("Start game")) {
                button.addActionListener(e -> {
                    new PlayerBoardSelection();
                    this.dispose();
                });
            } else if (label.equals("Tutorial")) {
                button.addActionListener(e -> {
                    new TutorialUI();
                    this.dispose();
                });
            } else if (label.equals("How to play")) {
                button.addActionListener(e -> showHowToPlay());
            } else if (label.equals("Settings")) {
                button.addActionListener(e -> showSettings());
            } else if (label.equals("Quit")) {
                button.addActionListener(e -> System.exit(0));
            }

            buttonPanel.add(button);
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 12))); // Further reduced spacing
        }

        // Center wrapper with better padding
        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        centerWrapper.setBackground(Color.WHITE);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50)); // Add side padding
        centerWrapper.add(Box.createVerticalGlue());
        centerWrapper.add(buttonPanel);
        centerWrapper.add(Box.createVerticalGlue());

        add(centerWrapper, BorderLayout.CENTER);

        // Footer - reduced font size and margin
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 20)); // Reduced bottom margin

        JLabel footer = new JLabel(
                "<html><div style='text-align:right;'>FIT3077 Santorini Game by:<br>"
                        + "Jeremias Yovinus<br>John Vo<br>Vincent Nguyen<br>Dulesha Gunarathe</div></html>",
                SwingConstants.RIGHT);
        footer.setFont(new Font("SansSerif", Font.PLAIN, 12)); // Reduced from 14
        footerPanel.add(footer, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Shows the How to Play instructions dialog.
     */
    private void showHowToPlay() {
        String howToPlayText =
                "<html><h1>How to Play Santorini</h1>" +
                        "<p><b>Objective:</b> Be the first to move one of your workers to a level 3 building.</p>" +
                        "<h2>Game Turn</h2>" +
                        "<ol>" +
                        "<li>Select one of your workers</li>" +
                        "<li>Move the selected worker to an adjacent space (including diagonally)</li>" +
                        "<li>Build a building level on an adjacent space to your moved worker</li>" +
                        "</ol>" +
                        "<h2>Movement Rules</h2>" +
                        "<ul>" +
                        "<li>Workers can move up a maximum of 1 level</li>" +
                        "<li>Workers can move down any number of levels</li>" +
                        "<li>Workers cannot move to spaces occupied by other workers</li>" +
                        "<li>Workers cannot move to spaces with domes</li>" +
                        "</ul>" +
                        "<h2>Building Rules</h2>" +
                        "<ul>" +
                        "<li>You can only build on empty adjacent spaces</li>" +
                        "<li>Buildings grow from level 0 to 3, then are capped with a dome</li>" +
                        "<li>You cannot build on spaces with workers or domes</li>" +
                        "</ul>" +
                        "<h2>God Powers</h2>" +
                        "<p>Each player has a unique god power that can affect movement, building, or win conditions.</p>" +
                        "<p>Examples:</p>" +
                        "<ul>" +
                        "<li><b>Artemis:</b> Your worker may move one additional time, but not back to its initial space</li>" +
                        "<li><b>Demeter:</b> Your worker may build one additional time, but not on the same space</li>" +
                        "</ul>" +
                        "<p><b>💡 Tip:</b> Try the <strong>Tutorial</strong> to learn the game step-by-step with interactive guidance!</p></html>";

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(howToPlayText);
        textPane.setEditable(false);
        textPane.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(600, 500));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "How to Play Santorini",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Shows the settings dialog (placeholder).
     */
    private void showSettings() {
        JOptionPane.showMessageDialog(
                this,
                "Settings options will be available in a future version.\n\n" +
                        "For now, try the Tutorial to learn how to play!",
                "Settings",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}