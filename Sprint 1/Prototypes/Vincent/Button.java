import javax.swing.JButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Button extends JButton {

    public Button() {
        super("pls click then check terminal");
        setBackground(Color.white);
        setForeground(Color.black);
        setBorderPainted(false);
        setFocusPainted(false);
        addClickEvent();
    }

    private void addClickEvent() {

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Hi John");
            }
        });
    }
}