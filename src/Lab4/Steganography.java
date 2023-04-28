package Lab4;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;

public class Steganography {
    private JPanel panel;
    private JTabbedPane tabbedPane;
    private JButton chooseClearImageButton, chooseTxtWithMessageButton, chooseOutputImageLocationButton, encryptButton;
    private JCheckBox clearImageChosenCheckBox, txtChosenCheckBox, outputImageChosenCheckBox;
    private JButton chooseEncryptedImageButton, chooseOutputTxtLocationButton, decryptButton;
    private JCheckBox encryptedImageChosenCheckBox, outputTxtChosenCheckBox;
    private JTextField charsToReadTextField;
    private File img, msg, encodedImg, decodedMsg;
    private static final int DEGREE = 4;

    public Steganography() {
        chooseClearImageButton.addActionListener(e -> img = chooseImage(clearImageChosenCheckBox));
        chooseTxtWithMessageButton.addActionListener(e -> msg = chooseTxt(txtChosenCheckBox));
        chooseOutputImageLocationButton.addActionListener(e -> encodedImg = chooseOutput("/encryptedImage.bmp", outputImageChosenCheckBox));
        encryptButton.addActionListener(e -> encrypt());

        chooseEncryptedImageButton.addActionListener(e -> encodedImg = chooseImage(encryptedImageChosenCheckBox));
        chooseOutputTxtLocationButton.addActionListener(e -> decodedMsg = chooseOutput("/decryptedMessage.txt", outputTxtChosenCheckBox));
        decryptButton.addActionListener(e -> decrypt());
    }

    private File chooseImage(JCheckBox checkBox) {
        File file = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("BMP image (.bmp)", "bmp"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        int result = fileChooser.showDialog(null, "Choose");
        if(result == JFileChooser.APPROVE_OPTION) {
            file = new File(fileChooser.getSelectedFile().getAbsolutePath());
            checkBox.setSelected(true);
        }
        return file;
    }

    private File chooseTxt(JCheckBox checkBox) {
        File file = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text file (.txt)", "txt"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        int result = fileChooser.showDialog(null, "Choose");
        if(result == JFileChooser.APPROVE_OPTION) {
            file = new File(fileChooser.getSelectedFile().getAbsolutePath());
            checkBox.setSelected(true);
        }
        return file;
    }

    private File chooseOutput(String fileName, JCheckBox checkBox) {
        File file = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showDialog(null, "Choose");
        if(result == JFileChooser.APPROVE_OPTION) {
            file = new File(fileChooser.getSelectedFile().getAbsolutePath() + fileName);
            checkBox.setSelected(true);
        }
        return file;
    }

    private void encrypt(){
        if(clearImageChosenCheckBox.isSelected() & txtChosenCheckBox.isSelected() & outputImageChosenCheckBox.isSelected()) {
            try(FileInputStream imgIn = new FileInputStream(img);
                FileInputStream msgIn = new FileInputStream(msg);
                FileOutputStream imgOut = new FileOutputStream(encodedImg)) {

                if (msg.length() > img.length() * DEGREE / 8 - 54) {
                    throw new IllegalArgumentException();
                }

                byte[] bmpHeader = new byte[54];
                imgIn.read(bmpHeader, 0, 54);

                for (byte b : bmpHeader) {
                    imgOut.write(b);
                }

                int textMask = createTextMask();
                int imgMask = createImgMaskEncrypt();
                int nextChar = msgIn.read();
                while (nextChar != -1) {
                    int imageByte = 0;
                    int codePoint = nextChar;
                    for (int i = 0; i < 8; i += DEGREE) {
                        byte[] buffer = new byte[1];
                        int numBytesRead = imgIn.read(buffer);
                        if (numBytesRead > 0) {
                            imageByte = (buffer[0] & 0xff) & imgMask;
                        }
                        int bits = codePoint & textMask;
                        bits >>= (8 - DEGREE);
                        imageByte |= bits;
                        imgOut.write(imageByte);
                        codePoint <<= DEGREE;
                    }
                    nextChar = msgIn.read();
                }
                byte[] remainingBytes = new byte[imgIn.available()];
                imgIn.read(remainingBytes);
                imgOut.write(remainingBytes);

                clearImageChosenCheckBox.setSelected(false);
                txtChosenCheckBox.setSelected(false);
                outputImageChosenCheckBox.setSelected(false);

                JOptionPane.showMessageDialog(null, "Message was successfully hidden into image", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, "Image file is too small to hide so many information inside", "Too long message", JOptionPane.WARNING_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "You have to choose all files locations above", "Choose all file locations", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void decrypt() {
        if(encryptedImageChosenCheckBox.isSelected() & outputTxtChosenCheckBox.isSelected()) {
            try(RandomAccessFile encodedImageIn = new RandomAccessFile(encodedImg, "rw");
                FileOutputStream msgOut = new FileOutputStream(decodedMsg)) {
                int charsToRead = Integer.parseInt(charsToReadTextField.getText());

                encodedImageIn.seek(54);
                int image_mask = createImgMaskDecrypt();
                for (int readed = 0; readed < charsToRead; readed++) {
                    int symbol = 0;
                    int imageByte = 0;
                    for (int i = 0; i < 8; i += DEGREE) {
                        byte[] buffer = new byte[1];
                        int numBytesRead = encodedImageIn.read(buffer);
                        if (numBytesRead > 0) {
                            imageByte = (buffer[0] & 0xff) & image_mask;
                        }
                        symbol <<= DEGREE;
                        symbol %= 256;
                        symbol |= imageByte;
                    }
                    msgOut.write((char) symbol);
                }

                encryptedImageChosenCheckBox.setSelected(false);
                outputTxtChosenCheckBox.setSelected(false);

                JOptionPane.showMessageDialog(null, "Message was successfully written from image", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "You can enter only integer numbers to \"Symbols to read\" text field", "Wrong symbols to read value", JOptionPane.WARNING_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "You have to choose all files locations above", "Choose all file locations", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static int createImgMaskDecrypt() {
        int imageMask = 0b11111111;
        imageMask >>= (8 - DEGREE);
        return imageMask;
    }

    private static int createImgMaskEncrypt() {
        int imageMask = 0b11111111;
        imageMask >>= DEGREE;
        imageMask <<= DEGREE;
        return imageMask;
    }

    private static int createTextMask() {
        int textMask = 0b11111111;
        textMask <<= (8 - DEGREE);
        textMask %= 256;
        return textMask;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Steganographer");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setContentPane(new Steganography().panel);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        frame.setBounds(toolkit.getScreenSize().width / 2 - 190,toolkit.getScreenSize().height / 2 - 210,380,420);
    }
}
