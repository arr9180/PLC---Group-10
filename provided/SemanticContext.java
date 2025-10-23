package provided;

import java.util.ArrayDeque;
import java.util.Deque;

public class SemanticContext {

	private static class FunctionScope {
		private final FunctionSignature signature;
		private final VariableTable variables;

		private FunctionScope(FunctionSignature signature) {
			this.signature = signature;
			this.variables = new VariableTable();
			this.variables.pushScope();
		}
	}

	private final FunctionTable functions = new FunctionTable();
	private final Deque<FunctionScope> functionStack = new ArrayDeque<>();
	private boolean hasError = false;

	public void reset() {
		functions.reset();
		functionStack.clear();
		hasError = false;
	}

	public FunctionTable functions() {
		return functions;
	}

	public void enterFunction(FunctionSignature signature) {
		functionStack.push(new FunctionScope(signature));
	}

	public void exitFunction() {
		if (!functionStack.isEmpty()) {
			functionStack.pop();
		}
	}

	public VariableTable variables() {
		return functionStack.isEmpty() ? null : functionStack.peek().variables;
	}

	public FunctionSignature currentFunction() {
		return functionStack.isEmpty() ? null : functionStack.peek().signature;
	}

	public void reportSemanticError(String message, Token token) {
		// only report first error
		if (hasError) {
			return;
		}
		System.err.println("Semantic Error");
		System.err.println(message);
		if (token != null) {
			System.err.println(token.getFilename() + ":" + token.getLineNum());
		}
		hasError = true;
	}

	public boolean hasError() {
		return hasError;
	}
}
