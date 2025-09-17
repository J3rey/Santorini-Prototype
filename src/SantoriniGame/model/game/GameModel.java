package SantoriniGame.model.game;

import SantoriniGame.model.action.BuildAction;
import SantoriniGame.model.action.GodPowerAction;
import SantoriniGame.model.action.MoveAction;
import SantoriniGame.god.God;
import SantoriniGame.god.PowerStatus;
import SantoriniGame.model.board.Board;
import SantoriniGame.model.board.BoardTile;
import SantoriniGame.model.board.Building;
import SantoriniGame.model.player.Player;
import SantoriniGame.model.player.Team;
import SantoriniGame.model.player.Worker;
import SantoriniGame.model.player.WorkerGender;
import SantoriniGame.model.victory.DefaultVictoryCondition;
import SantoriniGame.model.victory.StandardVictoryCondition;
import SantoriniGame.model.victory.TimeoutVictoryCondition;
import SantoriniGame.model.victory.VictoryCondition;

import java.util.*;

/**
 * Improved GameModel class following SOLID principles.
 * Manages game logic for Santorini with better action handling.
 * Uses the Command pattern via Action objects.
 */
public class GameModel {
    private TurnState turnState;
    private final Board board;
    private Player[] players;
    private int currentPlayerIndex;
    private Team[] teams = new Team[2];

    // Statistics tracking
    private final GameStatistics gameStats;

    /**
     * Constructs a new GameModel with the specified players and their gods.
     *
     * @param playerGods Array of gods for each player
     */
    public GameModel(God[] playerGods, int boardSize) {
        this.board = new Board(boardSize);
        this.players = new Player[playerGods.length];
        this.currentPlayerIndex = 0;
        this.gameStats = new GameStatistics();

        // Create teams with explicit IDs
        Team team1 = new Team(0);
        Team team2 = new Team(1);
        teams[0] = team1;
        teams[1] = team2;

        List<Integer> team1ids = new ArrayList<>();
        List<Integer> team2ids = new ArrayList<>();
        switch (playerGods.length) {
            case 2 -> { team1ids.add(1); team2ids.add(2); }
            case 3 -> { team1ids.add(1); team2ids.add(2); team2ids.add(3); }
            case 4 -> { team1ids.add(1); team2ids.add(2); team1ids.add(3); team2ids.add(4); }
            default -> throw new IllegalStateException("Game was not given 2, 3, or 4 players (and their gods).");
        }

        // Initialize players and their gods
        for (int i = 0; i < playerGods.length; i++) {
            players[i] = new Player(i + 1);
            players[i].setSelectedGod(playerGods[i]);
            // Add player to team (and let player know their team)
            if (team1ids.contains(i + 1)) {
                team1.addTeamPlayer(players[i]);
            } else {
                team2.addTeamPlayer(players[i]);
            }
        }

        // Place initial workers
        initializeWorkers();

        // Set up initial turn state
        initializeTurnState();
    }

    /**
     * Randomises a worker placement given a board size.
     * @param team      team to assign this worker to
     * @param random    Random
     * @param boardSize int
     * @param gender    gender of worker
     */
    private void randomiseWorker(Team team, Random random, int boardSize, WorkerGender gender) {
        // --- Place Worker ---
        Worker worker;
        BoardTile tile;
        int attempts = 0; // Prevent potential infinite loop on very small/full boards
        final int maxAttempts = boardSize * boardSize * 2; // Generous limit

        do {
            int x = random.nextInt(boardSize);
            int y = random.nextInt(boardSize);
            tile = board.getTile(x, y);
            attempts++;
            if (attempts > maxAttempts) {
                throw new IllegalStateException("Could not find an empty tile for worker placement after " + maxAttempts + " attempts.");
            }
        } while (tile == null || tile.getOccupant() != null); // Keep trying until an empty tile is found

        // Create and assign the worker
        worker = new Worker(gender, team, tile.getX(), tile.getY());
        switch (gender) { // Assign worker to player
            case WorkerGender.MALE -> team.setMaleWorker(worker);
            case WorkerGender.FEMALE -> team.setFemaleWorker(worker);
            default -> throw new IllegalStateException("When randomising worker placement, a gender case was missing (not MALE or FEMALE).");
        }
        tile.setOccupant(worker); // Place worker on board tile
    }

