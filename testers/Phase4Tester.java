package testers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import provided.JottParser;
import provided.JottTokenizer;
import provided.JottTree;
import provided.SemanticContext;
import provided.Token;

public class Phase4Tester {

	private static class TestCase {
		String name;
		String path;
		String expected;

		TestCase(String name, String path, String expected) {
			this.name = name;
			this.path = path;
			this.expected = expected;
		}
	}

	private final List<TestCase> tests = new ArrayList<>();

	private void buildTests() {
		tests.add(new TestCase("hello", "phase4testcases/hello.jott", join("Hello")));
		tests.add(new TestCase("math ints", "phase4testcases/math.jott", join("7", "3", "10", "2")));
		tests.add(new TestCase("math doubles", "phase4testcases/doublemath.jott", join("7.0", "3.0", "10.0", "2.5")));
		tests.add(new TestCase("while loop", "phase4testcases/loop.jott", join("3", "2", "1")));
		tests.add(new TestCase("if elseif else", "phase4testcases/ifelse.jott", join("gt3")));
		tests.add(new TestCase("functions", "phase4testcases/functions.jott", join("11")));
		tests.add(new TestCase("concat length", "phase4testcases/concatlen.jott", join("foobar", "5")));
		tests.add(new TestCase("nested calls", "phase4testcases/nestedCall.jott", join("3")));
		tests.add(new TestCase("booleans", "phase4testcases/boolprint.jott", join("True", "False")));
		tests.add(new TestCase("zero loop", "phase4testcases/zeroloop.jott", ""));
		tests.add(new TestCase("while return", "phase4testcases/whileReturn.jott", join("3", "0")));
		tests.add(new TestCase("negatives", "phase4testcases/negatives.jott", join("-3", "-10", "-2.0")));
		tests.add(new TestCase("relational", "phase4testcases/relational.jott", join("True", "True", "True", "True")));
		tests.add(new TestCase("strings nested", "phase4testcases/strings.jott", join("foobar", "9")));
		tests.add(new TestCase("if all returns", "phase4testcases/ifreturns.jott", join("1", "0", "-1")));
		tests.add(new TestCase("really long", "phase4testcases/reallyLong.jott", join("5", "4", "3", "2", "1", "a1a1", "3", "4", "ran foo", "ran foo", "ran foo")));
	}

	private String join(String... lines) {
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
		PrintStream origOut = System.out;
		PrintStream origErr = System.err;
		ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
		ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outBuffer));
		System.setErr(new PrintStream(errBuffer));
		boolean success = true;
		try {
			ArrayList<Token> tokens = JottTokenizer.tokenize(test.path);
			if (tokens == null) {
				System.out.println("Tokenizer returned null for " + test.name);
				return false;
			}
			ArrayList<Token> parseTokens = new ArrayList<>(tokens);
			JottTree tree = JottParser.parse(parseTokens);
			if (tree == null) {
				System.out.println("Parser returned null for " + test.name);
				return false;
			}
			SemanticContext ctx = new SemanticContext();
			ctx.reset();
			boolean valid = tree.validateTree(ctx);
			if (!valid || ctx.hasError()) {
				System.out.println("Semantic validation failed for " + test.name);
				return false;
			}
			tree.execute();
		} catch (Exception e) {
			success = false;
			System.out.println("Exception during test " + test.name + ": " + e.getMessage());
		} finally {
			System.setOut(origOut);
			System.setErr(origErr);
		}
		String output = outBuffer.toString().replace("\r\n", "\n").trim();
		String expected = test.expected.replace("\r\n", "\n").trim();
		if (!success) {
			return false;
		}
		if (!output.equals(expected)) {
			System.out.println("Output mismatch for " + test.name);
			System.out.println("Expected:\n" + expected);
			System.out.println("Got:\n" + output);
			return false;
		}
		return true;
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
		new Phase4Tester().run();
	}
}
