package testers;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import provided.JottParser;
import provided.JottTokenizer;
import provided.JottTree;
import provided.SemanticContext;
import provided.Token;

public class JottMainTester {

	private final Map<String, Boolean> expectations = new LinkedHashMap<>();

	private void createExpectations() {
		expectations.put("helloWorld.jott", true);
		expectations.put("ifStmtReturns.jott", true);
		expectations.put("largerValid.jott", true);
		expectations.put("providedExample1.jott", true);
		expectations.put("validLoop.jott", true);

		expectations.put("funcNotDefined.jott", false);
		expectations.put("funcCallParamInvalid.jott", false);
		expectations.put("funcWrongParamType.jott", false);
		expectations.put("funcReturnInExpr.jott", false);
		expectations.put("mismatchedReturn.jott", false);
		expectations.put("missingFuncParams.jott", false);
		expectations.put("missingMain.jott", false);
		expectations.put("mainReturnNotInt.jott", false);
		expectations.put("missingReturn.jott", false);
		expectations.put("noReturnIf.jott", false);
		expectations.put("noReturnWhile.jott", false);
		expectations.put("returnId.jott", false);
		expectations.put("voidReturn.jott", false);
		expectations.put("whileKeyword.jott", false);
		expectations.put("mixedMathTypes.jott", false);
	}

	private boolean runTest(String filename, boolean expectSuccess) {
		System.out.println("Running Test: " + filename + " (expect " + (expectSuccess ? "success" : "failure") + ")");

		ArrayList<Token> tokens = JottTokenizer.tokenize("phase3testcases/" + filename);
		if (tokens == null) {
			System.err.println("\tTokenizer returned null.");
			return false;
		}

		ArrayList<Token> parseTokens = new ArrayList<>(tokens);
		JottTree tree = JottParser.parse(parseTokens);
		if (tree == null) {
			System.err.println("\tParser returned null.");
			return false;
		}

		SemanticContext context = new SemanticContext();
		context.reset();
		boolean valid = tree.validateTree(context);
		boolean semanticFailed = context.hasError() || !valid;

		if (expectSuccess && semanticFailed) {
			System.err.println("\tExpected semantic success but errors were reported.");
			return false;
		}

		if (!expectSuccess && !semanticFailed) {
			System.err.println("\tExpected semantic failure but validation succeeded.");
			return false;
		}

		System.out.println("\tPassed\n");
		return true;
	}

	public static void main(String[] args) {
		System.out.println("NOTE: System.err may print during tests. This is expected.");

		JottMainTester tester = new JottMainTester();
		tester.createExpectations();

		File dir = new File("phase3testcases");
		if (!dir.isDirectory()) {
			System.err.println("phase3testcases directory not found.");
			return;
		}

		int total = 0;
		int passed = 0;

		for (Map.Entry<String, Boolean> entry : tester.expectations.entrySet()) {
			total++;
			String filename = entry.getKey();
			boolean expectSuccess = entry.getValue();
			if (tester.runTest(filename, expectSuccess)) {
				passed++;
			} else {
				System.out.println("\tFailed\n");
			}
		}

		for (File file : dir.listFiles()) {
			if (file.getName().endsWith(".jott") && !tester.expectations.containsKey(file.getName())) {
				System.out.println("Warning: no expectation defined for " + file.getName());
			}
		}

		System.out.printf("Passed: %d/%d%n", passed, total);
	}
}
