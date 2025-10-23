package provided;

import java.util.Collections;
import java.util.List;

public class FunctionSignature {

	private final String name;
	private final List<JottType> parameterTypes;
	private final JottType returnType;
	private final boolean builtin;

	public FunctionSignature(String name, List<JottType> parameterTypes, JottType returnType, boolean builtin) {
		this.name = name;
		this.parameterTypes = Collections.unmodifiableList(parameterTypes);
		this.returnType = returnType;
		this.builtin = builtin;
	}

	public String getName() {
		return name;
	}

	public List<JottType> getParameterTypes() {
		return parameterTypes;
	}

	public JottType getReturnType() {
		return returnType;
	}

	public boolean isBuiltin() {
		return builtin;
	}
}
