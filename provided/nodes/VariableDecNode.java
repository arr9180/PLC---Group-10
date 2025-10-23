package provided.nodes;

import java.util.ArrayList;

import provided.JottTree;
import provided.JottType;
import provided.SemanticContext;
import provided.Token;
import provided.TokenType;
import provided.VariableTable;

public class VariableDecNode implements JottTree {

	private final Token typeToken;
	private final Token idToken;
	private final JottType type;

	private VariableDecNode(Token typeToken, Token idToken, JottType type) {
		this.typeToken = typeToken;
		this.idToken = idToken;
		this.type = type;
	}

	public static VariableDecNode parse(ArrayList<Token> tokens) {
		if (tokens.size() < 3) {
			System.err.println("Syntax Error");
			System.err.println("Invalid variable declaration");
			return null;
		}

		Token typeToken = tokens.get(0);
		if (typeToken.getTokenType() != TokenType.ID_KEYWORD || !isValidType(typeToken.getToken())) {
			System.err.println("Syntax Error");
			System.err.println("Invalid type \"" + typeToken.getToken() + "\" in declaration");
			System.err.println(typeToken.getFilename() + ":" + typeToken.getLineNum());
			return null;
		}
		tokens.remove(0);

		Token idToken = tokens.get(0);
        if (idToken.getTokenType() != TokenType.ID_KEYWORD || idToken.getToken().isEmpty() || !Character.isLetter(idToken.getToken().charAt(0))) {
            System.err.println("Syntax Error");
            System.err.println("Invalid identifier \"" + idToken.getToken() + "\" in declaration");
            System.err.println(idToken.getFilename() + ":" + idToken.getLineNum());
            return null;
        }
		tokens.remove(0);

		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected ; after variable declaration");
			return null;
		}

		Token semicolon = tokens.get(0);
		if (semicolon.getTokenType() != TokenType.SEMICOLON) {
			System.err.println("Syntax Error");
			System.err.println("Expected ; after variable declaration but found \"" + semicolon.getToken() + "\"");
			System.err.println(semicolon.getFilename() + ":" + semicolon.getLineNum());
			return null;
		}
		tokens.remove(0);

		JottType type = JottType.fromString(typeToken.getToken());
		return new VariableDecNode(typeToken, idToken, type);
	}

	static boolean isValidType(String text) {
		return "Integer".equals(text) || "Double".equals(text) || "String".equals(text) || "Boolean".equals(text);
	}

	public Token getIdToken() {
		return idToken;
	}

	public JottType getType() {
		return type;
	}

	@Override
	public String convertToJott() {
		return typeToken.getToken() + " " + idToken.getToken() + ";";
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
		if (context.hasError()) {
			return false;
		}

		String name = idToken.getToken();
		if (!(name != null && !name.isEmpty() && Character.isLowerCase(name.charAt(0)))) {
			context.reportSemanticError("Variable \"" + name + "\" must start with a lowercase letter", idToken);
			return false;
		}
		if (isReserved(name) || isBuiltinFunction(name)) {
			context.reportSemanticError("Variable \"" + name + "\" cannot use a reserved keyword", idToken);
			return false;
		}

		VariableTable variables = context.variables();
		if (!variables.declare(idToken.getToken(), type, false)) {
			context.reportSemanticError("Variable \"" + idToken.getToken() + "\" already defined in this scope", idToken);
			return false;
		}
		return true;
	}

	private boolean isReserved(String name) {
		if (name == null) {
			return false;
		}
		String lower = name.toLowerCase();
		return lower.equals("def") || lower.equals("return") || lower.equals("if")
				|| lower.equals("else") || lower.equals("elseif") || lower.equals("while")
				|| lower.equals("true") || lower.equals("false") || lower.equals("void")
				|| lower.equals("integer") || lower.equals("double") || lower.equals("boolean")
				|| lower.equals("string");
	}

	private boolean isBuiltinFunction(String name) {
		if (name == null) {
			return false;
		}
		String lower = name.toLowerCase();
		return lower.equals("print") || lower.equals("concat") || lower.equals("length");
	}
}
