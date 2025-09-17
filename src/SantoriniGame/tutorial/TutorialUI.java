package SantoriniGame.tutorial;

import SantoriniGame.model.player.Worker;
import SantoriniGame.ui.BoardUI;
import SantoriniGame.ui.SantoriniMainMenu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Main tutorial UI that provides the complete tutorial experience.
 * Combines game board with tutorial instructions and controls.
 */
public class TutorialUI extends JFrame implements TutorialController.TutorialEventListener {
    private static final Color TUTORIAL_BACKGROUND = new Color(240, 248, 255);
    private static final Color INSTRUCTION_BACKGROUND = new Color(255, 255, 240);
    private static final Color STATUS_SUCCESS = new Color(220, 255, 220);
    private static final Color STATUS_ERROR = new Color(255, 220, 220);
    private static final Color STATUS_INFO = new Color(220, 235, 255);

    private final TutorialController tutorialController;
    private final BoardUI boardUI;

    // UI Components
    private JLabel stepIndicatorLabel;
    private JLabel stepTitleLabel;
    private JTextArea instructionArea;
    private JLabel statusLabel;
    private JButton nextButton;
    private JButton previousButton;
    private JButton godPowerButton;
    private JButton endTurnButton;
    private JButton exitButton;

    /**
     * Creates a new tutorial UI.
     */
    public TutorialUI() {
        this.tutorialController = new TutorialController();
        this.tutorialController.setTutorialEventListener(this);

        this.boardUI = new BoardUI(5, tutorialController.getModel().getBoard(), this::handleBoardClick);

        setupUI();
        updateDisplay();
    }

