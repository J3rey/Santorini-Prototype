import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CorrectAnswer implements ActionListener {

    private final ImprobableQuiz impQuiz;

    public CorrectAnswer(ImprobableQuiz quiz) {
        impQuiz = quiz;
    }

    public void actionPerformed(ActionEvent e) {
        impQuiz.correct();
    }
}
