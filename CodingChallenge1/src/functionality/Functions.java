package functionality;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Functions {

    public int charCount(StringBuilder fileContent) {
        return fileContent.length();
    }

    public int lineCount(StringBuilder fileContent) {
        return fileContent.toString().replaceAll("[^\\n]", "").length();
    }

    public int wordCount(StringBuilder fileContent) {
        return fileContent.toString().split("\\s+").length;
    }

    // Doesn't work
    public int multibyteCount(StringBuilder fileContent) {
        int output = 0;

        for (int i = 0; i < fileContent.length();) {
            output++;
            int codePoint = fileContent.codePointAt(i);
            i += Character.charCount(codePoint);
        }

        return output;
    }
}
