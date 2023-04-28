package Lab5;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Des {
    private JTabbedPane tabbedPane;
    private JPanel mainPanel;
    private JButton chooseMessageButton, chooseEncryptOutputLocationButton, encryptButton;
    private JCheckBox msgChosenCheckBox, encryptOutputLocationChosenCheckBox;
    private JButton chooseEncryptedMessageButton, chooseTheKeyButton, chooseDecryptOutputLocationButton, decryptButton;
    private JCheckBox encryptedMsgChosenCheckBox, keyChosenCheckBox, decryptOutputLocationChosenCheckBox;
    private File msgTxt, keyTxt;
    private String outputLocation;

    public Des() {
        chooseMessageButton.addActionListener(e -> msgTxt = chooseFile(msgChosenCheckBox, "Text files (.txt)", "txt"));
        chooseEncryptOutputLocationButton.addActionListener(e -> outputLocation = chooseOutputLocation(encryptOutputLocationChosenCheckBox));
        encryptButton.addActionListener(e -> encrypt());

        chooseEncryptedMessageButton.addActionListener(e -> msgTxt = chooseFile(encryptedMsgChosenCheckBox, "Text files (.txt)", "txt"));
        chooseTheKeyButton.addActionListener(e -> keyTxt = chooseFile(keyChosenCheckBox, "Serialized key (.ser)", "ser"));
        chooseDecryptOutputLocationButton.addActionListener(e -> outputLocation = chooseOutputLocation(decryptOutputLocationChosenCheckBox));
        decryptButton.addActionListener(e -> decrypt());

    }

    public File chooseFile(JCheckBox checkBox, String descryption, String extention) {
        File file = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(descryption, extention));
        fileChooser.setAcceptAllFileFilterUsed(false);
        int result = fileChooser.showDialog(null, "Choose");
        if(result == JFileChooser.APPROVE_OPTION) {
            file = new File(fileChooser.getSelectedFile().getAbsolutePath());
            checkBox.setSelected(true);
        }
        return file;
    }

    public String chooseOutputLocation(JCheckBox checkBox) {
        String location = "";
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showDialog(null, "Choose");
        if (result == JFileChooser.APPROVE_OPTION) {
            location = fileChooser.getSelectedFile().getAbsolutePath();
            checkBox.setSelected(true);
        }
        return location;
    }

    public void encrypt() {
        if(msgChosenCheckBox.isSelected() & encryptOutputLocationChosenCheckBox.isSelected()) {
            try (FileOutputStream msgOut = new FileOutputStream(outputLocation + "/encryptedMsg.txt");
                 ObjectOutputStream keyOut = new ObjectOutputStream(new FileOutputStream(outputLocation + "/encryptionKey.ser"))) {

                byte[] message = Files.readAllBytes(Paths.get(msgTxt.getAbsolutePath()));

                SecretKey key = KeyGenerator.getInstance("DES").generateKey();
                keyOut.writeObject(key);

                Cipher desCipher = Cipher.getInstance("DES");

                desCipher.init(Cipher.ENCRYPT_MODE, key);
                byte[] encryptedMsg = desCipher.doFinal(message);

                msgOut.write(encryptedMsg);
                msgOut.flush();

                msgChosenCheckBox.setSelected(false);
                encryptOutputLocationChosenCheckBox.setSelected(false);
                JOptionPane.showMessageDialog(null, "Message was successfully encrypted", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e.getStackTrace(), "ERROR!!!", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "You have to choose all files locations above", "Choose all file locations", JOptionPane.WARNING_MESSAGE);
        }
    }
    public void decrypt() {
        if(encryptedMsgChosenCheckBox.isSelected() & keyChosenCheckBox.isSelected() & decryptOutputLocationChosenCheckBox.isSelected()) {
            try (ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream(keyTxt.getAbsolutePath()));
                 FileOutputStream msgOut = new FileOutputStream(outputLocation + "/decryptedMsg.txt")) {


                byte[] message = Files.readAllBytes(Paths.get(msgTxt.getAbsolutePath()));

                SecretKey key = (SecretKey) keyIn.readObject();

                Cipher desCipher = Cipher.getInstance("DES");

                desCipher.init(Cipher.DECRYPT_MODE, key);
                byte[] decryptedMsg = desCipher.doFinal(message);

                msgOut.write(decryptedMsg);
                msgOut.flush();

                encryptedMsgChosenCheckBox.setSelected(false);
                keyChosenCheckBox.setSelected(false);
                decryptOutputLocationChosenCheckBox.setSelected(false);
                JOptionPane.showMessageDialog(null, "Message was successfully decrypted", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e.getStackTrace(), "ERROR!!!", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "You have to choose all files locations above", "Choose all file locations", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("DES app");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setContentPane(new Des().mainPanel);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        frame.setBounds(toolkit.getScreenSize().width / 2 - 250, toolkit.getScreenSize().height / 2 - 150, 500, 300);
    }
}
