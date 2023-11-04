import parser.JSONParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        JSONParser jsonParser = new JSONParser();

        File file = new File("test/pass1.json");

        StringBuilder jsonContent = new StringBuilder("");

        try (FileInputStream fis = new FileInputStream(file)) {
            int data;
            while ((data = fis.read()) != -1) {
                jsonContent.append((char) data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(jsonParser.isValidJSON(jsonContent.toString()));
    }
}