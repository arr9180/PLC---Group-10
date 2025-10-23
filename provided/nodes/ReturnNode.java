package provided.nodes;

import java.util.ArrayList;

import provided.FunctionSignature;
import provided.JottTree;
import provided.JottType;
import provided.SemanticContext;
import provided.Token;
import provided.TokenType;

public class ReturnNode implements JottTree {

	private final Token returnToken;
	private final ExpressionNode expression;

	private ReturnNode(Token returnToken, ExpressionNode expression) {
		this.returnToken = returnToken;
		this.expression = expression;
	}

	public static ReturnNode parse(ArrayList<Token> tokens) {
		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected Return but reached end of input");
			return null;
		}

		Token returnToken = tokens.get(0);
		if (returnToken.getTokenType() != TokenType.ID_KEYWORD || !"Return".equals(returnToken.getToken())) {
			System.err.println("Syntax Error");
			System.err.println("Expected Return but found \"" + returnToken.getToken() + "\"");
			System.err.println(returnToken.getFilename() + ":" + returnToken.getLineNum());
			return null;
		}
		tokens.remove(0);

		ExpressionNode expression = ExpressionNode.parse(tokens);
		if (expression == null) {
			return null;
		}

		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected ; after return statement but reached end of input");
			return null;
		}

		Token semicolon = tokens.get(0);
		if (semicolon.getTokenType() != TokenType.SEMICOLON) {
			System.err.println("Syntax Error");
			System.err.println("Expected ; after return statement but found \"" + semicolon.getToken() + "\"");
			System.err.println(semicolon.getFilename() + ":" + semicolon.getLineNum());
			return null;
		}
		tokens.remove(0);

		return new ReturnNode(returnToken, expression);
	}

	@Override
	public String convertToJott() {
		return returnToken.getToken() + " " + expression.convertToJott() + ";";
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
		if (!expression.validateTree(context)) {
			return false;
		}

		if (context.hasError()) {
			return false;
		}

		FunctionSignature signature = context.currentFunction();
		if (signature == null) {
			return false;
		}

		JottType expected = signature.getReturnType();
		JottType exprType = expression.getType();

		if (expected == JottType.VOID) {
			if (exprType != JottType.VOID) {
				context.reportSemanticError("Void functions cannot return a value", returnToken);
				return false;
			}
		} else {
			if (exprType == JottType.VOID) {
				context.reportSemanticError("Return expression cannot be Void", returnToken);
				return false;
			}
			if (exprType != expected) {
				context.reportSemanticError("Return type mismatch for function \"" + signature.getName() + "\"", returnToken);
				return false;
			}
		}

		return true;
	}
}
