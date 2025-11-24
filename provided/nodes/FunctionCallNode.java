package provided.nodes;

import java.util.ArrayList;
import java.util.List;

import provided.FunctionSignature;
import provided.JottType;
import provided.RuntimeState;
import provided.SemanticContext;
import provided.Token;
import provided.TokenType;
import provided.RuntimeValue;

public class FunctionCallNode implements OperandNode {

	private final Token functionNameToken;
	private final List<ExpressionNode> arguments;
	private JottType type = null;

	private FunctionCallNode(Token functionNameToken, List<ExpressionNode> arguments) {
		this.functionNameToken = functionNameToken;
		this.arguments = arguments;
	}

	public static FunctionCallNode parseFunctionCallNode(ArrayList<Token> tokens) {
		if (tokens == null || tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected function call but reached end of input");
			return null;
		}

		Token header = tokens.get(0);
		if (header.getTokenType() != TokenType.FC_HEADER) {
			System.err.println("Syntax Error");
			System.err.println("Expected function call header '::' but found \"" + header.getToken() + "\"");
			System.err.println(header.getFilename() + ":" + header.getLineNum());
			return null;
		}
		tokens.remove(0);

		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected function name after '::' but reached end of input");
			return null;
		}

		Token nameToken = tokens.get(0);
		if (nameToken.getTokenType() != TokenType.ID_KEYWORD) {
			System.err.println("Syntax Error");
			System.err.println("Expected function name but found \"" + nameToken.getToken() + "\"");
			System.err.println(nameToken.getFilename() + ":" + nameToken.getLineNum());
			return null;
		}

		String nameLexeme = nameToken.getToken();
		if (nameLexeme.isEmpty() || !Character.isLowerCase(nameLexeme.charAt(0))) {
			System.err.println("Syntax Error");
			System.err.println("Invalid function name \"" + nameLexeme + "\". Names must start with a lowercase letter");
			System.err.println(nameToken.getFilename() + ":" + nameToken.getLineNum());
			return null;
		}
		tokens.remove(0);

		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected '[' after function name but reached end of input");
			return null;
		}

		Token bracketToken = tokens.get(0);
		if (bracketToken.getTokenType() != TokenType.L_BRACKET) {
			System.err.println("Syntax Error");
			System.err.println("Expected '[' after function name but found \"" + bracketToken.getToken() + "\"");
			System.err.println(bracketToken.getFilename() + ":" + bracketToken.getLineNum());
			return null;
		}
		tokens.remove(0);

		List<ExpressionNode> args = new ArrayList<>();

		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected ']' to close function call but reached end of input");
			return null;
		}

		if (tokens.get(0).getTokenType() != TokenType.R_BRACKET) {
			while (true) {
				ExpressionNode expression = ExpressionNode.parse(tokens);
				if (expression == null) {
					return null;
				}
				args.add(expression);

				if (tokens.isEmpty()) {
					System.err.println("Syntax Error");
					System.err.println("Expected ',' or ']' after function argument but reached end of input");
					return null;
				}

				Token separator = tokens.get(0);
				if (separator.getTokenType() == TokenType.COMMA) {
					tokens.remove(0);
					if (tokens.isEmpty()) {
						System.err.println("Syntax Error");
						System.err.println("Expected operand after ',' in function call");
						return null;
					}
					continue;
				}

				if (separator.getTokenType() == TokenType.R_BRACKET) {
					break;
				}

				System.err.println("Syntax Error");
				System.err.println("Expected ',' or ']' after function argument but found \"" + separator.getToken() + "\"");
				System.err.println(separator.getFilename() + ":" + separator.getLineNum());
				return null;
			}
		}

		tokens.remove(0);

		return new FunctionCallNode(nameToken, args);
	}

	public Token getFunctionNameToken() {
		return functionNameToken;
	}

	@Override
	public String convertToJott() {
		StringBuilder builder = new StringBuilder();
		builder.append("::").append(functionNameToken.getToken()).append("[");
		for (int i = 0; i < arguments.size(); i++) {
			if (i > 0) {
				builder.append(",");
			}
			builder.append(arguments.get(i).convertToJott());
		}
		builder.append("]");
		return builder.toString();
	}

	@Override
	public boolean validateTree(SemanticContext context) {
		if (context.hasError()) {
			return false;
		}

		FunctionSignature signature = context.functions().get(functionNameToken.getToken());
		if (signature == null) {
			context.reportSemanticError("Call to unknown function \"" + functionNameToken.getToken() + "\"", functionNameToken);
			return false;
		}

		FunctionSignature currentFunction = context.currentFunction();
		String callerName = currentFunction == null ? null : currentFunction.getName();
		if (!context.functions().isDefinedBefore(functionNameToken.getToken(), callerName)) {
			context.reportSemanticError("Call to function \"" + functionNameToken.getToken() + "\" before its definition", functionNameToken);
			return false;
		}

		if (arguments.size() != signature.getParameterTypes().size()) {
			context.reportSemanticError("Incorrect argument count for \"" + signature.getName() + "\"", functionNameToken);
			return false;
		}

		for (int i = 0; i < arguments.size(); i++) {
			ExpressionNode argument = arguments.get(i);
			if (!argument.validateTree(context)) {
				return false;
			}
			if (context.hasError()) {
				return false;
			}

			JottType argType = argument.getType();
			JottType expected = signature.getParameterTypes().get(i);

			if (argType == JottType.VOID) {
				context.reportSemanticError("Void expression not allowed for parameter " + (i + 1) + " of \"" + signature.getName() + "\"", argument.getToken());
				return false;
			}

			if (expected != JottType.ANY && expected != argType) {
				context.reportSemanticError("Type mismatch for parameter " + (i + 1) + " of \"" + signature.getName() + "\"", argument.getToken());
				return false;
			}
		}

		type = signature.getReturnType();
		return true;
	}

	@Override
	public JottType getType() {
		return type;
	}

	@Override
	public Token getToken() {
		return functionNameToken;
	}

	@Override
	public void execute() {
		throw new UnsupportedOperationException("Execute not implemented yet");
	}

	@Override
	public RuntimeValue evaluate(RuntimeState state) {
		List<RuntimeValue> evaluatedArgs = new ArrayList<>();
		for (ExpressionNode arg : arguments) {
			evaluatedArgs.add(arg.evaluate(state));
		}
		return state.callFunction(functionNameToken.getToken(), evaluatedArgs);
	}
}
