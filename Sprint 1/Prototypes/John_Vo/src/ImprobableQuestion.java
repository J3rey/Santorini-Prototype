import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ImprobableQuestion extends JPanel {

    public ImprobableQuestion(String qPrompt, String[] qOpts, int qCorrOpt, ActionListener correctL,
                              ActionListener wrongL) {
        // Make a panel for the Q and A's
        // - Will be box layout, top is Q, bottom is A's
        super();
        this.setLayout(new BorderLayout());

        // Create the question prompt as un-editable text
        JLabel qPanel = new JLabel(qPrompt);

        // Create 4 boxes with the options
        // - Also add actions to each (e.g. take a life or answer Q correctly)
        JButton[] optionBtns = new JButton[4];
        for(int i = 0; i < qOpts.length; i++) {
            optionBtns[i] = newOptionBtn(qOpts[i], (i == qCorrOpt), correctL, wrongL);
        }

        // Create a panel for option buttons
        // - Grid layout with 2 rows and 2 cols
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridLayout(2, 2));
        for (JButton optionBtn : optionBtns) {
            btnPanel.add(optionBtn);
        }

        // Add questions and options
        this.add(qPanel, BorderLayout.PAGE_START);
        this.add(btnPanel, BorderLayout.CENTER);
    }

    private JButton newOptionBtn(String option, boolean isCorrect, ActionListener correctL, ActionListener wrongL) {
        JButton optionBtn = new JButton(option);
        optionBtn.setBorder(BorderFactory.createLineBorder(Color.black));
        // Add a listener dep. on if correct or not
        if (isCorrect) {
            optionBtn.addActionListener(correctL);
        }
        else {
            optionBtn.addActionListener(wrongL);
        }
        return optionBtn;
    }
}
