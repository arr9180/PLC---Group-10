package phase2.nodes;

import java.util.ArrayList;
import java.util.List;

import phase2.JottTree;
import phase2.Token;
import phase2.TokenType;

public class FunctionDefNode implements JottTree {

    private final Token nameToken;
    private final List<FunctionParameterNode> parameters;
    private final Token returnTypeToken;
    private final FunctionBodyNode body;

    private FunctionDefNode(Token nameToken, List<FunctionParameterNode> parameters, Token returnTypeToken, FunctionBodyNode body) {
        this.nameToken = nameToken;
        this.parameters = parameters;
        this.returnTypeToken = returnTypeToken;
        this.body = body;
    }

    public static FunctionDefNode parse(ArrayList<Token> tokens) {
        if (tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected Def but reached end of input");
            return null;
        }
        Token defToken = tokens.get(0);
        if (defToken.getTokenType() != TokenType.ID_KEYWORD || !"Def".equals(defToken.getToken())) {
            System.err.println("Syntax Error");
            System.err.println("Expected Def but found \"" + defToken.getToken() + "\"");
            System.err.println(defToken.getFilename() + ":" + defToken.getLineNum());
            return null;
        }
        tokens.remove(0);
        if (tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected function name but reached end of input");
            return null;
        }
        Token nameToken = tokens.get(0);
        if (nameToken.getTokenType() != TokenType.ID_KEYWORD || nameToken.getToken().isEmpty() || !Character.isLowerCase(nameToken.getToken().charAt(0))) {
            System.err.println("Syntax Error");
            System.err.println("Expected identifier for function name but found \"" + nameToken.getToken() + "\"");
            System.err.println(nameToken.getFilename() + ":" + nameToken.getLineNum());
            return null;
        }
        tokens.remove(0);
        if (!expectToken(tokens, TokenType.L_BRACKET, "[", "[ after function name")) {
            return null;
        }
        List<FunctionParameterNode> parameters = FunctionParameterNode.parseParameters(tokens);
        if (parameters == null) {
            return null;
        }
        if (!expectToken(tokens, TokenType.R_BRACKET, "]", "] after parameter list")) {
            return null;
        }
        if (!expectToken(tokens, TokenType.COLON, ":", ": after parameter list")) {
            return null;
        }
        if (tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected return type but reached end of input");
            return null;
        }
        Token returnTypeToken = tokens.get(0);
        if (returnTypeToken.getTokenType() != TokenType.ID_KEYWORD || !isReturnType(returnTypeToken.getToken())) {
            System.err.println("Syntax Error");
            System.err.println("Invalid return type \"" + returnTypeToken.getToken() + "\"");
            System.err.println(returnTypeToken.getFilename() + ":" + returnTypeToken.getLineNum());
            return null;
        }
        tokens.remove(0);
        if (!expectToken(tokens, TokenType.L_BRACE, "{", "{ to start function body")) {
            return null;
        }
        FunctionBodyNode body = FunctionBodyNode.parse(tokens);
        if (body == null) {
            return null;
        }
        if (!expectToken(tokens, TokenType.R_BRACE, "}", "} to close function body")) {
            return null;
        }
        return new FunctionDefNode(nameToken, parameters, returnTypeToken, body);
    }

    private static boolean expectToken(ArrayList<Token> tokens, TokenType type, String expected, String message) {
        if (tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected " + message + " but reached end of input");
            return false;
        }
        Token token = tokens.get(0);
        if (token.getTokenType() != type || (expected != null && !expected.equals(token.getToken()))) {
            System.err.println("Syntax Error");
            System.err.println("Expected " + message + " but found \"" + token.getToken() + "\"");
            System.err.println(token.getFilename() + ":" + token.getLineNum());
            return false;
        }
        tokens.remove(0);
        return true;
    }

    private static boolean isReturnType(String text) {
        return "Void".equals(text) || "Integer".equals(text) || "Double".equals(text) || "Boolean".equals(text) || "String".equals(text);
    }

    @Override
    public String convertToJott() {
        StringBuilder builder = new StringBuilder();
        builder.append("Def ");
        builder.append(nameToken.getToken());
        builder.append("[");
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(parameters.get(i).convertToJott());
        }
        builder.append("]:");
        builder.append(returnTypeToken.getToken());
        builder.append("{");
        builder.append(body.convertToJott());
        builder.append("}");
        return builder.toString();
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
