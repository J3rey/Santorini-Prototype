import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WrongAnswer implements ActionListener {

    private final ImprobableQuiz impQuiz;

    public WrongAnswer(ImprobableQuiz quiz) {
        impQuiz = quiz;
    }

    public void actionPerformed(ActionEvent e) {
        impQuiz.wrong();
    }
}
