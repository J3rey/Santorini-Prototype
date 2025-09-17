package SantoriniGame.model.game;

import SantoriniGame.god.God;
import SantoriniGame.model.player.Player;
import SantoriniGame.model.player.Team;
import SantoriniGame.model.player.Worker;
import SantoriniGame.ui.VictoryScreen;
import SantoriniGame.model.victory.VictoryCondition;
import SantoriniGame.ui.BoardUI;
import SantoriniGame.controller.GameController;
import SantoriniGame.ui.TimerUIPanel;
import SantoriniGame.model.timer.PlayerTimer;
import SantoriniGame.model.timer.TimerListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Main Santorini game UI implementation.
 * Updated to match the clean UI theme from setup screens.
 */
public class SantoriniGame extends JFrame {

    // Game timing configuration
    public static final int DEFAULT_TIMER_MINUTES = 1;

    // Game components
    private final GameModel model;
    private final GameController controller;
    private final BoardUI boardUI;

    // Timer components
    private TimerUIPanel timerPanel;

    // UI components
    private JPanel sidePanel;
    private JLabel titleLabel;
    private JLabel currentPlayerLabel;
    private JLabel currentPhaseLabel;
    private JLabel selectedGodLabel;
    private JTextArea godDescriptionArea;
    private JLabel movesLabel;
    private JLabel domesLabel;
    private JLabel powersLabel;
    private JLabel statusLabel;
    private JButton godPowerButton;
    private JButton endTurnButton;
    private JButton deselectWorkerButton;

    /**
     * Constructor that initializes a game with the specified gods for players.
     * Uses the default 15-minute timer for all games.
     */
    public SantoriniGame(God[] playerGods, int boardSize) {
        this(playerGods, boardSize, DEFAULT_TIMER_MINUTES);
    }

