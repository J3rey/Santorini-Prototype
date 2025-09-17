package SantoriniGame.tutorial;

/**
 * Enum representing the different actions that can be performed during the tutorial.
 * Used for validation and step progression.
 */
public enum TutorialAction {
    /**
     * Action of selecting a worker on the board.
     */
    SELECT_WORKER("Select Worker"),

    /**
     * Action of moving a selected worker to a new position.
     */
    MOVE_WORKER("Move Worker"),

    /**
     * Action of building a structure on the board.
     */
    BUILD("Build"),

    /**
     * Action of using a god power ability.
     */
    USE_GOD_POWER("Use God Power"),

    /**
     * Action of ending the current turn.
     */
    END_TURN("End Turn"),

    /**
     * Action of proceeding to the next tutorial step.
     */
    NEXT_STEP("Next Step"),

    /**
     * Action of going back to the previous tutorial step.
     */
    PREVIOUS_STEP("Previous Step"),

    /**
     * Unknown or invalid action.
     */
    UNKNOWN("Unknown");

    private final String displayName;

    TutorialAction(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the human-readable display name for this action.
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Determines the tutorial action based on the current game phase.
     * @param phase The current turn phase
     * @return The corresponding tutorial action
     */
    public static TutorialAction fromTurnPhase(SantoriniGame.model.game.TurnPhase phase) {
        switch (phase) {
            case SELECT_WORKER:
                return SELECT_WORKER;
            case MOVE:
                return MOVE_WORKER;
            case BUILD:
                return BUILD;
            case END_TURN:
                return END_TURN;
            default:
                return UNKNOWN;
        }
    }
}