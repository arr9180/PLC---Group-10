package phase2.nodes;

import java.util.ArrayList;

import phase2.JottTree;
import phase2.Token;
import phase2.TokenType;

public class StatementNode implements JottTree {

    private final JottTree node;

    private StatementNode(JottTree node) {
        this.node = node;
    }

    public static StatementNode parse(ArrayList<Token> tokens) {
        if (tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected statement but reached end of input");
            return null;
        }
        Token next = tokens.get(0);
        if (next.getTokenType() == TokenType.ID_KEYWORD && "If".equals(next.getToken())) {
            IfNode ifNode = IfNode.parse(tokens);
            if (ifNode == null) {
                return null;
            }
            return new StatementNode(ifNode);
        }
        if (next.getTokenType() == TokenType.ID_KEYWORD && "While".equals(next.getToken())) {
            WhileNode whileNode = WhileNode.parse(tokens);
            if (whileNode == null) {
                return null;
            }
            return new StatementNode(whileNode);
        }
        if (next.getTokenType() == TokenType.FC_HEADER) {
            FunctionCallNode functionCall = FunctionCallNode.parseFunctionCallNode(tokens);
            if (functionCall == null) {
                return null;
            }
            if (!consumeSemicolon(tokens, functionCall.getFunctionNameToken())) {
                return null;
            }
            return new StatementNode(functionCall);
        }
        AssignmentNode assignment = AssignmentNode.parse(tokens);
        if (assignment == null) {
            return null;
        }
        return new StatementNode(assignment);
    }

    private static boolean consumeSemicolon(ArrayList<Token> tokens, Token reference) {
        if (tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected ; after function call but reached end of input");
            if (reference != null) {
                System.err.println(reference.getFilename() + ":" + reference.getLineNum());
            }
            return false;
        }
        Token token = tokens.get(0);
        if (token.getTokenType() != TokenType.SEMICOLON) {
            System.err.println("Syntax Error");
            System.err.println("Expected ; after function call but found \"" + token.getToken() + "\"");
            System.err.println(token.getFilename() + ":" + token.getLineNum());
            return false;
        }
        tokens.remove(0);
        return true;
    }

    @Override
    public String convertToJott() {
        if (node instanceof FunctionCallNode) {
            return node.convertToJott() + ";";
        }
        return node.convertToJott();
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
