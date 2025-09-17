package SantoriniGame.ui;

import SantoriniGame.model.timer.PlayerTimer;
import SantoriniGame.model.timer.TimerListener;
import SantoriniGame.model.timer.TimerManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * UI component that displays all player timers.
 * Updates in real-time and provides visual feedback for timer status.
 */
public class TimerUIPanel extends JPanel implements TimerListener {
    private static final Color ACTIVE_TIMER_COLOR = new Color(220, 255, 220); // Light green
    private static final Color INACTIVE_TIMER_COLOR = new Color(240, 240, 240); // Light gray
    private static final Color WARNING_COLOR = new Color(255, 220, 220); // Light red
    private static final Color CRITICAL_COLOR = new Color(255, 180, 180); // Red
    private static final int WARNING_THRESHOLD_SECONDS = 60; // 1 minute
    private static final int CRITICAL_THRESHOLD_SECONDS = 30; // 30 seconds

    private final TimerManager timerManager;
    private final Map<Integer, PlayerTimerDisplay> playerDisplays;
    private Timer uiUpdateTimer;

    /**
     * Creates a new TimerUIPanel.
     * @param timerManager the timer manager to display
     */
    public TimerUIPanel(TimerManager timerManager) {
        this.timerManager = timerManager;
        this.uiUpdateTimer = uiUpdateTimer;
        this.playerDisplays = new HashMap<>();

        setupUI();
        createPlayerDisplays();
        setupUpdateTimer();

        // Register as listener for timer events
        timerManager.addTimerListener(this);
    }

