package SantoriniGame;

import SantoriniGame.ui.SantoriniMainMenu;

import javax.swing.*;

/**
 * Main entry point for Santorini game.
 * Launches the main menu from which players can begin the game.
 */
public class SantoriniGameLauncher {

    /**
     * Main method that starts the game.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set look and feel to the system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Failed to set look and feel: " + e.getMessage());
        }

        // Launch the main menu on the Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new SantoriniMainMenu();
        });
    }
}