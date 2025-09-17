package SantoriniGame.model.board;

/**
 * Building class. Represents a building on a given BoardTile.
 */
public class Building {
    /**
     * Maximum level the building can be built to.
     */
    public static final int MAX_LEVEL = 3;

    private int level = 0;

    /**
     * Creates a new building at level 0.
     */
    public Building() { }

    /**
     * Creates a new building with specified level.
     * @param l the initial level
     */
    public Building(int l) {
        this.level = l;
    }

    /**
     * Builds another level on the building if possible.
     * @return true if a level has been built, false otherwise
     */
    public boolean build() {
        // Build level if possible
        if (this.getLevel() < MAX_LEVEL) {
            this.level += 1;
            return true;
        }
        return false;
    }

    /**
     * Gets the current building level.
     * @return the building level
     */
    public int getLevel() {
        return level;
    }
}