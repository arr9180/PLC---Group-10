package phase2.nodes;

import java.util.ArrayList;
import java.util.List;

import phase2.JottTree;
import phase2.Token;

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
    public boolean validateTree() {
        return true;
    }
}
