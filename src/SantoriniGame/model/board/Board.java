package SantoriniGame.model.board;

import SantoriniGame.model.player.Worker;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private int boardSize;
    private BoardTile[][] tiles;

    public Board(int boardSize) {
        this.boardSize = boardSize;
        initializeTiles();
    }

    private void initializeTiles() {
        tiles = new BoardTile[boardSize][boardSize];
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                tiles[x][y] = new BoardTile(x, y, this);
            }
        }
    }

    public BoardTile getTile(int x, int y) {
        if (isValidCoordinate(x, y)) {
            return tiles[x][y];
        }
        return null;
    }

    public boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < boardSize && y >= 0 && y < boardSize;
    }

    public boolean areAdjacent(int x1, int y1, int x2, int y2) {
        if (!isValidCoordinate(x1, y1) || !isValidCoordinate(x2, y2)) {
            return false;
        }

        int xDiff = Math.abs(x1 - x2);
        int yDiff = Math.abs(y1 - y2);

        return (xDiff <= 1 && yDiff <= 1) && !(xDiff == 0 && yDiff == 0);
    }

    public List<BoardTile> getAdjacentTiles(int x, int y) {
        List<BoardTile> adjacentTiles = new ArrayList<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                int newX = x + dx;
                int newY = y + dy;

                if (isValidCoordinate(newX, newY)) {
                    adjacentTiles.add(tiles[newX][newY]);
                }
            }
        }

        return adjacentTiles;
    }

    public List<BoardTile> getValidMoveLocations(Worker worker) {
        List<BoardTile> validMoves = new ArrayList<>();
        BoardTile currentTile = getTile(worker.getX(), worker.getY());

        if (currentTile == null) return validMoves;

        for (BoardTile tile : getAdjacentTiles(worker.getX(), worker.getY())) {
            if (tile.canWorkerMove(currentTile)) {
                validMoves.add(tile);
            }
        }

        return validMoves;
    }

    public List<BoardTile> getValidBuildLocations(Worker worker) {
        List<BoardTile> validBuilds = new ArrayList<>();
        BoardTile workerTile = getTile(worker.getX(), worker.getY());

        if (workerTile == null) return validBuilds;

        for (BoardTile tile : getAdjacentTiles(worker.getX(), worker.getY())) {
            if (tile.isEmpty() && tile.canWorkerBuild()) {
                validBuilds.add(tile);
            }
        }

        return validBuilds;
    }

    public boolean isValidMove(Worker worker, int toX, int toY) {
        BoardTile currentTile = getTile(worker.getX(), worker.getY());
        BoardTile targetTile = getTile(toX, toY);

        if (currentTile == null || targetTile == null) return false;
        if (!areAdjacent(currentTile.getX(), currentTile.getY(), toX, toY)) return false;

        return targetTile.canWorkerMove(currentTile);
    }

    public boolean isValidBuild(Worker worker, int buildX, int buildY) {
        BoardTile workerTile = getTile(worker.getX(), worker.getY());
        BoardTile targetTile = getTile(buildX, buildY);

        if (workerTile == null || targetTile == null) return false;
        if (!areAdjacent(workerTile.getX(), workerTile.getY(), buildX, buildY)) return false;
        if (!targetTile.isEmpty()) return false;

        return targetTile.canWorkerBuild();
    }

    public int getBoardSize() {
        return boardSize;
    }

    /**
     * Removes the occupant at the tile located at (x, y).
     *
     * @param x
     * @param y
     */
    public void emptyTile(int x, int y) {
        getTile(x, y).removeOccupant();
    }
}
