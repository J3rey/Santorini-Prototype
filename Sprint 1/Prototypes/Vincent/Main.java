// Main.java
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Run GUI for Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        // Create Frame, take a look at Swing dev docs for full methods
        JFrame frame = new JFrame("Button Executable");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.getContentPane().setBackground(Color.white);

        Button button = new Button();

        frame.getContentPane().add(button);


        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}