    /**
     * Places the initial workers randomly on the board for all players.
     * Ensures workers are placed on unoccupied tiles.
     */
    private void initializeWorkers() {
        Random random = new Random();
        int boardSize = board.getBoardSize();

        for (Team team : teams) {
            // --- Place Male Worker ---
            randomiseWorker(team, random, boardSize, WorkerGender.MALE);

            // --- Place Female Worker ---
            randomiseWorker(team, random, boardSize, WorkerGender.FEMALE);
        }
    }

    /**
     * Initialize the turn state with gods and victory conditions.
     */
    private void initializeTurnState() {
        // Create god list from player selections
        List<God> allGods = new ArrayList<>();
        for (Player player : players) {
            allGods.add(player.getSelectedGod());
        }

        // Create victory conditions
        List<VictoryCondition> winConditions = new ArrayList<>();
        for (Team team : teams) {
            winConditions.add(new StandardVictoryCondition(team));
            winConditions.add(new DefaultVictoryCondition(team));
            // Note: TimeoutVictoryCondition will be added dynamically when a timeout occurs
        }

        // Create initial turn state
        turnState = new TurnState(
                players[currentPlayerIndex],
                TurnPhase.SELECT_WORKER,
                board,
                null, // No worker selected initially
                null, // No last action initially
                allGods,
                winConditions
        );
    }

    /**
     * Handles a player timing out by removing their team and checking for game end.
     * This method encapsulates the timeout logic in the model layer.
     *
     * @param playerId The ID of the player who timed out
     * @return VictoryCondition if the game ended due to timeout, null if game continues
     */
    public VictoryCondition handlePlayerTimeout(int playerId) {
        // Find the player who timed out
        Player timedOutPlayer = null;
        for (Player player : players) {
            if (player.getId() == playerId) {
                timedOutPlayer = player;
                break;
            }
        }

        if (timedOutPlayer == null) {
            throw new IllegalArgumentException("Player with ID " + playerId + " not found");
        }

        Team timedOutTeam = timedOutPlayer.getTeam();

        // Remove the timed-out player's team
        removeTeam(timedOutTeam);

        // Check if game is over (only one team left)
        if (teams.length == 1) {
            // Game over - create and return timeout victory condition for winning team
            Team winningTeam = teams[0];
            TimeoutVictoryCondition timeoutVictory = new TimeoutVictoryCondition(winningTeam);

            // Add the timeout victory condition to the turn state
            turnState.getAllWinConditions().add(timeoutVictory);

            // Mark this victory condition as satisfied in the turn state
            turnState.victoryCheck(); // This will detect the timeout victory

            return timeoutVictory;
        }

        // Game continues with remaining players
        return null;
    }

    /**
     * Gets the current turn state.
     *
     * @return Current TurnState
     */
    public TurnState getTurnState() {
        return turnState;
    }

    /**
     * Gets the game board.
     *
     * @return Game Board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the array of players.
     *
     * @return Array of Players
     */
    public Player[] getPlayers() {
        return players;
    }

    /**
     * Gets the current player.
     *
     * @return Current Player
     */
    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

    /**
     * Gets the array of teams.
     *
     * @return Array of Teams
     */
    public Team[] getTeams() {
        return teams;
    }

    /**
     * Handles selecting a worker.
     *
     * @param worker Worker to select
     */
    public void selectWorker(Worker worker) {
        if (turnState.getCurrPhase() != TurnPhase.SELECT_WORKER) {
            return; // Can't select worker in other phases
        }

        if (worker != null && worker.getTeam() != turnState.getCurrPlayer().getTeam()) {
            return; // Can't select another player's worker
        }

        // Update turn state with selected worker and move to MOVE phase
        turnState.setCurrentWorker(worker);

        if (worker != null) {
            turnState.setCurrPhase(TurnPhase.MOVE);
        }
    }

