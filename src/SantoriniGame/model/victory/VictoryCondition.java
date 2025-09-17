package SantoriniGame.model.victory;

import SantoriniGame.model.player.Team;
import SantoriniGame.model.game.TurnState;

/**
 * VictoryCondition abstract class. Checks if the given team has won.
 */
abstract public class VictoryCondition {
    private final Team team;
    private final String description;

    /**
     * Creates a victory condition for a team.
     * @param t the team this condition applies to
     * @param desc description of the victory condition
     */
    public VictoryCondition(Team t, String desc) {
        this.team = t;
        this.description = desc;
    }

    /**
     * Checks if this victory condition is satisfied.
     * @param turnState the current turn state
     * @return true if victory condition is met, false otherwise
     */
    abstract public boolean isSatisfied(TurnState turnState);

    /**
     * Gets the team this condition applies to.
     * @return the team
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Gets the description of this victory condition.
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}