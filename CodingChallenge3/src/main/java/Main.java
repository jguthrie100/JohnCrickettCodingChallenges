import data.HoffmanTools;
import data.CompressionTools;
import data.HuffmanNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        String filename = "";

        Boolean compressFile = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--compressFile")) {
                compressFile = true;
                filename = args[i+1];
            }
            if (args[i].equals("--decompressFile")) {
                compressFile = false;
                filename = args[i+1];
            }
        }

        if (compressFile == null) {
            throw new IllegalArgumentException("Please include either --compressFile or --decompressFile as an argument flag followed by the filename");
        }

        HoffmanTools hoffTools = new HoffmanTools();
        CompressionTools ct = new CompressionTools();

        File file = new File(filename);

        StringBuilder text = new StringBuilder("");

        try (FileInputStream fis = new FileInputStream(file)) {
            int data;
            while ((data = fis.read()) != -1) {
                text.append((char) data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (compressFile) {
            Map<Character, Integer> frequencyMap = hoffTools.buildFrequencyMap(text);
            HuffmanNode huffmanTree = hoffTools.buildFrequencyTree(frequencyMap);
            Map<Character, String> codeTable = hoffTools.buildCodeTable(huffmanTree, "");

            ct.writeFile(ct.generateCompressedFileContent(text, codeTable), filename + ".compressed");
        } else {
            ct.writeFile(ct.decompressFileContent(text), filename + ".decompressed");
        }
    }
}