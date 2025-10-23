package provided.nodes;

import java.util.ArrayList;
import java.util.List;

import provided.JottTree;
import provided.JottType;
import provided.SemanticContext;
import provided.Token;
import provided.TokenType;

public class FunctionParameterNode implements JottTree {

	private final Token idToken;
	private final Token typeToken;
	private final JottType type;

	private FunctionParameterNode(Token idToken, Token typeToken, JottType type) {
		this.idToken = idToken;
		this.typeToken = typeToken;
		this.type = type;
	}

	public static List<FunctionParameterNode> parseParameters(ArrayList<Token> tokens) {
		List<FunctionParameterNode> parameters = new ArrayList<>();

		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected parameter list but reached end of input");
			return null;
		}

		if (tokens.get(0).getTokenType() == TokenType.R_BRACKET) {
			return parameters;
		}

		while (true) {
			FunctionParameterNode parameter = parseSingleParameter(tokens);
			if (parameter == null) {
				return null;
			}
			parameters.add(parameter);

			if (tokens.isEmpty()) {
				System.err.println("Syntax Error");
				System.err.println("Expected , or ] after function parameter");
				return null;
			}

			Token next = tokens.get(0);
			if (next.getTokenType() == TokenType.COMMA) {
				tokens.remove(0);
				if (tokens.isEmpty()) {
					System.err.println("Syntax Error");
					System.err.println("Expected operand after , in function parameter list");
					return null;
				}
				continue;
			}

			if (next.getTokenType() == TokenType.R_BRACKET) {
				break;
			}

			System.err.println("Syntax Error");
			System.err.println("Expected , or ] after function parameter but found \"" + next.getToken() + "\"");
			System.err.println(next.getFilename() + ":" + next.getLineNum());
			return null;
		}

		return parameters;
	}

	private static FunctionParameterNode parseSingleParameter(ArrayList<Token> tokens) {
		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected parameter but reached end of input");
			return null;
		}

		Token idToken = tokens.get(0);
		if (idToken.getTokenType() != TokenType.ID_KEYWORD || idToken.getToken().isEmpty() || !Character.isLowerCase(idToken.getToken().charAt(0))) {
			System.err.println("Syntax Error");
			System.err.println("Invalid parameter name \"" + idToken.getToken() + "\"");
			System.err.println(idToken.getFilename() + ":" + idToken.getLineNum());
			return null;
		}
		tokens.remove(0);

		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected : after parameter name but reached end of input");
			return null;
		}

		Token colon = tokens.get(0);
		if (colon.getTokenType() != TokenType.COLON) {
			System.err.println("Syntax Error");
			System.err.println("Expected : after parameter name but found \"" + colon.getToken() + "\"");
			System.err.println(colon.getFilename() + ":" + colon.getLineNum());
			return null;
		}
		tokens.remove(0);

		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected type after : but reached end of input");
			return null;
		}

		Token typeToken = tokens.get(0);
		if (typeToken.getTokenType() != TokenType.ID_KEYWORD || !isType(typeToken.getToken())) {
			System.err.println("Syntax Error");
			System.err.println("Invalid parameter type \"" + typeToken.getToken() + "\"");
			System.err.println(typeToken.getFilename() + ":" + typeToken.getLineNum());
			return null;
		}
		tokens.remove(0);

		JottType type = JottType.fromString(typeToken.getToken());
		return new FunctionParameterNode(idToken, typeToken, type);
	}

	private static boolean isType(String text) {
		return "Integer".equals(text) || "Double".equals(text) || "String".equals(text) || "Boolean".equals(text);
	}

	public Token getIdToken() {
		return idToken;
	}

	public Token getTypeToken() {
		return typeToken;
	}

	public String getName() {
		return idToken.getToken();
	}

	public JottType getType() {
		return type;
	}

	@Override
	public String convertToJott() {
		return idToken.getToken() + ":" + typeToken.getToken();
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
}
