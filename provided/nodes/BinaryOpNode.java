package provided.nodes;

import provided.JottTree;
import provided.JottType;
import provided.SemanticContext;
import provided.Token;
import provided.TokenType;

public class BinaryOpNode implements JottTree {

	private final OperandNode left;
	private final Token operatorToken;
	private final OperandNode right;
	private JottType resultType;

	public BinaryOpNode(OperandNode left, Token operatorToken, OperandNode right) {
		this.left = left;
		this.operatorToken = operatorToken;
		this.right = right;
	}

	@Override
	public String convertToJott() {
		return left.convertToJott() + operatorToken.getToken() + right.convertToJott();
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
		boolean ok = left.validateTree(context) & right.validateTree(context);
		if (!ok || context.hasError()) {
			return false;
		}

		JottType leftType = left.getType();
		JottType rightType = right.getType();
		String op = operatorToken.getToken();

		if (operatorToken.getTokenType() == TokenType.MATH_OP) {
			if ("/".equals(op) && right instanceof NumberNode && ((NumberNode) right).isZero()) {
				context.reportSemanticError("Division by zero", operatorToken);
				return false;
			}
			if (!leftType.isNumeric() || !rightType.isNumeric()) {
				context.reportSemanticError("Math operations require numeric operands", operatorToken);
				return false;
			}
			if (leftType != rightType) {
				context.reportSemanticError("Math operands must share the same type", operatorToken);
				return false;
			}
			resultType = leftType;
			return true;
		}

		if ("==".equals(op) || "!=".equals(op)) {
			if (leftType == JottType.VOID || rightType == JottType.VOID) {
				context.reportSemanticError("Cannot compare Void values", operatorToken);
				return false;
			}
			if (leftType != rightType) {
				context.reportSemanticError("Comparison operands must share the same type", operatorToken);
				return false;
			}
			resultType = JottType.BOOLEAN;
			return true;
		}

		if (leftType.isNumeric() && rightType.isNumeric() && leftType == rightType) {
			resultType = JottType.BOOLEAN;
			return true;
		}

		context.reportSemanticError("Relational operands must be numeric", operatorToken);
		return false;
	}

	public JottType getType() {
		return resultType;
	}
}
