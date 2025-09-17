import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.plaf.ColorUIResource;


public class Main {
    public static void main(String[] args) {

        JLabel label = new JLabel();
        label.setText("'Winds of Winter' when?");
        label.setForeground(ColorUIResource.BLUE);

        JFrame frame = new JFrame();
        frame.setTitle("Help me!");
        frame.setSize(400, 420);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.add(label);
    }
}