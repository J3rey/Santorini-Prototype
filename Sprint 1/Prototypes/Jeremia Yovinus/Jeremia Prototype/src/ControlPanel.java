import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    public ControlPanel(GamePanel gamePanel) {
        setLayout(new GridLayout(2, 3));

        JButton upButton = new JButton("Up");
        JButton downButton = new JButton("Down");
        JButton leftButton = new JButton("Left");
        JButton rightButton = new JButton("Right");

        add(new JLabel());
        add(upButton);
        add(new JLabel());
        add(leftButton);
        add(downButton);
        add(rightButton);

        upButton.addActionListener(e -> gamePanel.moveUp());
        downButton.addActionListener(e -> gamePanel.moveDown());
        leftButton.addActionListener(e -> gamePanel.moveLeft());
        rightButton.addActionListener(e -> gamePanel.moveRight());
    }
}