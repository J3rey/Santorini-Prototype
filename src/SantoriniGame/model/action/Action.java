package SantoriniGame.model.action;

import SantoriniGame.model.board.Board;
import SantoriniGame.model.player.Worker;

/**
 * Abstract base class representing actions in the Santorini game.
 * Following the Template Method pattern and SOLID principles.
 */
public abstract class Action {
    private final ActionType type;
    private final Worker worker; // Can be null for actions that don't involve workers

    /**
     * Constructor for the Action base class.
     *
     * @param type The type of action being performed
     * @param worker The worker performing the action (can be null for some actions)
     */
    protected Action(ActionType type, Worker worker) {
        this.type = type;
        this.worker = worker;
    }

    /**
     * Gets the type of the action.
     *
     * @return The action type
     */
    public ActionType getType() {
        return type;
    }

    /**
     * Gets the worker that performed the action.
     *
     * @return The worker (can be null for some actions)
     */
    public Worker getWorker() {
        return worker;
    }

    /**
     * Abstract method to apply this action to the game.
     * This follows the Command pattern and allows actions to be undone.
     *
     * @param board The game board
     * @return True if the action was successfully applied
     */
    public abstract boolean apply(Board board);

    /**
     * Optional method to undo this action.
     * By default, actions cannot be undone.
     *
     * @param board The game board
     * @return True if the action was successfully undone
     */
    public boolean undo(Board board) {
        // By default, actions cannot be undone
        return false;
    }

    /**
     * Returns a string representation of the action for logging.
     *
     * @return A descriptive string of the action
     */
    public abstract String getDescription();
}