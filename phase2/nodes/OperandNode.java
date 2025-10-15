package phase2.nodes;

import java.util.ArrayList;

import phase2.Token;
import phase2.TokenType;
import phase2.JottTree;

// Interface for nodes that can be used as operands in expressions
public interface OperandNode extends JottTree {

    static OperandNode parseOperand(ArrayList<Token> tokens) {
        // Check for end of input
        if (tokens == null || tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected operand but reached end of input");
            return null;
        }

        Token next = tokens.get(0);
        TokenType type = next.getTokenType();

        // Check for function call
        if (type == TokenType.FC_HEADER) {
            return FunctionCallNode.parseFunctionCallNode(tokens);
        }

        // Check for string literal
        if (type == TokenType.STRING) {
            return StringNode.parse(tokens);
        }

        // Check for boolean or identifier
        if (type == TokenType.ID_KEYWORD) {
            if (BooleanNode.isBooleanLiteral(next.getToken())) {
                return BooleanNode.parse(tokens);
            }
            return IdNode.parseIDNode(tokens);
        }

        // Check for number (with optional negative sign)
        if (type == TokenType.NUMBER || (type == TokenType.MATH_OP && "-".equals(next.getToken()))) {
            return NumberNode.parseNumberNode(tokens);
        }

        // Invalid operand
        System.err.println("Syntax Error");
        System.err.println("Invalid operand starting with token \"" + next.getToken() + "\"");
        System.err.println(next.getFilename() + ":" + next.getLineNum());
        return null;
    }
}
