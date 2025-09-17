package SantoriniGame.model.victory;

import SantoriniGame.model.player.Team;
import SantoriniGame.model.game.TurnState;

/**
 * Victory condition triggered when a player times out.
 * The remaining teams win when an opponent runs out of time.
 */
public class TimeoutVictoryCondition extends VictoryCondition {
    public static final String TIMEOUT_DESC = "Timeout Victory: Opponent ran out of time.";

    /**
     * Creates a timeout victory condition for the winning team.
     * @param winningTeam the team that wins due to opponent timeout
     */
    public TimeoutVictoryCondition(Team winningTeam) {
        super(winningTeam, TIMEOUT_DESC);
    }

    /**
     * Checks if this timeout victory condition is satisfied.
     * This is triggered externally when a timeout occurs.
     * @param turnState the current turn state
     * @return false (not checked during normal game flow)
     */
    @Override
    public boolean isSatisfied(TurnState turnState) {
        // This victory condition is triggered externally when a timeout occurs
        // It's not checked during normal game flow
        return false;
    }
}