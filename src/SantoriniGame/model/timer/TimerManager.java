package SantoriniGame.model.timer;

import SantoriniGame.model.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all player timers in the Santorini game.
 * Handles timer coordination, turn transitions, and timeout detection.
 */
public class TimerManager {
    private final List<PlayerTimer> playerTimers;
    private int currentPlayerIndex;
    private final List<TimerListener> listeners;
    private boolean gameActive;

    /**
     * Creates a TimerManager for the specified players.
     * @param players array of players in the game
     * @param timeLimitMinutes time limit per player in minutes
     */
    public TimerManager(Player[] players, int timeLimitMinutes) {
        this.playerTimers = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.gameActive = true;

        // Create timers for each player
        for (Player player : players) {
            playerTimers.add(new PlayerTimer(player.getId(), timeLimitMinutes));
        }
    }

    /**
     * Starts the timer for the current player.
     */
    public void startCurrentPlayerTimer() {
        if (gameActive && currentPlayerIndex < playerTimers.size()) {
            PlayerTimer currentTimer = playerTimers.get(currentPlayerIndex);
            currentTimer.start();
            notifyTimerStarted(currentTimer);
        }
    }

    /**
     * Switches to the next player.
     * @param nextPlayerIndex index of the next player
     */
    public void switchToNextPlayer(int nextPlayerIndex) {
        if (!gameActive) return;

        // Pause current player's timer
        if (currentPlayerIndex < playerTimers.size()) {
            PlayerTimer currentTimer = playerTimers.get(currentPlayerIndex);
            currentTimer.pause();
            notifyTimerPaused(currentTimer);
        }

        // Update current player index
        this.currentPlayerIndex = nextPlayerIndex;

        // Start next player's timer
        startCurrentPlayerTimer();
    }

    /**
     * Checks all timers for expiration.
     * @return PlayerTimer that expired, or null if no timeout
     */
    public PlayerTimer checkForTimeout() {
        if (!gameActive) return null;

        for (PlayerTimer timer : playerTimers) {
            if (timer.hasExpired()) {
                gameActive = false;
                notifyTimeout(timer);
                return timer;
            }
        }
        return null;
    }

    /**
     * Gets the timer for the current player.
     * @return current player's timer
     */
    public PlayerTimer getCurrentPlayerTimer() {
        if (currentPlayerIndex < playerTimers.size()) {
            return playerTimers.get(currentPlayerIndex);
        }
        return null;
    }

    /**
     * Gets all player timers.
     * @return list of all PlayerTimers
     */
    public List<PlayerTimer> getAllTimers() {
        return new ArrayList<>(playerTimers);
    }

    /**
     * Pauses all timers.
     */
    public void pauseAllTimers() {
        for (PlayerTimer timer : playerTimers) {
            timer.pause();
        }
        notifyAllTimersPaused();
    }

    /**
     * Stops all timers and deactivates the timer system.
     */
    public void stopAllTimers() {
        gameActive = false;
        pauseAllTimers();
        notifyTimersStop();
    }

    /**
     * Checks if the timer system is active.
     * @return true if timers are active, false otherwise
     */
    public boolean isActive() {
        return gameActive;
    }

    /**
     * Adds a timer listener.
     * @param listener the listener to add
     */
    public void addTimerListener(TimerListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a timer listener.
     * @param listener the listener to remove
     */
    public void removeTimerListener(TimerListener listener) {
        listeners.remove(listener);
    }

    // Notification methods for observers
    private void notifyTimerStarted(PlayerTimer timer) {
        for (TimerListener listener : listeners) {
            listener.onTimerStarted(timer);
        }
    }

    private void notifyTimerPaused(PlayerTimer timer) {
        for (TimerListener listener : listeners) {
            listener.onTimerPaused(timer);
        }
    }

    private void notifyTimeout(PlayerTimer timer) {
        for (TimerListener listener : listeners) {
            listener.onTimeout(timer);
        }
    }

    private void notifyAllTimersPaused() {
        for (TimerListener listener : listeners) {
            listener.onAllTimersPaused();
        }
    }

    private void notifyTimersStop() {
        for (TimerListener listener : listeners) {
            listener.onTimersStop();
        }
    }
}