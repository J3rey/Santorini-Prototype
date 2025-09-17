package SantoriniGame.controller;

import SantoriniGame.god.God;
import SantoriniGame.model.board.BoardTile;
import SantoriniGame.model.victory.VictoryCondition;
import SantoriniGame.model.game.GameModel;
import SantoriniGame.model.game.TurnPhase;
import SantoriniGame.model.game.TurnState;
import SantoriniGame.model.player.Player;
import SantoriniGame.model.player.Worker;
import SantoriniGame.model.timer.TimerManager;
import SantoriniGame.model.timer.PlayerTimer;

import java.util.List;

/**
 * GameController class - Responsible for game flow and logic coordination.
 * Follows the Controller part of MVC pattern, separating game logic from view and model.
 * Now includes timer management functionality for turn-based time limits.
 */
public class GameController {
    // Game state references
    private final GameModel model;

    // Timer system
    private TimerManager timerManager;
    private boolean timerEnabled;

    // Game phases tracking
    private boolean selectingWorker = true;
    private boolean selectingMoveLocation = false;
    private boolean selectingBuildLocation = false;

    /**
     * Creates a new GameController instance.
     *
     * @param model The GameModel to control
     */
    public GameController(GameModel model) {
        this.model = model;
        this.timerEnabled = false;
        this.timerManager = null;
    }

    /**
     * Initializes the timer system for the game.
     *
     * @param players Array of players in the game
     * @param timeLimitMinutes Time limit per player in minutes (0 to disable)
     */
    public void initializeTimers(Player[] players, int timeLimitMinutes) {
        if (timeLimitMinutes > 0) {
            this.timerManager = new TimerManager(players, timeLimitMinutes);
            this.timerEnabled = true;

            // Start the first player's timer
            timerManager.startCurrentPlayerTimer();
        } else {
            this.timerEnabled = false;
            this.timerManager = null;
        }
    }

    /**
     * Handles a click on a board tile based on the current game state.
     *
     * @param x X-coordinate of the clicked tile
     * @param y Y-coordinate of the clicked tile
     * @return Updated TurnState
     */
    public TurnState handleTileClick(int x, int y) {
        // Check for timeout before processing any game action
        if (timerEnabled) {
            PlayerTimer expiredTimer = checkForTimeout();
            if (expiredTimer != null) {
                // Return current state - timeout will be handled by the UI
                return model.getTurnState();
            }
        }

        BoardTile clickedTile = model.getBoard().getTile(x, y);
        TurnState turnState = model.getTurnState();

        if (clickedTile == null) {
            return turnState;
        }

        if (selectingWorker) {
            return handleWorkerSelection(clickedTile);
        }
        else if (selectingMoveLocation) {
            return handleWorkerMove(clickedTile);
        }
        else if (selectingBuildLocation) {
            return handleBuild(clickedTile);
        }

        return turnState;
    }

    /**
     * Deselects the currently selected worker and returns to worker selection phase.
     * This allows players to change their worker selection before making a move.
     *
     * @return Updated TurnState after deselection
     */
    public TurnState deselectWorker() {
        // Reset the worker in the TurnState
        model.getTurnState().setCurrentWorker(null);

        // Update the phase in TurnState
        model.getTurnState().setCurrPhase(TurnPhase.SELECT_WORKER);

        // Reset controller state to worker selection phase
        updateControllerState(TurnPhase.SELECT_WORKER);

        // Return the updated TurnState
        return model.getTurnState();
    }

    /**
     * Handles worker selection phase.
     *
     * @param clickedTile The tile that was clicked
     * @return Updated TurnState
     */
    private TurnState handleWorkerSelection(BoardTile clickedTile) {
        // Only proceed if the tile contains a worker
        if (clickedTile.getOccupant() == null) {
            return model.getTurnState();
        }

        Worker worker = clickedTile.getOccupant();

        // Check if the worker belongs to the current player
        if (worker.getTeam() == model.getCurrentPlayer().getTeam()) {
            // Select worker and update game phase
            model.selectWorker(worker);

            // Update controller state
            selectingWorker = false;
            selectingMoveLocation = true;

            return model.getTurnState();
        }

        // If we get here, the selection was invalid
        return model.getTurnState();
    }

    /**
     * Handles the worker move phase.
     *
     * @param clickedTile The tile that was clicked
     * @return Updated TurnState
     */
    private TurnState handleWorkerMove(BoardTile clickedTile) {
        // Execute the move if it's a valid location
        TurnState newState = model.moveWorker(clickedTile.getX(), clickedTile.getY());

        // If move was successful, the phase would have changed
        if (newState.getCurrPhase() == TurnPhase.BUILD) {
            // Update controller state
            selectingMoveLocation = false;
            selectingBuildLocation = true;
        }

        return newState;
    }

    /**
     * Handles the build phase.
     *
     * @param clickedTile The tile that was clicked
     * @return Updated TurnState
     */
    private TurnState handleBuild(BoardTile clickedTile) {
        // Execute the build if it's a valid location
        TurnState newState = model.buildOnTile(clickedTile.getX(), clickedTile.getY());

        // If build was successful, the phase would have changed
        if (newState.getCurrPhase() == TurnPhase.END_TURN) {
            // Update controller state
            selectingBuildLocation = false;
        }

        return newState;
    }

    /**
     * Activates the current player's god power.
     *
     * @return Updated TurnState
     */
    public TurnState activateGodPower() {
        TurnState newState = model.activateGodPower();

        // Update controller state based on the new phase
        updateControllerState(newState.getCurrPhase());

        return newState;
    }

