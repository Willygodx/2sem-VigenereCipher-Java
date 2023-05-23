import java.util.*;
import java.util.logging.*;
import java.io.*;

public class Cipher {
    private final int[][] table;
    private static Logger logger;
    static {
        try(FileInputStream inputStream = new FileInputStream("logConfig.config")){
            LogManager.getLogManager().readConfiguration(inputStream);
            logger = Logger.getLogger(Cipher.class.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Cipher() {
        table = new int[26][52];
        initMatrix();
    }

    private void initMatrix() {
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 26 - i; j++) {
                table[i][j] = j + i;
            }

            for (int j = 26 - i; j < 26; j++) {
                table[i][j] = (j + i) - 26;
            }

            for (int j = 26; j < 52 - i; j++) {
                table[i][j] = j + i - 26;
            }

            for (int j = 52 - i; j < 52; j++) {
                table[i][j] = (j + i) - 52;
            }
        }
    }

    public void encryptionText(String fileName, String textToEncrypt, String keyWordEncrypt) throws IOException {
        int keyLength = keyWordEncrypt.length();
        keyWordEncrypt = keyWordEncrypt.toUpperCase();

        int counter = 0;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < textToEncrypt.length(); i++) {
                char character = textToEncrypt.charAt(i);
                if (Character.isUpperCase(character)) {
                    writer.append((char) (table[keyWordEncrypt.charAt(counter) - 'A'][character - 'A'] + 'A'));
                } else if (Character.isLowerCase(character)) {
                    writer.append((char) (table[keyWordEncrypt.charAt(counter) - 'A'][character - 'a' + 26] + 'a'));
                } else {
                    writer.append(character);
                }
                counter++;
                if (counter == keyLength) {
                    counter = 0;
                }
            }
        }
    }

    public void decryptionText(String keyWordEncrypt) throws IOException {
        keyWordEncrypt = keyWordEncrypt.toUpperCase();

        try (BufferedReader reader = new BufferedReader(new FileReader("CipherText.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("DecryptedText.txt"))) {

            int counter = 0;
            int symbol;
            while ((symbol = reader.read()) != -1) {
                char character = (char) symbol;
                char decryptedChar = decipherCharacter(character, keyWordEncrypt.charAt(counter));
                writer.write(decryptedChar);
                counter++;
                if (counter == keyWordEncrypt.length()) {
                    counter = 0;
                }
            }
        }
    }

    private char decipherCharacter(char character, char charFromKey) {
        if (Character.isUpperCase(character)) {
            for (int i = 0; i < 26; i++) {
                if (table[charFromKey - 'A'][i] == character - 'A') {
                    return (char) (i + 'A');
                }
            }
        } else if (Character.isLowerCase(character)) {
            for (int i = 26; i < 52; i++) {
                if (table[charFromKey - 'A'][i] == character - 'a') {
                    return (char) (i + 'a' - 26);
                }
            }
        }
        return character;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String fileName = "CipherText.txt";
        String textToEncrypt = checkInput(scanner, "Input TEXT you want to encrypt! ");
        String keyWordEncrypt = checkInput(scanner, "Input KEYWORD for encryption! ");

        Cipher cipher = new Cipher();

        try {
            cipher.encryptionText(fileName, textToEncrypt, keyWordEncrypt);
            cipher.decryptionText(keyWordEncrypt);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String checkInput(Scanner scanner, String message) {
        String input;

        do {
            logger.log(Level.INFO, message);
            input = scanner.nextLine();
        } while (!input.matches("[a-zA-Z]+"));

        return input;
    }
}