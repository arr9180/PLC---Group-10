package provided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionTable {

	private final Map<String, FunctionSignature> functions = new HashMap<>();
	private final Map<String, Integer> definitionOrder = new HashMap<>();

	public FunctionTable() {
		reset();
	}

	public void reset() {
		functions.clear();
		definitionOrder.clear();
		addBuiltin("print", single(JottType.ANY), JottType.VOID);
		addBuiltin("concat", pair(JottType.STRING, JottType.STRING), JottType.STRING);
		addBuiltin("length", single(JottType.STRING), JottType.INTEGER);
	}

	private void addBuiltin(String name, List<JottType> params, JottType returnType) {
		functions.put(name, new FunctionSignature(name, params, returnType, true));
		definitionOrder.put(name, -1);
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

	public boolean addUserFunction(FunctionSignature signature, int order) {
		if (functions.containsKey(signature.getName())) {
			return false;
		}
		functions.put(signature.getName(), signature);
		definitionOrder.put(signature.getName(), order);
		return true;
	}

	public FunctionSignature get(String name) {
		return functions.get(name);
	}

	public boolean isDefinedBefore(String callee, String caller) {
		Integer calleeOrder = definitionOrder.get(callee);
		if (calleeOrder == null) {
			return false;
		}
		if (calleeOrder < 0) {
			return true;
		}
		if (caller == null) {
			return true;
		}
		Integer callerOrder = definitionOrder.get(caller);
		if (callerOrder == null) {
			return false;
		}
		return calleeOrder <= callerOrder;
	}
}
