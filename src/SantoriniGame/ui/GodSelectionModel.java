package SantoriniGame.ui;
import SantoriniGame.god.God;
import SantoriniGame.model.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model for God Selection.
 * Holds the application's data and business logic related to god selection.
 * Knows nothing about the UI (Swing).
 */
public class GodSelectionModel {
    private final List<God> availableGods;
    private final List<Player> players;
    private int currentPlayerIndex;
    private boolean selectionComplete;

    /**
     * Constructor for the model.
     *
     * @param numPlayers Number of players in the game
     * @param availableGods List of all available gods
     */
    public GodSelectionModel(int numPlayers, List<God> availableGods) {
        this.availableGods = new ArrayList<>(availableGods);
        this.players = initializePlayers(numPlayers);
        this.currentPlayerIndex = 0;
        this.selectionComplete = false;
    }

    /**
     * Initializes player objects.
     *
     * @param numPlayers Number of players
     * @return List of initialized players
     */
    private List<Player> initializePlayers(int numPlayers) {
        List<Player> result = new ArrayList<>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            result.add(new Player(i + 1));
        }
        return result;
    }

    /**
     * Attempts to select a god for the specified player.
     *
     * @param playerIndex Player trying to select the god
     * @param god God to select
     * @return true if selection was successful, false otherwise
     */
    public boolean selectGod(int playerIndex, God god) {
        if (playerIndex < 0 || playerIndex >= players.size() || isSelectionComplete()) {
            return false;
        }

        // If the god is already selected by another player, don't allow selection
        for (int i = 0; i < players.size(); i++) {
            if (i != playerIndex && god.equals(players.get(i).getSelectedGod())) {
                return false;
            }
        }

        players.get(playerIndex).setSelectedGod(god);
        return true;
    }

    /**
     * Deselects the god for the specified player.
     *
     * @param playerIndex Player index
     */
    public void deselectGod(int playerIndex) {
        if (playerIndex >= 0 && playerIndex < players.size()) {
            players.get(playerIndex).setSelectedGod(null);
        }
    }

    /**
     * Advances to the next player's turn. If all players have selected,
     * sets selection to complete.
     */
    public void advancePlayerTurn() {
        currentPlayerIndex++;

        if (currentPlayerIndex >= players.size()) {
            selectionComplete = true;
            // Keep currentPlayerIndex within bounds for safety
            currentPlayerIndex = players.size() - 1;
        }
    }

    /**
     * @return Whether the selection process is complete
     */
    public boolean isSelectionComplete() {
        return selectionComplete;
    }

    /**
     * @return List of all available gods
     */
    public List<God> getAllGods() {
        return Collections.unmodifiableList(availableGods);
    }

    /**
     * Gets the current player.
     *
     * @return Current player
     */
    public Player getCurrentPlayer() {
        if (currentPlayerIndex < 0 || currentPlayerIndex >= players.size()) {
            // Return the last player as a fallback if out of bounds
            return players.get(players.size() - 1);
        }
        return players.get(currentPlayerIndex);
    }

    /**
     * @return Current player's index
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * Gets the god selected by a specific player.
     *
     * @param playerIndex Player index
     * @return Selected god or null if none selected
     */
    public God getSelectedGod(int playerIndex) {
        if (playerIndex >= 0 && playerIndex < players.size()) {
            return players.get(playerIndex).getSelectedGod();
        }
        return null;
    }

    /**
     * @return List of all players
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Creates an array of selected gods.
     *
     * @return Array of selected gods, where each index corresponds to a player
     */
    public God[] getSelectedGodsArray() {
        God[] result = new God[players.size()];
        for (int i = 0; i < players.size(); i++) {
            result[i] = players.get(i).getSelectedGod();
        }
        return result;
    }

    /**
     * Finds which player has selected a specific god.
     *
     * @param god God to check
     * @return Player index or -1 if not selected
     */
    public int getPlayerWhoSelected(God god) {
        if (god == null) return -1;

        for (int i = 0; i < players.size(); i++) {
            if (god.equals(players.get(i).getSelectedGod())) {
                return i;
            }
        }
        return -1;
    }
}