    /**
     * Handles moving a worker.
     *
     * @param toX X-coordinate to move to
     * @param toY Y-coordinate to move to
     * @return Updated TurnState
     */
    public TurnState moveWorker(int toX, int toY) {
        if (turnState.getCurrPhase() != TurnPhase.MOVE) {
            return turnState; // Can't move in other phases
        }

        Worker worker = turnState.getCurrWorker();
        if (worker == null) {
            return turnState; // No worker selected
        }

        BoardTile targetTile = board.getTile(toX, toY);
        if (targetTile == null) {
            return turnState; // Invalid coordinates
        }

        // Check if the move is valid using the player's god power to determine valid moves
        God playerGod = turnState.getCurrPlayer().getSelectedGod();
        List<BoardTile> validMoves = playerGod.getValidMoveLocations(worker, board, turnState);

        boolean validMove = false;
        for (BoardTile tile : validMoves) {
            if (tile.getX() == toX && tile.getY() == toY) {
                validMove = true;
                break;
            }
        }

        if (!validMove) {
            return turnState; // Invalid move
        }

        // Get starting position before move
        int oldX = worker.getX();
        int oldY = worker.getY();
        BoardTile startTile = board.getTile(oldX, oldY);

        // Create move action
        MoveAction moveAction = new MoveAction(worker, startTile, targetTile);

        // Apply the move action through the turn state
        boolean success = turnState.applyAction(moveAction);

        if (success) {
            // Track statistic
            gameStats.incrementMoveCount();

            // Let gods check their statuses
            turnState.godPowerCheck();

            // Check for victory after applying the move
            VictoryCondition winCondition = turnState.getSatisfiedCondition();
            if (winCondition != null) {
                return turnState;
            }
        }

        return turnState;
    }

    /**
     * Handles building on a tile.
     *
     * @param buildX X-coordinate to build on
     * @param buildY Y-coordinate to build on
     * @return Updated TurnState
     */
    public TurnState buildOnTile(int buildX, int buildY) {
        if (turnState.getCurrPhase() != TurnPhase.BUILD) {
            return turnState; // Can't build in other phases
        }

        Worker worker = turnState.getCurrWorker();
        if (worker == null) {
            return turnState; // No worker selected
        }

        BoardTile targetTile = board.getTile(buildX, buildY);
        if (targetTile == null) {
            return turnState; // Invalid coordinates
        }

        // Use the player's god to get valid build locations
        God playerGod = turnState.getCurrPlayer().getSelectedGod();
        List<BoardTile> validBuilds = playerGod.getValidBuildLocations(worker, board, turnState);

        boolean validBuild = false;
        for (BoardTile tile : validBuilds) {
            if (tile.getX() == buildX && tile.getY() == buildY) {
                validBuild = true;
                break;
            }
        }

        if (!validBuild) {
            return turnState; // Invalid build
        }

        // Record the level before building
        int previousLevel = targetTile.getLevel();
        boolean hadDome = targetTile.hasDome();

        // Create build action
        BuildAction buildAction = new BuildAction(
                worker,
                targetTile,
                previousLevel,
                previousLevel + 1, // New level will be previous + 1 (or unchanged if dome built)
                !hadDome && previousLevel == Building.MAX_LEVEL // Dome is built if at max level
        );

        // Apply the build action through the turn state
        boolean success = turnState.applyAction(buildAction);

        if (success) {
            // Track statistics
            gameStats.incrementBuildCount();

            // If a dome was built, update dome count
            if (buildAction.wasDomeBuilt()) {
                gameStats.incrementDomeCount();
            }

            // Let god powers that trigger after build check their status
            playerGod.checkActivation(turnState);
        }

        return turnState;
    }

