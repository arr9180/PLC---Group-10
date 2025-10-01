package phase2.nodes;

import java.util.ArrayList;

import phase2.Token;
import phase2.TokenType;
import phase2.JottTree;

public interface OperandNode extends JottTree {

    static OperandNode parseOperand(ArrayList<Token> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected operand but reached end of input");
            return null;
        }

        Token next = tokens.get(0);
        TokenType type = next.getTokenType();

        if (type == TokenType.FC_HEADER) {
            return FunctionCallNode.parseFunctionCallNode(tokens);
        }

        if (type == TokenType.STRING) {
            return StringNode.parse(tokens);
        }

        if (type == TokenType.ID_KEYWORD) {
            if (BooleanNode.isBooleanLiteral(next.getToken())) {
                return BooleanNode.parse(tokens);
            }
            return IdNode.parseIDNode(tokens);
        }

        if (type == TokenType.NUMBER || (type == TokenType.MATH_OP && "-".equals(next.getToken()))) {
            return NumberNode.parseNumberNode(tokens);
        }

        System.err.println("Syntax Error");
        System.err.println("Invalid operand starting with token \"" + next.getToken() + "\"");
        System.err.println(next.getFilename() + ":" + next.getLineNum());
        return null;
    }
}
