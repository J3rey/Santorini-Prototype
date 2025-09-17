package SantoriniGame.ui;
import SantoriniGame.god.God;
import SantoriniGame.model.player.Player;
import SantoriniGame.controller.GodSelectionPresenter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the GodSelectionView interface using Swing.
 * Updated to match the styling of PlayerBoardSelection.
 */
public class GodSelection extends JFrame implements GodSelectionView {

    // UI Constants - matching PlayerBoardSelection styling
    private static final Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    private static final Color ACTIVE_PLAYER_COLOR = new Color(220, 240, 220); // Light green
    private static final Color SELECTED_GOD_COLOR = new Color(220, 240, 220);
    private static final int BORDER_THICKNESS = 4; // Thicker borders like PlayerBoardSelection
    private static final Dimension GOD_IMAGE_SIZE = new Dimension(300, 300);

    // UI Components
    private final List<JLabel> playerLabels;
    private final JPanel playerIndicatorPanel;
    private JLabel godNameLabel;
    private JTextArea godDescriptionArea;
    private JLabel godImageLabel;
    private JPanel godListPanel;
    private final Map<God, JButton> godButtons;
    private JButton confirmButton;
    private final JPanel mainPanel;
    private JButton backButton;

    // Presenter reference
    private GodSelectionPresenter presenter;

