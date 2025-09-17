import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartQuiz implements ActionListener {

    private final ImprobableQuiz impQuiz;

    public StartQuiz(ImprobableQuiz quiz) {
        impQuiz = quiz;
    }

    public void actionPerformed(ActionEvent e) {
        impQuiz.startQuiz();
    }
}
