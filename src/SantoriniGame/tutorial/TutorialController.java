package SantoriniGame.tutorial;

import SantoriniGame.controller.GameController;
import SantoriniGame.god.Artemis;
import SantoriniGame.god.God;
import SantoriniGame.model.game.GameModel;
import SantoriniGame.model.game.TurnPhase;
import SantoriniGame.model.game.TurnState;
import SantoriniGame.model.board.BoardTile;

import java.util.List;

/**
 * Tutorial-specific controller that extends GameController with tutorial restrictions.
 * Manages tutorial flow and validates actions according to the current tutorial step.
 */
public class TutorialController extends GameController {
    private final TutorialStateManager tutorialState;
    private TutorialEventListener eventListener;

    /**
     * Creates a new tutorial controller with a pre-configured game model.
     */
    public TutorialController() {
        super(createTutorialGameModel());
        this.tutorialState = new TutorialStateManager();

        // Set up tutorial state listener
        this.tutorialState.setTutorialListener(new TutorialStateManager.TutorialListener() {
            @Override
            public void onStepChanged(TutorialStep newStep) {
                if (eventListener != null) {
                    eventListener.onTutorialStepChanged(newStep);
                }
            }

            @Override
            public void onStepCompleted(TutorialStep completedStep) {
                if (eventListener != null) {
                    eventListener.onTutorialStepCompleted(completedStep);
                }
            }
        });
    }

    /**
     * Creates a game model specifically configured for the tutorial.
     * @return A GameModel with Artemis gods for both players
     */
    private static GameModel createTutorialGameModel() {
        God[] gods = {new Artemis(), new Artemis()};
        return new GameModel(gods, 5); // 5x5 board
    }

    /**
     * Sets the event listener for tutorial events.
     * @param listener The event listener
     */
    public void setTutorialEventListener(TutorialEventListener listener) {
        this.eventListener = listener;
    }

    /**
     * Handles tile clicks with tutorial validation.
     * Only allows actions that are permitted by the current tutorial step.
     */
    @Override
    public TurnState handleTileClick(int x, int y) {
        TurnPhase phaseBefore = getCurrentPhase();
        TutorialAction attemptedAction = TutorialAction.fromTurnPhase(phaseBefore);

        // Check if this action is allowed in the current tutorial step
        if (!tutorialState.isActionAllowed(attemptedAction, phaseBefore)) {
            if (eventListener != null) {
                String errorMessage = tutorialState.getErrorMessage(attemptedAction);
                eventListener.onInvalidActionAttempted(attemptedAction, errorMessage);
            }
            return getModel().getTurnState(); // Return current state without changes
        }

        // Perform the action using the parent controller
        TurnState newTurnState = super.handleTileClick(x, y);
        TurnPhase phaseAfter = getCurrentPhase();

        // Check if this action completed the current tutorial step
        boolean stepCompleted = tutorialState.tryCompleteStep(attemptedAction, phaseBefore, phaseAfter);

        if (stepCompleted && eventListener != null) {
            eventListener.onActionSuccessful(attemptedAction, tutorialState.getCurrentStep());
        }

        return newTurnState;
    }

    /**
     * Activates god power with tutorial validation.
     */
    @Override
    public TurnState activateGodPower() {
        TurnPhase currentPhase = getCurrentPhase();
        TutorialStep currentStep = tutorialState.getCurrentStep();

        // For GOD_POWER step, allow god power activation when available
        if (currentStep == TutorialStep.GOD_POWER) {
            if (super.canActivateGodPower()) {
                TurnState result = super.activateGodPower();

                if (eventListener != null) {
                    eventListener.onActionSuccessful(TutorialAction.USE_GOD_POWER, tutorialState.getCurrentStep());
                }

                return result;
            } else {
                if (eventListener != null) {
                    eventListener.onInvalidActionAttempted(TutorialAction.USE_GOD_POWER,
                            "God power is not available. Try moving first!");
                }
                return getModel().getTurnState();
            }
        } else if (!tutorialState.isActionAllowed(TutorialAction.USE_GOD_POWER, currentPhase)) {
            if (eventListener != null) {
                String errorMessage = tutorialState.getErrorMessage(TutorialAction.USE_GOD_POWER);
                eventListener.onInvalidActionAttempted(TutorialAction.USE_GOD_POWER, errorMessage);
            }
            return getModel().getTurnState();
        }

        TurnState result = super.activateGodPower();

        if (eventListener != null) {
            eventListener.onActionSuccessful(TutorialAction.USE_GOD_POWER, tutorialState.getCurrentStep());
        }

        return result;
    }