    /**
     * Sets up the basic UI layout.
     */
    private void setupUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Player Timers"),
                new EmptyBorder(10, 10, 10, 10)
        ));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(250, 200));
    }

    /**
     * Creates individual timer displays for each player.
     */
    private void createPlayerDisplays() {
        removeAll(); // Clear existing components

        for (PlayerTimer timer : timerManager.getAllTimers()) {
            PlayerTimerDisplay display = new PlayerTimerDisplay(timer);
            playerDisplays.put(timer.getPlayerId(), display);
            add(display);
            add(Box.createVerticalStrut(5)); // Spacing between displays
        }

        revalidate();
        repaint();
    }

    /**
     * Sets up the timer that updates the UI every second.
     */
    private void setupUpdateTimer() {
        uiUpdateTimer = new Timer(1000, e -> updateAllDisplays());
        uiUpdateTimer.start();
    }

    /**
     * Updates all player timer displays.
     */
    private void updateAllDisplays() {
        if (!timerManager.isActive()) return;

        PlayerTimer currentTimer = timerManager.getCurrentPlayerTimer();
        int currentPlayerId = currentTimer != null ? currentTimer.getPlayerId() : -1;

        for (PlayerTimerDisplay display : playerDisplays.values()) {
            display.updateDisplay(currentPlayerId);
        }

        // Check for timeouts
        PlayerTimer expiredTimer = timerManager.checkForTimeout();
        if (expiredTimer != null) {
            handleTimeout(expiredTimer);
        }
    }

    /**
     * Handles timer timeout events.
     * @param expiredTimer the timer that expired
     */
    private void handleTimeout(PlayerTimer expiredTimer) {
        uiUpdateTimer.stop();

        // Show timeout dialog
        String message = String.format("Player %d has run out of time!", expiredTimer.getPlayerId());
        JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(this),
                message,
                "Time's Up!",
                JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Stops the UI update timer when the panel is no longer needed.
     */
    public void dispose() {
        if (uiUpdateTimer != null) {
            uiUpdateTimer.stop();
        }
        timerManager.removeTimerListener(this);
    }

    // TimerListener implementation

    /**
     * Called when a timer is started.
     * @param timer the timer that was started
     */
    @Override
    public void onTimerStarted(PlayerTimer timer) {
        SwingUtilities.invokeLater(() -> {
            PlayerTimerDisplay display = playerDisplays.get(timer.getPlayerId());
            if (display != null) {
                display.setActive(true);
            }
        });
    }

    /**
     * Called when a timer is paused.
     * @param timer the timer that was paused
     */
    @Override
    public void onTimerPaused(PlayerTimer timer) {
        SwingUtilities.invokeLater(() -> {
            PlayerTimerDisplay display = playerDisplays.get(timer.getPlayerId());
            if (display != null) {
                display.setActive(false);
            }
        });
    }

    /**
     * Called when a timer times out.
     * @param timer the timer that expired
     */
    @Override
    public void onTimeout(PlayerTimer timer) {
        SwingUtilities.invokeLater(() -> {
            PlayerTimerDisplay display = playerDisplays.get(timer.getPlayerId());
            if (display != null) {
                display.setExpired(true);
            }
        });
    }

    /**
     * Called when all timers are paused.
     */
    @Override
    public void onAllTimersPaused() {
        SwingUtilities.invokeLater(() -> {
            for (PlayerTimerDisplay display : playerDisplays.values()) {
                display.setActive(false);
            }
        });
    }

    /**
     * Called when timers are stopped.
     */
    @Override
    public void onTimersStop() {
        SwingUtilities.invokeLater(() -> {
            if (uiUpdateTimer != null) {
                uiUpdateTimer.stop();
            }
        });
    }

    /**
     * Inner class representing the display for a single player's timer.
     */
    private static class PlayerTimerDisplay extends JPanel {
        private final PlayerTimer timer;
        private final JLabel nameLabel;
        private final JLabel timeLabel;
        private final JProgressBar timeBar;
        private boolean isActive = false;
        private boolean isExpired = false;

        /**
         * Creates a new player timer display.
         * @param timer the player timer to display
         */
        public PlayerTimerDisplay(PlayerTimer timer) {
            this.timer = timer;

            setLayout(new BorderLayout(5, 5));
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
            setBackground(INACTIVE_TIMER_COLOR);

            // Player name label
            nameLabel = new JLabel("Player " + timer.getPlayerId());
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Time remaining label
            timeLabel = new JLabel(timer.getFormattedTime());
            timeLabel.setFont(timeLabel.getFont().deriveFont(Font.BOLD, 16f));
            timeLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Progress bar showing time remaining
            timeBar = new JProgressBar(0, 100);
            timeBar.setValue((int) (timer.getTimeRemainingPercentage() * 100));
            timeBar.setStringPainted(false);
            timeBar.setBackground(Color.WHITE);
            timeBar.setForeground(Color.GREEN);

            add(nameLabel, BorderLayout.NORTH);
            add(timeLabel, BorderLayout.CENTER);
            add(timeBar, BorderLayout.SOUTH);

            setPreferredSize(new Dimension(200, 70));
        }

        /**
         * Updates the display for this timer.
         * @param currentPlayerId the ID of the currently active player
         */
        public void updateDisplay(int currentPlayerId) {
            if (isExpired) return;

            // Update time display
            timeLabel.setText(timer.getFormattedTime());

            // Update progress bar
            double percentage = timer.getTimeRemainingPercentage();
            timeBar.setValue((int) (percentage * 100));

            // Update active status
            boolean shouldBeActive = (timer.getPlayerId() == currentPlayerId);
            if (shouldBeActive != isActive) {
                setActive(shouldBeActive);
            }

            // Update color based on time remaining
            updateColorBasedOnTime();
        }

        /**
         * Updates colors based on remaining time.
         */
        private void updateColorBasedOnTime() {
            if (isExpired) return;

            long remainingSeconds = timer.getRemainingTimeMs() / 1000;
            Color backgroundColor;
            Color progressColor;

            if (remainingSeconds <= CRITICAL_THRESHOLD_SECONDS) {
                backgroundColor = CRITICAL_COLOR;
                progressColor = Color.RED;
            } else if (remainingSeconds <= WARNING_THRESHOLD_SECONDS) {
                backgroundColor = WARNING_COLOR;
                progressColor = Color.ORANGE;
            } else if (isActive) {
                backgroundColor = ACTIVE_TIMER_COLOR;
                progressColor = Color.GREEN;
            } else {
                backgroundColor = INACTIVE_TIMER_COLOR;
                progressColor = Color.GRAY;
            }

            setBackground(backgroundColor);
            timeBar.setForeground(progressColor);
        }

        /**
         * Sets the active state of this timer display.
         * @param active true if this timer is active
         */
        public void setActive(boolean active) {
            this.isActive = active;

            if (active) {
                setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                nameLabel.setForeground(Color.BLUE);
            } else {
                setBorder(BorderFactory.createLineBorder(Color.GRAY));
                nameLabel.setForeground(Color.BLACK);
            }

            updateColorBasedOnTime();
            repaint();
        }

        /**
         * Sets the expired state of this timer display.
         * @param expired true if this timer has expired
         */
        public void setExpired(boolean expired) {
            this.isExpired = expired;

            if (expired) {
                setBackground(CRITICAL_COLOR);
                timeLabel.setText("TIME UP!");
                timeLabel.setForeground(Color.RED);
                timeBar.setValue(0);
                timeBar.setForeground(Color.RED);
                setBorder(BorderFactory.createLineBorder(Color.RED, 3));
            }

            repaint();
        }

        /**
         * Resets this timer display to default state.
         */
        public void reset() {
            isActive = false;
            isExpired = false;
            timeLabel.setForeground(Color.BLACK);
            nameLabel.setForeground(Color.BLACK);
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
            updateDisplay(-1); // No active player
        }

        /**
         * Shows a bonus time effect animation.
         * @param bonusMinutes the amount of bonus time added
         */
        public void showBonusTimeEffect(int bonusMinutes) {
            // Create a temporary visual effect to show bonus time was added
            Timer effectTimer = new Timer(100, null);
            final int[] flashCount = {0};

            effectTimer.addActionListener(e -> {
                if (flashCount[0] < 6) { // Flash 3 times (6 half-cycles)
                    if (flashCount[0] % 2 == 0) {
                        setBackground(Color.YELLOW);
                        timeLabel.setText("+" + bonusMinutes + " min!");
                    } else {
                        updateColorBasedOnTime();
                        timeLabel.setText(timer.getFormattedTime());
                    }
                    flashCount[0]++;
                } else {
                    effectTimer.stop();
                    updateColorBasedOnTime();
                    timeLabel.setText(timer.getFormattedTime());
                }
            });

            effectTimer.start();
        }
    }
}