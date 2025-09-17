package SantoriniGame.model.player;

import SantoriniGame.model.victory.VictoryCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * Team class. Allows for handling multiple players in the same team.
 * When a player is trapped or unable to build, their entire team is removed.
 */
public class Team {
    private List<Player> teamPlayers = new ArrayList<>();
    private List<VictoryCondition> teamVictoryConditions = new ArrayList<>();
    private Worker maleWorker;
    private Worker femaleWorker;
    private final int teamId;

    /**
     * Creates a team with specified ID.
     * @param teamId the team identifier
     */
    public Team(int teamId) {
        this.teamId = teamId;
    }

    /**
     * Creates a team with players and victory conditions.
     * @param teamId the team identifier
     * @param teamPlayers list of players
     * @param teamVictoryConditions list of victory conditions
     */
    public Team(int teamId, List<Player> teamPlayers, List<VictoryCondition> teamVictoryConditions) {
        this.teamId = teamId;
        this.teamPlayers = new ArrayList<>(teamPlayers);
        this.teamVictoryConditions = new ArrayList<>(teamVictoryConditions);
    }

    /**
     * Gets the team players.
     * @return copy of team players list
     */
    public List<Player> getTeamPlayers() {
        return new ArrayList<>(teamPlayers);
    }

    /**
     * Gets the team victory conditions.
     * @return copy of victory conditions list
     */
    public List<VictoryCondition> getTeamVictoryConditions() {
        return new ArrayList<>(teamVictoryConditions);
    }

    /**
     * Adds a player to this team.
     * @param player the player to add
     */
    public void addTeamPlayer(Player player) {
        teamPlayers.add(player);
        player.setTeam(this);
    }

    /**
     * Sets the male worker for this team.
     * @param maleWorker the male worker
     */
    public void setMaleWorker(Worker maleWorker) {
        this.maleWorker = maleWorker;
    }

    /**
     * Sets the female worker for this team.
     * @param femaleWorker the female worker
     */
    public void setFemaleWorker(Worker femaleWorker) {
        this.femaleWorker = femaleWorker;
    }

    /**
     * Gets both workers as an array.
     * @return array containing male and female workers
     */
    public Worker[] getWorkers() {
        return new Worker[] { maleWorker, femaleWorker };
    }

    /**
     * Gets the team ID.
     * @return the team identifier
     */
    public int getTeamId() {
        return teamId;
    }

    /**
     * Removes workers from the team.
     */
    public void removeWorkers() {
        // Remove workers from team
        setMaleWorker(null);
        setFemaleWorker(null);
    }
}