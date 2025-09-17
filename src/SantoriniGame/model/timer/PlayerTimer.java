package SantoriniGame.model.timer;

/**
 * Represents a timer for an individual player in the Santorini game.
 * Tracks remaining time and provides methods to start, pause, and check timer status.
 * Follows SRP by focusing only on individual player timer functionality.
 */
public class PlayerTimer {
    private long remainingTimeMs; // Remaining time in milliseconds
    private long startTime; // When the timer was last started
    private boolean isRunning;
    private final int playerId;
    private final long initialTimeMs;

    /**
     * Creates a new PlayerTimer with the specified time limit.
     *
     * @param playerId         The ID of the player this timer belongs to
     * @param timeLimitMinutes The time limit in minutes
     */
    public PlayerTimer(int playerId, int timeLimitMinutes) {
        this.playerId = playerId;
        this.initialTimeMs = timeLimitMinutes * 60 * 1000L; // Convert to milliseconds
        this.remainingTimeMs = initialTimeMs;
        this.isRunning = false;
        this.startTime = 0;
    }

    /**
     * Starts the timer countdown.
     */
    public void start() {
        if (!isRunning && remainingTimeMs > 0) {
            isRunning = true;
            startTime = System.currentTimeMillis();
        }
    }

    /**
     * Pauses the timer, preserving remaining time.
     */
    public void pause() {
        if (isRunning) {
            updateRemainingTime();
            isRunning = false;
        }
    }

    /**
     * Updates the remaining time based on elapsed time since start.
     */
    private void updateRemainingTime() {
        if (isRunning) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            remainingTimeMs = Math.max(0, remainingTimeMs - elapsedTime);
            startTime = System.currentTimeMillis(); // Reset start time for next calculation
        }
    }

    /**
     * Gets the current remaining time in milliseconds.
     * Updates the time if the timer is currently running.
     *
     * @return Remaining time in milliseconds
     */
    public long getRemainingTimeMs() {
        updateRemainingTime();
        return remainingTimeMs;
    }

    /**
     * Gets the remaining time formatted as MM:SS.
     *
     * @return Formatted time string
     */
    public String getFormattedTime() {
        long timeMs = getRemainingTimeMs();
        long totalSeconds = timeMs / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Checks if the timer has expired (reached zero).
     *
     * @return true if timer has expired, false otherwise
     */
    public boolean hasExpired() {
        return getRemainingTimeMs() <= 0;
    }

    /**
     * Checks if the timer is currently running.
     *
     * @return true if running, false if paused
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Gets the player ID this timer belongs to.
     *
     * @return Player ID
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * Gets the percentage of time remaining (0.0 to 1.0).
     *
     * @return Percentage of time remaining
     */
    public double getTimeRemainingPercentage() {
        if (initialTimeMs == 0) return 0.0;
        return (double) getRemainingTimeMs() / initialTimeMs;
    }

    /**
     * Resets the timer to its initial time limit.
     */
    public void reset() {
        pause();
        remainingTimeMs = initialTimeMs;
    }
}