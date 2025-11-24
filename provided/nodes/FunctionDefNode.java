package provided.nodes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import provided.ReturnSignal;
import provided.FunctionSignature;
import provided.JottTree;
import provided.JottType;
import provided.RuntimeState;
import provided.SemanticContext;
import provided.Token;
import provided.TokenType;
import provided.RuntimeValue;
import provided.VariableTable;

public class FunctionDefNode implements JottTree {

	private final Token nameToken;
	private final List<FunctionParameterNode> parameters;
	private final Token returnTypeToken;
	private final FunctionBodyNode body;
	private FunctionSignature signature;

	private FunctionDefNode(Token nameToken, List<FunctionParameterNode> parameters, Token returnTypeToken, FunctionBodyNode body) {
		this.nameToken = nameToken;
		this.parameters = parameters;
		this.returnTypeToken = returnTypeToken;
		this.body = body;
	}

	public static FunctionDefNode parse(ArrayList<Token> tokens) {
		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected Def but reached end of input");
			return null;
		}

		Token defToken = tokens.get(0);
		if (defToken.getTokenType() != TokenType.ID_KEYWORD || !"Def".equals(defToken.getToken())) {
			System.err.println("Syntax Error");
			System.err.println("Expected Def but found \"" + defToken.getToken() + "\"");
			System.err.println(defToken.getFilename() + ":" + defToken.getLineNum());
			return null;
		}
		tokens.remove(0);

		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected function name but reached end of input");
			return null;
		}

		Token nameToken = tokens.get(0);
		if (nameToken.getTokenType() != TokenType.ID_KEYWORD || nameToken.getToken().isEmpty() || !Character.isLowerCase(nameToken.getToken().charAt(0))) {
			System.err.println("Syntax Error");
			System.err.println("Expected identifier for function name but found \"" + nameToken.getToken() + "\"");
			System.err.println(nameToken.getFilename() + ":" + nameToken.getLineNum());
			return null;
		}
		tokens.remove(0);

		if (!expectToken(tokens, TokenType.L_BRACKET, "[", "[ after function name")) {
			return null;
		}

		List<FunctionParameterNode> parameters = FunctionParameterNode.parseParameters(tokens);
		if (parameters == null) {
			return null;
		}

		if (!expectToken(tokens, TokenType.R_BRACKET, "]", "] after parameter list")) {
			return null;
		}

		if (!expectToken(tokens, TokenType.COLON, ":", ": after parameter list")) {
			return null;
		}

		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected return type but reached end of input");
			return null;
		}

		Token returnTypeToken = tokens.get(0);
		if (returnTypeToken.getTokenType() != TokenType.ID_KEYWORD || !isReturnType(returnTypeToken.getToken())) {
			System.err.println("Syntax Error");
			System.err.println("Invalid return type \"" + returnTypeToken.getToken() + "\"");
			System.err.println(returnTypeToken.getFilename() + ":" + returnTypeToken.getLineNum());
			return null;
		}
		tokens.remove(0);

		if (!expectToken(tokens, TokenType.L_BRACE, "{", "{ to start function body")) {
			return null;
		}

		FunctionBodyNode body = FunctionBodyNode.parse(tokens);
		if (body == null) {
			return null;
		}

		if (!expectToken(tokens, TokenType.R_BRACE, "}", "} to close function body")) {
			return null;
		}

		return new FunctionDefNode(nameToken, parameters, returnTypeToken, body);
	}

	private static boolean expectToken(ArrayList<Token> tokens, TokenType type, String expected, String message) {
		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected " + message + " but reached end of input");
			return false;
		}

		Token token = tokens.get(0);
		if (token.getTokenType() != type || (expected != null && !expected.equals(token.getToken()))) {
			System.err.println("Syntax Error");
			System.err.println("Expected " + message + " but found \"" + token.getToken() + "\"");
			System.err.println(token.getFilename() + ":" + token.getLineNum());
			return false;
		}

		tokens.remove(0);
		return true;
	}

	private static boolean isReturnType(String text) {
		return "Void".equals(text) || "Integer".equals(text) || "Double".equals(text) || "Boolean".equals(text) || "String".equals(text);
	}

	public Token getNameToken() {
		return nameToken;
	}

	public String getName() {
		return nameToken.getToken();
	}

	public FunctionSignature getSignature() {
		return signature;
	}

	public FunctionSignature buildSignature(SemanticContext context) {
		if (signature != null) {
			return signature;
		}

		JottType returnType = JottType.fromString(returnTypeToken.getToken());
		if (returnType == null) {
			context.reportSemanticError("Unknown return type \"" + returnTypeToken.getToken() + "\"", returnTypeToken);
			return null;
		}

		String functionName = nameToken.getToken();
		if (!(functionName != null && !functionName.isEmpty() && Character.isLowerCase(functionName.charAt(0)))) {
			context.reportSemanticError("Function name \"" + functionName + "\" must start with a lowercase letter", nameToken);
			return null;
		}
		if (isReserved(functionName) || isBuiltinFunction(functionName)) {
			context.reportSemanticError("Function name \"" + functionName + "\" is reserved", nameToken);
			return null;
		}

		List<JottType> parameterTypes = new ArrayList<>();
		Set<String> seenNames = new HashSet<>();

		for (FunctionParameterNode parameter : parameters) {
			JottType type = parameter.getType();
			if (type == null) {
				context.reportSemanticError("Unknown parameter type \"" + parameter.getTypeToken().getToken() + "\"", parameter.getTypeToken());
				return null;
			}
			parameterTypes.add(type);
			if (!seenNames.add(parameter.getName())) {
				context.reportSemanticError("Duplicate parameter \"" + parameter.getName() + "\"", parameter.getIdToken());
				return null;
			}
		}

		signature = new FunctionSignature(nameToken.getToken(), parameterTypes, returnType, false);
		return signature;
	}

	@Override
	public String convertToJott() {
		StringBuilder builder = new StringBuilder();
		builder.append("Def ");
		builder.append(nameToken.getToken());
		builder.append("[");
		for (int i = 0; i < parameters.size(); i++) {
			if (i > 0) {
				builder.append(",");
			}
			builder.append(parameters.get(i).convertToJott());
		}
		builder.append("]:");
		builder.append(returnTypeToken.getToken());
		builder.append("{");
		builder.append(body.convertToJott());
		builder.append("}");
		return builder.toString();
	}

	@Override
	public boolean validateTree(SemanticContext context) {
		if (context.hasError()) {
			return false;
		}

		if (signature == null) {
			buildSignature(context);
			if (signature == null) {
				return false;
			}
		}

		context.enterFunction(signature);
		VariableTable variables = context.variables();

		// check all params are valid
		for (FunctionParameterNode parameter : parameters) {
			String paramName = parameter.getName();
			if (!(paramName != null && !paramName.isEmpty() && Character.isLowerCase(paramName.charAt(0)))) {
				context.reportSemanticError("Parameter \"" + paramName + "\" must start with a lowercase letter", parameter.getIdToken());
				context.exitFunction();
				return false;
			}
			if (isReserved(paramName) || isBuiltinFunction(paramName)) {
				context.reportSemanticError("Parameter \"" + paramName + "\" cannot use a reserved keyword", parameter.getIdToken());
				context.exitFunction();
				return false;
			}
			if (!variables.declare(parameter.getName(), parameter.getType(), true)) {
				context.reportSemanticError("Duplicate parameter \"" + parameter.getName() + "\"", parameter.getIdToken());
				context.exitFunction();
				return false;
			}
		}

		if (!body.validateTree(context)) {
			context.exitFunction();
			return false;
		}

		if (context.hasError()) {
			context.exitFunction();
			return false;
		}

		if (signature.getReturnType() != JottType.VOID && !body.alwaysReturns()) {
			context.reportSemanticError("Missing return for non-Void function " + signature.getName(), nameToken);
			context.exitFunction();
			return false;
		}

		context.exitFunction();
		return !context.hasError();
	}

	@Override
	public void execute() {
		throw new UnsupportedOperationException("Execute not implemented yet");
	}

	public RuntimeValue invoke(RuntimeState state, List<RuntimeValue> args) {
		state.pushFrame();
		for (int i = 0; i < parameters.size(); i++) {
			FunctionParameterNode param = parameters.get(i);
			state.declareVar(param.getName(), param.getType(), true);
			state.setVar(param.getName(), args.get(i));
		}
		for (VariableDecNode dec : body.getVariableDeclarations()) {
			state.declareVar(dec.getIdToken().getToken(), dec.getType(), false);
		}
		ReturnSignal result = body.executeBody(state);
		state.popFrame();
		if (result.hasReturned()) {
			return result.getValue();
		}
		return RuntimeValue.voidValue();
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
