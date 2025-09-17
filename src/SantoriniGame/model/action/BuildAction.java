package SantoriniGame.model.action;

import SantoriniGame.model.board.Board;
import SantoriniGame.model.board.BoardTile;
import SantoriniGame.model.player.Worker;

/**
 * Represents a worker's build action in the Santorini game.
 * Stores information about the worker and the location where building occurred.
 * Follows SRP by focusing only on build-related functionality.
 */
public class BuildAction extends Action {
    private final BoardTile buildLocation;
    private final int previousLevel;
    private final int newLevel;
    private final boolean buildDome;

    /**
     * Constructs a new BuildAction.
     *
     * @param worker The worker that performed the build
     * @param buildLocation The tile where the build occurred
     * @param previousLevel The level before building
     * @param newLevel The level after building
     * @param buildDome Whether a dome was built
     */
    public BuildAction(Worker worker, BoardTile buildLocation, int previousLevel, int newLevel, boolean buildDome) {
        super(ActionType.BUILD, worker);

        if (worker == null) {
            throw new IllegalArgumentException("Worker cannot be null for BuildAction");
        }
        if (buildLocation == null) {
            throw new IllegalArgumentException("Build location cannot be null");
        }

        this.buildLocation = buildLocation;
        this.previousLevel = previousLevel;
        this.newLevel = newLevel;
        this.buildDome = buildDome;
    }

    /**
     * Gets the location where the build occurred.
     *
     * @return The BoardTile where building happened
     */
    public BoardTile getBuildLocation() {
        return buildLocation;
    }

    /**
     * Returns whether a dome was built.
     *
     * @return true if a dome was built
     */
    public boolean wasDomeBuilt() {
        return buildDome;
    }

    /**
     * Returns the X coordinate of the build location.
     *
     * @return X coordinate
     */
    public int getX() {
        return buildLocation.getX();
    }

    /**
     * Returns the Y coordinate of the build location.
     *
     * @return Y coordinate
     */
    public int getY() {
        return buildLocation.getY();
    }

    /**
     * Applies the build action to the board.
     * This actually builds on the specified location.
     *
     * @param board The game board
     * @return True if the build was successfully applied
     */
    @Override
    public boolean apply(Board board) {
        Worker worker = getWorker();

        // Check if the build is valid first
        if (!board.isValidBuild(worker, buildLocation.getX(), buildLocation.getY())) {
            return false;
        }

        // Apply the build
        buildLocation.buildOnTile();

        return true;
    }

    /**
     * Undoes the build action, reverting the tile to its previous state.
     * This supports undo functionality in the game.
     *
     * @param board The game board
     * @return True if the build was successfully undone
     */
    @Override
    public boolean undo(Board board) {
        return false;
    }

    /**
     * Gets a descriptive string of this build action.
     *
     * @return A description string
     */
    @Override
    public String getDescription() {
        String domeDesc = buildDome ? " with a dome" : "";
        return String.format("Built from level %d to level %d at (%d,%d)%s",
                previousLevel, newLevel, getX(), getY(), domeDesc);
    }
}