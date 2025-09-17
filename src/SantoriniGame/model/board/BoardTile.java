package SantoriniGame.model.board;

import SantoriniGame.model.player.Worker;

/**
 * Represents a single tile on the Santorini game board.
 * Each tile has coordinates, a building height, and may have an occupant (a worker).
 */
public class BoardTile {
    private final int x;
    private final int y;
    private final Building building = new Building(0);
    private final Board board; // Reference to the parent board
    private boolean hasDome = false;
    private Worker occupant = null; // can be null

    /**
     * Creates a new BoardTile at the specified coordinates.
     *
     * @param x the x-coordinate of the tile
     * @param y the y-coordinate of the tile
     * @param board the parent board
     */
    public BoardTile(int x, int y, Board board) {
        this.x = x;
        this.y = y;
        this.board = board;
    }

    /**
     * Gets the x-coordinate of the tile.
     *
     * @return the x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the tile.
     *
     * @return the y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the parent board that contains this tile.
     *
     * @return the parent board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the current building level, from 0 (none) to 3. Does not consider dome.
     *
     * @return level of the tile
     */
    public int getLevel() {
        return this.building.getLevel();
    }

    /**
     * Gets the worker currently occupying the tile.
     *
     * @return the Worker occupying the tile, or null if empty
     */
    public Worker getOccupant() {
        return occupant;
    }

    /**
     * Sets the worker occupying the tile.
     *
     * @param worker the Worker to place on the tile
     */
    public void setOccupant(Worker worker) {
        this.occupant = worker;
    }

    /**
     * Removes the current occupying worker from the tile.
     *
     * Throws an IllegalStateException if called on a tile without any occupants already.
     */
    public void removeOccupant() {
        if (this.isEmpty()) {
            throw new IllegalStateException("A tile was told to remove the occupant, but there was no occupant.");
        }
        this.occupant = null;
    }

    /**
     * Checks if the tile is empty (has no worker).
     *
     * @return true if the tile has no occupant, false otherwise
     */
    public boolean isEmpty() {
        return occupant == null;
    }

    /**
     * Gets if the tile has a dome or not.
     *
     * @return If tile is domed.
     */
    public boolean hasDome() {
        return hasDome;
    }

    /**
     * Builds the next building on the tile, including a dome.
     *
     * Throws an IllegalStateException if the tile already has a dome.
     */
    public void buildOnTile() {
        // Prevent building if domed
        if (this.hasDome()) {
            throw new IllegalStateException("Someone is trying to build on a tile, but the tile already has a dome.");
        }

        // If building at max level, build dome
        if (!this.building.build()) {
            this.hasDome = true;
        }
    }

    public void moveToTile(Worker newWorker, BoardTile oldTile) {
        this.occupant = newWorker;
        oldTile.removeOccupant();
    }

    /**
     * Returns if a worker can build on this tile.
     * It is allowed if there is no dome.
     *
     * @return If a building can be built here
     */
    public boolean canWorkerBuild() {
        return !this.hasDome();
    }

    /**
     * Returns if a worker can move to this tile.
     * It is allowed, by default, if no worker is currently occupying it, and there is no dome.
     *
     * @return If a worker can move here
     */
    public boolean canWorkerMove(BoardTile oldTile) {
        // If isn't domed and is empty, check level differences
        if (!this.hasDome() && this.isEmpty()) {
            int oldLevel = oldTile.getLevel();
            int newLevel = this.getLevel();
            // If coming up, check one level diff
            if (oldLevel < newLevel) {
                return oldLevel + 1 == newLevel;
            }
            // If going down/straight, irrelevant
            return true;
        }

        return false;
    }
}