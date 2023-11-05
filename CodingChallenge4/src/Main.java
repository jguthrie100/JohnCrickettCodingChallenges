import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        final List<Integer> cutColumns = new ArrayList<>();
        String filename = "";
        Character delimiter = '\t';

        for (int i = 0; i < args.length; i++) {
            String argTmp = args[i];
            if (args[i].startsWith("-f")) {
                cutColumns.addAll(getCutColumns(argTmp));
            }
            if (args[i].startsWith("-d")) {
                delimiter = argTmp.replaceAll("-d", "").charAt(0);
            }
            if (!args[i].startsWith("-")) {
                filename = args[i];
            }
        }

        StringBuilder fileText = readFile(filename);

        StringBuilder output = new StringBuilder(fileText);

        if (cutColumns.size() > 0) {
            List<String> columns;
            output = new StringBuilder();

            while (!fileText.isEmpty()) {
                AtomicInteger currCol = new AtomicInteger(1);
                int endLinePos = fileText.indexOf("\n") >= 0 ? fileText.indexOf("\n") : fileText.length();

                columns = List.of(fileText.substring(0, endLinePos).split("" + delimiter))
                        .stream()
                        .filter(f -> cutColumns.contains(currCol.getAndIncrement()))
                        .collect(Collectors.toList());
                fileText.delete(0, endLinePos + 1);

                // Append line and include newline char if required
                output.append(String.join("" + delimiter, columns) + (endLinePos <= fileText.length() ? "\n" : ""));
            }
        }

        System.out.println(output);
    }

    private static List<Integer> getCutColumns(String arg) {
        return List.of(arg.replaceAll("-f", "")
                .split("[, ]"))
                .stream()
                .map(c -> Integer.parseInt(c))
                .collect(Collectors.toList());
    }

    private static StringBuilder readFile(String filename) {

        StringBuilder output = new StringBuilder();

        try {
            FileInputStream inputStream = new FileInputStream(filename);

            int data;
            while ((data = inputStream.read()) != -1) {
                // Process the data (byte) read from the file
                output.append((char) data);
            }

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }
}