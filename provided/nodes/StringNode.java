package provided.nodes;

import java.util.ArrayList;

import provided.JottType;
import provided.RuntimeState;
import provided.SemanticContext;
import provided.Token;
import provided.TokenType;
import provided.RuntimeValue;

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

	@Override
	public void execute() {
		throw new UnsupportedOperationException("Execute not implemented yet");
	}

	@Override
	public RuntimeValue evaluate(RuntimeState state) {
		String lexeme = stringToken.getToken();
		String content = lexeme.substring(1, lexeme.length() - 1);
		return RuntimeValue.stringValue(content);
	}
}
