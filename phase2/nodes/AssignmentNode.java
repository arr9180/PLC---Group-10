package phase2.nodes;

import java.util.ArrayList;

import phase2.JottTree;
import phase2.Token;
import phase2.TokenType;

public class AssignmentNode implements JottTree {

    private final IdNode identifier;
    private final ExpressionNode expression;

    private AssignmentNode(IdNode identifier, ExpressionNode expression) {
        this.identifier = identifier;
        this.expression = expression;
    }

    public static AssignmentNode parse(ArrayList<Token> tokens) {
        // Parse the identifier on the left side
        IdNode identifier = IdNode.parseIDNode(tokens);
        if (identifier == null) {
            return null;
        }

        // Check for = assignment operator
        if (tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected = in assignment but reached end of input");
            return null;
        }

        Token assign = tokens.get(0);
        if (assign.getTokenType() != TokenType.ASSIGN) {
            System.err.println("Syntax Error");
            System.err.println("Expected = in assignment but found \"" + assign.getToken() + "\"");
            System.err.println(assign.getFilename() + ":" + assign.getLineNum());
            return null;
        }
        tokens.remove(0);

        // Parse the expression on the right side
        ExpressionNode expression = ExpressionNode.parse(tokens);
        if (expression == null) {
            return null;
        }

        // Check for semicolon at end
        if (tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected ; after assignment but reached end of input");
            return null;
        }

        Token semicolon = tokens.get(0);
        if (semicolon.getTokenType() != TokenType.SEMICOLON) {
            System.err.println("Syntax Error");
            System.err.println("Expected ; after assignment but found \"" + semicolon.getToken() + "\"");
            System.err.println(semicolon.getFilename() + ":" + semicolon.getLineNum());
            return null;
        }
        tokens.remove(0);

        return new AssignmentNode(identifier, expression);
    }

    @Override
    public String convertToJott() {
        return identifier.convertToJott() + "=" + expression.convertToJott() + ";";
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
