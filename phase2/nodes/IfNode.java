package phase2.nodes;

import java.util.ArrayList;
import java.util.List;

import phase2.JottTree;
import phase2.Token;
import phase2.TokenType;

public class IfNode implements JottTree {

    private final ExpressionNode condition;
    private final List<StatementNode> bodyStatements;
    private final ReturnNode bodyReturn;
    private final List<ElseIfClause> elseIfClauses;
    private final ElseClause elseClause;

    private IfNode(ExpressionNode condition, List<StatementNode> bodyStatements, ReturnNode bodyReturn, List<ElseIfClause> elseIfClauses, ElseClause elseClause) {
        this.condition = condition;
        this.bodyStatements = bodyStatements;
        this.bodyReturn = bodyReturn;
        this.elseIfClauses = elseIfClauses;
        this.elseClause = elseClause;
    }

    public static IfNode parse(ArrayList<Token> tokens) {
        if (tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected If but reached end of input");
            return null;
        }
        Token ifToken = tokens.get(0);
        if (ifToken.getTokenType() != TokenType.ID_KEYWORD || !"If".equals(ifToken.getToken())) {
            System.err.println("Syntax Error");
            System.err.println("Expected If but found \"" + ifToken.getToken() + "\"");
            System.err.println(ifToken.getFilename() + ":" + ifToken.getLineNum());
            return null;
        }
        tokens.remove(0);
        if (!expectToken(tokens, TokenType.L_BRACKET, "[", "[ after If")) {
            return null;
        }
        ExpressionNode condition = ExpressionNode.parse(tokens);
        if (condition == null) {
            return null;
        }
        if (!expectToken(tokens, TokenType.R_BRACKET, "]", "] after If condition")) {
            return null;
        }
        if (!expectToken(tokens, TokenType.L_BRACE, "{", "{ to start If body")) {
            return null;
        }
        FunctionBodyNode.BodyBlock bodyBlock = FunctionBodyNode.parseBodyBlock(tokens);
        if (bodyBlock == null) {
            return null;
        }
        if (!expectToken(tokens, TokenType.R_BRACE, "}", "} to close If body")) {
            return null;
        }
        List<ElseIfClause> elseIfClauses = new ArrayList<>();
        while (hasElseIf(tokens)) {
            ElseIfClause clause = parseElseIf(tokens);
            if (clause == null) {
                return null;
            }
            elseIfClauses.add(clause);
        }
        ElseClause elseClause = null;
        if (hasElse(tokens)) {
            elseClause = parseElse(tokens);
            if (elseClause == null) {
                return null;
            }
        }
        return new IfNode(condition, bodyBlock.statements, bodyBlock.returnNode, elseIfClauses, elseClause);
    }

    private static boolean expectToken(ArrayList<Token> tokens, TokenType type, String expectedToken, String message) {
        if (tokens.isEmpty()) {
            System.err.println("Syntax Error");
            System.err.println("Expected " + message + " but reached end of input");
            return false;
        }
        Token token = tokens.get(0);
        if (token.getTokenType() != type || (expectedToken != null && !expectedToken.equals(token.getToken()))) {
            System.err.println("Syntax Error");
            System.err.println("Expected " + message + " but found \"" + token.getToken() + "\"");
            System.err.println(token.getFilename() + ":" + token.getLineNum());
            return false;
        }
        tokens.remove(0);
        return true;
    }

    private static boolean hasElseIf(ArrayList<Token> tokens) {
        if (tokens.isEmpty()) {
            return false;
        }
        Token token = tokens.get(0);
        return token.getTokenType() == TokenType.ID_KEYWORD && "Elseif".equals(token.getToken());
    }

    private static ElseIfClause parseElseIf(ArrayList<Token> tokens) {
        Token keyword = tokens.get(0);
        tokens.remove(0);
        if (!expectToken(tokens, TokenType.L_BRACKET, "[", "[ after Elseif")) {
            return null;
        }
        ExpressionNode condition = ExpressionNode.parse(tokens);
        if (condition == null) {
            return null;
        }
        if (!expectToken(tokens, TokenType.R_BRACKET, "]", "] after Elseif condition")) {
            return null;
        }
        if (!expectToken(tokens, TokenType.L_BRACE, "{", "{ to start Elseif body")) {
            return null;
        }
        FunctionBodyNode.BodyBlock bodyBlock = FunctionBodyNode.parseBodyBlock(tokens);
        if (bodyBlock == null) {
            return null;
        }
        if (!expectToken(tokens, TokenType.R_BRACE, "}", "} to close Elseif body")) {
            return null;
        }
        return new ElseIfClause(condition, bodyBlock.statements, bodyBlock.returnNode);
    }

    private static boolean hasElse(ArrayList<Token> tokens) {
        if (tokens.isEmpty()) {
            return false;
        }
        Token token = tokens.get(0);
        return token.getTokenType() == TokenType.ID_KEYWORD && "Else".equals(token.getToken());
    }

    private static ElseClause parseElse(ArrayList<Token> tokens) {
        tokens.remove(0);
        if (!expectToken(tokens, TokenType.L_BRACE, "{", "{ to start Else body")) {
            return null;
        }
        FunctionBodyNode.BodyBlock bodyBlock = FunctionBodyNode.parseBodyBlock(tokens);
        if (bodyBlock == null) {
            return null;
        }
        if (!expectToken(tokens, TokenType.R_BRACE, "}", "} to close Else body")) {
            return null;
        }
        return new ElseClause(bodyBlock.statements, bodyBlock.returnNode);
    }

    @Override
    public String convertToJott() {
        StringBuilder builder = new StringBuilder();
        builder.append("If[");
        builder.append(condition.convertToJott());
        builder.append("]{");
        for (StatementNode statement : bodyStatements) {
            builder.append(statement.convertToJott());
        }
        if (bodyReturn != null) {
            builder.append(bodyReturn.convertToJott());
        }
        builder.append("}");
        for (ElseIfClause clause : elseIfClauses) {
            builder.append("Elseif[");
            builder.append(clause.condition.convertToJott());
            builder.append("]{");
            for (StatementNode statement : clause.statements) {
                builder.append(statement.convertToJott());
            }
            if (clause.returnNode != null) {
                builder.append(clause.returnNode.convertToJott());
            }
            builder.append("}");
        }
        if (elseClause != null) {
            builder.append("Else{");
            for (StatementNode statement : elseClause.statements) {
                builder.append(statement.convertToJott());
            }
            if (elseClause.returnNode != null) {
                builder.append(elseClause.returnNode.convertToJott());
            }
            builder.append("}");
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

    private static class ElseIfClause {
        final ExpressionNode condition;
        final List<StatementNode> statements;
        final ReturnNode returnNode;

        ElseIfClause(ExpressionNode condition, List<StatementNode> statements, ReturnNode returnNode) {
            this.condition = condition;
            this.statements = statements;
            this.returnNode = returnNode;
        }
    }

    private static class ElseClause {
        final List<StatementNode> statements;
        final ReturnNode returnNode;

        ElseClause(List<StatementNode> statements, ReturnNode returnNode) {
            this.statements = statements;
            this.returnNode = returnNode;
        }
    }
}
