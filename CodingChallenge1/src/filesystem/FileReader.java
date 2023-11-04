package filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileReader {
    public StringBuilder readFile(String filename) {
        if (filename == null || filename.length() == 0) {
            return null;
        }

        StringBuilder output = new StringBuilder("");

        File file = new File(filename);

        try (FileInputStream fis = new FileInputStream(file)) {
            int data;
            while ((data = fis.read()) != -1) {
                output.append((char) data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }
}
