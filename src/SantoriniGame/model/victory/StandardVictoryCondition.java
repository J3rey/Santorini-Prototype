package SantoriniGame.model.victory;

import SantoriniGame.model.action.Action;
import SantoriniGame.model.action.MoveAction;
import SantoriniGame.model.board.BoardTile;
import SantoriniGame.model.player.Team;
import SantoriniGame.model.game.TurnState;

/**
 * The standard victory condition for Santorini.
 * A player wins by moving one of their workers to the third level of a building.
 */
public class StandardVictoryCondition extends VictoryCondition {
    public static final String DEFAULT_DESC = "Standard Victory: Move one of your workers to the third level of a building.";
    public static final int VICTORY_LEVEL = 3;

    public StandardVictoryCondition(Team t) {
        super(t, DEFAULT_DESC);
    }

    @Override
    public boolean isSatisfied(TurnState turnState) {
        // If not our player's turn currently, ignore
        if (turnState.getCurrPlayer().getTeam() != getTeam()) {
            return false;
        }

        // If last action was a move, check if it was to level 3
        Action lastAction = turnState.getLastAction();
        if (lastAction instanceof MoveAction) {
            MoveAction moveAction = (MoveAction) lastAction;
            BoardTile destination = moveAction.getDestination();

            // Victory if moved UP to level 3
            return moveAction.wasAscent() && destination.getLevel() == VICTORY_LEVEL;
        }

        return false;
    }
}