    /**
     * Activates the current player's god power.
     *
     * @return Updated TurnState
     */
    public TurnState activateGodPower() {
        God playerGod = turnState.getCurrPlayer().getSelectedGod();
        Worker worker = turnState.getCurrWorker();

        if (playerGod.canActivatePower(turnState) && worker != null) {
            // Create god power action
            GodPowerAction godPowerAction = new GodPowerAction(worker, playerGod);

            // Apply the god power
            TurnState updatedState = playerGod.activatePower(turnState);

            // Track god power usage
            gameStats.incrementGodPowerActivationCount();

            return updatedState;
        }

        return turnState;
    }

    /**
     * Declines to use the current player's god power.
     *
     * @return Updated TurnState
     */
    public TurnState declineGodPower() {
        // Just move to end turn phase if not already there
        if (turnState.getCurrPhase() != TurnPhase.END_TURN) {
            turnState.setCurrPhase(TurnPhase.END_TURN);
        }

        return turnState;
    }

    /**
     * Returns if current player is trapped
     *
     * @return if player is trapped
     */
    public boolean playerIsTrapped() {
        Player currPlayer = turnState.getCurrPlayer();
        Worker[] allWorkers = currPlayer.getTeam().getWorkers();

        // Check each worker, if not trapped, all good
        for (Worker worker : allWorkers) {
            if (!workerIsTrapped(currPlayer, worker)) {
                return false;
            }
        }
        // All workers are trapped, return true
        return true;
    }

    /**
     * Returns if worker for this player is trapped.
     *
     * @return if worker is trapped
     */
    public boolean workerIsTrapped(Player player, Worker worker) {
        // Sanity check
        if (player.getTeam() != worker.getTeam()) {
            throw new IllegalArgumentException("Tried to check if a worker was trapped for a given player, but they" +
                    "aren't on the same team");
        }

        God currGod = player.getSelectedGod();
        PowerStatus prevStatus = currGod.getPowerStatus();
        if (prevStatus == PowerStatus.CAN_ACTIVATE) {
            currGod.setPowerStatus(PowerStatus.ACTIVE);
        }

        // Get current tile, neighbours, and possible moves given by the god
        BoardTile currTile = board.getTile(worker.getX(), worker.getY());
        List<BoardTile> neighbours = board.getAdjacentTiles(worker.getX(), worker.getY());
        List<BoardTile> godAccessibleTiles = currGod.getValidMoveLocations(worker, board, turnState);

        // For each neighbour, check if worker can move
        for (BoardTile neighbour : neighbours) {
            // Check if worker can move normally to tile
            if (neighbour.canWorkerMove(currTile)) {
                currGod.setPowerStatus(prevStatus);
                return false;
            }

            // Check if worker can move to tile with god power
            if (godAccessibleTiles.contains(neighbour)) {
                currGod.setPowerStatus(prevStatus);
                return false;
            }
        }
        // Revert god power status
        currGod.setPowerStatus(prevStatus);

        // Have now checked that workers are trapped, even if they activated god powers
        return true;
    }

    /**
     * Returns if worker can't build.
     * Implemented but not used. Can't imagine a situation where player moves but can't build, but if players do find
     * one, they can submit a bug report and get bragging rights.
     *
     * @return if worker can't build
     */
    public boolean workerCantBuild(Player player, Worker worker) {
        // Sanity check
        if (player.getTeam() != worker.getTeam()) {
            throw new IllegalArgumentException("Tried to check if a worker was trapped for a given player, but they" +
                    "aren't on the same team");
        }

        God currGod = player.getSelectedGod();
        PowerStatus prevStatus = currGod.getPowerStatus();
        if (prevStatus == PowerStatus.CAN_ACTIVATE) {
            currGod.setPowerStatus(PowerStatus.ACTIVE);
        }

        // Get current tile, neighbours, and possible moves given by the god
        List<BoardTile> neighbours = board.getAdjacentTiles(worker.getX(), worker.getY());
        List<BoardTile> godAccessibleTiles = currGod.getValidBuildLocations(worker, board, turnState);

        // For each neighbour, check if worker can move
        for (BoardTile neighbour : neighbours) {
            // Check if worker can build normally on tile
            if (neighbour.canWorkerBuild()) {
                currGod.setPowerStatus(prevStatus);
                return false;
            }

            // Check if worker can build on with god power
            if (godAccessibleTiles.contains(neighbour)) {
                currGod.setPowerStatus(prevStatus);
                return false;
            }
        }
        // Revert god power status
        currGod.setPowerStatus(prevStatus);

        // Have now checked that workers can't build, even if they activated god powers
        return true;
    }

