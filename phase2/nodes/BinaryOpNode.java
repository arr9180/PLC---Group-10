package phase2.nodes;

import phase2.JottTree;
import phase2.Token;

public class BinaryOpNode implements JottTree {

    private final JottTree left;
    private final Token operatorToken;
    private final JottTree right;

    public BinaryOpNode(JottTree left, Token operatorToken, JottTree right) {
        this.left = left;
        this.operatorToken = operatorToken;
        this.right = right;
    }

    @Override
    public String convertToJott() {
        return left.convertToJott() + operatorToken.getToken() + right.convertToJott();
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
