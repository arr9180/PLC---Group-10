package phase2.nodes;

import java.util.ArrayList;

import phase2.Token;
import phase2.TokenType;

public class NumberNode implements OperandNode {

    private final Token numberToken;
    private final boolean negative;

    private NumberNode(Token numberToken, boolean negative) {
        this.numberToken = numberToken;
        this.negative = negative;
    }

    public static NumberNode parseNumberNode(ArrayList<Token> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected number but reached end of input");
            return null;
        }

        boolean negative = false;
        Token next = tokens.get(0);
        if (next.getTokenType() == TokenType.MATH_OP && "-".equals(next.getToken())) {
            negative = true;
            tokens.remove(0);
            if (tokens.isEmpty()) {
                System.err.println("Syntax Error");
                System.err.println("Expected number after - but reached end of input");
                return null;
            }
            next = tokens.get(0);
        }

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
        return new NumberNode(next, negative);
    }

    @Override
    public String convertToJott() {
        if (negative) {
            return "-" + numberToken.getToken();
        }
        return numberToken.getToken();
    }

    @Override
    public String convertToJava(String className) {
        if (negative) {
            return "-" + numberToken.getToken();
        }
        return numberToken.getToken();
    }

    @Override
    public String convertToC() {
        if (negative) {
            return "-" + numberToken.getToken();
        }
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