    /**
     * Constructor that initializes a game with the specified gods for players.
     */
    public SantoriniGame(God[] playerGods, int boardSize, int timeLimitMinutes) {
        // Initialize model and controller
        model = new GameModel(playerGods, boardSize);
        controller = new GameController(model);

        // Initialize timers if time limit is set
        if (timeLimitMinutes > 0) {
            controller.initializeTimers(model.getPlayers(), timeLimitMinutes);
        }

        // Set up the UI
        setTitle("Santorini Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(20, 20));
        getContentPane().setBackground(Color.WHITE);

        // Create board UI with tile click handler
        boardUI = new BoardUI(boardSize, model.getBoard(), this::handleTileClick);

        // Set up UI components
        setupUI();

        // Initial UI update
        updateStatusLabel("Game started! " + model.getCurrentPlayer().getDisplayName() + ", select one of your workers.");
        updateUI();

        // Set window properties
        setSize(1600, 1000);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Gets the default timer limit for Santorini games.
     */
    public static int getDefaultTimerLimit() {
        return DEFAULT_TIMER_MINUTES;
    }

    /**
     * Sets up the main game UI components with consistent theme.
     */
    private void setupUI() {
        // Main container with padding
        JPanel mainContainer = new JPanel(new BorderLayout(20, 20));
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainContainer.setBackground(Color.WHITE);

        // Add title at the top
        titleLabel = new JLabel("Santorini", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainContainer.add(titleLabel, BorderLayout.NORTH);

        // Create central game area
        JPanel gameArea = new JPanel(new BorderLayout(20, 0));
        gameArea.setBackground(Color.WHITE);

        // Add timer panel if enabled (left side)
        if (controller.isTimerEnabled()) {
            timerPanel = new TimerUIPanel(controller.getTimerManager());
            timerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            timerPanel.setPreferredSize(new Dimension(280, 400));

            // Add timer listener to handle timeout
            controller.getTimerManager().addTimerListener(new TimerListener() {
                @Override
                public void onTimerStarted(PlayerTimer timer) {
                    // Timer started - no special action needed
                }

                @Override
                public void onTimerPaused(PlayerTimer timer) {
                    // Timer paused - no special action needed
                }

                @Override
                public void onTimeout(PlayerTimer timer) {
                    // Handle timeout - remove the player's team
                    SwingUtilities.invokeLater(() -> handlePlayerTimeout(timer));
                }

                @Override
                public void onAllTimersPaused() {
                    // All timers paused - no special action needed
                }

                @Override
                public void onTimersStop() {
                    // Timers stopped - no special action needed
                }
            });

            gameArea.add(timerPanel, BorderLayout.WEST);
        }

        // Add board in center with border
        JPanel boardContainer = new JPanel(new BorderLayout());
        boardContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        boardContainer.setBackground(Color.WHITE);
        boardContainer.add(boardUI.getBoardPanel(), BorderLayout.CENTER);
        gameArea.add(boardContainer, BorderLayout.CENTER);

        // Add side panel (right side)
        sidePanel = createSidePanel();
        gameArea.add(sidePanel, BorderLayout.EAST);

        mainContainer.add(gameArea, BorderLayout.CENTER);

        // Add status at the bottom
        JPanel statusPanel = createStatusPanel();
        mainContainer.add(statusPanel, BorderLayout.SOUTH);

        add(mainContainer, BorderLayout.CENTER);
    }

    /**
     * Creates the side panel with game info and controls using consistent theming.
     */
    private JPanel createSidePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel.setPreferredSize(new Dimension(350, 600));

        // Player info section
        JPanel playerInfoPanel = createPlayerInfoPanel();
        panel.add(playerInfoPanel, BorderLayout.NORTH);

        // Game statistics section
        JPanel statsPanel = createStatsPanel();
        panel.add(statsPanel, BorderLayout.CENTER);

        // Action buttons section
        JPanel buttonPanel = createActionButtonPanel();
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the player information panel.
     */
    private JPanel createPlayerInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

        // Section title
        JLabel sectionTitle = new JLabel("Current Player");
        sectionTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        sectionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(sectionTitle);
        panel.add(Box.createVerticalStrut(10));

        // Current player info
        TurnState turnState = model.getTurnState();
        Player currentPlayer = turnState.getCurrPlayer();

        currentPlayerLabel = new JLabel(currentPlayer.getDisplayName());
        currentPlayerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        currentPlayerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(currentPlayerLabel);
        panel.add(Box.createVerticalStrut(5));

        currentPhaseLabel = new JLabel("Phase: Select Worker");
        currentPhaseLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        currentPhaseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(currentPhaseLabel);
        panel.add(Box.createVerticalStrut(15));

        // God power info
        selectedGodLabel = new JLabel("God Power: " + currentPlayer.getSelectedGod().getName());
        selectedGodLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        selectedGodLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(selectedGodLabel);
        panel.add(Box.createVerticalStrut(8));

        godDescriptionArea = new JTextArea(currentPlayer.getSelectedGod().getDescription());
        godDescriptionArea.setWrapStyleWord(true);
        godDescriptionArea.setLineWrap(true);
        godDescriptionArea.setEditable(false);
        godDescriptionArea.setBackground(Color.WHITE);
        godDescriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        godDescriptionArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane descScrollPane = new JScrollPane(godDescriptionArea);
        descScrollPane.setPreferredSize(new Dimension(300, 80));
        descScrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        descScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(descScrollPane);

        return panel;
    }

    /**
     * Creates the game statistics panel.
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Section title
        JLabel statsTitle = new JLabel("Game Statistics");
        statsTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        statsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(statsTitle);
        panel.add(Box.createVerticalStrut(15));

        // Statistics
        movesLabel = new JLabel("Worker Moves: " + model.getMoveCount());
        movesLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        movesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(movesLabel);
        panel.add(Box.createVerticalStrut(8));

        domesLabel = new JLabel("Domes Built: " + model.getDomeCount());
        domesLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        domesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(domesLabel);
        panel.add(Box.createVerticalStrut(8));

        powersLabel = new JLabel("God Powers Used: " + model.getGodPowerActivationCount());
        powersLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        powersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(powersLabel);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    /**
     * Creates the action button panel with consistent styling.
     */
    private JPanel createActionButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        // Action buttons with consistent theming
        godPowerButton = createStyledButton("Use God Power");
        godPowerButton.addActionListener(e -> activateGodPower());
        godPowerButton.setEnabled(false);

        deselectWorkerButton = createStyledButton("Deselect Worker");
        deselectWorkerButton.addActionListener(e -> deselectWorker());
        deselectWorkerButton.setEnabled(false);

        endTurnButton = createStyledButton("End Turn");
        endTurnButton.addActionListener(e -> endTurn());
        endTurnButton.setEnabled(false);

        // Add buttons with spacing
        panel.add(godPowerButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(deselectWorkerButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(endTurnButton);

        return panel;
    }

    /**
     * Creates a styled button matching the theme.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 16));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setPreferredSize(new Dimension(280, 45));
        button.setMaximumSize(new Dimension(280, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    /**
     * Creates the status panel at the bottom.
     */
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        statusLabel = new JLabel("Welcome to Santorini");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(240, 248, 255));
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                new EmptyBorder(15, 20, 15, 20)
        ));

