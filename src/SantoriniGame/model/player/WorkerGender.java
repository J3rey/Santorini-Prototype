package SantoriniGame.model.player;

/**
 * Enum for worker gender representation.
 */
public enum WorkerGender {
    MALE("M"),
    FEMALE("F"),
    ;

    private final String displayString;

    /**
     * Creates a WorkerGender with display string.
     * @param displayString the string to display for this gender
     */
    WorkerGender(String displayString) {
        this.displayString = displayString;
    }

    /**
     * Gets the display string for this gender.
     * @return the display string
     */
    public String getDisplayString() {
        return displayString;
    }
}