    /**
     * Removes a team, in effect making them lose.
     * Removes players, gods, victory conditions, and workers.
     */
    public void removeTeam(Team badTeam) {
        // Removes team from teams
        List<Team> remainingTeams = new ArrayList<>(Arrays.stream(this.teams).toList());
        remainingTeams.removeIf(t -> t == badTeam);
        this.teams = remainingTeams.toArray(new Team[0]);

        // Remove gods and victory conditions
        turnState.removeTeam(badTeam);

        // Remove workers from board
        for (Worker worker : badTeam.getWorkers()) {
            board.emptyTile(worker.getX(), worker.getY());
        }

        // Remove workers
        badTeam.removeWorkers();

        // Find next player to play
        List<Player> allPlayers = Arrays.stream(this.players).toList();
        Player nextPlayer;

        // To help get index of next player by a bad circular search
        class Pair<T, U> {
            private final T first;
            private final U second;

            public Pair(T first, U second) {
                this.first = first;
                this.second = second;
            }

            public T getFirst() {
                return first;
            }

            public U getSecond() {
                return second;
            }
        }

        // Get first player after if they exist
        Optional<Player> afterFirst = allPlayers.subList(currentPlayerIndex+1, allPlayers.size())
                .stream()
                .filter(p -> p.getTeam() != badTeam)
                .findFirst();
        if (afterFirst.isPresent()) { nextPlayer = afterFirst.get(); }
        // If not after in the list, check before
        else {
            Optional<Player> beforeFirst = allPlayers.subList(0, currentPlayerIndex)
                    .stream()
                    .filter(p -> p.getTeam() != badTeam)
                    .findFirst();
            if (beforeFirst.isPresent()) { nextPlayer = beforeFirst.get(); }
            // If not before in the list, we just tried removing the only team in play
            else { throw new IllegalStateException("Model tried to remove a team, but they were already the last ones alive."); }
        }

        // Removes players in badTeam
        List<Player> remainingPlayers = new ArrayList<Player>(Arrays.stream(this.players).toList());
        remainingPlayers.removeIf(p -> p.getTeam() == badTeam);
        this.players = remainingPlayers.toArray(new Player[0]);

        // Update the current player index by finding intended next player
        this.currentPlayerIndex = Arrays.asList(this.players).indexOf(nextPlayer) - 1 % (this.players.length);
    }

    /**
     * Ends the current turn and advances to the next player.
     *
     * @return Updated TurnState
     */
    public void endTurn() {
        // Reset power statuses for next turn
        for (God god : turnState.getAllGods()) {
            god.resetPowerState();
        }

        // Determine next player
        int nextPlayerIndex = (currentPlayerIndex + 1) % players.length;
        Player nextPlayer = players[nextPlayerIndex];

        // Update current player index
        currentPlayerIndex = nextPlayerIndex;

        // Create end turn action (this is handled inside TurnState.gotoNextTurn)
        turnState.gotoNextTurn(nextPlayer);
    }

    /**
     * Gets the move count.
     *
     * @return Number of moves performed
     */
    public int getMoveCount() {
        return gameStats.getMoveCount();
    }


    /**
     * Gets the dome count.
     *
     * @return Number of domes built
     */
    public int getDomeCount() {
        return gameStats.getDomeCount();
    }

    /**
     * Gets the god power activation count.
     *
     * @return Number of times god powers were activated
     */
    public int getGodPowerActivationCount() {
        return gameStats.getGodPowerActivationCount();
    }
}