package provided.nodes;

import java.util.ArrayList;

import provided.ReturnSignal;
import provided.JottTree;
import provided.JottType;
import provided.RuntimeState;
import provided.SemanticContext;
import provided.Token;
import provided.TokenType;
import provided.RuntimeValue;
import provided.VariableTable;

public class AssignmentNode implements JottTree {

	private final IdNode identifier;
	private final ExpressionNode expression;

	private AssignmentNode(IdNode identifier, ExpressionNode expression) {
		this.identifier = identifier;
		this.expression = expression;
	}

	public static AssignmentNode parse(ArrayList<Token> tokens) {
		IdNode identifier = IdNode.parseIDNode(tokens);
		if (identifier == null) {
			return null;
		}

		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected = in assignment but reached end of input");
			return null;
		}

		Token assign = tokens.get(0);
		if (assign.getTokenType() != TokenType.ASSIGN) {
			System.err.println("Syntax Error");
			System.err.println("Expected = in assignment but found \"" + assign.getToken() + "\"");
			System.err.println(assign.getFilename() + ":" + assign.getLineNum());
			return null;
		}
		tokens.remove(0);

		ExpressionNode expression = ExpressionNode.parse(tokens);
		if (expression == null) {
			return null;
		}

		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected ; after assignment but reached end of input");
			return null;
		}

		Token semicolon = tokens.get(0);
		if (semicolon.getTokenType() != TokenType.SEMICOLON) {
			System.err.println("Syntax Error");
			System.err.println("Expected ; after assignment but found \"" + semicolon.getToken() + "\"");
			System.err.println(semicolon.getFilename() + ":" + semicolon.getLineNum());
			return null;
		}
		tokens.remove(0);

		return new AssignmentNode(identifier, expression);
	}

	@Override
	public String convertToJott() {
		return identifier.convertToJott() + "=" + expression.convertToJott() + ";";
	}

	@Override
	public boolean validateTree(SemanticContext context) {
		if (context.hasError()) {
			return false;
		}

		VariableTable.VariableEntry entry = context.variables().lookup(identifier.getName());
		if (entry == null) {
			context.reportSemanticError("Assigning to unknown variable \"" + identifier.getName() + "\"", identifier.getToken());
			return false;
		}

		if (!expression.validateTree(context)) {
			return false;
		}

		if (context.hasError()) {
			return false;
		}

		JottType exprType = expression.getType();
		if (exprType == JottType.VOID) {
			context.reportSemanticError("Cannot assign Void result to \"" + identifier.getName() + "\"", identifier.getToken());
			return false;
		}

		if (entry.getType() != exprType) {
			context.reportSemanticError("Type mismatch assigning to \"" + identifier.getName() + "\"", identifier.getToken());
			return false;
		}

		entry.setInitialized();
		return true;
	}

	@Override
	public void execute() {
		throw new UnsupportedOperationException("Execute not implemented yet");
	}

	public ReturnSignal execute(RuntimeState state) {
		RuntimeValue val = expression.evaluate(state);
		state.setVar(identifier.getName(), val);
		return ReturnSignal.continueFlow();
	}
}
