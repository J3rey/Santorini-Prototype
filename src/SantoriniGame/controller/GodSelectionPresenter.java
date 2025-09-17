package SantoriniGame.controller;

import SantoriniGame.god.God;
import SantoriniGame.model.game.SantoriniGame;
import SantoriniGame.ui.GodSelection;
import SantoriniGame.ui.GodSelectionModel;
import SantoriniGame.ui.GodSelectionView;

/**
 * Presenter for the God Selection screen.
 * Acts as the intermediary between Model and View.
 * Simplified to use SantoriniGame's default timer settings.
 */
public class GodSelectionPresenter {

    // Model and View references
    private final GodSelectionModel model;
    private final GodSelectionView view;

    // Game configuration
    private God currentlyPreviewedGod;
    private int boardSize;

    /**
     * Constructor for the presenter - uses SantoriniGame's default timer.
     *
     * @param model The data model for god selection
     * @param view The view interface for god selection
     * @param boardSize The size of the game board
     */
    public GodSelectionPresenter(GodSelectionModel model, GodSelectionView view, int boardSize) {
        this.model = model;
        this.view = view;
        this.boardSize = boardSize;

        // Connect view to this presenter
        view.setPresenter(this);

        // Initialize the view
        initializeView();
    }

    /**
     * Initializes the view with initial data.
     */
    private void initializeView() {
        currentlyPreviewedGod = null;
        updateView();
    }

    /**
     * Handles when a god button is clicked.
     *
     * @param god The god that was clicked
     */
    public void onGodButtonClicked(God god) {
        if (model.isSelectionComplete()) {
            return; // Don't allow changes if selection is complete
        }

        // Update the preview
        currentlyPreviewedGod = god;
        view.displayGodDetails(god);

        int currentPlayer = model.getCurrentPlayerIndex();
        God currentSelection = model.getSelectedGod(currentPlayer);

        // Determine if this is a select or deselect action
        if (god.equals(currentSelection)) {
            // Deselect the god
            model.deselectGod(currentPlayer);
            view.setConfirmEnabled(false);
        } else {
            // Select the god
            boolean success = model.selectGod(currentPlayer, god);
            if (success) {
                view.setConfirmEnabled(true);
            } else {
                // Selection failed (god already taken by another player)
                view.setConfirmEnabled(false);
            }
        }

        // Update the full view state
        updateView();
    }

    /**
     * Handles when the confirm button is clicked.
     */
    public void onConfirmClicked() {
        int currentPlayer = model.getCurrentPlayerIndex();

        // Check if the current player has selected a god
        if (model.getSelectedGod(currentPlayer) != null) {
            // Advance to the next player
            model.advancePlayerTurn();

            // Clear the preview
            currentlyPreviewedGod = null;

            // Update the view
            updateView();

            // Check if selection is complete
            if (model.isSelectionComplete()) {
                // Start the game with the selected gods and default timer
                startGame();
            }
        }
    }

    /**
     * Starts the main game with the selected gods and default timer.
     */
    private void startGame() {
        // Get all the selected gods as an array
        God[] selectedGods = model.getSelectedGodsArray();

        // Create message with default timer info
        String message = String.format(
                "God selection complete! Starting the game on a %dx%d board with %d minute timers...",
                boardSize, boardSize, SantoriniGame.getDefaultTimerLimit()
        );

        // Show completion message
        view.showCompletionMessage("Selection Complete", message);

        // Close the god selection view
        if (view instanceof GodSelection) {
            ((GodSelection) view).dispose();
        }

        // Start the main game with default timer - SantoriniGame handles the timer internally
        SantoriniGame game = new SantoriniGame(selectedGods, boardSize);
        game.setVisible(true);
    }

    /**
     * Checks if the selection process is complete.
     *
     * @return Whether the selection is complete
     */
    public boolean isSelectionComplete() {
        return model.isSelectionComplete();
    }

    /**
     * Gets the player who has selected a specific god.
     *
     * @param god The god to check
     * @return The player index or -1 if not selected
     */
    public int getPlayerWhoSelected(God god) {
        return model.getPlayerWhoSelected(god);
    }

    /**
     * Updates the entire view based on the current model state.
     */
    private void updateView() {
        // Update player turn indicators
        view.displayPlayerTurn(model.getCurrentPlayerIndex(), model.getPlayers());

        // Update god list with current selection state
        view.displayGodList(model.getAllGods(), model.getPlayers(), model.getCurrentPlayerIndex());

        // Update confirm button state
        updateConfirmButtonState();

        // Update god details if a god is being previewed
        if (currentlyPreviewedGod != null) {
            view.displayGodDetails(currentlyPreviewedGod);
        }
    }

    /**
     * Updates the confirm button state based on current selection.
     */
    private void updateConfirmButtonState() {
        if (model.isSelectionComplete()) {
            // If selection is complete, disable the confirm button
            view.setConfirmEnabled(false);
        } else {
            // Enable confirm button only if current player has selected a god
            boolean hasSelectedGod = model.getCurrentPlayer().hasSelectedGod();
            view.setConfirmEnabled(hasSelectedGod);
        }
    }
}