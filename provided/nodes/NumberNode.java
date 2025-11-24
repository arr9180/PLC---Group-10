package provided.nodes;

import java.util.ArrayList;

import provided.JottType;
import provided.RuntimeState;
import provided.SemanticContext;
import provided.Token;
import provided.TokenType;
import provided.RuntimeValue;

public class NumberNode implements OperandNode {

	private final Token numberToken;
	private final boolean negative;
	private final JottType type;

	private NumberNode(Token numberToken, boolean negative) {
		this.numberToken = numberToken;
		this.negative = negative;
		this.type = numberToken.getToken().contains(".") ? JottType.DOUBLE : JottType.INTEGER;
	}

	public static NumberNode parseNumberNode(ArrayList<Token> tokens) {
		if (tokens == null || tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected number but reached end of input");
			return null;
		}

		boolean negative = false;
		Token next = tokens.get(0);
		if (next.getTokenType() == TokenType.MATH_OP && "-".equals(next.getToken())) {
			negative = true;
			tokens.remove(0);

			if (tokens.isEmpty()) {
				System.err.println("Syntax Error");
				System.err.println("Expected number after - but reached end of input");
				return null;
			}
			next = tokens.get(0);
		}

		if (next.getTokenType() != TokenType.NUMBER) {
			System.err.println("Syntax Error");
			System.err.println("Expected number but found \"" + next.getToken() + "\"");
			System.err.println(next.getFilename() + ":" + next.getLineNum());
			return null;
		}

		String lexeme = next.getToken();
		if (!lexeme.matches("(?:\\d+(?:\\.\\d*)?|\\.\\d+)")) {
			System.err.println("Syntax Error");
			System.err.println("Invalid number literal \"" + lexeme + "\"");
			System.err.println(next.getFilename() + ":" + next.getLineNum());
			return null;
		}

		tokens.remove(0);
		return new NumberNode(next, negative);
	}

	@Override
	public String convertToJott() {
		if (negative) {
			return "-" + numberToken.getToken();
		}
		return numberToken.getToken();
	}

	@Override
	public boolean validateTree(SemanticContext context) {
		return true;
	}

	public boolean isZero() {
		try {
			double value = Double.parseDouble(numberToken.getToken());
			if (negative) {
				value = -value;
			}
			return Double.compare(value, 0.0) == 0;
		} catch (NumberFormatException exception) {
			return false;
		}
	}

	@Override
	public JottType getType() {
		return type;
	}

	@Override
	public Token getToken() {
		return numberToken;
	}

	@Override
	public void execute() {
		throw new UnsupportedOperationException("Execute not implemented yet");
	}

	@Override
	public RuntimeValue evaluate(RuntimeState state) {
		double v = Double.parseDouble(numberToken.getToken());
		if (negative) {
			v = -v;
		}
		if (type == JottType.INTEGER) {
			return RuntimeValue.integerValue((int) v);
		}
		return RuntimeValue.doubleValue(v);
	}
}
