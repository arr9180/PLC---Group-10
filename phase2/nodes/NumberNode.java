package phase2.nodes;

import java.util.ArrayList;

import phase2.Token;
import phase2.TokenType;

public class NumberNode implements OperandNode {

    private final Token numberToken;

    private NumberNode(Token numberToken) {
        this.numberToken = numberToken;
    }

    public static NumberNode parseNumberNode(ArrayList<Token> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected number but reached end of input");
            return null;
        }

        Token next = tokens.get(0);
        if (next.getTokenType() != TokenType.NUMBER) {
            System.err.println("Syntax Error");
            System.err.println("Expected number but found \"" + next.getToken() + "\"");
            System.err.println(next.getFilename() + ":" + next.getLineNum());
            return null;
        }

        String lexeme = next.getToken();
        if (!lexeme.matches("(?:\\d+(?:\\.\\d*)?|\\.\\d+)")) {
            System.err.println("Syntax Error");
            System.err.println("Invalid number literal \"" + lexeme + "\"");
            System.err.println(next.getFilename() + ":" + next.getLineNum());
            return null;
        }

        tokens.remove(0);
        return new NumberNode(next);
    }

    @Override
    public String convertToJott() {
        return numberToken.getToken();
    }

    @Override
    public String convertToJava(String className) {
        return numberToken.getToken();
    }

    @Override
    public String convertToC() {
        return numberToken.getToken();
    }

    @Override
    public String convertToPython() {
        return numberToken.getToken();
    }

    @Override
    public boolean validateTree() {
        return true;
    }
}
