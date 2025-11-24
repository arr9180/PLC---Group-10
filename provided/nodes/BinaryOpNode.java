package provided.nodes;

import provided.JottTree;
import provided.JottType;
import provided.RuntimeState;
import provided.SemanticContext;
import provided.Token;
import provided.TokenType;
import provided.RuntimeValue;

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

	@Override
	public void execute() {
		throw new UnsupportedOperationException("Execute not implemented yet");
	}

	public RuntimeValue evaluate(RuntimeState state) {
		RuntimeValue lVal = left.evaluate(state);
		RuntimeValue rVal = right.evaluate(state);
		String op = operatorToken.getToken();
		if (operatorToken.getTokenType() == TokenType.MATH_OP) {
			if (lVal.getType() == JottType.INTEGER && rVal.getType() == JottType.INTEGER) {
				int a = lVal.getInt();
				int b = rVal.getInt();
				if (op.equals("/")) {
					if (b == 0) {
						throw new RuntimeException("Division by zero");
					}
					return RuntimeValue.integerValue(a / b);
				}
				if (op.equals("+")) {
					return RuntimeValue.integerValue(a + b);
				}
				if (op.equals("-")) {
					return RuntimeValue.integerValue(a - b);
				}
				return RuntimeValue.integerValue(a * b);
			}
			double a = lVal.getDouble();
			double b = rVal.getDouble();
			if (op.equals("/")) {
				if (Double.compare(b, 0.0) == 0) {
					throw new RuntimeException("Division by zero");
				}
				return RuntimeValue.doubleValue(a / b);
			}
			if (op.equals("+")) {
				return RuntimeValue.doubleValue(a + b);
			}
			if (op.equals("-")) {
				return RuntimeValue.doubleValue(a - b);
			}
			return RuntimeValue.doubleValue(a * b);
		}
		if (op.equals("==")) {
			return RuntimeValue.booleanValue(equalsValue(lVal, rVal));
		}
		if (op.equals("!=")) {
			return RuntimeValue.booleanValue(!equalsValue(lVal, rVal));
		}
		if (lVal.getType().isNumeric() && rVal.getType().isNumeric()) {
			double a = lVal.getDouble();
			double b = rVal.getDouble();
			if (op.equals("<")) {
				return RuntimeValue.booleanValue(a < b);
			}
			if (op.equals("<=")) {
				return RuntimeValue.booleanValue(a <= b);
			}
			if (op.equals(">")) {
				return RuntimeValue.booleanValue(a > b);
			}
			return RuntimeValue.booleanValue(a >= b);
		}
		return RuntimeValue.booleanValue(false);
	}

	private boolean equalsValue(RuntimeValue a, RuntimeValue b) {
		if (a.getType() != b.getType()) {
			return false;
		}
		if (a.getType() == JottType.INTEGER) {
			return a.getInt() == b.getInt();
		}
		if (a.getType() == JottType.DOUBLE) {
			return Double.compare(a.getDouble(), b.getDouble()) == 0;
		}
		if (a.getType() == JottType.BOOLEAN) {
			return a.getBoolean() == b.getBoolean();
		}
		if (a.getType() == JottType.STRING) {
			return a.getString().equals(b.getString());
		}
		return true;
	}
}
