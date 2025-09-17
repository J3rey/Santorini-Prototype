import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private final int gridSize = 5;
    private final int cellSize = 60;
    private int circleRow = 2;
    private int circleCol = 2;

    public GamePanel() {
        setPreferredSize(new Dimension(gridSize * cellSize, gridSize * cellSize));
        setBackground(Color.BLACK);
    }

    public void moveUp() {
        if (circleRow > 0) circleRow--;
        repaint();
    }

    public void moveDown() {
        if (circleRow < gridSize - 1) circleRow++;
        repaint();
    }

    public void moveLeft() {
        if (circleCol > 0) circleCol--;
        repaint();
    }

    public void moveRight() {
        if (circleCol < gridSize - 1) circleCol++;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);

        // Draw grid
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                g.drawRect(j * cellSize, i * cellSize, cellSize, cellSize);
            }
        }

        // Draw circle
        g.setColor(Color.GREEN);
        g.fillOval(circleCol * cellSize + 10, circleRow * cellSize + 10, cellSize - 20, cellSize - 20);
    }
}