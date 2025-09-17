package SantoriniGame.god;

import SantoriniGame.model.action.MoveAction;
import SantoriniGame.model.board.Board;
import SantoriniGame.model.board.BoardTile;
import SantoriniGame.model.game.TurnPhase;
import SantoriniGame.model.game.TurnState;
import SantoriniGame.model.player.Worker;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the God "Artemis" in the Santorini game.
 * Artemis's ability allows moving twice, but not back to the starting position.
 */
public class Artemis extends God {
    public static final String GOD_NAME = "Artemis";
    public static final String GOD_DESCRIPTION = "Timing: Your Move.\n\n" +
            "Power: Your Worker may move one additional time, but not back to its initial space.";

    private BoardTile initialPosition;
    private boolean extraMoveUsed;

    /**
     * Constructs an Artemis object with the appropriate image path.
     */
    public Artemis() {
        super("/images/artemis.png", GOD_NAME, GOD_DESCRIPTION);
        this.initialPosition = null;
        this.extraMoveUsed = false;
    }

    @Override
    public void checkActivation(TurnState currState) {
        // Can only activate after first move and in MOVE phase
        if (currState.getCurrPhase() == TurnPhase.BUILD &&
                !extraMoveUsed &&
                currState.getLastAction() instanceof MoveAction) {

            // We've just completed a normal move, now we can offer the extra move
            setPowerStatus(PowerStatus.CAN_ACTIVATE);

            // Store initial position if not already set
            if (initialPosition == null && currState.getCurrWorker() != null) {
                MoveAction moveAction = (MoveAction) currState.getLastAction();
                initialPosition = moveAction.getStart();
            }
        } else {
            setPowerStatus(PowerStatus.INACTIVE);
        }
    }

    @Override
    public List<BoardTile> getValidMoveLocations(Worker worker, Board board, TurnState turnState) {
        List<BoardTile> standardMoves = board.getValidMoveLocations(worker);

        // If this is the second move (power active), exclude the initial position
        if (getPowerStatus() == PowerStatus.ACTIVE && initialPosition != null) {
            List<BoardTile> filteredMoves = new ArrayList<>();
            for (BoardTile tile : standardMoves) {
                // Skip the initial position
                if (tile.getX() != initialPosition.getX() || tile.getY() != initialPosition.getY()) {
                    filteredMoves.add(tile);
                }
            }
            return filteredMoves;
        }

        return standardMoves;
    }

    @Override
    public String getPowerPrompt() {
        return "Would you like to move your worker one additional time? (You cannot move back to your initial space)";
    }

    /**
     * Activates Artemis's power to move an additional time.
     */
    @Override
    public TurnState activatePower(TurnState turnState) {
        setPowerStatus(PowerStatus.ACTIVE);
        extraMoveUsed = true;

        // Change the phase back to MOVE to allow an additional move
        turnState.setCurrPhase(TurnPhase.MOVE);

        return turnState;
    }

    /**
     * Resets the power state at the end of the turn.
     */
    @Override
    public void resetPowerState() {
        initialPosition = null;
        extraMoveUsed = false;
        setPowerStatus(PowerStatus.INACTIVE);
    }
}