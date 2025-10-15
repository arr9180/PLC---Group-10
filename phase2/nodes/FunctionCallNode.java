package phase2.nodes;

import java.util.ArrayList;
import java.util.List;

import phase2.Token;
import phase2.TokenType;

public class FunctionCallNode implements OperandNode {

    private final Token functionNameToken;
    private final List<ExpressionNode> arguments;

    private FunctionCallNode(Token functionNameToken, List<ExpressionNode> arguments) {
        this.functionNameToken = functionNameToken;
        this.arguments = arguments;
    }

    public static FunctionCallNode parseFunctionCallNode(ArrayList<Token> tokens) {
        // Check for end of input
        if (tokens == null || tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected function call but reached end of input");
            return null;
        }

        // Expect :: header
        Token header = tokens.get(0);
        if (header.getTokenType() != TokenType.FC_HEADER) {
            System.err.println("Syntax Error");
            System.err.println("Expected function call header '::' but found \"" + header.getToken() + "\"");
            System.err.println(header.getFilename() + ":" + header.getLineNum());
            return null;
        }
        tokens.remove(0);

        // Expect function name
        if (tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected function name after '::' but reached end of input");
            return null;
        }

        Token nameToken = tokens.get(0);
        if (nameToken.getTokenType() != TokenType.ID_KEYWORD) {
            System.err.println("Syntax Error");
            System.err.println("Expected function name but found \"" + nameToken.getToken() + "\"");
            System.err.println(nameToken.getFilename() + ":" + nameToken.getLineNum());
            return null;
        }

        // Verify function name starts with lowercase
        String nameLexeme = nameToken.getToken();
        if (nameLexeme.isEmpty() || !Character.isLowerCase(nameLexeme.charAt(0))) {
            System.err.println("Syntax Error");
            System.err.println("Invalid function name \"" + nameLexeme + "\". Names must start with a lowercase letter");
            System.err.println(nameToken.getFilename() + ":" + nameToken.getLineNum());
            return null;
        }
        tokens.remove(0);

        // Expect [ to start arguments
        if (tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected '[' after function name but reached end of input");
            return null;
        }

        Token bracketToken = tokens.get(0);
        if (bracketToken.getTokenType() != TokenType.L_BRACKET) {
            System.err.println("Syntax Error");
            System.err.println("Expected '[' after function name but found \"" + bracketToken.getToken() + "\"");
            System.err.println(bracketToken.getFilename() + ":" + bracketToken.getLineNum());
            return null;
        }
        tokens.remove(0);

        // Parse argument list
        List<ExpressionNode> args = new ArrayList<>();

        if (tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected ']' to close function call but reached end of input");
            return null;
        }

        // Parse arguments separated by commas
        if (tokens.get(0).getTokenType() != TokenType.R_BRACKET) {
            while (true) {
                ExpressionNode expression = ExpressionNode.parse(tokens);
                if (expression == null) {
                    return null;
                }
                args.add(expression);

                if (tokens.isEmpty()) {
                    System.err.println("Syntax Error");
                    System.err.println("Expected ',' or ']' after function argument but reached end of input");
                    return null;
                }

                Token separator = tokens.get(0);
                if (separator.getTokenType() == TokenType.COMMA) {
                    tokens.remove(0);
                    if (tokens.isEmpty()) {
                        System.err.println("Syntax Error");
                        System.err.println("Expected operand after ',' in function call");
                        return null;
                    }
                    continue;
                }

                if (separator.getTokenType() == TokenType.R_BRACKET) {
                    break;
                }

                System.err.println("Syntax Error");
                System.err.println("Expected ',' or ']' after function argument but found \"" + separator.getToken() + "\"");
                System.err.println(separator.getFilename() + ":" + separator.getLineNum());
                return null;
            }
        }

        tokens.remove(0);

        return new FunctionCallNode(nameToken, args);
    }

    public Token getFunctionNameToken() {
        return functionNameToken;
    }

    @Override
    public String convertToJott() {
        StringBuilder builder = new StringBuilder();
        builder.append("::").append(functionNameToken.getToken()).append("[");
        for (int i = 0; i < arguments.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(arguments.get(i).convertToJott());
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String convertToJava(String className) {
        return convertToJott();
    }

    @Override
    public String convertToC() {
        return convertToJott();
    }

    @Override
    public String convertToPython() {
        return convertToJott();
    }

    @Override
    public boolean validateTree() {
        for (ExpressionNode argument : arguments) {
            if (!argument.validateTree()) {
                return false;
            }
        }
        return true;
    }
}
