package SantoriniGame.model.action;
/**
 * Enum defining the types of actions in the game.
 * Allows for easy extension of action types.
 */
public enum ActionType {
    MOVE("Move"),
    BUILD("Build"),
    END_TURN("End Turn"),
    GOD_POWER("God Power");

    ActionType(String displayName) {
    }
}
