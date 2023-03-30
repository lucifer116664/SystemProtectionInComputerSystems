package Lab2;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class SubstitutionCipher {
    private JPanel panel;
    private JTextField msgTextField, resultTextField, keyTextField;
    private JButton performButton, clearButton;
    private JRadioButton encryptRadioButton, decryptRadioButton;

    public SubstitutionCipher() {
        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(encryptRadioButton);
        btnGroup.add(decryptRadioButton);
        encryptRadioButton.setSelected(true);

        performButton.addActionListener(e -> {
            resultTextField.setText("");
            try {
                Integer.parseInt(keyTextField.getText());

                if (encryptRadioButton.isSelected())
                    encrypt();
                else
                    decrypt();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Enter key using only integer numbers without spaces", "Wrong key entered!", JOptionPane.WARNING_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> {
            encryptRadioButton.setSelected(true);
            msgTextField.setText("");
            resultTextField.setText("");
            keyTextField.setText("");
        });
    }

    public void encrypt() {
        String text = msgTextField.getText();
        for(int i = 0; i < text.length() % keyTextField.getText().length(); i++)
            text += text.toCharArray()[i];

        for(int i = 0; i < text.length(); i += keyTextField.getText().length()) {
            char[] buffer = new char[keyTextField.getText().length()];

            for(int j = 0; j < buffer.length; j++) {
                buffer[Character.getNumericValue(keyTextField.getText().toCharArray()[j]) - 1] = text.toCharArray()[i + j];
            }

            resultTextField.setText(resultTextField.getText() + new String(buffer));
        }
    }

    public void decrypt() {
        try {
            String text = msgTextField.getText();

            for (int i = 0; i < text.length(); i += keyTextField.getText().length()) {
                char[] buffer = new char[keyTextField.getText().length()];

                for (int j = 0; j < buffer.length; j++) {
                    buffer[j] = text.toCharArray()[i + Character.getNumericValue(keyTextField.getText().toCharArray()[j]) - 1];
                }

                resultTextField.setText(resultTextField.getText() + new String(buffer));
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            resultTextField.setText("");
            JOptionPane.showMessageDialog(null, "You are trying to decrypt message that wasn't encrypted with current key", "Undecryptable message!", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Substitution encrypter and decrypter");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setContentPane(new SubstitutionCipher().panel);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        frame.setBounds(toolkit.getScreenSize().width / 2 - 150,toolkit.getScreenSize().height / 2 - 200,400,170);
    }
}
