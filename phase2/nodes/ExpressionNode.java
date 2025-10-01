package phase2.nodes;

import java.util.ArrayList;

import phase2.JottTree;
import phase2.Token;
import phase2.TokenType;

public class ExpressionNode implements JottTree {

    private final JottTree node;

    private ExpressionNode(JottTree node) {
        this.node = node;
    }

    public static ExpressionNode parse(ArrayList<Token> tokens) {
        if (tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected expression but reached end of input");
            return null;
        }
        Token first = tokens.get(0);
        if (first.getTokenType() == TokenType.STRING) {
            StringNode stringNode = StringNode.parse(tokens);
            if (stringNode == null) {
                return null;
            }
            return new ExpressionNode(stringNode);
        }
        if (first.getTokenType() == TokenType.ID_KEYWORD && BooleanNode.isBooleanLiteral(first.getToken())) {
            BooleanNode booleanNode = BooleanNode.parse(tokens);
            if (booleanNode == null) {
                return null;
            }
            return new ExpressionNode(booleanNode);
        }
        OperandNode leftOperand = OperandNode.parseOperand(tokens);
        if (leftOperand == null) {
            return null;
        }
        if (tokens.isEmpty()) {
            return new ExpressionNode((JottTree) leftOperand);
        }
        Token operator = tokens.get(0);
        if (operator.getTokenType() != TokenType.REL_OP && operator.getTokenType() != TokenType.MATH_OP) {
            return new ExpressionNode((JottTree) leftOperand);
        }
        tokens.remove(0);
        OperandNode rightOperand = OperandNode.parseOperand(tokens);
        if (rightOperand == null) {
            return null;
        }
        BinaryOpNode binary = new BinaryOpNode((JottTree) leftOperand, operator, (JottTree) rightOperand);
        return new ExpressionNode(binary);
    }

    @Override
    public String convertToJott() {
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
