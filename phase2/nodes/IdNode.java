package phase2.nodes;

import java.util.ArrayList;

import phase2.Token;
import phase2.TokenType;

public class IdNode implements OperandNode {

    private final Token idToken;

    private IdNode(Token idToken) {
        this.idToken = idToken;
    }

    public static IdNode parseIDNode(ArrayList<Token> tokens) {
        // Check for end of input
        if (tokens == null || tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected identifier but reached end of input");
            return null;
        }

        // Verify token is an identifier
        Token next = tokens.get(0);
        if (next.getTokenType() != TokenType.ID_KEYWORD) {
            System.err.println("Syntax Error");
            System.err.println("Expected identifier but found \"" + next.getToken() + "\"");
            System.err.println(next.getFilename() + ":" + next.getLineNum());
            return null;
        }

        // Verify identifier starts with lowercase letter
        String lexeme = next.getToken();
        if (lexeme.isEmpty() || !Character.isLowerCase(lexeme.charAt(0))) {
            System.err.println("Syntax Error");
            System.err.println("Invalid identifier \"" + lexeme + "\". Identifiers must start with a lowercase letter");
            System.err.println(next.getFilename() + ":" + next.getLineNum());
            return null;
        }

        tokens.remove(0);
        return new IdNode(next);
    }

    @Override
    public String convertToJott() {
        return idToken.getToken();
    }

    @Override
    public String convertToJava(String className) {
        return idToken.getToken();
    }

    @Override
    public String convertToC() {
        return idToken.getToken();
    }

    @Override
    public String convertToPython() {
        return idToken.getToken();
    }

    @Override
    public boolean validateTree() {
        return true;
    }
}
