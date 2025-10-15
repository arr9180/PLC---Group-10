package phase2.nodes;

import java.util.ArrayList;
import java.util.List;

import phase2.JottTree;
import phase2.Token;
import phase2.TokenType;

public class FunctionBodyNode implements JottTree {

    private final List<VariableDecNode> variableDeclarations;
    private final List<StatementNode> statements;
    private final ReturnNode returnNode;

    private FunctionBodyNode(List<VariableDecNode> variableDeclarations, List<StatementNode> statements, ReturnNode returnNode) {
        this.variableDeclarations = variableDeclarations;
        this.statements = statements;
        this.returnNode = returnNode;
    }

    public static FunctionBodyNode parse(ArrayList<Token> tokens) {
        // Parse variable declarations at the start of function body
        List<VariableDecNode> variableDeclarations = new ArrayList<>();
        while (looksLikeVariableDeclaration(tokens)) {
            VariableDecNode declaration = VariableDecNode.parse(tokens);
            if (declaration == null) {
                return null;
            }
            variableDeclarations.add(declaration);
        }

        // Parse statements and return
        BodyBlock bodyBlock = parseBodyBlock(tokens);
        if (bodyBlock == null) {
            return null;
        }

        return new FunctionBodyNode(variableDeclarations, bodyBlock.statements, bodyBlock.returnNode);
    }

    static BodyBlock parseBodyBlock(ArrayList<Token> tokens) {
        // Parse all statements until return or closing brace
        List<StatementNode> statements = new ArrayList<>();
        while (!tokens.isEmpty()) {
            Token next = tokens.get(0);

            // Stop at closing brace
            if (next.getTokenType() == TokenType.R_BRACE) {
                break;
            }

            // Stop at return statement
            if (isReturnToken(next)) {
                break;
            }

            StatementNode statement = StatementNode.parse(tokens);
            if (statement == null) {
                return null;
            }
            statements.add(statement);
        }

        // Parse optional return statement
        ReturnNode returnNode = null;
        if (!tokens.isEmpty() && isReturnToken(tokens.get(0))) {
            returnNode = ReturnNode.parse(tokens);
            if (returnNode == null) {
                return null;
            }
        }

        return new BodyBlock(statements, returnNode);
    }

    // Check if next tokens look like a variable declaration
    private static boolean looksLikeVariableDeclaration(ArrayList<Token> tokens) {
        if (tokens.size() < 3) {
            return false;
        }

        Token typeToken = tokens.get(0);
        if (typeToken.getTokenType() != TokenType.ID_KEYWORD || !VariableDecNode.isValidType(typeToken.getToken())) {
            return false;
        }

        Token idToken = tokens.get(1);
        if (idToken.getTokenType() != TokenType.ID_KEYWORD || idToken.getToken().isEmpty() || !Character.isLowerCase(idToken.getToken().charAt(0))) {
            return false;
        }

        Token third = tokens.get(2);
        return third.getTokenType() == TokenType.SEMICOLON;
    }

    // Check if token is Return keyword
    private static boolean isReturnToken(Token token) {
        return token.getTokenType() == TokenType.ID_KEYWORD && "Return".equals(token.getToken());
    }

    @Override
    public String convertToJott() {
        StringBuilder builder = new StringBuilder();
        for (VariableDecNode declaration : variableDeclarations) {
            builder.append(declaration.convertToJott());
        }
        for (StatementNode statement : statements) {
            builder.append(statement.convertToJott());
        }
        if (returnNode != null) {
            builder.append(returnNode.convertToJott());
        }
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

    static class BodyBlock {
        final List<StatementNode> statements;
        final ReturnNode returnNode;

        BodyBlock(List<StatementNode> statements, ReturnNode returnNode) {
            this.statements = statements;
            this.returnNode = returnNode;
        }
    }
}
