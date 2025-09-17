package SantoriniGame.model.timer;

/**
 * Interface for listening to timer events.
 * Follows the Observer pattern to decouple timer management from UI updates.
 */
public interface TimerListener {

    /**
     * Called when a player's timer is started.
     * @param timer the timer that was started
     */
    void onTimerStarted(PlayerTimer timer);

    /**
     * Called when a player's timer is paused.
     * @param timer the timer that was paused
     */
    void onTimerPaused(PlayerTimer timer);

    /**
     * Called when a player's timer expires.
     * @param timer the timer that expired
     */
    void onTimeout(PlayerTimer timer);

    /**
     * Called when all timers are paused.
     */
    void onAllTimersPaused();

    /**
     * Called when the timer system is stopped.
     */
    void onTimersStop();
}