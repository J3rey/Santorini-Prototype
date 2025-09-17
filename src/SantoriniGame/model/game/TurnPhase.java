package SantoriniGame.model.game;

/**
 * TurnPhase enumeration. Stores the specific phase the player is currently in.
 *
 * SELECT_WORKER: Player is deciding which worker to select
 * MOVE: Player is deciding where to move a worker
 * BUILD: Player is deciding where to build a building
 * END_TURN: Player is finished, allows for end-of-turn actions
 *
 * Created by: John Vo
 * Last edited by: John Vo
 * Last edited at: 19/04/2025
 */
public enum TurnPhase {
    SELECT_WORKER,  // Player selects worker
    MOVE,           // Player moves the worker
    BUILD,          // Player builds with the worker
    END_TURN,       // Any end-turn actions allowed here
}
