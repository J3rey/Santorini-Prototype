package SantoriniGame.model.player;

/**
 * Worker class representing a game piece on the board.
 */
public class Worker {
    private final WorkerGender gender;
    private int x; // x-coordinate on the grid
    private int y; // y-coordinate on the grid
    private final Team team;
    private boolean selected;

    /**
     * Creates a new worker.
     * @param gender the worker's gender
     * @param team the team this worker belongs to
     * @param x initial x-coordinate
     * @param y initial y-coordinate
     */
    public Worker(WorkerGender gender, Team team, int x, int y) {
        this.gender = gender;
        this.team = team;
        this.x = x;
        this.y = y;
        this.selected = false;
    }

    /**
     * Gets the worker's gender.
     * @return the worker gender
     */
    public WorkerGender getGender() {
        return gender;
    }

    /**
     * Gets the worker's x-coordinate.
     * @return the x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the worker's y-coordinate.
     * @return the y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the worker's team.
     * @return the team
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Checks if the worker is selected.
     * @return true if selected, false otherwise
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets the worker's selection status.
     * @param selected true to select, false to deselect
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Moves the worker to new coordinates.
     * @param newX the new x-coordinate
     * @param newY the new y-coordinate
     */
    public void moveTo(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }
}