    /**
     * Ends the current turn and advances to the next player.
     */
    public void endTurn() {
        // Get next player index before ending turn
        int nextPlayerIndex = (getCurrentPlayerIndex() + 1) % model.getPlayers().length;

        model.endTurn();

        // Reset controller state for the new player's turn
        resetControllerState();

        // Switch timers to next player
        if (timerEnabled && timerManager != null) {
            timerManager.switchToNextPlayer(nextPlayerIndex);
        }

        // Before player input, check if player is trapped
        if (model.playerIsTrapped()) {
            // If so, remove team then end turn for the next player to play
            model.removeTeam(model.getCurrentPlayer().getTeam());
            this.endTurn();
        }
    }

    /**
     * Gets the current player index from the model.
     *
     * @return Current player index
     */
    private int getCurrentPlayerIndex() {
        Player currentPlayer = model.getCurrentPlayer();
        Player[] allPlayers = model.getPlayers();

        for (int i = 0; i < allPlayers.length; i++) {
            if (allPlayers[i].equals(currentPlayer)) {
                return i;
            }
        }
        return 0; // Fallback to first player
    }

    /**
     * Resets the controller state for a new turn.
     */
    private void resetControllerState() {
        selectingWorker = true;
        selectingMoveLocation = false;
        selectingBuildLocation = false;
    }

    /**
     * Updates the controller state based on the current game phase.
     *
     * @param phase Current TurnPhase
     */
    private void updateControllerState(TurnPhase phase) {
        switch (phase) {
            case SELECT_WORKER:
                selectingWorker = true;
                selectingMoveLocation = false;
                selectingBuildLocation = false;
                break;
            case MOVE:
                selectingWorker = false;
                selectingMoveLocation = true;
                selectingBuildLocation = false;
                break;
            case BUILD:
                selectingWorker = false;
                selectingMoveLocation = false;
                selectingBuildLocation = true;
                break;
            case END_TURN:
                selectingWorker = false;
                selectingMoveLocation = false;
                selectingBuildLocation = false;
                break;
        }
    }

    /**
     * Gets the valid move locations for the current worker.
     *
     * @return List of valid moves or empty list if no worker selected
     */
    public List<BoardTile> getValidMoves() {
        Worker worker = model.getTurnState().getCurrWorker();
        if (worker == null) {
            return List.of();
        }

        God playerGod = model.getCurrentPlayer().getSelectedGod();
        return playerGod.getValidMoveLocations(worker, model.getBoard(), model.getTurnState());
    }

    /**
     * Gets the valid build locations for the current worker.
     *
     * @return List of valid builds or empty list if no worker selected
     */
    public List<BoardTile> getValidBuilds() {
        Worker worker = model.getTurnState().getCurrWorker();
        if (worker == null) {
            return List.of();
        }

        God playerGod = model.getCurrentPlayer().getSelectedGod();
        return playerGod.getValidBuildLocations(worker, model.getBoard(), model.getTurnState());
    }

    /**
     * Checks if the current player can activate their god power.
     *
     * @return True if god power can be activated
     */
    public boolean canActivateGodPower() {
        God playerGod = model.getCurrentPlayer().getSelectedGod();
        return playerGod.canActivatePower(model.getTurnState());
    }

    /**
     * Gets the current phase of the game.
     *
     * @return Current TurnPhase
     */
    public TurnPhase getCurrentPhase() {
        return model.getTurnState().getCurrPhase();
    }

    /**
     * Gets the current player.
     *
     * @return Current Player
     */
    public Player getCurrentPlayer() {
        return model.getCurrentPlayer();
    }

    /**
     * Gets the currently selected worker.
     *
     * @return Selected Worker or null if none selected
     */
    public Worker getSelectedWorker() {
        return model.getTurnState().getCurrWorker();
    }

    /**
     * Checks if there is a winner.
     *
     * @return VictoryCondition that was satisfied, or null if no winner yet
     */
    public VictoryCondition checkForWinner() {
        return model.getTurnState().getSatisfiedCondition();
    }

    /**
     * Gets the game model.
     *
     * @return The GameModel
     */
    public GameModel getModel() {
        return model;
    }

    /**
     * Gets the current controller state.
     *
     * @return Current controller state as a string for display
     */
    public String getStateDescription() {
        if (selectingWorker) {
            return "Select one of your workers";
        } else if (selectingMoveLocation) {
            return "Choose a location to move";
        } else if (selectingBuildLocation) {
            return "Choose a location to build";
        } else {
            return "End your turn";
        }
    }

    // ==================== TIMER SYSTEM METHODS ====================

    /**
     * Checks if any player has timed out.
     *
     * @return PlayerTimer that expired, or null if no timeout
     */
    public PlayerTimer checkForTimeout() {
        if (timerEnabled && timerManager != null) {
            return timerManager.checkForTimeout();
        }
        return null;
    }

    /**
     * Gets the timer manager.
     *
     * @return TimerManager instance, or null if timers are disabled
     */
    public TimerManager getTimerManager() {
        return timerManager;
    }

    /**
     * Checks if timers are enabled for this game.
     *
     * @return true if timers are enabled, false otherwise
     */
    public boolean isTimerEnabled() {
        return timerEnabled;
    }

    /**
     * Stops all timers (typically called when game ends).
     */
    public void stopAllTimers() {
        if (timerEnabled && timerManager != null) {
            timerManager.stopAllTimers();
        }
    }
}