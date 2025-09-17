import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResetToStart implements ActionListener {

    private final ImprobableQuiz impQuiz;

    public ResetToStart(ImprobableQuiz quiz) {
        impQuiz = quiz;
    }

    public void actionPerformed(ActionEvent e) {
        impQuiz.reset();
    }
}