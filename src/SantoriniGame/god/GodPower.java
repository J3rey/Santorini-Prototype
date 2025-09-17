package SantoriniGame.god;

import SantoriniGame.model.board.Board;
import SantoriniGame.model.board.BoardTile;
import SantoriniGame.model.game.TurnState;
import SantoriniGame.model.player.Worker;

import java.util.List;

/**
 * Interface for god powers in Santorini.
 * Following the Interface Segregation Principle, this defines the contract
 * that all god powers must implement.
 */
public interface GodPower {

    /**
     * Gets valid move locations considering this god's power.
     *
     * @param worker Worker to check moves for
     * @param board Game board
     * @param turnState Current turn state
     * @return List of valid tiles the worker can move to
     */
    List<BoardTile> getValidMoveLocations(Worker worker, Board board, TurnState turnState);

    /**
     * Gets valid build locations considering this god's power.
     *
     * @param worker Worker to check builds for
     * @param board Game board
     * @param turnState Current turn state
     * @return List of valid tiles the worker can build on
     */
    List<BoardTile> getValidBuildLocations(Worker worker, Board board, TurnState turnState);

    /**
     * Gets a user-friendly message explaining the god power option
     * to display when prompting the player.
     *
     * @return Prompt message for the god power
     */
    String getPowerPrompt();

    /**
     * Activates the god's power for the current turn state.
     * This allows for custom behavior when a power is activated.
     *
     * @param turnState Current turn state
     * @return Updated turn state after power activation
     */
    TurnState activatePower(TurnState turnState);

    /**
     * Resets the god's power state, typically at the end of a turn.
     */
    void resetPowerState();
}