        panel.add(statusLabel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Updates the status label with a message.
     */
    private void updateStatusLabel(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    /**
     * Handles a click on a board tile.
     */
    private void handleTileClick(ActionEvent e) {
        // Check for timeout before processing the click
        if (controller.isTimerEnabled()) {
            PlayerTimer expiredTimer = controller.checkForTimeout();
            if (expiredTimer != null) {
                handlePlayerTimeout(expiredTimer);
                return; // Don't process the tile click
            }
        }

        // Parse coordinates from action command
        String[] coords = e.getActionCommand().split(",");
        int x = Integer.parseInt(coords[0]);
        int y = Integer.parseInt(coords[1]);

        // Let controller handle the click
        TurnState newState = controller.handleTileClick(x, y);

        // Check for victory after the action
        VictoryCondition winCondition = controller.checkForWinner();
        if (winCondition != null) {
            handleVictory(winCondition);
            return;
        }

        // Update UI based on current state
        TurnPhase phase = controller.getCurrentPhase();
        updateUIForPhase(phase);

        // Update status message
        updateStatusLabel(controller.getStateDescription());

        // Update the entire UI
        updateUI();
    }

    /**
     * Updates UI elements based on the current game phase.
     */
    private void updateUIForPhase(TurnPhase phase) {
        // Update phase label
        currentPhaseLabel.setText("Phase: " + getPhaseDisplayName(phase));

        // Clear previous highlights
        boardUI.clearHighlights();

        // Update UI based on phase
        switch (phase) {
            case SELECT_WORKER:
                // Highlight current player's workers
                boardUI.highlightPlayerWorkers(controller.getCurrentPlayer().getTeam().getTeamId());
                godPowerButton.setEnabled(false);
                deselectWorkerButton.setEnabled(false);
                endTurnButton.setEnabled(false);
                break;

            case MOVE:
                // Highlight valid moves
                boardUI.highlightValidMoves(controller.getValidMoves());
                godPowerButton.setEnabled(controller.canActivateGodPower());
                deselectWorkerButton.setEnabled(true);
                endTurnButton.setEnabled(false);
                break;

            case BUILD:
                // Highlight valid builds
                boardUI.highlightValidBuilds(controller.getValidBuilds());
                godPowerButton.setEnabled(controller.canActivateGodPower());
                deselectWorkerButton.setEnabled(false);
                endTurnButton.setEnabled(false);
                break;

            case END_TURN:
                // No highlights, just enable end turn
                godPowerButton.setEnabled(controller.canActivateGodPower());
                deselectWorkerButton.setEnabled(false);
                endTurnButton.setEnabled(true);
                break;
        }

        // Always highlight selected worker if there is one
        Worker selectedWorker = controller.getSelectedWorker();
        if (selectedWorker != null) {
            boardUI.highlightSelectedWorker(selectedWorker);
        }
    }

    /**
     * Activates the current player's god power.
     */
    private void activateGodPower() {
        // Let controller handle power activation
        TurnState newState = controller.activateGodPower();

        // Update UI for the new state
        updateUIForPhase(newState.getCurrPhase());
        updateStatusLabel(controller.getStateDescription());
        updateUI();
    }

    /**
     * Ends the current player's turn.
     */
    private void endTurn() {
        // Let controller handle turn end
        controller.endTurn();

        // Update UI for new player
        updateUIForPhase(controller.getCurrentPhase());
        updateStatusLabel(controller.getCurrentPlayer().getDisplayName() + "'s turn. " + controller.getStateDescription());
        updateUI();
    }

    /**
     * Deselects the currently selected worker.
     */
    private void deselectWorker() {
        // Let controller handle deselection
        TurnState newState = controller.deselectWorker();

        // Update UI for worker selection phase
        updateUIForPhase(TurnPhase.SELECT_WORKER);
        updateStatusLabel(controller.getCurrentPlayer().getDisplayName() + ", select one of your workers.");
        updateUI();
    }

    /**
     * Updates the entire UI to reflect the current game state.
     */
    private void updateUI() {
        // Update board
        boardUI.updateBoardUI();

        // Update player information
        Player currentPlayer = controller.getCurrentPlayer();
        currentPlayerLabel.setText(currentPlayer.getDisplayName());
        selectedGodLabel.setText("God Power: " + currentPlayer.getSelectedGod().getName());
        godDescriptionArea.setText(currentPlayer.getSelectedGod().getDescription());

        // Update game statistics
        GameModel model = controller.getModel();
        movesLabel.setText("Worker Moves: " + model.getMoveCount());
        domesLabel.setText("Domes Built: " + model.getDomeCount());
        powersLabel.setText("God Powers Used: " + model.getGodPowerActivationCount());

        // Update UI for the phase as relevant
        updateUIForPhase(controller.getCurrentPhase());
    }

    /**
     * Handles a team winning the game.
     */
    private void handleVictory(VictoryCondition winCondition) {
        Team winningTeam = winCondition.getTeam();

        updateStatusLabel("TEAM " + (winningTeam.getTeamId() + 1) + " WINS!");

        // Stop all timers
        if (controller.isTimerEnabled()) {
            controller.stopAllTimers();
        }

        // Show victory screen and dispose of current game window
        SwingUtilities.invokeLater(() -> {
            VictoryScreen victoryScreen = new VictoryScreen(
                    winningTeam,
                    model.getDomeCount(),
                    model.getMoveCount(),
                    model.getGodPowerActivationCount()
            );
            victoryScreen.setVisible(true);
            this.dispose();
        });
    }

    /**
     * Gets a display name for a turn phase.
     */
    private String getPhaseDisplayName(TurnPhase phase) {
        switch (phase) {
            case SELECT_WORKER: return "Select Worker";
            case MOVE: return "Move Worker";
            case BUILD: return "Build";
            case END_TURN: return "End Turn";
            default: return phase.toString();
        }
    }

    /**
     * Handles a player timing out.
     */
    private void handlePlayerTimeout(PlayerTimer expiredTimer) {
        // Delegate timeout handling to the model
        VictoryCondition timeoutVictory = model.handlePlayerTimeout(expiredTimer.getPlayerId());

        if (timeoutVictory != null) {
            // Game over due to timeout - show victory screen
            handleVictory(timeoutVictory);
        } else {
            // Game continues with remaining players
            updateStatusLabel("Player " + expiredTimer.getPlayerId() + " timed out! " +
                    controller.getCurrentPlayer().getDisplayName() + "'s turn.");
            updateUI();
        }
    }

    /**
     * Cleanup method to properly dispose of timer resources.
     */
    @Override
    public void dispose() {
        if (timerPanel != null) {
            timerPanel.dispose();
        }
        super.dispose();
    }
}