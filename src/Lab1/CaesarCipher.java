package Lab1;

import javax.swing.*;
import java.awt.*;

public class CaesarCipher {
    private JPanel panel;
    private JTextField stepTextField, messageTextField, encryptedMsgTextField;
    private JButton encryptButton;
    private JButton clearButton;
    private JComboBox<String> signComboBox;
    private final String ALPHABET = "абвгґдеєжзиіїйклмнопрстуфхцчшщьюя";
    public CaesarCipher() {
        signComboBox.addItem("+");
        signComboBox.addItem("-");

        encryptButton.addActionListener(e -> {
            encryptedMsgTextField.setText("");
            try {
                int step = Integer.parseInt(stepTextField.getText());
                for (int i = 0; i < messageTextField.getText().length(); i++) {
                    int index = ALPHABET.indexOf(messageTextField.getText().toCharArray()[i]);
                    if (signComboBox.getSelectedItem() == "+") {
                        /*for (int j = 0; j < step; j++) {
                            index++;
                            if (index == 33)
                                index = 0;
                        }*/
                        index += step;
                        index %= 33;
                        encryptedMsgTextField.setText(encryptedMsgTextField.getText() + ALPHABET.toCharArray()[index]);
                    } else {
                        /*for (int j = 0; j < step; j++) {
                            index--;
                            if (index == -1)
                                index = 32;
                        }*/
                        index -= step;
                        index %= 33;
                        if(index < 0)
                            index += ALPHABET.length();
                        encryptedMsgTextField.setText(encryptedMsgTextField.getText() + ALPHABET.toCharArray()[index]);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "You can enter only integer numbers in \"Step\" text field", "Wrong step entered", JOptionPane.WARNING_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> {
            signComboBox.setSelectedIndex(0);
            stepTextField.setText("");
            messageTextField.setText("");
            encryptedMsgTextField.setText("");
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Caesar encrypter");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setContentPane(new CaesarCipher().panel);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        frame.setBounds(toolkit.getScreenSize().width / 2 - 150,toolkit.getScreenSize().height / 2 - 200,400,150);
    }
}
