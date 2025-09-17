package SantoriniGame.model.player;

import SantoriniGame.god.God;

/**
 * Represents a player in the Santorini game.
 * Encapsulates player-specific attributes and selected god.
 */
public class Player {
    private final int id;
    private God selectedGod;

    private Team team;

    /**
     * Constructor for a player. Use setSelectedGod and setTeam for game purposes.
     *
     * @param id The player's unique identifier
     */
    public Player(int id) {
        this.id = id;
        this.selectedGod = null;
        this.team = null;
    }

    /**
     * Gets the player's display name.
     *
     * @return The player's name
     */
    public String getDisplayName() {
        return "Player " + id;
    }

    /**
     * Gets the player's unique identifier.
     *
     * @return The player's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the team of this player.
     *
     * @return The player's team
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Let the player know the team they're in
     * @param team  new team of this player
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Gets the player's selected god.
     *
     * @return The selected god or null if none selected
     */
    public God getSelectedGod() {
        return selectedGod;
    }

    /**
     * Sets the player's selected god.
     *
     * @param god The god to select
     */
    public void setSelectedGod(God god) {
        this.selectedGod = god;
    }

    /**
     * Checks if the player has selected a god.
     *
     * @return true if a god is selected, false otherwise
     */
    public boolean hasSelectedGod() {
        return selectedGod != null;
    }

    /**
     * Gets both workers as an array.
     *
     * @return Array containing the player's workers
     */
    public Worker[] getWorkers() {
        return team.getWorkers();
    }

    /**
     * Determines if this player equals another player.
     *
     * @param obj The object to compare with
     * @return true if the players are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return id == player.id;
    }

    /**
     * Gets a hash code for this player.
     *
     * @return A hash code based on the player's ID
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}