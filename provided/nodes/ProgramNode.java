package provided.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import provided.FunctionSignature;
import provided.JottTree;
import provided.JottType;
import provided.RuntimeState;
import provided.SemanticContext;
import provided.Token;

public class ProgramNode implements JottTree {

	private final List<FunctionDefNode> functions;

	private ProgramNode(List<FunctionDefNode> functions) {
		this.functions = functions;
	}

	public static ProgramNode parse(ArrayList<Token> tokens) {
		List<FunctionDefNode> functions = new ArrayList<>();

		while (!tokens.isEmpty()) {
			int sizeBefore = tokens.size();

			FunctionDefNode function = FunctionDefNode.parse(tokens);
			if (function == null) {
				return null;
			}

			functions.add(function);

			if (tokens.size() == sizeBefore) {
				return null;
			}
		}

		return new ProgramNode(functions);
	}

	@Override
	public String convertToJott() {
		StringBuilder builder = new StringBuilder();
		for (FunctionDefNode function : functions) {
			builder.append(function.convertToJott());
		}
		return builder.toString();
	}

	private boolean populateFunctionTable(SemanticContext context) {
		context.functions().reset();
		FunctionDefNode mainFunction = null;

		int order = 0;
		for (FunctionDefNode function : functions) {
			if (context.hasError()) {
				break;
			}
			FunctionSignature signature = function.buildSignature(context);
			if (signature == null) {
				continue;
			}
			if (!context.functions().addUserFunction(signature, order)) {
				context.reportSemanticError("Function \"" + signature.getName() + "\" already defined", function.getNameToken());
				return false;
			}
			if ("main".equals(signature.getName())) {
				mainFunction = function;
			}
			order++;
		}

		if (context.hasError()) {
			return false;
		}

		// main is required
		if (mainFunction == null) {
			context.reportSemanticError("Missing main[]:Void function", functions.isEmpty() ? null : functions.get(0).getNameToken());
			return false;
		}

		FunctionSignature mainSig = mainFunction.getSignature();
		if (mainSig == null || mainSig.getReturnType() != JottType.VOID || !mainSig.getParameterTypes().isEmpty()) {
			context.reportSemanticError("main must be defined as main[]:Void", mainFunction.getNameToken());
			return false;
		}

		return true;
	}

	@Override
	public boolean validateTree(SemanticContext context) {
		if (context == null) {
			return false;
		}

		context.reset();

		if (!populateFunctionTable(context)) {
			return false;
		}

		for (FunctionDefNode function : functions) {
			if (context.hasError()) {
				break;
			}
			function.validateTree(context);
		}

		return !context.hasError();
	}

	@Override
	public void execute() {
		Map<String, FunctionDefNode> map = new HashMap<>();
		for (FunctionDefNode fn : functions) {
			map.put(fn.getName(), fn);
		}
		RuntimeState runtime = new RuntimeState(map);
		runtime.callFunction("main", Collections.emptyList());
	}
}
