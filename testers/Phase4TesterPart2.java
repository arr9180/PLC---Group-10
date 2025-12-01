package testers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import provided.JottParser;
import provided.JottTokenizer;
import provided.JottTree;
import provided.SemanticContext;
import provided.Token;

public class Phase4TesterPart2 {

	private static class TestCase {
		final String name;
		final String path;
		final boolean expectSuccess;
		final String expectedOutput;

		TestCase(String name, String path, boolean expectSuccess, String expectedOutput) {
			this.name = name;
			this.path = path;
			this.expectSuccess = expectSuccess;
			this.expectedOutput = expectedOutput;
		}

		TestCase(String name, String path, boolean expectSuccess) {
			this(name, path, expectSuccess, null);
		}
	}

	private final List<TestCase> tests = new ArrayList<>();

	private void buildTests() {
		tests.add(new TestCase("hello world", "phase3testcases/helloWorld.jott", true, join("Hello World")));
		tests.add(new TestCase("if stmt returns", "phase3testcases/ifStmtReturns.jott", true, join()));
		tests.add(new TestCase("larger valid", "phase3testcases/largerValid.jott", true, join(
				"5",
				"Hello World",
				"foo",
				"4",
				"Hello World",
				"foo",
				"3",
				"Hello World",
				"foo",
				"2",
				"Hello World",
				"foo",
				"1",
				"bar",
				"Hello World")));
		tests.add(new TestCase("provided example 1", "phase3testcases/providedExample1.jott", true, join("5", "foo bar")));
		tests.add(new TestCase("valid loop", "phase3testcases/validLoop.jott", true, join("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")));

		tests.add(new TestCase("function not defined", "phase3testcases/funcNotDefined.jott", false));
		tests.add(new TestCase("function call param invalid", "phase3testcases/funcCallParamInvalid.jott", false));
		tests.add(new TestCase("function wrong param type", "phase3testcases/funcWrongParamType.jott", false));
		tests.add(new TestCase("function return in expr", "phase3testcases/funcReturnInExpr.jott", false));
		tests.add(new TestCase("mismatched return", "phase3testcases/mismatchedReturn.jott", false));
		tests.add(new TestCase("missing func params", "phase3testcases/missingFuncParams.jott", false));
		tests.add(new TestCase("missing main", "phase3testcases/missingMain.jott", false));
		tests.add(new TestCase("main return not int", "phase3testcases/mainReturnNotInt.jott", false));
		tests.add(new TestCase("missing return", "phase3testcases/missingReturn.jott", false));
		tests.add(new TestCase("no return if", "phase3testcases/noReturnIf.jott", false));
		tests.add(new TestCase("no return while", "phase3testcases/noReturnWhile.jott", false));
		tests.add(new TestCase("return id", "phase3testcases/returnId.jott", false));
		tests.add(new TestCase("void return", "phase3testcases/voidReturn.jott", false));
		tests.add(new TestCase("while keyword", "phase3testcases/whileKeyword.jott", false));
		tests.add(new TestCase("mixed math types", "phase3testcases/mixedMathTypes.jott", false));
	}

	private String join(String... lines) {
		if (lines == null || lines.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lines.length; i++) {
			if (i > 0) {
				sb.append("\n");
			}
			sb.append(lines[i]);
		}
		return sb.toString();
	}

	private boolean runSingle(TestCase test) {
		PrintStream consoleOut = System.out;
		PrintStream consoleErr = System.err;
		consoleOut.println("Running Test: " + test.name + " (expect " + (test.expectSuccess ? "success" : "failure") + ")");

		ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
		ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();

		boolean semanticOk = false;
		boolean runtimeOk = false;
		String failureReason = null;

		try {
			System.setOut(new PrintStream(outBuffer));
			System.setErr(new PrintStream(errBuffer));

			ArrayList<Token> tokens = JottTokenizer.tokenize(test.path);
			if (tokens == null) {
				failureReason = "Tokenizer returned null";
			} else {
				ArrayList<Token> parseTokens = new ArrayList<>(tokens);
				JottTree tree = JottParser.parse(parseTokens);
				if (tree == null) {
					failureReason = "Parser returned null";
				} else {
					SemanticContext ctx = new SemanticContext();
					ctx.reset();
					boolean valid = tree.validateTree(ctx);
					if (!valid || ctx.hasError()) {
						failureReason = "Semantic validation failed";
					} else {
						semanticOk = true;
						tree.execute();
						runtimeOk = true;
					}
				}
			}
		} catch (Exception e) {
			failureReason = "Exception during execution: " + e.getMessage();
		} finally {
			System.setOut(consoleOut);
			System.setErr(consoleErr);
		}

		String normalizedOut = normalize(outBuffer.toString());
		String normalizedErr = normalize(errBuffer.toString());

		if (test.expectSuccess) {
			if (!semanticOk || !runtimeOk) {
				consoleOut.println("\tFailed - " + (failureReason != null ? failureReason : "unknown failure"));
				if (!normalizedErr.isEmpty()) {
					consoleOut.println("\tStderr:\n" + normalizedErr);
				}
				return false;
			}
			String expected = normalize(test.expectedOutput);
			if (!normalizedOut.equals(expected)) {
				consoleOut.println("\tFailed - output mismatch");
				consoleOut.println("\tExpected:\n" + expected);
				consoleOut.println("\tGot:\n" + normalizedOut);
				if (!normalizedErr.isEmpty()) {
					consoleOut.println("\tStderr:\n" + normalizedErr);
				}
				return false;
			}
		} else {
			if (semanticOk || runtimeOk) {
				consoleOut.println("\tFailed - expected rejection but all phases passed");
				if (!normalizedOut.isEmpty()) {
					consoleOut.println("\tStdout:\n" + normalizedOut);
				}
				return false;
			}
		}

		consoleOut.println("\tPassed\n");
		return true;
	}

	private String normalize(String raw) {
		if (raw == null) {
			return "";
		}
		return raw.replace("\r\n", "\n").trim();
	}

	public void run() {
		buildTests();
		int total = tests.size();
		int passed = 0;
		for (TestCase t : tests) {
			if (runSingle(t)) {
				passed++;
			}
		}
		System.out.printf("Passed %d/%d tests%n", passed, total);
	}

	public static void main(String[] args) {
		new Phase4TesterPart2().run();
	}
}
