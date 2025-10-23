package provided.nodes;

import java.util.ArrayList;

import provided.JottType;
import provided.SemanticContext;
import provided.Token;
import provided.TokenType;

public class StringNode implements OperandNode {

	private final Token stringToken;

	private StringNode(Token stringToken) {
		this.stringToken = stringToken;
	}

	public static StringNode parse(ArrayList<Token> tokens) {
		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected string literal but reached end of input");
			return null;
		}

		Token token = tokens.get(0);
		if (token.getTokenType() != TokenType.STRING) {
			System.err.println("Syntax Error");
			System.err.println("Expected string literal but found \"" + token.getToken() + "\"");
			System.err.println(token.getFilename() + ":" + token.getLineNum());
			return null;
		}

		tokens.remove(0);
		return new StringNode(token);
	}

	@Override
	public String convertToJott() {
		return stringToken.getToken();
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
		return true;
	}

	@Override
	public JottType getType() {
		return JottType.STRING;
	}

	@Override
	public Token getToken() {
		return stringToken;
	}
}
