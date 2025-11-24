package provided.nodes;

import java.util.ArrayList;

import provided.JottType;
import provided.RuntimeState;
import provided.SemanticContext;
import provided.Token;
import provided.TokenType;
import provided.RuntimeValue;

public class BooleanNode implements OperandNode {

	private final Token booleanToken;

	private BooleanNode(Token booleanToken) {
		this.booleanToken = booleanToken;
	}

	public static BooleanNode parse(ArrayList<Token> tokens) {
		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected boolean literal but reached end of input");
			return null;
		}

		Token token = tokens.get(0);
		if (token.getTokenType() != TokenType.ID_KEYWORD || !isBooleanLiteral(token.getToken())) {
			System.err.println("Syntax Error");
			System.err.println("Expected boolean literal but found \"" + token.getToken() + "\"");
			System.err.println(token.getFilename() + ":" + token.getLineNum());
			return null;
		}

		tokens.remove(0);
		return new BooleanNode(token);
	}

	static boolean isBooleanLiteral(String text) {
		return "True".equals(text) || "False".equals(text);
	}

	@Override
	public String convertToJott() {
		return booleanToken.getToken();
	}

	@Override
	public boolean validateTree(SemanticContext context) {
		return true;
	}

	@Override
	public JottType getType() {
		return JottType.BOOLEAN;
	}

	@Override
	public Token getToken() {
		return booleanToken;
	}

	@Override
	public void execute() {
		throw new UnsupportedOperationException("Execute not implemented yet");
	}

	@Override
	public RuntimeValue evaluate(RuntimeState state) {
		return RuntimeValue.booleanValue("True".equals(booleanToken.getToken()));
	}
}
