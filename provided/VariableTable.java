package provided;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class VariableTable {

	private final Deque<Map<String, VariableEntry>> scopes = new ArrayDeque<>();

	public void clear() {
		scopes.clear();
	}

	public void pushScope() {
		scopes.push(new HashMap<>());
	}

	public void popScope() {
		if (!scopes.isEmpty()) {
			scopes.pop();
		}
	}

	public boolean declare(String name, JottType type, boolean initialized) {
		if (scopes.isEmpty()) {
			pushScope();
		}
		Map<String, VariableEntry> scope = scopes.peek();
		if (scope.containsKey(name)) {
			return false;
		}
		scope.put(name, new VariableEntry(type, initialized));
		return true;
	}

	public VariableEntry lookup(String name) {
		for (Map<String, VariableEntry> scope : scopes) {
			VariableEntry entry = scope.get(name);
			if (entry != null) {
				return entry;
			}
		}
		return null;
	}

	public static class VariableEntry {
		private final JottType type;
		private boolean initialized;

		private VariableEntry(JottType type, boolean initialized) {
			this.type = type;
			this.initialized = initialized;
		}

		public JottType getType() {
			return type;
		}

		public boolean isInitialized() {
			return initialized;
		}

		public void setInitialized() {
			this.initialized = true;
		}
	}
}