    /**
     * Constructor for the god selection view.
     * @param numPlayers number of players for initial UI setup
     */
    public GodSelection(int numPlayers) {
        // Set up the frame
        setTitle("Santorini God Selection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 900);
        setLocationRelativeTo(null);

        this.playerLabels = new ArrayList<>();
        this.godButtons = new HashMap<>();

        // Create main panel to hold all components
        mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 50, 20, 50)); // More padding like PlayerBoardSelection
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        // Create title at the top
        JLabel titleLabel = new JLabel("God Selection", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 55)); // Match PlayerBoardSelection title size
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create Player Indicator Panel (below title)
        playerIndicatorPanel = createPlayerIndicatorPanel(numPlayers);

        // Create Central Panel with god selection
        JPanel centralPanel = createCentralPanel();

        // Combine player indicators and central panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(playerIndicatorPanel, BorderLayout.NORTH);
        contentPanel.add(centralPanel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Create bottom panel with navigation buttons
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Make the frame visible
        setVisible(true);
    }

    /**
     * Sets the presenter for this view.
     * @param presenter the GodSelectionPresenter instance
     */
    @Override
    public void setPresenter(GodSelectionPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Updates the player turn indicators in the UI.
     * @param currentPlayerIndex index of the current player
     * @param players list of players with their selected gods
     */
    @Override
    public void displayPlayerTurn(int currentPlayerIndex, List<Player> players) {
        // Update all player labels to reflect current state
        for (int i = 0; i < playerLabels.size() && i < players.size(); i++) {
            JLabel label = playerLabels.get(i);
            Player player = players.get(i);
            String labelText = player.getDisplayName();

            // Background and border styling based on selection status
            if (i == currentPlayerIndex && !presenter.isSelectionComplete()) {
                // Current player's turn - highlight with thick border like PlayerBoardSelection
                label.setBackground(ACTIVE_PLAYER_COLOR);
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK, BORDER_THICKNESS));
            } else {
                // Not current player
                label.setBackground(DEFAULT_BACKGROUND_COLOR);
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            }

            // Show selected god name if applicable
            if (player.hasSelectedGod()) {
                labelText += ": " + player.getSelectedGod().getName();
            }
            label.setText(labelText);
        }

        playerIndicatorPanel.revalidate();
        playerIndicatorPanel.repaint();
    }

    /**
     * Shows details for the specified god.
     * @param god god to display details for
     */
    @Override
    public void displayGodDetails(God god) {
        if (god == null) {
            godNameLabel.setText("Select a God");
            godDescriptionArea.setText("Choose your god from the list.");
            godImageLabel.setIcon(null);
            godImageLabel.setText("No God Selected");
            return;
        }

        godNameLabel.setText(god.getName());
        godDescriptionArea.setText(god.getDescription());
        godDescriptionArea.setCaretPosition(0); // Scroll to top

        // Resize icon to fit the display area
        ImageIcon originalIcon = god.getImageIcon();
        if (originalIcon != null) {
            Image img = originalIcon.getImage();
            Image resizedImg = img.getScaledInstance(
                    GOD_IMAGE_SIZE.width,
                    GOD_IMAGE_SIZE.height,
                    Image.SCALE_SMOOTH
            );
            godImageLabel.setIcon(new ImageIcon(resizedImg));
            godImageLabel.setText(null);
        } else {
            godImageLabel.setIcon(null);
            godImageLabel.setText("Image Not Available");
        }
    }

    /**
     * Updates the list of available gods with selection status.
     * @param availableGods list of all gods in the game
     * @param players list of players with their selected gods
     * @param currentPlayerIndex index of the current player
     */
    @Override
    public void displayGodList(List<God> availableGods, List<Player> players, int currentPlayerIndex) {
        // First clear the existing buttons from the panel
        godListPanel.removeAll();
        godButtons.clear();

        // Create a new layout for the gods
        godListPanel.setLayout(new GridLayout(0, 1, 0, 15)); // Increased spacing

        // Add buttons for each god
        for (God god : availableGods) {
            JButton godButton = createGodButton(god);
            godButtons.put(god, godButton);
            godListPanel.add(godButton);
        }

        // Update the visual state of each button
        for (God god : godButtons.keySet()) {
            JButton button = godButtons.get(god);

            // Check if this god is already selected
            int selectedByPlayer = presenter.getPlayerWhoSelected(god);
            boolean isSelected = selectedByPlayer != -1;

            // Enable only if not already selected by anyone else
            boolean enableButton = !isSelected || selectedByPlayer == currentPlayerIndex;
            button.setEnabled(enableButton && !presenter.isSelectionComplete());

            // Visual styling - match PlayerBoardSelection button styling
            if (isSelected) {
                if (selectedByPlayer == currentPlayerIndex) {
                    // This is the current player's selection
                    button.setBackground(SELECTED_GOD_COLOR);
                    button.setBorder(BorderFactory.createLineBorder(Color.BLACK, BORDER_THICKNESS));
                } else {
                    // Selected by another player
                    button.setBackground(Color.LIGHT_GRAY);
                    button.setForeground(Color.DARK_GRAY);
                    button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                }
            } else {
                // Reset border and colors for available gods
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                button.setBackground(Color.WHITE);
                button.setForeground(Color.BLACK);
            }
        }

        godListPanel.revalidate();
        godListPanel.repaint();
    }

    /**
     * Enables or disables the confirm button.
     * @param enabled whether the button should be enabled
     */
    @Override
    public void setConfirmEnabled(boolean enabled) {
        confirmButton.setEnabled(enabled);
    }

    /**
     * Shows a completion message to the user.
     * @param title title of the message
     * @param message content of the message
     */
    @Override
    public void showCompletionMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // UI Creation Helper Methods

    /**
     * Creates the panel showing player turn indicators.
     * @param numPlayers number of players to create indicators for
     * @return configured JPanel
     */
    private JPanel createPlayerIndicatorPanel(int numPlayers) {
        JPanel panel = new JPanel(new GridLayout(1, numPlayers, 30, 0)); // Horizontal layout like PlayerBoardSelection
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 100, 20, 100)); // Center with padding

        for (int i = 0; i < numPlayers; i++) {
            JLabel playerLabel = new JLabel("Player " + (i + 1), SwingConstants.CENTER);
            playerLabel.setOpaque(true);
            playerLabel.setBackground(DEFAULT_BACKGROUND_COLOR);
            playerLabel.setForeground(Color.BLACK);
            playerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            playerLabel.setFont(new Font("SansSerif", Font.BOLD, 24)); // Larger font like PlayerBoardSelection
            playerLabel.setPreferredSize(new Dimension(200, 60)); // Fixed size like PlayerBoardSelection
            playerLabels.add(playerLabel);
            panel.add(playerLabel);
        }
        return panel;
    }

    /**
     * Creates the central panel with god selection interface.
     * @return configured JPanel
     */
    private JPanel createCentralPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 30, 0)); // Equal spacing like PlayerBoardSelection
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // God Info Panel (Left)
        JPanel godInfoPanel = createGodInfoPanel();
        panel.add(godInfoPanel);

        // God Image Panel (Middle)
        JPanel godImagePanel = createGodImagePanel();
        panel.add(godImagePanel);

        // God List Panel (Right)
        JScrollPane godListScrollPane = createGodListScrollPane();
        panel.add(godListScrollPane);

        return panel;
    }

    /**
     * Creates the panel for god description and details.
     * @return configured JPanel
     */
    private JPanel createGodInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Match PlayerBoardSelection border style
        panel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("God Power:", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        panel.add(titleLabel, BorderLayout.NORTH);

        // God name
        godNameLabel = new JLabel("Select a God", SwingConstants.CENTER);
        godNameLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        godNameLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(godNameLabel, BorderLayout.CENTER);

        // Description area
        godDescriptionArea = new JTextArea("Choose your god from the list.");
        godDescriptionArea.setWrapStyleWord(true);
        godDescriptionArea.setLineWrap(true);
        godDescriptionArea.setEditable(false);
        godDescriptionArea.setFocusable(false);
        godDescriptionArea.setBackground(Color.WHITE);
        godDescriptionArea.setBorder(new EmptyBorder(10, 15, 15, 15));
        godDescriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JScrollPane descScrollPane = new JScrollPane(godDescriptionArea);
        descScrollPane.setBorder(null);
        descScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(descScrollPane, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the panel for displaying the god image.
     * @return configured JPanel
     */
    private JPanel createGodImagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("God Image:", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        panel.add(titleLabel, BorderLayout.NORTH);

        godImageLabel = new JLabel("No God Selected", SwingConstants.CENTER);
        godImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        godImageLabel.setPreferredSize(GOD_IMAGE_SIZE);
        godImageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        panel.add(godImageLabel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates the scrollable panel for the god list.
     * @return configured JScrollPane
     */
    private JScrollPane createGodListScrollPane() {
        godListPanel = new JPanel();
        godListPanel.setLayout(new GridLayout(0, 1, 0, 15));
        godListPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        godListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(godListPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    /**
     * Creates a button for a specific god.
     * @param god god to create button for
     * @return configured JButton
     */
    private JButton createGodButton(God god) {
        JButton godButton = new JButton(god.getName());
        godButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        godButton.setFocusable(true);
        godButton.setBackground(Color.WHITE);
        godButton.setFont(new Font("SansSerif", Font.BOLD, 18)); // Larger font
        godButton.setPreferredSize(new Dimension(250, 50)); // Fixed size like PlayerBoardSelection

        godButton.addActionListener(e -> {
            if (presenter != null) {
                presenter.onGodButtonClicked(god);
            }
        });
        return godButton;
    }

    /**
     * Creates the bottom panel with navigation buttons.
     * @return configured JPanel
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Back button (left)
        backButton = new JButton("Back");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 24)); // Match PlayerBoardSelection
        backButton.setPreferredSize(new Dimension(300, 60)); // Match PlayerBoardSelection
        backButton.setBackground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        backButton.addActionListener(e -> goBackToPlayerSelection());

        // Confirm button (right)
        confirmButton = new JButton("Confirm");
        confirmButton.setFont(new Font("SansSerif", Font.PLAIN, 24));
        confirmButton.setPreferredSize(new Dimension(300, 60));
        confirmButton.setBackground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(e -> {
            if (presenter != null) {
                presenter.onConfirmClicked();
            }
        });

        panel.add(backButton, BorderLayout.WEST);
        panel.add(confirmButton, BorderLayout.EAST);

        return panel;
    }

    /**
     * Returns to the player and board selection screen.
     */
    private void goBackToPlayerSelection() {
        // Create a new PlayerBoardSelection screen
        new PlayerBoardSelection();

        // Close this window
        this.dispose();
    }
}