    /**
     * Override endTurn to handle tutorial-specific logic.
     */
    @Override
    public void endTurn() {
        TutorialStep currentStep = tutorialState.getCurrentStep();

        // For END_TURN step, actually complete the step and allow progression
        if (currentStep == TutorialStep.END_TURN) {
            // Mark the step as completed
            tutorialState.completeCurrentStep();

            // Don't actually end the turn in tutorial, just reset for next attempt
            getModel().getTurnState().setCurrPhase(TurnPhase.SELECT_WORKER);
            getModel().getTurnState().setCurrentWorker(null);

            // Reset god powers for a fresh attempt
            God playerGod = getCurrentPlayer().getSelectedGod();
            if (playerGod != null) {
                playerGod.resetPowerState();
            }

            return;
        }

        // For most tutorial steps, don't actually end the turn - let them practice
        if (currentStep != TutorialStep.END_TURN) {
            // Reset the turn to SELECT_WORKER phase so they can practice again
            getModel().getTurnState().setCurrPhase(TurnPhase.SELECT_WORKER);
            getModel().getTurnState().setCurrentWorker(null);

            // Reset god powers for a fresh attempt
            God playerGod = getCurrentPlayer().getSelectedGod();
            if (playerGod != null) {
                playerGod.resetPowerState();
            }

            return;
        }

        // For END_TURN step, allow normal end turn behavior
        super.endTurn();
    }

    /**
     * Gets valid moves with tutorial restrictions.
     */
    @Override
    public List<BoardTile> getValidMoves() {
        if (tutorialState.isActionAllowed(TutorialAction.MOVE_WORKER, getCurrentPhase())) {
            return super.getValidMoves();
        }
        return List.of();
    }

    /**
     * Gets valid builds with tutorial restrictions.
     */
    @Override
    public List<BoardTile> getValidBuilds() {
        if (tutorialState.isActionAllowed(TutorialAction.BUILD, getCurrentPhase())) {
            return super.getValidBuilds();
        }
        return List.of();
    }

    /**
     * Checks if god power can be activated in the current tutorial context.
     */
    @Override
    public boolean canActivateGodPower() {
        TutorialStep currentStep = tutorialState.getCurrentStep();

        // For GOD_POWER step, delegate to the base game controller
        if (currentStep == TutorialStep.GOD_POWER) {
            return super.canActivateGodPower();
        }

        // For other steps, check both base game logic and tutorial permissions
        return super.canActivateGodPower() &&
                tutorialState.isActionAllowed(TutorialAction.USE_GOD_POWER, getCurrentPhase());
    }

    // Tutorial-specific methods

    /**
     * Gets the current tutorial step.
     */
    public TutorialStep getCurrentTutorialStep() {
        return tutorialState.getCurrentStep();
    }

    /**
     * Advances to the next tutorial step.
     */
    public boolean nextTutorialStep() {
        if (tutorialState.isActionAllowed(TutorialAction.NEXT_STEP, getCurrentPhase())) {
            tutorialState.completeCurrentStep();

            // Special handling for GOD_POWER step - make sure we go to BUILD next
            if (tutorialState.getCurrentStep() == TutorialStep.GOD_POWER) {
                // If we're in BUILD phase, go to BUILD step
                if (getCurrentPhase() == TurnPhase.BUILD) {
                    return tutorialState.nextStep(); // Should go to BUILD step
                }
            }

            return tutorialState.nextStep();
        }
        return false;
    }

    /**
     * Goes back to the previous tutorial step.
     */
    public boolean previousTutorialStep() {
        return tutorialState.previousStep();
    }

    /**
     * Checks if the current tutorial step has been completed.
     */
    public boolean isCurrentStepCompleted() {
        return tutorialState.isStepCompleted();
    }

    /**
     * Checks if the entire tutorial is complete.
     */
    public boolean isTutorialComplete() {
        return tutorialState.isTutorialComplete();
    }

    /**
     * Gets a status message for the current tutorial step.
     */
    public String getTutorialStatusMessage() {
        return tutorialState.getStatusMessage(getCurrentPhase());
    }

    /**
     * Interface for receiving tutorial events.
     */
    public interface TutorialEventListener {
        void onTutorialStepChanged(TutorialStep newStep);
        void onTutorialStepCompleted(TutorialStep completedStep);
        void onActionSuccessful(TutorialAction action, TutorialStep currentStep);
        void onInvalidActionAttempted(TutorialAction attemptedAction, String errorMessage);
    }
}