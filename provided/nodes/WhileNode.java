package provided.nodes;

import java.util.ArrayList;
import java.util.List;

import provided.JottTree;
import provided.JottType;
import provided.SemanticContext;
import provided.Token;
import provided.TokenType;
import provided.VariableTable;

public class WhileNode implements JottTree {

	private final ExpressionNode condition;
	private final List<StatementNode> bodyStatements;
	private final ReturnNode bodyReturn;

	private WhileNode(ExpressionNode condition, List<StatementNode> bodyStatements, ReturnNode bodyReturn) {
		this.condition = condition;
		this.bodyStatements = bodyStatements;
		this.bodyReturn = bodyReturn;
	}

	public static WhileNode parse(ArrayList<Token> tokens) {
		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected While but reached end of input");
			return null;
		}

		Token keyword = tokens.get(0);
		if (keyword.getTokenType() != TokenType.ID_KEYWORD || !"While".equals(keyword.getToken())) {
			System.err.println("Syntax Error");
			System.err.println("Expected While but found \"" + keyword.getToken() + "\"");
			System.err.println(keyword.getFilename() + ":" + keyword.getLineNum());
			return null;
		}
		tokens.remove(0);

		if (!expect(tokens, TokenType.L_BRACKET, "[", "[ after While")) {
			return null;
		}

		ExpressionNode condition = ExpressionNode.parse(tokens);
		if (condition == null) {
			return null;
		}

		if (!expect(tokens, TokenType.R_BRACKET, "]", "] after While condition")) {
			return null;
		}

		if (!expect(tokens, TokenType.L_BRACE, "{", "{ to start While body")) {
			return null;
		}

		FunctionBodyNode.BodyBlock bodyBlock = FunctionBodyNode.parseBodyBlock(tokens);
		if (bodyBlock == null) {
			return null;
		}

		if (!expect(tokens, TokenType.R_BRACE, "}", "} to close While body")) {
			return null;
		}

		return new WhileNode(condition, bodyBlock.statements, bodyBlock.returnNode);
	}

	private static boolean expect(ArrayList<Token> tokens, TokenType type, String expectedToken, String message) {
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

	@Override
	public String convertToJott() {
		StringBuilder builder = new StringBuilder();
		builder.append("While[");
		builder.append(condition.convertToJott());
		builder.append("]{");
		for (StatementNode statement : bodyStatements) {
			builder.append(statement.convertToJott());
		}
		if (bodyReturn != null) {
			builder.append(bodyReturn.convertToJott());
		}
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
	public boolean validateTree(SemanticContext context) {
		boolean ok = true;

		if (!condition.validateTree(context)) {
			ok = false;
		}
		// while needs boolean condition
		if (!context.hasError() && condition.getType() != JottType.BOOLEAN) {
			context.reportSemanticError("While condition must be Boolean", condition.getToken());
			return false;
		}

		VariableTable variables = context.variables();
		variables.pushScope();
		FunctionBodyNode.ValidationSummary summary = FunctionBodyNode.validateBlock(context, bodyStatements, bodyReturn);
		variables.popScope();
		ok &= summary.valid;

		return ok && !context.hasError();
	}
}
