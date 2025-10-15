package phase2.nodes;

import java.util.ArrayList;

import phase2.JottTree;
import phase2.Token;
import phase2.TokenType;

public class ReturnNode implements JottTree {

    private final Token returnToken;
    private final ExpressionNode expression;

    private ReturnNode(Token returnToken, ExpressionNode expression) {
        this.returnToken = returnToken;
        this.expression = expression;
    }

    public static ReturnNode parse(ArrayList<Token> tokens) {
        // Expect Return keyword
        if (tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected Return but reached end of input");
            return null;
        }

        Token returnToken = tokens.get(0);
        if (returnToken.getTokenType() != TokenType.ID_KEYWORD || !"Return".equals(returnToken.getToken())) {
            System.err.println("Syntax Error");
            System.err.println("Expected Return but found \"" + returnToken.getToken() + "\"");
            System.err.println(returnToken.getFilename() + ":" + returnToken.getLineNum());
            return null;
        }
        tokens.remove(0);

        // Parse return expression
        ExpressionNode expression = ExpressionNode.parse(tokens);
        if (expression == null) {
            return null;
        }

        // Expect semicolon
        if (tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected ; after return statement but reached end of input");
            return null;
        }

        Token semicolon = tokens.get(0);
        if (semicolon.getTokenType() != TokenType.SEMICOLON) {
            System.err.println("Syntax Error");
            System.err.println("Expected ; after return statement but found \"" + semicolon.getToken() + "\"");
            System.err.println(semicolon.getFilename() + ":" + semicolon.getLineNum());
            return null;
        }
        tokens.remove(0);

        return new ReturnNode(returnToken, expression);
    }

    @Override
    public String convertToJott() {
        return returnToken.getToken() + " " + expression.convertToJott() + ";";
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
