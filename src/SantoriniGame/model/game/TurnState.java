package SantoriniGame.model.game;

import SantoriniGame.model.action.Action;
import SantoriniGame.model.action.ActionType;
import SantoriniGame.model.action.EndTurnAction;
import SantoriniGame.god.God;
import SantoriniGame.model.board.Board;
import SantoriniGame.model.player.Player;
import SantoriniGame.model.player.Team;
import SantoriniGame.model.player.Worker;
import SantoriniGame.model.victory.VictoryCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Improved TurnState class following SOLID principles.
 * Encapsulates the current state of the game, including player, turn phase, etc.
 * Supports action history for undo functionality.
 */
public class TurnState {
    private Player currPlayer;
    private TurnPhase currPhase;
    private final Board board;
    private final List<God> allGods;
    private final List<VictoryCondition> allWinConditions;
    private Worker currWorker; // Can be null
    private Action lastAction; // Can be null
    private VictoryCondition satisfiedCondition; // Can be null

    // New field to support action history
    private final Stack<Action> actionHistory;

    /**
     * Constructor for TurnState.
     *
     * @param currPlayer Current player
     * @param currPhase Current turn phase
     * @param board Game board
     * @param currWorker Current worker (can be null)
     * @param lastAction Last action (can be null)
     * @param allGods List of all gods in the game
     * @param allWinConditions List of all victory conditions
     */
    public TurnState(Player currPlayer, TurnPhase currPhase, Board board, Worker currWorker, Action lastAction,
                     List<God> allGods, List<VictoryCondition> allWinConditions) {
        this.currPlayer = currPlayer;
        this.currPhase = currPhase;
        this.board = board;
        this.currWorker = currWorker;
        this.lastAction = lastAction;
        this.allGods = new ArrayList<>(allGods);
        this.allWinConditions = new ArrayList<>(allWinConditions);
        this.satisfiedCondition = null;
        this.actionHistory = new Stack<>();

        // Add the initial action to history if provided
        if (lastAction != null) {
            actionHistory.push(lastAction);
        }
    }

    /**
     * Applies an action to the current turn state.
     *
     * @param action The action to apply
     * @return True if the action was successfully applied
     */
    public boolean applyAction(Action action) {
        if (action == null) {
            return false;
        }

        // Apply the action to the board
        boolean success = action.apply(board);

        if (success) {
            // Record the action
            lastAction = action;
            actionHistory.push(action);

            // Update the phase based on action type
            updatePhaseAfterAction(action);

            // Check for god power activations and victory conditions
            godPowerCheck();
            victoryCheck();
        }

        return success;
    }

    /**
     * Updates the turn phase based on the action type.
     *
     * @param action The action that was applied
     */
    private void updatePhaseAfterAction(Action action) {
        switch (action.getType()) {
            case MOVE:
                this.currPhase = TurnPhase.BUILD;
                break;
            case BUILD:
                this.currPhase = TurnPhase.END_TURN;
                break;
            case END_TURN:
                this.currPhase = TurnPhase.SELECT_WORKER;
                // If this was an end turn action, update the current player
                if (action instanceof EndTurnAction) {
                    this.currPlayer = ((EndTurnAction) action).getNextPlayer();
                    this.currWorker = null;
                }
                break;
            case GOD_POWER:
                // God power actions might change the phase in different ways
                // The specific god power implementation should handle this
                break;
        }
    }

    /**
     * Ends the current turn and passes to the next.
     *
     * @param nextPlayer Who's playing after the current turn ends
     */
    public void gotoNextTurn(Player nextPlayer) {
        // Create a proper EndTurnAction
        EndTurnAction endTurnAction = new EndTurnAction(currPlayer, nextPlayer);

        // Apply the end turn action
        applyAction(endTurnAction);

        // Reset for next turn
        this.currPlayer = nextPlayer;
        this.currPhase = TurnPhase.SELECT_WORKER;
        this.currWorker = null;
        this.satisfiedCondition = null;

        // Let gods check their statuses for the new turn
        godPowerCheck();
        victoryCheck();
    }

    /**
     * Acts as the notification to god subscribers to update their statuses.
     */
    public void godPowerCheck() {
        for (God god : allGods) {
            // For each god power, check if they're activated
            god.checkActivation(this);
        }
    }

    /**
     * Acts as the notification to victory conditions to check if they're satisfied.
     * Updates satisfiedCondition if needed.
     */
    public void victoryCheck() {
        for (VictoryCondition victCondn : allWinConditions) {
            // For each victory condition, check and return if they're satisfied
            if (victCondn.isSatisfied(this)) {
                this.satisfiedCondition = victCondn;
                return;
            }
        }
    }

    /**
     * Removes a team's gods and victory conditions.
     */
    public void removeTeam(Team badTeam) {
        // For each player, get their god and remove them
        for (Player player : badTeam.getTeamPlayers()) {
            God removedGod = player.getSelectedGod();

            // If already not here, something weird is happening, oh no
            if (!allGods.remove(removedGod)) {
                throw new IllegalStateException("Tried to remove a team, and thus their gods, but a player's god" +
                        "is already not included.");
            }
        }

        // Remove victory conditions
        this.allWinConditions.removeIf(v -> v.getTeam() == badTeam);
    }

    /**
     * Sets the current worker.
     *
     * @param worker The worker to set as current
     */
    public void setCurrentWorker(Worker worker) {
        this.currWorker = worker;
    }

    /**
     * Sets the current turn phase.
     *
     * @param phase The phase to set
     */
    public void setCurrPhase(TurnPhase phase) {
        this.currPhase = phase;
    }

    // Getters

    /**
     * Gets the current player.
     *
     * @return Current player
     */
    public Player getCurrPlayer() {
        return currPlayer;
    }

    /**
     * Gets the current turn phase.
     *
     * @return Current phase
     */
    public TurnPhase getCurrPhase() {
        return currPhase;
    }

    /**
     * Gets the game board.
     *
     * @return Current board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the current worker.
     *
     * @return Current worker (can be null)
     */
    public Worker getCurrWorker() {
        return currWorker;
    }

    /**
     * Gets the last action performed.
     *
     * @return Last action (can be null)
     */
    public Action getLastAction() {
        return lastAction;
    }

    /**
     * Gets the satisfied victory condition.
     *
     * @return Satisfied condition (can be null)
     */
    public VictoryCondition getSatisfiedCondition() {
        return satisfiedCondition;
    }

    /**
     * Gets all gods in the game.
     *
     * @return Defensive copy of gods list
     */
    public List<God> getAllGods() {
        return new ArrayList<>(allGods);
    }

    /**
     * Gets all victory conditions.
     *
     * @return Defensive copy of victory conditions list
     */
    public List<VictoryCondition> getAllWinConditions() {
        return new ArrayList<>(allWinConditions);
    }
}