import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    private final int[][] table;

    public Main() {
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

    public void encrypt(String filename, String key) throws IOException {
        int n = key.length();
        key = key.toUpperCase();

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        BufferedWriter writer = new BufferedWriter(new FileWriter("CipherText.txt"));

        int count = 0;
        int ch;
        while ((ch = reader.read()) != -1) {
            char character = (char) ch;
            if (Character.isUpperCase(character)) {
                writer.write((char) (table[key.charAt(count) - 'A'][character - 'A'] + 'A'));
            } else if (Character.isLowerCase(character)) {
                writer.write((char) (table[key.charAt(count) - 'A'][character - 'a' + 26] + 'a'));
            } else {
                writer.write(character);
            }

            count++;
            if (count == n) {
                count = 0;
            }
        }

        reader.close();
        writer.close();
    }

    public void decrypt(String key) throws IOException {
        key = key.toUpperCase();

        BufferedReader reader = new BufferedReader(new FileReader("CipherText.txt"));
        BufferedWriter writer = new BufferedWriter(new FileWriter("DecryptedText.txt"));

        int count = 0;
        int ch;
        while ((ch = reader.read()) != -1) {
            char character = (char) ch;
            if (Character.isUpperCase(character)) {
                for (int i = 0; i < 26; i++) {
                    if (table[key.charAt(count) - 'A'][i] == character - 'A') {
                        writer.write((char) (i + 'A'));
                        break;
                    }
                }
            } else if (Character.isLowerCase(character)) {
                for (int i = 26; i < 52; i++) {
                    if (table[key.charAt(count) - 'A'][i] == character - 'a') {
                        writer.write((char) (i + 'a' - 26));
                        break;
                    }
                }
            } else {
                writer.write(character);
            }

            count++;
            if (count == key.length()) {
                count = 0;
            }
        }

        reader.close();
        writer.close();
    }

    public static void main(String[] args) {
        String filename = "PlainText.txt";
        String key = "LEMONLEMONLE";

        Main cipher = new Main();

        try {
            cipher.encrypt(filename, key);
            cipher.decrypt(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}