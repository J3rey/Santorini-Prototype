package SantoriniGame.ui;
import SantoriniGame.god.God;
import SantoriniGame.model.player.Player;
import SantoriniGame.controller.GodSelectionPresenter;

import java.util.List;

/**
 * Interface for the God Selection view.
 * Decouples the Presenter from the specific UI technology (Swing).
 */
public interface GodSelectionView {

    /**
     * Sets the presenter for this view.
     *
     * @param presenter The GodSelectionPresenter instance
     */
    void setPresenter(GodSelectionPresenter presenter);

    /**
     * Updates the player turn indicators in the UI.
     *
     * @param currentPlayerIndex Index of the current player
     * @param players List of players with their selected gods
     */
    void displayPlayerTurn(int currentPlayerIndex, List<Player> players);

    /**
     * Shows details for the specified god.
     *
     * @param god God to display details for
     */
    void displayGodDetails(God god);

    /**
     * Updates the list of available gods with selection status.
     *
     * @param availableGods List of all gods in the game
     * @param players List of players with their selected gods
     * @param currentPlayerIndex Index of the current player
     */
    void displayGodList(List<God> availableGods, List<Player> players, int currentPlayerIndex);

    /**
     * Enables or disables the confirm button.
     *
     * @param enabled Whether the button should be enabled
     */
    void setConfirmEnabled(boolean enabled);

    /**
     * Shows a completion message to the user.
     *
     * @param title Title of the message
     * @param message Content of the message
     */
    void showCompletionMessage(String title, String message);
}

