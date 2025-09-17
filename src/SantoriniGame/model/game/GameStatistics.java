package SantoriniGame.model.game;

/**
 * Class to track game statistics.
 * Follows SRP by separating statistics tracking from game logic.
 */
class GameStatistics {
    private int moveCount = 0;
    private int buildCount = 0;
    private int domeCount = 0;
    private int godPowerActivationCount = 0;

    /**
     * Increments the move count.
     */
    public void incrementMoveCount() {
        moveCount++;
    }

    /**
     * Increments the build count.
     */
    public void incrementBuildCount() {
        buildCount++;
    }

    /**
     * Increments the dome count.
     */
    public void incrementDomeCount() {
        domeCount++;
    }

    /**
     * Increments the god power activation count.
     */
    public void incrementGodPowerActivationCount() {
        godPowerActivationCount++;
    }

    /**
     * Gets the move count.
     * @return number of moves performed
     */
    public int getMoveCount() {
        return moveCount;
    }

    /**
     * Gets the dome count.
     * @return number of domes built
     */
    public int getDomeCount() {
        return domeCount;
    }

    /**
     * Gets the god power activation count.
     * @return number of times god powers were activated
     */
    public int getGodPowerActivationCount() {
        return godPowerActivationCount;
    }
}