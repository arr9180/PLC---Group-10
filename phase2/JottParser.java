package phase2;

/**
 * This class is responsible for paring Jott Tokens
 * into a Jott parse tree.
 *
 * @author
 */

import java.util.ArrayList;
import phase2.nodes.ProgramNode;

public class JottParser {

    /**
     * Parses an ArrayList of Jotton tokens into a Jott Parse Tree.
     * @param tokens the ArrayList of Jott tokens to parse
     * @return the root of the Jott Parse Tree represented by the tokens.
     *         or null upon an error in parsing.
     */
    public static JottTree parse(ArrayList<Token> tokens){
        ProgramNode program = ProgramNode.parse(tokens);
        if (program == null) {
            return null;
        }
        if (!tokens.isEmpty()) {
            Token token = tokens.get(0);
            System.err.println("Syntax Error");
            System.err.println("Unexpected token \"" + token.getToken() + "\"");
            System.err.println(token.getFilename() + ":" + token.getLineNum());
            return null;
        }
        return program;
    }
}