    /**
     * Sets up the main UI components and layout.
     */
    private void setupUI() {
        setTitle("Santorini Tutorial");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(TUTORIAL_BACKGROUND);

        // Handle window closing
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                confirmExit();
            }
        });

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Create main content area
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(TUTORIAL_BACKGROUND);

        // Add board (center)
        JPanel boardContainer = new JPanel(new BorderLayout());
        boardContainer.setBorder(BorderFactory.createTitledBorder("Game Board"));
        boardContainer.add(boardUI.getBoardPanel(), BorderLayout.CENTER);
        mainPanel.add(boardContainer, BorderLayout.CENTER);

        // Add instruction panel (right)
        JPanel instructionPanel = createInstructionPanel();
        mainPanel.add(instructionPanel, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);

        // Create bottom panel with status and controls
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // Set window properties
        setSize(1300, 900);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Creates the header panel with title and exit button.
     * @return the header panel
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(100, 149, 237));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Santorini Tutorial", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        exitButton = new JButton("Exit Tutorial");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exitButton.addActionListener(e -> confirmExit());

        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(exitButton, BorderLayout.EAST);

        return panel;
    }

    /**
     * Creates the instruction panel with step details and navigation.
     * @return the instruction panel
     */
    private JPanel createInstructionPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(400, 600));
        panel.setBorder(BorderFactory.createTitledBorder("Tutorial Instructions"));
        panel.setBackground(INSTRUCTION_BACKGROUND);

        // Step indicator
        stepIndicatorLabel = new JLabel("Step 1 of 8", SwingConstants.CENTER);
        stepIndicatorLabel.setFont(new Font("Arial", Font.BOLD, 16));
        stepIndicatorLabel.setBorder(new EmptyBorder(10, 5, 5, 5));
        panel.add(stepIndicatorLabel, BorderLayout.NORTH);

        // Main content area
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBackground(INSTRUCTION_BACKGROUND);

        // Step title
        stepTitleLabel = new JLabel("Welcome to Santorini!");
        stepTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        stepTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        stepTitleLabel.setBorder(new EmptyBorder(5, 10, 10, 10));
        contentPanel.add(stepTitleLabel, BorderLayout.NORTH);

        // Instruction text
        instructionArea = new JTextArea();
        instructionArea.setWrapStyleWord(true);
        instructionArea.setLineWrap(true);
        instructionArea.setEditable(false);
        instructionArea.setFont(new Font("Arial", Font.PLAIN, 13));
        instructionArea.setBackground(Color.WHITE);
        instructionArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(instructionArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Navigation buttons
        JPanel navPanel = createNavigationPanel();
        panel.add(navPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the navigation panel with game action buttons and Previous/Next buttons.
     * @return the navigation panel
     */
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(INSTRUCTION_BACKGROUND);

        // Game action buttons (center)
        JPanel gameButtonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        gameButtonPanel.setBackground(INSTRUCTION_BACKGROUND);

        // God power button
        godPowerButton = new JButton("Use God Power");
        godPowerButton.setFont(new Font("Arial", Font.BOLD, 14));
        godPowerButton.setBackground(new Color(255, 215, 0));
        godPowerButton.addActionListener(e -> handleGodPowerClick());
        godPowerButton.setEnabled(false);

        // End turn button
        endTurnButton = new JButton("End Turn");
        endTurnButton.setFont(new Font("Arial", Font.BOLD, 14));
        endTurnButton.setBackground(new Color(255, 160, 160));
        endTurnButton.addActionListener(e -> handleEndTurnClick());
        endTurnButton.setEnabled(false);

        gameButtonPanel.add(godPowerButton);
        gameButtonPanel.add(endTurnButton);
        panel.add(gameButtonPanel, BorderLayout.CENTER);

        // Previous/Next buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(INSTRUCTION_BACKGROUND);

        previousButton = new JButton("Previous");
        previousButton.setFont(new Font("Arial", Font.PLAIN, 14));
        previousButton.addActionListener(e -> handlePreviousClick());
        previousButton.setEnabled(false);

        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.PLAIN, 14));
        nextButton.addActionListener(e -> handleNextClick());

        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the bottom panel with status information.
     * @return the bottom panel
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        panel.setBackground(TUTORIAL_BACKGROUND);

        statusLabel = new JLabel("Welcome to the Santorini tutorial!");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(STATUS_INFO);
        statusLabel.setBorder(new EmptyBorder(8, 15, 8, 15));

        panel.add(statusLabel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Handles board tile clicks.
     * @param e the action event
     */
    private void handleBoardClick(ActionEvent e) {
        tutorialController.handleTileClick(
                Integer.parseInt(e.getActionCommand().split(",")[0]),
                Integer.parseInt(e.getActionCommand().split(",")[1])
        );
        updateDisplay();
    }

    /**
     * Handles god power button clicks.
     */
    private void handleGodPowerClick() {
        tutorialController.activateGodPower();
        updateDisplay();
    }

    /**
     * Handles end turn button clicks.
     */
    private void handleEndTurnClick() {
        tutorialController.endTurn();
        updateDisplay();
    }

    /**
     * Handles next button clicks.
     */
    private void handleNextClick() {
        TutorialStep currentStep = tutorialController.getCurrentTutorialStep();

        if (currentStep.isInformationStep() || tutorialController.isCurrentStepCompleted() ||
                currentStep == TutorialStep.GOD_POWER) {
            if (tutorialController.nextTutorialStep()) {
                updateDisplay();
            } else {
                completeTutorial();
            }
        }
    }

    /**
     * Handles previous button clicks.
     */
    private void handlePreviousClick() {
        if (tutorialController.previousTutorialStep()) {
            updateDisplay();
        }
    }

    /**
     * Updates the entire display based on current tutorial state.
     */
    private void updateDisplay() {
        TutorialStep currentStep = tutorialController.getCurrentTutorialStep();

        // Update step indicator
        stepIndicatorLabel.setText("Step " + currentStep.getStepNumber() + " of " + TutorialStep.getTotalSteps());

        // Update step content
        stepTitleLabel.setText(currentStep.getTitle());
        instructionArea.setText(currentStep.getDescription());
        instructionArea.setCaretPosition(0);

        // Update navigation buttons
        previousButton.setEnabled(currentStep != TutorialStep.INTRODUCTION);

        if (currentStep == TutorialStep.COMPLETE) {
            nextButton.setText("Finish Tutorial");
            nextButton.setEnabled(true);
        } else if (currentStep.isInformationStep() || tutorialController.isCurrentStepCompleted() ||
                currentStep == TutorialStep.GOD_POWER) {
            nextButton.setText("Next");
            nextButton.setEnabled(true);
        } else {
            nextButton.setText("Next");
            nextButton.setEnabled(false);
        }

        // Update game action buttons
        boolean canActivateGodPower = tutorialController.canActivateGodPower();
        godPowerButton.setEnabled(canActivateGodPower);

        // Enable end turn button for END_TURN step when in END_TURN phase
        boolean canEndTurn = (currentStep == TutorialStep.END_TURN &&
                tutorialController.getCurrentPhase() == SantoriniGame.model.game.TurnPhase.END_TURN);
        endTurnButton.setEnabled(canEndTurn);

        // Update status
        String statusText = tutorialController.getTutorialStatusMessage();
        statusLabel.setText(statusText);
        statusLabel.setBackground(STATUS_INFO);

        // Update board highlights
        updateBoardHighlights();
    }

    /**
     * Updates board highlights based on current tutorial step.
     */
    private void updateBoardHighlights() {
        // Update board display first
        boardUI.updateBoardUI();

        TutorialStep currentStep = tutorialController.getCurrentTutorialStep();

        // Clear existing highlights
        boardUI.clearHighlights();

        // Apply appropriate highlights based on current step
        switch (currentStep) {
            case SELECT_WORKER:
                if (tutorialController.getCurrentPhase() == SantoriniGame.model.game.TurnPhase.SELECT_WORKER) {
                    int currentTeamId = tutorialController.getCurrentPlayer().getTeam().getTeamId();
                    boardUI.highlightPlayerWorkers(currentTeamId);
                }
                break;

            case MOVE_WORKER:
            case GOD_POWER:
                if (tutorialController.getCurrentPhase() == SantoriniGame.model.game.TurnPhase.MOVE) {
                    boardUI.highlightValidMoves(tutorialController.getValidMoves());
                    Worker selectedWorker = tutorialController.getSelectedWorker();
                    if (selectedWorker != null) {
                        boardUI.highlightSelectedWorker(selectedWorker);
                    }
                }
                break;

            case BUILD:
                if (tutorialController.getCurrentPhase() == SantoriniGame.model.game.TurnPhase.BUILD) {
                    boardUI.highlightValidBuilds(tutorialController.getValidBuilds());
                    Worker selectedWorker = tutorialController.getSelectedWorker();
                    if (selectedWorker != null) {
                        boardUI.highlightSelectedWorker(selectedWorker);
                    }
                }
                break;

            case END_TURN:
                // No special highlights for end turn step
                break;
        }
    }

    /**
     * Completes the tutorial and shows congratulations.
     */
    private void completeTutorial() {
        JOptionPane.showMessageDialog(this,
                "🎉 Congratulations! 🎉\n\n" +
                        "You have successfully completed the Santorini tutorial!\n\n" +
                        "You now know:\n" +
                        "• How to select workers (Blue highlights)\n" +
                        "• How to move workers (Green highlights)\n" +
                        "• How to use god powers (Artemis ability)\n" +
                        "• How to build structures (Yellow highlights)\n" +
                        "• How to end your turn properly\n" +
                        "• How to achieve victory (reach level 3)\n\n" +
                        "You're ready to challenge others in the full game!\n" +
                        "Good luck, and may the gods favor your strategy!",
                "Tutorial Complete!",
                JOptionPane.INFORMATION_MESSAGE);

        exitToMainMenu();
    }

    /**
     * Confirms exit and returns to main menu.
     */
    private void confirmExit() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit the tutorial?\n" +
                        "Your progress will be lost.",
                "Exit Tutorial",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            exitToMainMenu();
        }
    }

    /**
     * Exits tutorial and returns to main menu.
     */
    private void exitToMainMenu() {
        dispose();
        SwingUtilities.invokeLater(() -> new SantoriniMainMenu());
    }

    // TutorialEventListener implementation

    /**
     * Called when tutorial step changes.
     * @param newStep the new tutorial step
     */
    @Override
    public void onTutorialStepChanged(TutorialStep newStep) {
        SwingUtilities.invokeLater(this::updateDisplay);
    }

    /**
     * Called when tutorial step is completed.
     * @param completedStep the completed tutorial step
     */
    @Override
    public void onTutorialStepCompleted(TutorialStep completedStep) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Step completed! Great job!");
            statusLabel.setBackground(STATUS_SUCCESS);

            // Auto-advance after a brief delay for most steps, but not GOD_POWER
            if (!completedStep.isInformationStep() && completedStep != TutorialStep.GOD_POWER) {
                Timer timer = new Timer(1500, e -> {
                    if (tutorialController.nextTutorialStep()) {
                        updateDisplay();
                    } else {
                        completeTutorial();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                updateDisplay();
            }
        });
    }

    /**
     * Called when a tutorial action is successful.
     * @param action the successful action
     * @param currentStep the current tutorial step
     */
    @Override
    public void onActionSuccessful(TutorialAction action, TutorialStep currentStep) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Good! " + action.getDisplayName() + " completed successfully.");
            statusLabel.setBackground(STATUS_SUCCESS);

            // Reset status color after a delay
            Timer timer = new Timer(2000, e -> {
                statusLabel.setBackground(STATUS_INFO);
                updateDisplay();
            });
            timer.setRepeats(false);
            timer.start();
        });
    }

    /**
     * Called when an invalid action is attempted.
     * @param attemptedAction the attempted action
     * @param errorMessage the error message
     */
    @Override
    public void onInvalidActionAttempted(TutorialAction attemptedAction, String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(errorMessage);
            statusLabel.setBackground(STATUS_ERROR);

            // Reset status color after a delay
            Timer timer = new Timer(3000, e -> {
                statusLabel.setBackground(STATUS_INFO);
                updateDisplay();
            });
            timer.setRepeats(false);
            timer.start();
        });
    }
}