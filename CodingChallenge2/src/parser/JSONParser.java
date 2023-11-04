package parser;

import java.text.ParseException;

public class JSONParser {

    /*
     Uses syntax diagram based at https://www.json.org/json-en.html
     */
    public int isValidJSON(String json) {
        json = json.strip();

        if (json == null || json.equals("")) return 1;

        try {
            if (json.charAt(0) == '[') {
                return validateArray(json, 0) == json.length() ? 0 : 1;
            } else if (json.charAt(0) == '{') {
                return validateObject(json, 0) == json.length() ? 0 : 1;
            } else {
                throw new ParseException("JSON string must be either an object or an array", 0);
            }
        } catch (ParseException e) {
            System.out.println("ERROR: " + e.getLocalizedMessage() + " at offset position " + e.getErrorOffset());
            return 1;
        }
    }

    public int validateObject(String json, int startIndex) throws ParseException {
        if (json.charAt(startIndex) != '{') throw new ParseException("Invalid JSON Object - Must start with { char", startIndex);

        // Handle an empty object (i.e. returns if the first char in the object is the closing brace
        for (int i = startIndex + 1; i < json.length(); i++) {
            if (("" + json.charAt(i)).matches("\\s")) continue;
            if (json.charAt(i) == '}') return i + 1;
            break;
        }

        int endObjectIndex = validateNextKeyValuePair(json, startIndex + 1);

        if (json.charAt(endObjectIndex) != '}') throw new ParseException("Invalid JSON Object - Must end with } char", endObjectIndex);

        return endObjectIndex + 1;
    }

    public int validateArray(String json, int startIndex) throws ParseException {
        if (json.charAt(startIndex) != '[') throw new ParseException("Invalid JSON Array - Must start with [ char", startIndex);

        boolean openComma = false;
        boolean firstToken = true;

        for (int i = startIndex+1; i < json.length(); i++) {
            if (("" + json.charAt(i)).matches("\\s")) continue;
            if (json.charAt(i) == ']') {
                if (openComma) throw new ParseException("Invalid JSON Array - array ends with a comma", i);
                return i+1;
            }
            if (json.charAt(i) == ',' && firstToken) throw new ParseException("Invalid JSON Array - Array starts with a comma", i);
            if (json.charAt(i) == ',') {
                if (openComma) throw new ParseException("Invalid JSON Array - multiple commas without values", i);
                openComma = true;
                continue;
            }
            i = validateValue(json, i) - 1;
            openComma = false;
            firstToken = false;
        }

        throw new ParseException("Invalid JSON Array", json.length()-1);
    }

    // Returns index of char following valid key value pair
    public int validateNextKeyValuePair(String json, int startIndex) throws ParseException {
        // validate key
        int endKeyIndex = validateKey(json, startIndex);

        int endSeparatorIndex = validateSeparator(json, endKeyIndex);

        int endObjectIndex = validateValue(json, endSeparatorIndex);

        // Match either end comma or end of object (if a comma is found, then validate following key-value pair)
        for (int i = endObjectIndex; i < json.length(); i++) {
            if (json.charAt(i) == ',') {
                return validateNextKeyValuePair(json, i + 1);
            }

            if (("" + json.charAt(i)).matches("\\s")) continue;

            return i;
        }

        throw new ParseException("Invalid JSON syntax - end of file was reached", startIndex);
    }

    public int validateKey(String json, int startIndex) throws ParseException {
        // validate key
        boolean startKey = false;

        for (int i = startIndex; i < json.length(); i++) {
            if (json.charAt(i) == '"') {
                if (!startKey) {
                    startKey = true;
                    continue;
                } else {
                    return i+1;
                }
            }

            if (!startKey && !("" + json.charAt(i)).matches("\\s")) throw new ParseException("Invalid JSON key", i);

            if (startKey && !("" + json.charAt(i)).matches("[\\w- ]")) throw new ParseException("Invalid JSON key", i);
        }

        throw new ParseException("Invalid JSON key", json.length()-1);
    }

    public int validateSeparator(String json, int startIndex) throws ParseException {
        boolean separatorFound = false;

        for (int i = startIndex; i < json.length(); i++) {
            if (!separatorFound && !("" + json.charAt(i)).matches("\\s")) throw new ParseException("Invalid JSON separator", i);

            if (separatorFound && !("" + json.charAt(i)).matches("\\s")) return i;

            if (json.charAt(i) == ':') {
                separatorFound = true;
            }
        }

        throw new ParseException("Invalid JSON separator", json.length()-1);
    }

    public int validateValue(String json, int startIndex) throws ParseException {
        if (json.charAt(startIndex) == '{') {
            return validateObject(json, startIndex);
        }

        if (json.charAt(startIndex) == '[') {
            return validateArray(json, startIndex);
        }

        return validateToken(json, startIndex);
    }

    private int validateToken(String json, int startIndex) throws ParseException {
        if (json.charAt(startIndex) == '"') {
            for (int i = startIndex + 1; i < json.length(); i++) {
                if (json.charAt(i) == '\t') throw new ParseException("Illegal tab character", i);
                if (json.charAt(i) == '\n') throw new ParseException("Illegal newline character", i);
                if (json.charAt(i) == '\\' && (json.charAt(i+1) == 'x' || json.charAt(i+1) == '0' || json.charAt(i+1) == ' ')) throw new ParseException("Illegal backslash escape", i);
                if (json.charAt(i) == '"' && json.charAt(i-1) != '\\') {
                    return i + 1;
                }
            }
        }

        if (json.substring(startIndex).startsWith("true")) return startIndex + 4;
        if (json.substring(startIndex).startsWith("false")) return startIndex + 5;
        if (json.substring(startIndex).startsWith("null")) return startIndex + 4;

        if (json.substring(startIndex, startIndex+1).matches("[0-9]")) {
            if (json.charAt(startIndex) == '0') throw new ParseException("Number cannot start with 0", startIndex);
            // loop until number ends
            for (int i = startIndex; i < json.length(); i++) {
                if (!json.substring(i, i+1).matches("[0-9]")) return i;
            }

            // Reached end of string and string was all numbers
            return json.length();
        }

        throw new ParseException("Invalid JSON token", startIndex);
    }
}
