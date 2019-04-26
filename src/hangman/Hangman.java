
package hangman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Hangman extends JFrame {
    public static void main(String[] args) {
        new Hangman();
    }

    private JLabel commandTitle = new JLabel("Type in the word to guess"), knownWordLabel, wrongGuesses = new JLabel();
    private JTextField wordTextField = new JTextField(20);
    private BufferedImage hangmanImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
    private JPanel hangmanPanel, inputPanel;
    private String wordToGuess = null;
    private int numOfWrongGuesses = 0;
    private String wrongGuessesString = "Wrong Guesses: ", wordKnown = "";

    public Hangman() {
        setLayout(new BorderLayout());
        drawHangmanStand(hangmanImage);

        wordTextField.setToolTipText("Net Connection: Leave Blank for Random Word");

        inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.gridy = 1;
        inputPanel.add(commandTitle, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        inputPanel.add(wordTextField, gridBagConstraints);

        add(inputPanel);

        wordTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (wordToGuess == null) {
                    wordToGuess = wordTextField.getText().toLowerCase();
                    if (wordToGuess.length() <= 0) {
                        try {
                            URL url = new URL("http://web.stanford.edu/class/cs106l/assignments/dictionary.txt");
                            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                            String str;
                            List<String> dictionary = new ArrayList();
                            while ((str = in.readLine()) != null) {
                                dictionary.add(str);
                            }
                            in.close();
                            wordToGuess = dictionary.get( (int) (dictionary.size()*Math.random()) );
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    wordTextField.setText("");
                    commandTitle.setText("Guess a letter");
                    String displayWord = "";
                    for (int i = 0; i < wordToGuess.length(); i++) {
                        wordKnown += "_";
                        displayWord += " _ ";
                    }
                    knownWordLabel = new JLabel(displayWord);
                    GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
                    gridBagConstraints1.gridx = 1;
                    gridBagConstraints1.gridy = 2;
                    gridBagConstraints1.gridwidth = 2;
                    inputPanel.add(knownWordLabel, gridBagConstraints1);
                    gridBagConstraints1 = new GridBagConstraints();
                    gridBagConstraints1.gridx = 1;
                    gridBagConstraints1.gridy = 3;
                    gridBagConstraints1.gridwidth = 2;
                    inputPanel.add(wrongGuesses, gridBagConstraints1);

                    return;
                }

                if (wordToGuess.indexOf(wordTextField.getText()) >= 0) {
                    guessRight();
                } else {
                    guessWrong();
                    wordTextField.setText("");
                }


            }
        });

        setSize(1000, 600);
        setVisible(true);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        showChangedHangman();
        revalidate();

    }

    private void guessRight() {
        String guess = wordTextField.getText().toLowerCase();
        addGuessToKnownWord(guess);
        String displayString = "";
        for (int i = 0; i < wordKnown.length(); i++) {
            displayString += wordKnown.substring(i, i + 1) + " ";
        }
        knownWordLabel.setText(displayString);

        if (wordKnown.indexOf("_") < 0) {
            JOptionPane.showMessageDialog(this, "You Win!");
        }
        wordTextField.setText("");
    }

    private void addGuessToKnownWord(String guess) {
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int index = wordToGuess.indexOf(guess);
             index >= 0;
             index = wordToGuess.indexOf(guess, index + 1)) {
            indexes.add(index);
        }
        for (int i = 0; i < indexes.size(); i++) {
            int index = indexes.get(i);
            StringBuilder stringBuilder = new StringBuilder(wordKnown);
            stringBuilder.replace(index, index + guess.length(), guess);
            wordKnown = stringBuilder.toString();
        }
    }

    private void guessWrong() {
        numOfWrongGuesses++;

        wrongGuessesString += wordTextField.getText() + ", ";
        wrongGuesses.setText(wrongGuessesString);
        Graphics2D g = (Graphics2D) hangmanImage.getGraphics();
        int x = 250, y = 200;
        g.setStroke(new BasicStroke(2));
        g.setColor(Color.BLACK);

        switch (numOfWrongGuesses) {
            case 1: // Head
                g.drawOval(-20 + x, y, 40, 40);
                break;
            case 2: // Body
                g.drawLine(x, y + 40, x, y + 40 + 80);
                break;
            case 3: // R Arm
                g.drawLine(x, y + 40 + 20, x + 20, y + 40 + 60);
                break;
            case 4: // L Arm
                g.drawLine(x, y + 40 + 20, x - 20, y + 40 + 60);
                break;
            case 5: // R Leg
                g.drawLine(x, y + 40 + 80, x + 20, y + 40 + 80 + 40);
                break;
            case 6: // L Leg
                g.drawLine(x, y + 40 + 80, x - 20, y + 40 + 80 + 40);
                break;
            default:
                JOptionPane.showMessageDialog(this, "You Lose!");
                break;
        }
        g.dispose();
        showChangedHangman();
        revalidate();
    }

    private void showChangedHangman() {
        if (hangmanPanel != null)
            remove(hangmanPanel);

        hangmanPanel = new JPanel();
        hangmanPanel.add(new JLabel(new ImageIcon(hangmanImage)));
        add(hangmanPanel, BorderLayout.WEST);
        revalidate();

    }

    private static void drawHangmanStand(BufferedImage image) {
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setStroke(new BasicStroke(2));
        g.setColor(Color.BLACK);

        g.drawLine(10, 475, 250, 475);
        g.drawLine(100, 475, 100, 100);
        g.drawLine(100, 100, 250, 100);
        g.drawLine(250, 100, 250, 200);

        g.dispose();
    }
}