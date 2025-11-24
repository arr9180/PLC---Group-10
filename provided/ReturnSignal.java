package provided;

public class ReturnSignal {
	private final boolean returned;
	private final RuntimeValue value;

	private ReturnSignal(boolean returned, RuntimeValue value) {
		this.returned = returned;
		this.value = value;
	}

	public static ReturnSignal continueFlow() {
		return new ReturnSignal(false, null);
	}

	public static ReturnSignal returned(RuntimeValue value) {
		return new ReturnSignal(true, value);
	}

	public boolean hasReturned() {
		return returned;
	}

	public RuntimeValue getValue() {
		return value;
	}
}
