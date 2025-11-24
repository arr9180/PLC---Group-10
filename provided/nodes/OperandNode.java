package provided.nodes;

import java.util.ArrayList;

import provided.JottTree;
import provided.JottType;
import provided.RuntimeState;
import provided.SemanticContext;
import provided.Token;
import provided.TokenType;
import provided.RuntimeValue;

// Interface for nodes that can be used as operands in expressions
public interface OperandNode extends JottTree {

	JottType getType();

	Token getToken();

	RuntimeValue evaluate(RuntimeState state);

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
