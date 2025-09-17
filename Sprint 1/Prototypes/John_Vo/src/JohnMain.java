// This structure is seen in some of the example code for
//   Java Swing tutorials.
//  e.g. https://docs.oracle.com/javase/tutorial/uiswing/examples/components/FrameDemoProject/src/components/FrameDemo.java

public class JohnMain {
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        String[] questions = {
                "Is this a question?",
                "Where does a general keep his armies?",
                "Where does the ting go?",
                "Sphynx cats are uniquely:",
                "Funny rat.",
        };
        String[][] answerOptions = {
                {"Yes", "No", "What?", "por que no los dos"},
                {"Under the sea", "Davy Jones' Locker", "This is plagiarism!", "Up his sleevies!"},
                {"Skrrrrrrrrah", "England is my city", "Oooo-eeee-uuuu", "what are these answers"},
                {"Ugly", "Naked", "Classy", "Funny"},
                {"Ratatouille", "Rickey Rat", "Mr. Boombastic", "KFC Employee of the Month"},
        };
        int[] correctAnswers = {
                1,
                2,
                0,
                3,
                2,
        };
        ImprobableQuiz quiz = new ImprobableQuiz(3, questions, answerOptions, correctAnswers);

        //Display the window.
        quiz.setSize(640, 360);
        quiz.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}