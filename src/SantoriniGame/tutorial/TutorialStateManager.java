package SantoriniGame.tutorial;

import SantoriniGame.model.game.TurnPhase;

/**
 * Manages the state and progression of the tutorial.
 * Handles step validation, progression, and action restrictions.
 */
public class TutorialStateManager {
    private TutorialStep currentStep;
    private boolean stepCompleted;
    private TutorialListener listener;

    /**
     * Creates a new tutorial state manager.
     */
    public TutorialStateManager() {
        this.currentStep = TutorialStep.INTRODUCTION;
        this.stepCompleted = false;
    }

    /**
     * Sets the tutorial listener for receiving events.
     */
    public void setTutorialListener(TutorialListener listener) {
        this.listener = listener;
    }

    /**
     * Gets the current tutorial step.
     */
    public TutorialStep getCurrentStep() {
        return currentStep;
    }

    /**
     * Checks if the current step has been completed.
     */
    public boolean isStepCompleted() {
        return stepCompleted;
    }

    /**
     * Advances to the next tutorial step if possible.
     */
    public boolean nextStep() {
        TutorialStep nextStep = currentStep.getNext();
        if (nextStep != null) {
            currentStep = nextStep;
            stepCompleted = false;
            if (listener != null) {
                listener.onStepChanged(currentStep);
            }
            return true;
        }
        return false;
    }

    /**
     * Goes back to the previous tutorial step if possible.
     */
    public boolean previousStep() {
        TutorialStep prevStep = currentStep.getPrevious();
        if (prevStep != null) {
            currentStep = prevStep;
            stepCompleted = false;
            if (listener != null) {
                listener.onStepChanged(currentStep);
            }
            return true;
        }
        return false;
    }

    /**
     * Validates if a specific action is allowed in the current tutorial step.
     */
    public boolean isActionAllowed(TutorialAction action, TurnPhase currentPhase) {
        switch (action) {
            case SELECT_WORKER:
                return currentStep.isAllowSelectWorker() && currentPhase == TurnPhase.SELECT_WORKER;
            case MOVE_WORKER:
                return currentStep.isAllowMove() && currentPhase == TurnPhase.MOVE;
            case BUILD:
                return currentStep.isAllowBuild() && currentPhase == TurnPhase.BUILD;
            case USE_GOD_POWER:
                return currentStep.isAllowGodPower();
            case END_TURN:
                return currentStep.isAllowEndTurn() && currentPhase == TurnPhase.END_TURN;
            case NEXT_STEP:
                // Allow Next for information steps, completed steps, or for GOD_POWER step (since it's optional)
                return currentStep.isInformationStep() || stepCompleted || currentStep == TutorialStep.GOD_POWER;
            default:
                return false;
        }
    }

    /**
     * Attempts to complete the current step based on a performed action and phase change.
     */
    public boolean tryCompleteStep(TutorialAction action, TurnPhase phaseBefore, TurnPhase phaseAfter) {
        if (stepCompleted) return false;

        boolean completed = false;

        switch (currentStep) {
            case SELECT_WORKER:
                // Completed when worker is selected (phase changes from SELECT_WORKER to MOVE)
                completed = action == TutorialAction.SELECT_WORKER &&
                        phaseBefore == TurnPhase.SELECT_WORKER &&
                        phaseAfter == TurnPhase.MOVE;
                break;

            case MOVE_WORKER:
                // Completed when worker moves (phase changes from MOVE to BUILD)
                completed = action == TutorialAction.MOVE_WORKER &&
                        phaseBefore == TurnPhase.MOVE &&
                        phaseAfter == TurnPhase.BUILD;
                break;

            case GOD_POWER:
                // GOD_POWER step is completed when they move (which puts them in BUILD phase)
                // This allows them to proceed to the BUILD step
                completed = action == TutorialAction.MOVE_WORKER &&
                        phaseBefore == TurnPhase.MOVE &&
                        phaseAfter == TurnPhase.BUILD;
                break;

            case BUILD:
                // Completed when building is done (phase changes from BUILD to END_TURN)
                completed = action == TutorialAction.BUILD &&
                        phaseBefore == TurnPhase.BUILD &&
                        phaseAfter == TurnPhase.END_TURN;
                break;

            case END_TURN:
                // END_TURN step is completed when they successfully end their turn
                completed = action == TutorialAction.END_TURN;
                break;

            default:
                // Information steps are completed manually via Next button
                break;
        }

        if (completed) {
            stepCompleted = true;
            if (listener != null) {
                listener.onStepCompleted(currentStep);
            }
        }

        return completed;
    }

