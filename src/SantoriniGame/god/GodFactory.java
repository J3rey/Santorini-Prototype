package SantoriniGame.god;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory class for creating God objects.
 * Follows the Factory pattern to centralize god creation logic.
 */
public class GodFactory {
    // Singleton instance
    private static GodFactory instance;

    // Map to store god creation functions
    private final Map<String, Supplier<God>> godCreators;

    /**
     * Private constructor for the singleton.
     */
    private GodFactory() {
        godCreators = new HashMap<>();

        // Register available gods
        registerGod(Artemis.GOD_NAME, Artemis::new);
        registerGod(Demeter.GOD_NAME, Demeter::new);
        registerGod(Triton.GOD_NAME, Triton::new);
    }

    /**
     * Gets the singleton instance.
     *
     * @return The GodFactory instance
     */
    public static GodFactory getInstance() {
        if (instance == null) {
            instance = new GodFactory();
        }
        return instance;
    }

    /**
     * Registers a god with the factory.
     *
     * @param godName Name of the god
     * @param creator Supplier that creates the god
     */
    public void registerGod(String godName, Supplier<God> creator) {
        godCreators.put(godName, creator);
    }


    /**
     * Creates a list of all available gods.
     *
     * @return List of all gods
     */
    public List<God> createAllGods() {
        List<God> gods = new ArrayList<>();
        for (Supplier<God> creator : godCreators.values()) {
            gods.add(creator.get());
        }
        return gods;
    }
}
