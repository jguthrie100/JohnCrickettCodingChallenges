import filesystem.FileReader;
import functionality.Functions;

public class Main {
    public static void main(String[] args) {
        FileReader fr = new FileReader();
        Functions f = new Functions();

        String filename = null;
        boolean charCount = false;
        boolean lineCount = false;
        boolean wordCount = false;
        boolean multibyteCount = false;

        for(int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-c":
                    charCount = true;
                    break;
                case "-l":
                    lineCount = true;
                    break;
                case "-w":
                    wordCount = true;
                    break;
                case "-m":
                    multibyteCount = true;
                    break;
                default:
                    break;
            }

            if (!args[i].startsWith("-")) {
                filename = args[i];
            }
        }

        if (filename == null || filename == "") {
            throw new IllegalArgumentException("A filename must be passed via argument to the process");
        }

        if (!charCount && !lineCount && !wordCount && !multibyteCount) {
            charCount = true;
            lineCount = true;
            wordCount = true;
        }

        StringBuilder fileContent = fr.readFile(filename);

        if (charCount) {
            System.out.print(f.charCount(fileContent) + "\t");
        }
        if (lineCount) {
            System.out.print(f.lineCount(fileContent) + "\t");
        }
        if (wordCount) {
            System.out.print(f.wordCount(fileContent) + "\t");
        }
        if (multibyteCount) {
            System.out.print(f.multibyteCount(fileContent) + "\t");
        }
    }
}