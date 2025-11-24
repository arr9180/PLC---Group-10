package provided;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import provided.nodes.FunctionDefNode;

public class RuntimeState {
	private final Map<String, FunctionDefNode> functions;
	private final ArrayDeque<Frame> stack = new ArrayDeque<>();

	private static class VarEntry {
		private final JottType type;
		private RuntimeValue value;
		private boolean initialized;

		private VarEntry(JottType type, boolean initialized) {
			this.type = type;
			this.initialized = initialized;
		}
	}

	private static class Frame {
		private final ArrayDeque<Map<String, VarEntry>> scopes = new ArrayDeque<>();

		private Frame() {
			pushScope();
		}

		private void pushScope() {
			scopes.push(new HashMap<>());
		}

		private void popScope() {
			if (!scopes.isEmpty()) {
				scopes.pop();
			}
		}

		private boolean declareVar(String name, JottType type, boolean initialized) {
			Map<String, VarEntry> scope = scopes.peek();
			if (scope.containsKey(name)) {
				return false;
			}
			scope.put(name, new VarEntry(type, initialized));
			return true;
		}

		private VarEntry lookup(String name) {
			for (Map<String, VarEntry> scope : scopes) {
				VarEntry entry = scope.get(name);
				if (entry != null) {
					return entry;
				}
			}
			return null;
		}
	}

	public RuntimeState(Map<String, FunctionDefNode> functions) {
		this.functions = functions;
	}

	public void pushFrame() {
		stack.push(new Frame());
	}

	public void popFrame() {
		if (!stack.isEmpty()) {
			stack.pop();
		}
	}

	private Frame currentFrame() {
		return stack.peek();
	}

	public void pushScope() {
		currentFrame().pushScope();
	}

	public void popScope() {
		currentFrame().popScope();
	}

	public boolean declareVar(String name, JottType type, boolean initialized) {
		return currentFrame().declareVar(name, type, initialized);
	}

	public void setVar(String name, RuntimeValue value) {
		VarEntry entry = currentFrame().lookup(name);
		if (entry != null) {
			entry.value = value;
			entry.initialized = true;
		}
	}

	public RuntimeValue getVar(String name) {
		VarEntry entry = currentFrame().lookup(name);
		if (entry == null || !entry.initialized) {
			throw new RuntimeException("Variable not initialized: " + name);
		}
		return entry.value;
	}

	public JottType getVarType(String name) {
		VarEntry entry = currentFrame().lookup(name);
		return entry == null ? null : entry.type;
	}

	public RuntimeValue callFunction(String name, List<RuntimeValue> args) {
		if ("print".equals(name)) {
			RuntimeValue v = args.isEmpty() ? RuntimeValue.voidValue() : args.get(0);
			if (v.getType() == JottType.VOID) {
				throw new RuntimeException("Cannot print Void");
			}
			System.out.println(v.toString());
			return RuntimeValue.voidValue();
		}
		if ("concat".equals(name)) {
			RuntimeValue a = args.get(0);
			RuntimeValue b = args.get(1);
			return RuntimeValue.stringValue(a.getString() + b.getString());
		}
		if ("length".equals(name)) {
			RuntimeValue a = args.get(0);
			return RuntimeValue.integerValue(a.getString().length());
		}
		FunctionDefNode fn = functions.get(name);
		if (fn == null) {
			throw new RuntimeException("Unknown function: " + name);
		}
		return fn.invoke(this, args);
	}
}
