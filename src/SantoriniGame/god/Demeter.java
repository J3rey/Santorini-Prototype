package SantoriniGame.god;

import SantoriniGame.model.action.BuildAction;
import SantoriniGame.model.board.Board;
import SantoriniGame.model.board.BoardTile;
import SantoriniGame.model.game.TurnPhase;
import SantoriniGame.model.game.TurnState;
import SantoriniGame.model.player.Worker;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the God "Demeter" in the Santorini game.
 * Demeter's ability allows building twice, but not on the same space.
 */
public class Demeter extends God {
    public static final String GOD_NAME = "Demeter";
    public static final String GOD_DESCRIPTION = "Timing: Your Build.\n\n" +
            "Power: Your Worker may build one additional time, but not on the same space.";

    private BoardTile firstBuildLocation;
    private boolean extraBuildUsed;

    /**
     * Constructs a Demeter object with the appropriate image path.
     */
    public Demeter() {
        super("/images/demeter.png", GOD_NAME, GOD_DESCRIPTION);
        this.firstBuildLocation = null;
        this.extraBuildUsed = false;
    }

    @Override
    public void checkActivation(TurnState currState) {
        // Can only activate after first build and in END_TURN phase
        if (currState.getCurrPhase() == TurnPhase.END_TURN &&
                !extraBuildUsed &&
                currState.getLastAction() instanceof BuildAction) {

            // We've just completed a normal build, now we can offer an extra build
            setPowerStatus(PowerStatus.CAN_ACTIVATE);

            // Store first build location
            if (firstBuildLocation == null) {
                BuildAction buildAction = (BuildAction) currState.getLastAction();
                firstBuildLocation = buildAction.getBuildLocation();
            }
        } else {
            setPowerStatus(PowerStatus.INACTIVE);
        }
    }

    @Override
    public List<BoardTile> getValidBuildLocations(Worker worker, Board board, TurnState turnState) {
        List<BoardTile> standardBuilds = board.getValidBuildLocations(worker);

        // If this is the second build (power active), exclude the first build location
        if (getPowerStatus() == PowerStatus.ACTIVE && firstBuildLocation != null) {
            List<BoardTile> filteredBuilds = new ArrayList<>();
            for (BoardTile tile : standardBuilds) {
                // Skip the first build location
                if (tile.getX() != firstBuildLocation.getX() || tile.getY() != firstBuildLocation.getY()) {
                    filteredBuilds.add(tile);
                }
            }
            return filteredBuilds;
        }

        return standardBuilds;
    }

    @Override
    public String getPowerPrompt() {
        return "Would you like to build one additional time? (You cannot build on the same space)";
    }

    /**
     * Activates Demeter's power to build an additional time.
     */
    @Override
    public TurnState activatePower(TurnState turnState) {
        setPowerStatus(PowerStatus.ACTIVE);
        extraBuildUsed = true;

        // Change the phase back to BUILD to allow an additional build
        turnState.setCurrPhase(TurnPhase.BUILD);

        return turnState;
    }

    /**
     * Resets the power state at the end of the turn.
     */
    @Override
    public void resetPowerState() {
        firstBuildLocation = null;
        extraBuildUsed = false;
        setPowerStatus(PowerStatus.INACTIVE);
    }
}