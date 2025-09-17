package SantoriniGame.model.action;

import SantoriniGame.model.board.Board;
import SantoriniGame.model.player.Player;

/**
 * Represents the end of a player's turn.
 * This action doesn't involve a specific worker, so worker is null.
 * Follows SRP by encapsulating turn-end logic.
 */
public class EndTurnAction extends Action {
    private final Player currentPlayer;
    private final Player nextPlayer;

    /**
     * Constructs a new EndTurnAction.
     *
     * @param currentPlayer The player ending their turn
     * @param nextPlayer The player who will go next
     */
    public EndTurnAction(Player currentPlayer, Player nextPlayer) {
        // EndTurn actions don't involve a specific worker, so pass null
        super(ActionType.END_TURN, null);
        this.currentPlayer = currentPlayer;
        this.nextPlayer = nextPlayer;
    }

    /**
     * Gets the next player.
     *
     * @return The next player
     */
    public Player getNextPlayer() {
        return nextPlayer;
    }

    /**
     * Applies the end turn action.
     * This handles any board-level cleanup needed when ending a turn.
     *
     * @param board The game board
     * @return True if the turn was successfully ended
     */
    @Override
    public boolean apply(Board board) {
        // End turn actions typically don't modify the board directly
        // The turn state management handles player transitions
        return true;
    }

    /**
     * Undoes the end turn action.
     * This would restore the previous player's turn.
     *
     * @param board The game board
     * @return True if the action was successfully undone
     */
    @Override
    public boolean undo(Board board) {
        // For undo functionality, we could restore the previous player
        // This would require coordination with the game state
        return false; // Not implemented for now
    }

    /**
     * Gets a descriptive string of this end turn action.
     *
     * @return A description string
     */
    @Override
    public String getDescription() {
        return String.format("%s ended their turn, next player: %s",
                currentPlayer.getDisplayName(), nextPlayer.getDisplayName());
    }
}