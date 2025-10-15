package phase2.nodes;

import java.util.ArrayList;

import phase2.JottTree;
import phase2.Token;
import phase2.TokenType;

public class VariableDecNode implements JottTree {

    private final Token typeToken;
    private final Token idToken;

    private VariableDecNode(Token typeToken, Token idToken) {
        this.typeToken = typeToken;
        this.idToken = idToken;
    }

    public static VariableDecNode parse(ArrayList<Token> tokens) {
        // Check for minimum tokens needed
        if (tokens.size() < 3) {
            System.err.println("Syntax Error");
            System.err.println("Invalid variable declaration");
            return null;
        }

        // Parse type
        Token typeToken = tokens.get(0);
        if (typeToken.getTokenType() != TokenType.ID_KEYWORD || !isValidType(typeToken.getToken())) {
            System.err.println("Syntax Error");
            System.err.println("Invalid type \"" + typeToken.getToken() + "\" in declaration");
            System.err.println(typeToken.getFilename() + ":" + typeToken.getLineNum());
            return null;
        }
        tokens.remove(0);

        // Parse identifier
        Token idToken = tokens.get(0);
        if (idToken.getTokenType() != TokenType.ID_KEYWORD || idToken.getToken().isEmpty() || !Character.isLowerCase(idToken.getToken().charAt(0))) {
            System.err.println("Syntax Error");
            System.err.println("Invalid identifier \"" + idToken.getToken() + "\" in declaration");
            System.err.println(idToken.getFilename() + ":" + idToken.getLineNum());
            return null;
        }
        tokens.remove(0);

        // Expect semicolon
        if (tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected ; after variable declaration");
            return null;
        }

        Token semicolon = tokens.get(0);
        if (semicolon.getTokenType() != TokenType.SEMICOLON) {
            System.err.println("Syntax Error");
            System.err.println("Expected ; after variable declaration but found \"" + semicolon.getToken() + "\"");
            System.err.println(semicolon.getFilename() + ":" + semicolon.getLineNum());
            return null;
        }
        tokens.remove(0);

        return new VariableDecNode(typeToken, idToken);
    }

    // Check if text is a valid type
    static boolean isValidType(String text) {
        return "Integer".equals(text) || "Double".equals(text) || "String".equals(text) || "Boolean".equals(text);
    }

    @Override
    public String convertToJott() {
        return typeToken.getToken() + " " + idToken.getToken() + ";";
    }

    @Override
    public String convertToJava(String className) {
        return "";
    }

    @Override
    public String convertToC() {
        return "";
    }

    @Override
    public String convertToPython() {
        return "";
    }

    @Override
    public boolean validateTree() {
        return true;
    }
}
