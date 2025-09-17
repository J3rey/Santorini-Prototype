package SantoriniGame.god;

import SantoriniGame.model.board.Board;
import SantoriniGame.model.board.BoardTile;
import SantoriniGame.model.game.TurnState;
import SantoriniGame.model.player.Worker;

import javax.swing.ImageIcon;
import java.net.URL;
import java.util.List;

/**
 * Abstract class representing a God character in the Santorini game.
 * Each God has a unique name, description, and associated image.
 * Implements GodPower interface according to the Interface Segregation Principle.
 */
public abstract class God implements GodPower {
    private final String path;
    private PowerStatus powerStatus = PowerStatus.INACTIVE;
    private final String name;
    private final String description;

    /**
     * Constructs a God object with the specified image path.
     *
     * @param path the path to the image file (relative to the resources folder)
     * @param name the name of the god
     * @param description the description of the god's power
     */
    protected God(String path, String name, String description) {
        this.path = path;
        this.name = name;
        this.description = description;
    }

    /**
     * Updates if the god power is activated or not, depending on the current TurnState.
     *
     * @param currState current state of the game
     */
    public abstract void checkActivation(TurnState currState);

    /**
     * Updates the power status
     * @param powerStatus new PowerStatus
     */
    public void setPowerStatus(PowerStatus powerStatus) {
        this.powerStatus = powerStatus;
    }

    /**
     * Gets the current power status.
     *
     * @return Current PowerStatus
     */
    public PowerStatus getPowerStatus() {
        return powerStatus;
    }

    /**
     * Gets the name of the God.
     *
     * @return the name of the God
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the God's ability.
     *
     * @return a multi-line string describing the God's ability
     */
    public String getDescription() {
        return description;
    }

    /**
     * Loads and returns the image icon associated with the God.
     *
     * @return the ImageIcon if the path is valid, or null if not found
     */
    public ImageIcon getImageIcon() {
        URL imgURL = getClass().getResource(path);
        return new ImageIcon(imgURL);
    }

    /**
     * Default implementation for getting valid move locations.
     * By default, returns the same locations as the standard rules.
     */
    @Override
    public List<BoardTile> getValidMoveLocations(Worker worker, Board board, TurnState turnState) {
        return board.getValidMoveLocations(worker);
    }

    /**
     * Default implementation for getting valid build locations.
     * By default, returns the same locations as the standard rules.
     */
    @Override
    public List<BoardTile> getValidBuildLocations(Worker worker, Board board, TurnState turnState) {
        return board.getValidBuildLocations(worker);
    }

    /**
     * Default implementation of getPowerPrompt.
     * Returns a generic prompt message.
     */
    @Override
    public String getPowerPrompt() {
        return "Would you like to use " + getName() + "'s power?";
    }

    /**
     * Default implementation for activating a god power.
     * Each god should override this with specific behavior.
     *
     * @param turnState Current turn state
     * @return Updated turn state after activation
     */
    @Override
    public TurnState activatePower(TurnState turnState) {
        setPowerStatus(PowerStatus.ACTIVE);
        return turnState;
    }

    /**
     * Default implementation for resetting power state.
     * Each god should override this with specific behavior.
     */
    @Override
    public void resetPowerState() {
        setPowerStatus(PowerStatus.INACTIVE);
    }

    /**
     * Determines if this god can activate its power in the current turn state.
     *
     * @param turnState Current game state
     * @return true if power can be activated
     */
    public boolean canActivatePower(TurnState turnState) {
        return getPowerStatus() == PowerStatus.CAN_ACTIVATE;
    }

    /**
     * Determines if two gods are equal based on their names.
     *
     * @param o Object to compare
     * @return true if gods are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        God otherGod = (God) o;
        return getName().equals(otherGod.getName());
    }

    /**
     * Generates a hash code based on the god's name.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}