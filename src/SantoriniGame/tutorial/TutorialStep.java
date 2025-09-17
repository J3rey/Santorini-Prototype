package SantoriniGame.tutorial;

/**
 * Enum representing the different steps in the Santorini tutorial.
 * Each step has a specific purpose and allowed actions.
 */
public enum TutorialStep {
    INTRODUCTION(
            "Welcome to Santorini!",
            "Welcome to the Santorini tutorial!\n\n" +
                    "In this strategic building game, you control workers who move and build on a 5x5 board. " +
                    "Your goal is to be the first to move one of your workers UP to level 3 of a building.\n\n" +
                    "VISUAL GUIDE:\n" +
                    "• BLUE highlights = Workers you can select\n" +
                    "• GREEN highlights = Valid move locations\n" +
                    "• YELLOW highlights = Valid build locations\n" +
                    "• RED border = Currently selected worker\n\n" +
                    "Let's learn step by step! Click 'Next' to begin.",
            false, false, false, false, false
    ),

    SELECT_WORKER(
            "Step 1: Select a Worker",
            "Every turn begins by selecting one of your workers.\n\n" +
                    "• You have 2 workers on the board (marked 'M' for male, 'F' for female)\n" +
                    "• Your workers are highlighted in BLUE\n" +
                    "• Click on any blue-highlighted worker to select it\n" +
                    "• The selected worker will show a red border\n\n" +
                    "Try it now: Click on one of your blue-highlighted workers!",
            true, false, false, false, false
    ),

    MOVE_WORKER(
            "Step 2: Move Your Worker",
            "Great! Now move your selected worker to an adjacent space.\n\n" +
                    "MOVEMENT RULES:\n" +
                    "• GREEN spaces show where you can move\n" +
                    "• Move to any of the 8 adjacent spaces (including diagonally)\n" +
                    "• You can move UP a maximum of 1 level\n" +
                    "• You can move DOWN any number of levels\n" +
                    "• Cannot move to occupied spaces or spaces with domes\n\n" +
                    "Click on a GREEN highlighted space to move there!",
            false, true, false, false, false
    ),

    GOD_POWER(
            "Step 3: Use God Power (Artemis)",
            "Perfect! Your worker has Artemis's divine power!\n\n" +
                    "ARTEMIS ABILITY:\n" +
                    "• Can move twice in one turn\n" +
                    "• Cannot return to the starting space\n" +
                    "• Very useful for strategic positioning\n\n" +
                    "After moving, Artemis's power becomes available. You can now move again " +
                    "if you want to! Try clicking the 'Use God Power' button to move your worker " +
                    "a second time, or proceed to building.\n\n" +
                    "This power is optional - you can choose when to use it strategically!",
            false, true, false, true, false
    ),

    BUILD(
            "Step 4: Build a Structure",
            "Now it's time to build on an adjacent space to your worker.\n\n" +
                    "BUILDING RULES:\n" +
                    "• YELLOW spaces show where you can build\n" +
                    "• Build on any space adjacent to your worker\n" +
                    "• Buildings progress: Level 0 → 1 → 2 → 3 → Dome\n" +
                    "• Numbers show the current building level\n" +
                    "• Cannot build on occupied spaces or existing domes\n\n" +
                    "Click on a YELLOW highlighted space to build!",
            false, false, true, false, false
    ),

    END_TURN(
            "Step 5: End Your Turn",
            "Excellent! You've completed a full turn with god power.\n\n" +
                    "TURN COMPLETION:\n" +
                    "• Every turn must end after all actions are complete\n" +
                    "• You've learned how to use Artemis's movement power\n" +
                    "• Once you're satisfied with your turn, end it\n" +
                    "• This passes control to the next player\n\n" +
                    "Click 'End Turn' to complete your turn and continue the tutorial.",
            false, false, false, false, true
    ),

    VICTORY(
            "How to Win the Game",
            "🏆 VICTORY CONDITION:\n" +
                    "Move one of your workers UP to level 3 of a building\n\n" +
                    "IMPORTANT NOTES:\n" +
                    "• You must MOVE UP to level 3 (not just be placed there)\n" +
                    "• Moving from level 2 to level 3 wins the game instantly\n" +
                    "• Use building and god powers strategically\n" +
                    "• Block opponents from reaching level 3\n\n" +
                    "🎯 STRATEGY TIPS:\n" +
                    "• Build up structures gradually\n" +
                    "• Use god powers for tactical advantage\n" +
                    "• Plan several moves ahead\n" +
                    "• Control key areas of the board\n\n" +
                    "You're now ready to play Santorini! Click 'Finish' to complete the tutorial.",
            false, false, false, false, false
    ),

    COMPLETE(
            "Tutorial Complete!",
            "Congratulations! You've learned all the basics of Santorini.\n\n" +
                    "YOU NOW KNOW:\n" +
                    "• How to select workers (Blue highlights)\n" +
                    "• How to move workers (Green highlights)\n" +
                    "• How to use god powers (Artemis ability)\n" +
                    "• How to build structures (Yellow highlights)\n" +
                    "• How to end your turn properly\n" +
                    "• How to achieve victory (reach level 3)\n\n" +
                    "You're ready to challenge opponents in the full game!\n" +
                    "Good luck, and may the gods favor your strategy!",
            false, false, false, false, false
    );

    private final String title;
    private final String description;
    private final boolean allowSelectWorker;
    private final boolean allowMove;
    private final boolean allowBuild;
    private final boolean allowGodPower;
    private final boolean allowEndTurn;

    TutorialStep(String title, String description, boolean allowSelectWorker,
                 boolean allowMove, boolean allowBuild, boolean allowGodPower, boolean allowEndTurn) {
        this.title = title;
        this.description = description;
        this.allowSelectWorker = allowSelectWorker;
        this.allowMove = allowMove;
        this.allowBuild = allowBuild;
        this.allowGodPower = allowGodPower;
        this.allowEndTurn = allowEndTurn;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isAllowSelectWorker() { return allowSelectWorker; }
    public boolean isAllowMove() { return allowMove; }
    public boolean isAllowBuild() { return allowBuild; }
    public boolean isAllowGodPower() { return allowGodPower; }
    public boolean isAllowEndTurn() { return allowEndTurn; }

    /**
     * Gets the next step in the tutorial sequence.
     * @return The next TutorialStep, or null if this is the last step
     */
    public TutorialStep getNext() {
        TutorialStep[] steps = values();
        int currentIndex = this.ordinal();
        if (currentIndex + 1 < steps.length) {
            return steps[currentIndex + 1];
        }
        return null;
    }

    /**
     * Gets the previous step in the tutorial sequence.
     * @return The previous TutorialStep, or null if this is the first step
     */
    public TutorialStep getPrevious() {
        TutorialStep[] steps = values();
        int currentIndex = this.ordinal();
        if (currentIndex > 0) {
            return steps[currentIndex - 1];
        }
        return null;
    }

    /**
     * Checks if this is an information-only step (no game actions required).
     * @return true if this step is read-only
     */
    public boolean isInformationStep() {
        return this == INTRODUCTION || this == VICTORY || this == COMPLETE;
    }

    /**
     * Gets the step number for display purposes.
     * @return The step number (1-based)
     */
    public int getStepNumber() {
        return this.ordinal() + 1;
    }

    /**
     * Gets the total number of tutorial steps.
     * @return Total number of steps
     */
    public static int getTotalSteps() {
        return values().length;
    }
}