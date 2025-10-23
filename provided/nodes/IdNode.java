package provided.nodes;

import java.util.ArrayList;

import provided.JottType;
import provided.SemanticContext;
import provided.Token;
import provided.TokenType;
import provided.VariableTable;

public class IdNode implements OperandNode {

	private final Token idToken;
	private JottType type;

	private IdNode(Token idToken) {
		this.idToken = idToken;
	}

	public static IdNode parseIDNode(ArrayList<Token> tokens) {
		if (tokens == null || tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected identifier but reached end of input");
			return null;
		}

		Token next = tokens.get(0);
		if (next.getTokenType() != TokenType.ID_KEYWORD) {
			System.err.println("Syntax Error");
			System.err.println("Expected identifier but found \"" + next.getToken() + "\"");
			System.err.println(next.getFilename() + ":" + next.getLineNum());
			return null;
		}

		String lexeme = next.getToken();
		if (lexeme.isEmpty() || !Character.isLowerCase(lexeme.charAt(0))) {
			System.err.println("Syntax Error");
			System.err.println("Invalid identifier \"" + lexeme + "\". Identifiers must start with a lowercase letter");
			System.err.println(next.getFilename() + ":" + next.getLineNum());
			return null;
		}

		tokens.remove(0);
		return new IdNode(next);
	}

	public String getName() {
		return idToken.getToken();
	}

	@Override
	public String convertToJott() {
		return idToken.getToken();
	}

	@Override
	public String convertToJava(String className) {
		return idToken.getToken();
	}

	@Override
	public String convertToC() {
		return idToken.getToken();
	}

	@Override
	public String convertToPython() {
		return idToken.getToken();
	}

	@Override
	public boolean validateTree(SemanticContext context) {
		VariableTable.VariableEntry entry = context.variables().lookup(getName());
		if (entry == null) {
			context.reportSemanticError("Use of undefined variable \"" + getName() + "\"", idToken);
			return false;
		}

		if (!entry.isInitialized()) {
			context.reportSemanticError("Variable \"" + getName() + "\" used before assignment", idToken);
			return false;
		}

		type = entry.getType();
		return true;
	}

	@Override
	public JottType getType() {
		return type;
	}

	@Override
	public Token getToken() {
		return idToken;
	}
}
