package provided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionTable {

	private final Map<String, FunctionSignature> functions = new HashMap<>();

	public FunctionTable() {
		reset();
	}

	public void reset() {
		functions.clear();
		addBuiltin("print", single(JottType.ANY), JottType.VOID);
		addBuiltin("concat", pair(JottType.STRING, JottType.STRING), JottType.STRING);
		addBuiltin("length", single(JottType.STRING), JottType.INTEGER);
	}

	private void addBuiltin(String name, List<JottType> params, JottType returnType) {
		functions.put(name, new FunctionSignature(name, params, returnType, true));
	}

	private List<JottType> single(JottType type) {
		List<JottType> list = new ArrayList<>();
		list.add(type);
		return list;
	}

	private List<JottType> pair(JottType first, JottType second) {
		List<JottType> list = new ArrayList<>();
		list.add(first);
		list.add(second);
		return list;
	}

	public boolean addUserFunction(FunctionSignature signature) {
		if (functions.containsKey(signature.getName())) {
			return false;
		}
		functions.put(signature.getName(), signature);
		return true;
	}

	public FunctionSignature get(String name) {
		return functions.get(name);
	}
}
