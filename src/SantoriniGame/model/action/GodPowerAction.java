package SantoriniGame.model.action;

import SantoriniGame.model.board.Board;
import SantoriniGame.god.God;
import SantoriniGame.model.player.Worker;

/**
 * Represents a god power activation action.
 * Following OCP by allowing for different god powers without modifying this class.
 */
public class GodPowerAction extends Action {
    private final God god;
    private final String powerDescription;

    /**
     * Constructs a new GodPowerAction.
     *
     * @param worker The worker associated with the god power
     * @param god The god whose power is being activated
     */
    public GodPowerAction(Worker worker, God god) {
        super(ActionType.GOD_POWER, worker);
        this.god = god;
        this.powerDescription = god.getPowerPrompt();
    }

    /**
     * Gets the god associated with this power.
     *
     * @return The god
     */
    public God getGod() {
        return god;
    }

    /**
     * Applies the god power activation.
     * Delegates to the specific god's power implementation.
     *
     * @param board The game board
     * @return True if the god power was successfully activated
     */
    @Override
    public boolean apply(Board board) {
        // This would typically update the TurnState
        // For this example, we're just showing the structure
        return true;
    }

    /**
     * Gets a descriptive string of this god power action.
     *
     * @return A description string
     */
    @Override
    public String getDescription() {
        return "Activated " + god.getName() + "'s power: " + powerDescription;
    }
}
