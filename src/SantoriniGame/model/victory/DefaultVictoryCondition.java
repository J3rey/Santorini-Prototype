package SantoriniGame.model.victory;

import SantoriniGame.model.player.Team;
import SantoriniGame.model.game.TurnState;

import java.util.List;

/**
 * DefaultVictoryCondition class. Team wins if they're the last surviving.
 */
public class DefaultVictoryCondition extends VictoryCondition {
    public static final String DEFAULT_DESC = "Default Victory: Your team is the last one surviving.";

    /**
     * Creates a default victory condition for a team.
     * @param t the team this condition applies to
     */
    public DefaultVictoryCondition(Team t) {
        super(t, DEFAULT_DESC);
    }

    /**
     * Checks if this team is the last one surviving.
     * @param turnState the current turn state
     * @return true if this team is the only one left
     */
    @Override
    public boolean isSatisfied(TurnState turnState) {
        // If not our team's turn currently, ignore
        if (turnState.getCurrPlayer().getTeam() != getTeam()) {
            return false;
        }

        // If no other team is still playing, our team has won
        // - Using VictoryCondition as a proxy (is removed when team removed)
        // - Code smell yup
        List<VictoryCondition> winConditions = turnState.getAllWinConditions();

        Team thisTeam = getTeam();
        return winConditions.stream().allMatch(v -> v.getTeam() == thisTeam);
    }
}