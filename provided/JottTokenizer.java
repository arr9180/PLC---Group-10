package provided;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class JottTokenizer {

	/**
     * Takes in a filename and tokenizes that file into Tokens
     * based on the rules of the Jott Language
     * @param filename the name of the file to tokenize; can be relative or absolute path
     * @return an ArrayList of Jott Tokens
     */
    public static ArrayList<Token> tokenize(String filename){
        // ArrayList to store tokens
        ArrayList<Token> tokens = new ArrayList<>();

        // Now use BufferedReader to read the file
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))){
            // Define current line and line number
            String currLine;
            int lineNum = 1;

            // Go through the file line by line
            while ((currLine = reader.readLine()) != null){

                // Define character index for current line
                int charIndex = 0;

                // Go through the line character by character
                while (charIndex < currLine.length()){
                    char currChar = currLine.charAt(charIndex);

                    if (Character.isWhitespace(currChar)){
                        charIndex++;
                    } else if (currChar == '#'){
                        charIndex = currLine.length();
                    } else if (currChar == ','){
                        tokens.add(new Token(",", filename, lineNum, TokenType.COMMA));
                        charIndex++;
                    } else if (currChar == ']'){
                        tokens.add(new Token("]", filename, lineNum, TokenType.R_BRACKET));
                        charIndex++;
                    } else if (currChar == '['){
                        tokens.add(new Token("[", filename, lineNum, TokenType.L_BRACKET));
                        charIndex++;
                    } else if (currChar == '}'){
                        tokens.add(new Token("}", filename, lineNum, TokenType.R_BRACE));
                        charIndex++;
                    } else if (currChar == '{'){
                        tokens.add(new Token("{", filename, lineNum, TokenType.L_BRACE));
                        charIndex++;
                    } else if (currChar == ';'){
                        tokens.add(new Token(";", filename, lineNum, TokenType.SEMICOLON));
                        charIndex++;
                    } else if (currChar == '='){
                        if (charIndex + 1 < currLine.length() && currLine.charAt(charIndex + 1) == '='){
                            tokens.add(new Token("==", filename, lineNum, TokenType.REL_OP));
                            charIndex += 2;
                        } else {
                            tokens.add(new Token("=", filename, lineNum, TokenType.ASSIGN));
                            charIndex++;
                        }
                    } else if (currChar == '<'){
                        if (charIndex + 1 < currLine.length() && currLine.charAt(charIndex + 1) == '='){
                            tokens.add(new Token("<=", filename, lineNum, TokenType.REL_OP));
                            charIndex += 2;
                        } else {
                            tokens.add(new Token("<", filename, lineNum, TokenType.REL_OP));
                            charIndex++;
                        }
                    } else if (currChar == '>'){
                        if (charIndex + 1 < currLine.length() && currLine.charAt(charIndex + 1) == '='){
                            tokens.add(new Token(">=", filename, lineNum, TokenType.REL_OP));
                            charIndex += 2;
                        } else {
                            tokens.add(new Token(">", filename, lineNum, TokenType.REL_OP));
                            charIndex++;
                        }
                    } else if (currChar == '!'){
                        if (charIndex + 1 < currLine.length() && currLine.charAt(charIndex + 1) == '='){
                            tokens.add(new Token("!=", filename, lineNum, TokenType.REL_OP));
                            charIndex += 2;
                        } else {
                            System.err.println("Syntax Error");
                            System.err.println("Invalid token \"!\". \"!\" expects following \"=\"");
                            System.err.println(filename + ":" + lineNum);
                            return null;
                        }
                    } else if (currChar == '+' || currChar == '-' || currChar == '*' || currChar == '/'){
                        tokens.add(new Token(String.valueOf(currChar), filename, lineNum, TokenType.MATH_OP));
                        charIndex++;
                    } else if (Character.isLetter(currChar)){
                        ArrayList<Object> result = parseIdentifier(currLine, charIndex, filename, lineNum);
                        Token token = (Token) result.get(0);
                        int newIndex = (Integer) result.get(1);

                        if (token == null){
                            return null;
                        }

                        tokens.add(token);
                        charIndex = newIndex;
                    } else if (currChar == '"'){
                        ArrayList<Object> result = parseString(currLine, charIndex, filename, lineNum);
                        Token token = (Token) result.get(0);
                        int newIndex = (Integer) result.get(1);

                        if (token == null){
                            return null;
                        }

                        tokens.add(token);
                        charIndex = newIndex;
                    } else if (currChar == '.'){
                        if (charIndex + 1 < currLine.length() && Character.isDigit(currLine.charAt(charIndex + 1))) {
                            ArrayList<Object> result = checkNumber(currLine, charIndex, filename, lineNum);
                            Token token = (Token) result.get(0);
                            int newIndex = (Integer) result.get(1);

                            if (token == null){
                                return null;
                            }

                            tokens.add(token);
                            charIndex = newIndex;
                        } else {
                            System.err.println("Syntax Error");
                            System.err.println("Invalid token \".\" without following digit");
                            System.err.println(filename + ":" + lineNum);
                            return null;
                        }
                    } else if (Character.isDigit(currChar)){
                        ArrayList<Object> result = checkNumber(currLine, charIndex, filename, lineNum);
                        Token token = (Token) result.get(0);
                        int newIndex = (Integer) result.get(1);

                        if (token == null){
                            return null;
                        }

                        tokens.add(token);
                        charIndex = newIndex;
                    } else if (currChar == ':'){
                        if (charIndex + 1 < currLine.length() && currLine.charAt(charIndex + 1) == ':'){
                            tokens.add(new Token("::", filename, lineNum, TokenType.FC_HEADER));
                            charIndex += 2;
                        } else {
                            tokens.add(new Token(":", filename, lineNum, TokenType.COLON));
                            charIndex++;
                        }
                    } else {
                        System.err.println("Syntax Error");
                        System.err.println("Unexpected character '" + currChar + "'");
                        System.err.println(filename + ":" + lineNum);
                        return null;
                    }

                }
                // Increment line number
                lineNum++;
            }
        
        } catch (IOException exception){
            System.err.println("Error reading file: " + filename);
            return null;
        }
        // Return the ArrayList of tokens
        return tokens;
	}

    private static ArrayList<Object> checkNumber(String currLine, int charIndex, String filename, int lineNum){
        // ArrayList for return values: Token and new index
        ArrayList<Object> result = new ArrayList<>();
        
        // Build the number string
        StringBuilder number = new StringBuilder();

        // Define current index
        int currIndex = charIndex;
        
        // Parse digits and decimal points
        while (currIndex < currLine.length() && (Character.isDigit(currLine.charAt(currIndex)) || currLine.charAt(currIndex) == '.')){
            // Handle decimal points
            if (currLine.charAt(currIndex) == '.'){
                if (!number.toString().contains(".")){
                    number.append('.');
                    currIndex++;
                } else {
                    break;
                }
            } else {
                number.append(currLine.charAt(currIndex));
                currIndex++;
            }
        }

        String numberStr = number.toString();
        if (numberStr.isEmpty() || numberStr.equals(".")) {
            System.err.println("Syntax Error");
            System.err.println("Invalid token \".\"");
            System.err.println(filename + ":" + lineNum);
            result.add(null);
            result.add(currIndex);
            return result;
        }

        Token token = new Token(numberStr, filename, lineNum, TokenType.NUMBER);
        result.add(token);
        result.add(currIndex);
        return result;
    }

    private static ArrayList<Object> parseString(String currLine, int charIndex, String filename, int lineNum){
        // ArrayList for return values: Token and new index
        ArrayList<Object> result = new ArrayList<>();
        
        // Build the string
        StringBuilder string = new StringBuilder();
        
        // Define current index
        int currIndex = charIndex;
        
        // Start with opening quote
        string.append(currLine.charAt(currIndex));
        currIndex++; 
        
        // Read characters until closing quote or end of line
        while (currIndex < currLine.length()) {
            char ch = currLine.charAt(currIndex);
            
            // Found closing quote
            if (ch == '"') {
                string.append('"'); // Add closing quote
                currIndex++; // Move past closing quote
                
                Token token = new Token(string.toString(), filename, lineNum, TokenType.STRING);
                result.add(token);
                result.add(currIndex);
                return result;
            }
            
            // Check for valid string characters (letters, digits, spaces only)
            if (!Character.isLetterOrDigit(ch) && ch != ' ') {
                System.err.println("Syntax Error");
                System.err.println("Invalid character in string: '" + ch + "'");
                System.err.println(filename + ":" + lineNum);
                result.add(null);
                result.add(currIndex);
                return result;
            }
            
            // Add character to string
            string.append(ch);
            currIndex++;
        }
        
        System.err.println("Syntax Error");
        System.err.println("Unclosed string");
        System.err.println(filename + ":" + lineNum);
        result.add(null);
        result.add(currIndex);
        return result;
    }

    private static ArrayList<Object> parseIdentifier(String currLine, int charIndex, String filename, int lineNum){
        // ArrayList for return values: Token and new index
        ArrayList<Object> result = new ArrayList<>();

        // Build the identifier string
        StringBuilder identifier = new StringBuilder();

        // Define current index
        int currIndex = charIndex;

        while (currIndex < currLine.length() && (Character.isLetter(currLine.charAt(currIndex)) || Character.isDigit(currLine.charAt(currIndex)))){
            identifier.append(currLine.charAt(currIndex));
            currIndex++;
        }

        Token token = new Token(identifier.toString(), filename, lineNum, TokenType.ID_KEYWORD);
        result.add(token);
        result.add(currIndex);
        return result;
    }
}