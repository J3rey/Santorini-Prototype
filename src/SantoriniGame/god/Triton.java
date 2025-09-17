package SantoriniGame.god;

import SantoriniGame.model.action.MoveAction;
import SantoriniGame.model.board.Board;
import SantoriniGame.model.board.BoardTile;
import SantoriniGame.model.game.TurnPhase;
import SantoriniGame.model.game.TurnState;
import SantoriniGame.model.player.Worker;

import java.util.List;

/**
 * Represents the God "Triton" in the Santorini game.
 * Triton's ability allows the player to move again if they move into a perimeter space.
 * This can be done multiple times in a single turn, as long as each move is to a perimeter space.
 */
public class Triton extends God {
    public static final String GOD_NAME = "Triton";
    public static final String GOD_DESCRIPTION = "Timing: Your Move.\n\n" +
            "Power: Each time your Worker moves into a perimeter space, it may immediately move again.";

    private boolean movedToPerimeter;
    private int extraMovesUsed;

    /**
     * Constructs a Triton object with the appropriate image path.
     */
    public Triton() {
        super("/images/triton.png", GOD_NAME, GOD_DESCRIPTION);
        this.movedToPerimeter = false;
        this.extraMovesUsed = 0;
    }

    @Override
    public void checkActivation(TurnState currState) {
        // Can only activate after a move and in BUILD phase (just completed a move)
        if (currState.getCurrPhase() == TurnPhase.BUILD &&
                currState.getLastAction() instanceof MoveAction) {

            MoveAction moveAction = (MoveAction) currState.getLastAction();
            BoardTile destination = moveAction.getDestination();

            // Check if the worker moved to a perimeter space
            if (isPerimeterSpace(destination)) {
                movedToPerimeter = true;
                setPowerStatus(PowerStatus.CAN_ACTIVATE);
            } else {
                setPowerStatus(PowerStatus.INACTIVE);
            }
        } else {
            setPowerStatus(PowerStatus.INACTIVE);
        }
    }

    /**
     * Checks if a tile is on the perimeter of the board.
     *
     * @param tile The tile to check
     * @return true if the tile is on the perimeter, false otherwise
     */
    private boolean isPerimeterSpace(BoardTile tile) {
        int x = tile.getX();
        int y = tile.getY();

        // Assuming a 5x5 board (0-4 indices)
        return x == 0 || x == 4 || y == 0 || y == 4;
    }

    @Override
    public List<BoardTile> getValidMoveLocations(Worker worker, Board board, TurnState turnState) {
        // Triton doesn't have special move restrictions, just use standard moves
        return board.getValidMoveLocations(worker);
    }

    @Override
    public String getPowerPrompt() {
        return "Would you like to move your worker again? (Your last move was to a perimeter space)";
    }

    /**
     * Activates Triton's power to move an additional time after moving to a perimeter space.
     */
    @Override
    public TurnState activatePower(TurnState turnState) {
        setPowerStatus(PowerStatus.ACTIVE);
        extraMovesUsed++;
        movedToPerimeter = false;

        // Change the phase back to MOVE to allow an additional move
        turnState.setCurrPhase(TurnPhase.MOVE);

        return turnState;
    }

    /**
     * Resets the power state at the end of the turn.
     */
    @Override
    public void resetPowerState() {
        movedToPerimeter = false;
        extraMovesUsed = 0;
        setPowerStatus(PowerStatus.INACTIVE);
    }
}