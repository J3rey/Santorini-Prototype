import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ImprobableQuiz extends JFrame {

    private int lives;
    private final JPanel startPanel;
    private final JPanel deathPanel;
    private final JPanel victoryPanel;

    private final ImprobableQuestion[] questions;
    private int currQuestionIndex;
    private final JPanel statsPanel;
    private JLabel lifeLabel;

    public ImprobableQuiz(int l, String[] qPrompts, String[][] options, int[] correctAnswers) {
        super();
        lives = l;

        startPanel = createStartPanel();
        deathPanel = createDeathPanel();
        victoryPanel = createVictoryPanel();
        statsPanel = createStatsPanel();

        // Create the event listeners to alter this quiz
        // - There's a circular dependency? Might be fine since stored
        //   as ActionListener (need to do some FIT2099 revision lol)
        ActionListener correctL = new CorrectAnswer(this);
        ActionListener wrongL = new WrongAnswer(this);

        // Create the questions and store them
        if(qPrompts.length != options.length || qPrompts.length != correctAnswers.length) {
            throw new IllegalArgumentException("Unequal number of prompts/options/correct answer indices");
        }
        ImprobableQuestion[] qs = new ImprobableQuestion[qPrompts.length];
        for(int i = 0; i < qPrompts.length; i++) {
            qs[i] = new ImprobableQuestion(qPrompts[i], options[i], correctAnswers[i], correctL, wrongL);
        }
        questions = qs;

        // Set start screen to appear and other initialising stuff
        this.setContentPane(this.startPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }



    private void gotoPanel(JPanel panel) {
        // Taken from - https://stackoverflow.com/a/30369760
        // - Changes what's shown to the given panel
        this.setContentPane(panel); //panel = panel you want to change too.
        this.repaint();             //Ensures that the frame swaps to the next panel and doesn't get stuck.
        this.revalidate();
    }

    private void gotoQuestion(int questionIndex) {
        // If next question doesn't exist, end the quiz (you won)
        if (questionIndex >= questions.length) {
            gotoPanel(victoryPanel);
            return;
        }

        // Else, goto that question
        ImprobableQuestion question = questions[questionIndex];

        // Whole pane is question on top then stats on bottom
        JPanel questionAndStats = new JPanel();
        questionAndStats.setLayout(new BorderLayout());

        // Add question then stat panel
        questionAndStats.add(question, BorderLayout.CENTER);
        questionAndStats.add(this.statsPanel, BorderLayout.PAGE_END);
        gotoPanel(questionAndStats);
    }



    public void wrong() {
        lives -= 1;
        lifeLabel.setText("Lives: " + lives);
        if (lives <= 0) {
            gotoPanel(this.deathPanel);
        }
    }

    public void correct() {
        // Goto next question
        currQuestionIndex += 1;
        gotoQuestion(currQuestionIndex);
    }

    public void reset() {
        lives = 3;
        currQuestionIndex = 0;
        lifeLabel.setText("Lives: " + lives);
        gotoPanel(this.startPanel);
    }

    public void startQuiz() {
        gotoQuestion(0);
    }



    // Creates start panel, like a main menu
    private JPanel createStartPanel() {
        JPanel start = new JPanel();
        start.setLayout(new BorderLayout());

        // Add title
        JLabel title = new JLabel("<html><font size=+2>The Improbable Quiz</font></html>");

        JButton startBtn = new JButton("Start");
        startBtn.addActionListener(new StartQuiz(this));

        start.add(title, BorderLayout.CENTER);
        start.add(startBtn, BorderLayout.PAGE_END);

        return start;
    }

    // Panel to show when dead, returns to start
    private JPanel createDeathPanel() {
        // Create title and text
        JLabel title = new JLabel("<html><font color=red size=+2>You died</font></html>");
        JLabel text = new JLabel("skill issue");

        return createEndPanel(title, text);
    }

    // Panel to show when chad, returns to start
    private JPanel createVictoryPanel() {
        // Create title and text
        JLabel title = new JLabel("<html><font color=green size=+2>You won!</font></html>");
        JLabel text = new JLabel("good job surviving this 'we have Impossible Quiz at home' game");

        return createEndPanel(title, text);
    }

    private JPanel createEndPanel(JLabel title, JLabel text) {
        JPanel endPanel = new JPanel();
        endPanel.setLayout(new BorderLayout());

        JButton restartBtn = new JButton("Return to start");
        restartBtn.addActionListener(new ResetToStart(this));

        endPanel.add(title, BorderLayout.PAGE_START);
        endPanel.add(text, BorderLayout.CENTER);
        endPanel.add(restartBtn, BorderLayout.PAGE_END);

        return endPanel;
    }

    // Panel along the bottom to show lives and score
    private JPanel createStatsPanel() {
        JPanel statPanel = new JPanel();

        JLabel stat = new JLabel("Lives: " + lives);

        statPanel.add(stat);

        this.lifeLabel = stat;
        return statPanel;
    }
}
