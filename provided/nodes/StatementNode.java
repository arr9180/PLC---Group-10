package provided.nodes;

import java.util.ArrayList;

import provided.JottTree;
import provided.SemanticContext;
import provided.Token;
import provided.TokenType;

public class StatementNode implements JottTree {

	private final JottTree node;
	private boolean alwaysReturns = false;

	private StatementNode(JottTree node) {
		this.node = node;
	}

	public static StatementNode parse(ArrayList<Token> tokens) {
		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected statement but reached end of input");
			return null;
		}

		Token next = tokens.get(0);

		if (next.getTokenType() == TokenType.ID_KEYWORD && "If".equals(next.getToken())
				&& hasFollowingBracket(tokens)) {
			IfNode ifNode = IfNode.parse(tokens);
			if (ifNode == null) {
				return null;
			}
			return new StatementNode(ifNode);
		}

		if (next.getTokenType() == TokenType.ID_KEYWORD && "While".equals(next.getToken())
				&& hasFollowingBracket(tokens)) {
			WhileNode whileNode = WhileNode.parse(tokens);
			if (whileNode == null) {
				return null;
			}
			return new StatementNode(whileNode);
		}

		if (next.getTokenType() == TokenType.FC_HEADER) {
			FunctionCallNode functionCall = FunctionCallNode.parseFunctionCallNode(tokens);
			if (functionCall == null) {
				return null;
			}
			if (!consumeSemicolon(tokens, functionCall.getFunctionNameToken())) {
				return null;
			}
			return new StatementNode(functionCall);
		}

		AssignmentNode assignment = AssignmentNode.parse(tokens);
		if (assignment == null) {
			return null;
		}

		return new StatementNode(assignment);
	}

	private static boolean hasFollowingBracket(ArrayList<Token> tokens) {
		return tokens.size() > 1 && tokens.get(1).getTokenType() == TokenType.L_BRACKET;
	}

	private static boolean consumeSemicolon(ArrayList<Token> tokens, Token reference) {
		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected ; after function call but reached end of input");
			if (reference != null) {
				System.err.println(reference.getFilename() + ":" + reference.getLineNum());
			}
			return false;
		}

		Token token = tokens.get(0);
		if (token.getTokenType() != TokenType.SEMICOLON) {
			System.err.println("Syntax Error");
			System.err.println("Expected ; after function call but found \"" + token.getToken() + "\"");
			System.err.println(token.getFilename() + ":" + token.getLineNum());
			return false;
		}

		tokens.remove(0);
		return true;
	}

	@Override
	public String convertToJott() {
		if (node instanceof FunctionCallNode) {
			return node.convertToJott() + ";";
		}
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
	public boolean validateTree(SemanticContext context) {
		if (node instanceof FunctionCallNode) {
			boolean ok = ((FunctionCallNode) node).validateTree(context);
			alwaysReturns = false;
			return ok;
		}

		if (node instanceof AssignmentNode) {
			boolean ok = ((AssignmentNode) node).validateTree(context);
			alwaysReturns = false;
			return ok;
		}

		if (node instanceof IfNode) {
			boolean ok = ((IfNode) node).validateTree(context);
			alwaysReturns = ((IfNode) node).alwaysReturns();
			return ok;
		}

		if (node instanceof WhileNode) {
			boolean ok = ((WhileNode) node).validateTree(context);
			alwaysReturns = false;
			return ok;
		}

		alwaysReturns = false;
		return node.validateTree(context);
	}

	public boolean alwaysReturns() {
		return alwaysReturns;
	}
}