    /**
     * Manually completes the current step (for information steps).
     */
    public void completeCurrentStep() {
        if (!stepCompleted) {
            stepCompleted = true;
            if (listener != null) {
                listener.onStepCompleted(currentStep);
            }
        }
    }

    /**
     * Checks if the tutorial is completely finished.
     */
    public boolean isTutorialComplete() {
        return currentStep == TutorialStep.COMPLETE && stepCompleted;
    }

    /**
     * Gets an appropriate error message for an invalid action.
     */
    public String getErrorMessage(TutorialAction attemptedAction) {
        switch (currentStep) {
            case INTRODUCTION:
            case VICTORY:
            case COMPLETE:
                return "This is an information step. Please read the instructions and click 'Next'.";

            case SELECT_WORKER:
                return "Please select one of your workers by clicking on a BLUE highlighted piece.";

            case MOVE_WORKER:
                return "Please move your selected worker by clicking on a GREEN highlighted space.";

            case GOD_POWER:
                if (attemptedAction == TutorialAction.USE_GOD_POWER) {
                    return "Move your worker first to activate Artemis's power!";
                }
                return "You can move your worker or use the god power if available.";

            case BUILD:
                return "Please build by clicking on a YELLOW highlighted space adjacent to your worker.";

            case END_TURN:
                if (attemptedAction == TutorialAction.END_TURN) {
                    return "Click the 'End Turn' button to end your turn.";
                }
                return "This step teaches you how to end your turn. Click 'End Turn' when ready.";

            default:
                return "Please follow the tutorial steps in order.";
        }
    }

    /**
     * Gets a status message for the current step.
     */
    public String getStatusMessage(TurnPhase currentPhase) {
        if (stepCompleted && currentStep != TutorialStep.GOD_POWER) {
            return "Step completed! Click 'Next' to continue.";
        }

        switch (currentStep) {
            case INTRODUCTION:
            case VICTORY:
            case COMPLETE:
                return "Read the instructions and click 'Next'";

            case SELECT_WORKER:
                return currentPhase == TurnPhase.SELECT_WORKER ?
                        "Click on a BLUE highlighted worker to select it" :
                        "Worker selected! Moving to next step...";

            case MOVE_WORKER:
                return currentPhase == TurnPhase.MOVE ?
                        "Click on a GREEN space to move your worker" :
                        "Worker moved! Moving to next step...";

            case GOD_POWER:
                switch (currentPhase) {
                    case BUILD:
                        return "Artemis's power is ready! Try 'Use God Power' for an extra move, or click 'Next' to continue";
                    case MOVE:
                        return "Use god power to move again, or continue to the next step";
                    default:
                        return "Artemis's power lets you move twice! Try it or click 'Next' to continue";
                }

            case BUILD:
                return currentPhase == TurnPhase.BUILD ?
                        "Click on a YELLOW space to build" :
                        "Building complete! Moving to next step...";

            case END_TURN:
                return stepCompleted ?
                        "Step completed! Click 'Next' to continue." :
                        "Click 'End Turn' to complete your turn";

            default:
                return "Follow the tutorial instructions";
        }
    }

    /**
     * Interface for receiving tutorial events.
     */
    public interface TutorialListener {
        void onStepChanged(TutorialStep newStep);
        void onStepCompleted(TutorialStep completedStep);
    }
}