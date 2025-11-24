package provided;

import java.util.ArrayList;

public class Jott {

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java provided.Jott <source_file>");
			return;
		}

		ArrayList<Token> tokens = JottTokenizer.tokenize(args[0]);
		if (tokens == null) {
			return;
		}

		ArrayList<Token> parseTokens = new ArrayList<>(tokens);
		JottTree tree = JottParser.parse(parseTokens);
		if (tree == null) {
			return;
		}

		SemanticContext context = new SemanticContext();
		context.reset();
		boolean valid = tree.validateTree(context);
		if (!valid || context.hasError()) {
			return;
		}

		tree.execute();
	}
}
