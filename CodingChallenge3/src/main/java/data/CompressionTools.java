package data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompressionTools {

    public void writeFile(StringBuilder text, String filename) {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            text.chars().forEach(c -> {
                try {
                    fos.write((byte) c);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringBuilder generateCompressedFileContent(StringBuilder text, Map<Character, String> codeTable) {
        StringBuilder compressedFileContent = new StringBuilder();
        StringBuilder bitBuffer = new StringBuilder();
        long charCount = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (codeTable.containsKey(c)) {
                charCount++;
                bitBuffer.append(codeTable.get(c));
                writeBitBuffer(compressedFileContent, bitBuffer, false);
            }
        }

        writeBitBuffer(compressedFileContent, bitBuffer, true);

        return generateFileHeader(codeTable, charCount).append(compressedFileContent);
    }

    private StringBuilder generateFileHeader(Map<Character, String> compressionMap, Long charCount) {
        StringBuilder output = new StringBuilder();

        for (Map.Entry<Character, String> e : compressionMap.entrySet()) {
            output.append(e.getKey() + "=" + e.getValue() + ";");
        }

        if (charCount != null) {
            output.append("[" + charCount + "]");
        }
        return output.append("==========");
    }

    private void writeBitBuffer(StringBuilder compressedFileContent, StringBuilder bitBuffer, boolean forcedWrite) {
        while (bitBuffer.length() >= 8 || (forcedWrite && bitBuffer.length() > 0)) {
            int numBits = Math.min(8, bitBuffer.length());

            String bitsToWrite = bitBuffer.substring(0, numBits);
            bitBuffer.delete(0, numBits);
            char nextChar = 0;

            for (int bitPos = 7; bitPos > 7 - numBits; bitPos--) {
                if (bitsToWrite.charAt(7-bitPos) == '1') {
                    char mask = (char) (1 << bitPos);
                    nextChar |= mask;
                }
            }

            compressedFileContent.append(nextChar);
        }
    }

    public StringBuilder decompressFileContent(StringBuilder compressedFileContent) {
        String header = compressedFileContent.substring(0, compressedFileContent.indexOf("=========="));
        compressedFileContent.delete(0, compressedFileContent.indexOf("==========") + 10);

        Long charCount = getHeaderCharCount(header);

        if (charCount != null) {
            header = header.replaceAll("\\[" + charCount + "\\]", "");
        }

        Map<String, Character> charMap = new HashMap<>();

        int i = 0;
        while (i < header.length()) {
            charMap.put(header.substring(i+2, header.indexOf(";", i+2)), header.charAt(i));
            i = header.indexOf(";", i) + 1;
        }

        AtomicLong decompressedCharCount = new AtomicLong();
        StringBuilder decompressedFileContent = new StringBuilder();
        StringBuilder bitBuffer = new StringBuilder();

        compressedFileContent.chars().forEach(c -> {
            for (int i2 = 7; i2 >= 0; i2--) {
                bitBuffer.append((c & (1 << i2)) != 0 ? '1' : '0');
            }

            Character decompressedChar;
            do {
                decompressedChar = readBitBuffer(bitBuffer, charMap);
                if (decompressedChar != null) {
                    decompressedFileContent.append(decompressedChar);
                    decompressedCharCount.getAndIncrement();

                    // Reached designated char count (avoids adding erroneous chars at end of file)
                    if (charCount != null && decompressedCharCount.get() >= charCount) {
                        return;
                    }
                }
            } while (decompressedChar != null);
        });

        return decompressedFileContent;
    }

    private Character readBitBuffer(StringBuilder bitBuffer, Map<String, Character> codeTable) {
        for (int i = 1; i <= bitBuffer.length(); i++) {
            Character decompressedChar = codeTable.get(bitBuffer.substring(0, i));
            if (decompressedChar != null) {
                bitBuffer.delete(0, i);
                return decompressedChar;
            }
        }
        return null;
    }

    private Long getHeaderCharCount(String header) {
        Pattern p = Pattern.compile("\\[(\\d+)\\]$");
        Matcher m = p.matcher(header);

        if (m.find()) {
            return Long.parseLong(m.group(1));
        }

        return null;
    }
}
