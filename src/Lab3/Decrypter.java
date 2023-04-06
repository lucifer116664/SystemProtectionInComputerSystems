package Lab3;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
//WedontneednoeducationWedontneednothoughtcontrolNodarksarcasmintheclassroomTeacherleavethemkidsaloneHeyTeacherLeavethemkidsalone
public class Decrypter {
    private JPanel panel;
    private JTextField msgTextField, resultTextField;
    private JButton decryptButton;
    private JButton clearButton;
    private String encodedMessage;
    private final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    private final Map<Character, Double> alphabetPercentage = new LinkedHashMap<Character, Double>();
    private final Map<Character, Integer> alphabetNumOfCharInMessage = new LinkedHashMap<Character, Integer>();
    private final Map<Character, Double> alphabetPercentageOfCharInMessage = new LinkedHashMap<Character, Double>();

    public Decrypter() {
        enterAlphabet();

        decryptButton.addActionListener(a -> {
            if(msgTextField.getText().equals(""))
                JOptionPane.showMessageDialog(null, "You have not entered message to decrypt", "Empty message field!", JOptionPane.WARNING_MESSAGE);
            else {
                encodedMessage = msgTextField.getText();
                countCharactersInMsg();
                transformToPercentage();
                resultTextField.setText(decode(compare()));
            }
        });

        clearButton.addActionListener(e -> {
            msgTextField.setText("");
            resultTextField.setText("");
        });
    }

    private void enterAlphabet() {
        alphabetPercentage.put('a', 7.25);
        alphabetPercentage.put('b', 1.25);
        alphabetPercentage.put('c', 3.5);
        alphabetPercentage.put('d', 4.25);
        alphabetPercentage.put('e', 12.75);
        alphabetPercentage.put('f', 3.0);
        alphabetPercentage.put('g', 2.0);
        alphabetPercentage.put('h', 3.5);
        alphabetPercentage.put('i', 7.75);
        alphabetPercentage.put('j', 0.25);
        alphabetPercentage.put('k', 0.5);
        alphabetPercentage.put('l', 3.75);
        alphabetPercentage.put('m', 2.75);
        alphabetPercentage.put('n', 7.75);
        alphabetPercentage.put('o', 7.5);
        alphabetPercentage.put('p', 2.75);
        alphabetPercentage.put('q', 0.5);
        alphabetPercentage.put('r', 8.5);
        alphabetPercentage.put('s', 6.0);
        alphabetPercentage.put('t', 9.25);
        alphabetPercentage.put('u', 3.0);
        alphabetPercentage.put('v', 1.5);
        alphabetPercentage.put('w', 1.5);
        alphabetPercentage.put('x', 0.5);
        alphabetPercentage.put('y', 2.25);
        alphabetPercentage.put('z', 0.25);
    }

    private void countCharactersInMsg() {
        encodedMessage = encodedMessage.toLowerCase();
        char element;
        int index;
        int amountOfChar;
        for (int i = 0; i < 26; i++) {
            index = 0;
            amountOfChar = 0;
            element = alphabet.charAt(i);
            do {
                index = encodedMessage.indexOf(element, index);
                if (index != -1) {
                    amountOfChar++;
                    index++;
                }
            } while (index != -1);
            alphabetNumOfCharInMessage.put(element, amountOfChar);
        }
    }

    private void transformToPercentage() {
        double percent;
        for (int i = 0; i < 26; i++) {
            percent = (double) (alphabetNumOfCharInMessage.get(alphabet.charAt(i))) * 100.0 / (double) encodedMessage.length();
            percent = new BigDecimal(Double.toString(percent)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            alphabetPercentageOfCharInMessage.put(alphabet.charAt(i), percent);
        }
    }

    private int compare() {
        Double[] idealArray = new Double[26];
        Double[] idealArraySorted = new Double[26];
        Double[] myArray = new Double[26];
        Double[] myArraySorted = new Double[26];

        alphabetPercentage.values().toArray(idealArray);
        alphabetPercentage.values().toArray(idealArraySorted);
        Arrays.sort(idealArraySorted, Comparator.reverseOrder());

        alphabetPercentageOfCharInMessage.values().toArray(myArray);
        alphabetPercentageOfCharInMessage.values().toArray(myArraySorted);
        Arrays.sort(myArraySorted, Comparator.reverseOrder());

        int idealIndex = Arrays.asList(idealArray).indexOf(idealArraySorted[0]);

        int offset = 0;
        boolean possible = false;

        for (int i = 0; i < 7 &&  !possible; i++) {
            int count = 0;
            int index = Arrays.asList(myArray).indexOf(myArraySorted[i]);
            offset = Math.abs(index - idealIndex);
            for (int j = 0; j < 7; j++) {
                if (j != i) {
                    int indexOfElement = Arrays.asList(myArray).indexOf(myArraySorted[j]);
                    indexOfElement -= offset;
                    if (indexOfElement < 0) {
                        indexOfElement += 26;
                    }
                    if (idealArray[indexOfElement] > 7.0) {
                        count++;
                    }
                }
            }
            if (count >= 4) {
                possible = true;
            }
        }
        return offset;
    }

    private String decode(int offset) {
        String message = "";
        int realIndex;
        for (int i = 0; i < encodedMessage.length(); i++) {
            realIndex = (alphabet.indexOf(encodedMessage.charAt(i))) - offset;
            if (realIndex < 0) {
                realIndex += 26;
            }
            message += alphabet.charAt(realIndex);
        }

        return message;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Caesar decrypter");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setContentPane(new Decrypter().panel);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        frame.setBounds(toolkit.getScreenSize().width / 2 - 150,toolkit.getScreenSize().height / 2 - 200,800,250);
    }
}
