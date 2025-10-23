package provided.nodes;

import java.util.ArrayList;

import provided.JottTree;
import provided.JottType;
import provided.SemanticContext;
import provided.Token;
import provided.TokenType;

public class ExpressionNode implements JottTree {

	private final JottTree node;
	private final Token firstToken;
	private JottType type = null;

	private ExpressionNode(JottTree node, Token firstToken) {
		this.node = node;
		this.firstToken = firstToken;
	}

	public static ExpressionNode parse(ArrayList<Token> tokens) {
		if (tokens.isEmpty()) {
			System.err.println("Syntax Error");
			System.err.println("Expected expression but reached end of input");
			return null;
		}

		Token first = tokens.get(0);

		if (first.getTokenType() == TokenType.STRING) {
			StringNode stringNode = StringNode.parse(tokens);
			if (stringNode == null) {
				return null;
			}
			return new ExpressionNode(stringNode, stringNode.getToken());
		}

		if (first.getTokenType() == TokenType.ID_KEYWORD && BooleanNode.isBooleanLiteral(first.getToken())) {
			BooleanNode booleanNode = BooleanNode.parse(tokens);
			if (booleanNode == null) {
				return null;
			}
			return new ExpressionNode(booleanNode, booleanNode.getToken());
		}

		OperandNode leftOperand = OperandNode.parseOperand(tokens);
		if (leftOperand == null) {
			return null;
		}

		if (tokens.isEmpty()) {
			return new ExpressionNode((JottTree) leftOperand, leftOperand.getToken());
		}

		Token operator = tokens.get(0);
		if (operator.getTokenType() != TokenType.REL_OP && operator.getTokenType() != TokenType.MATH_OP) {
			return new ExpressionNode((JottTree) leftOperand, leftOperand.getToken());
		}
		tokens.remove(0);

		OperandNode rightOperand = OperandNode.parseOperand(tokens);
		if (rightOperand == null) {
			return null;
		}

		BinaryOpNode binary = new BinaryOpNode(leftOperand, operator, rightOperand);
		return new ExpressionNode(binary, leftOperand.getToken());
	}

	@Override
	public String convertToJott() {
		return node.convertToJott();
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
		if (node instanceof OperandNode) {
			OperandNode operand = (OperandNode) node;
			boolean ok = operand.validateTree(context);
			type = operand.getType();
			return ok;
		}

		if (node instanceof BinaryOpNode) {
			BinaryOpNode binary = (BinaryOpNode) node;
			boolean ok = binary.validateTree(context);
			type = binary.getType();
			return ok;
		}

		type = null;
		return node.validateTree(context);
	}

	public JottType getType() {
		return type;
	}

	public Token getToken() {
		return firstToken;
	}
}
