package SantoriniGame.model.action;

import SantoriniGame.model.board.Board;
import SantoriniGame.model.board.BoardTile;
import SantoriniGame.model.player.Worker;

/**
 * Represents a worker's move action in the Santorini game.
 * Stores information about the start and destination positions.
 * Follows SRP by focusing only on move-related functionality.
 */
public class MoveAction extends Action {
    private final BoardTile start;
    private final BoardTile destination;

    /**
     * Constructs a new MoveAction.
     *
     * @param worker The worker that performed the move
     * @param start The starting tile
     * @param destination The destination tile
     */
    public MoveAction(Worker worker, BoardTile start, BoardTile destination) {
        super(ActionType.MOVE, worker);

        if (worker == null) {
            throw new IllegalArgumentException("Worker cannot be null for MoveAction");
        }
        if (start == null || destination == null) {
            throw new IllegalArgumentException("Start and destination tiles cannot be null");
        }

        this.start = start;
        this.destination = destination;
    }

    /**
     * Gets the starting tile of the move.
     *
     * @return The starting BoardTile
     */
    public BoardTile getStart() {
        return start;
    }

    /**
     * Gets the destination tile of the move.
     *
     * @return The destination BoardTile
     */
    public BoardTile getDestination() {
        return destination;
    }

    /**
     * Returns if the move was an ascent (going up a level).
     *
     * @return true if the worker moved up at least one level
     */
    public boolean wasAscent() {
        return destination.getLevel() > start.getLevel();
    }

    /**
     * Applies the move action to the board.
     * This actually moves the worker on the board.
     *
     * @param board The game board
     * @return True if the move was successfully applied
     */
    @Override
    public boolean apply(Board board) {
        Worker worker = getWorker();

        // Check if the move is valid first
        if (!board.isValidMove(worker, destination.getX(), destination.getY())) {
            return false;
        }

        // Apply the move
        destination.moveToTile(worker, start);
        worker.moveTo(destination.getX(), destination.getY());

        return true;
    }

    /**
     * Undoes the move action, returning the worker to the original position.
     * This provides support for undo functionality.
     *
     * @param board The game board
     * @return True if the move was successfully undone
     */
    @Override
    public boolean undo(Board board) {
        Worker worker = getWorker();

        // Get current tile which should be the destination
        BoardTile currentTile = board.getTile(worker.getX(), worker.getY());

        // Return worker to start position
        start.moveToTile(worker, currentTile);
        worker.moveTo(start.getX(), start.getY());

        return true;
    }

    /**
     * Gets a descriptive string of this move action.
     *
     * @return A description string
     */
    @Override
    public String getDescription() {
        return String.format("Worker moved from (%d,%d) to (%d,%d)",
                start.getX(), start.getY(), destination.getX(), destination.getY());
    }
}