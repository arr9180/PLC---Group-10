package provided;

public class RuntimeValue {
	private final JottType type;
	private final Object value;

	private RuntimeValue(JottType type, Object value) {
		this.type = type;
		this.value = value;
	}

	public static RuntimeValue integerValue(int v) {
		return new RuntimeValue(JottType.INTEGER, Integer.valueOf(v));
	}

	public static RuntimeValue doubleValue(double v) {
		return new RuntimeValue(JottType.DOUBLE, Double.valueOf(v));
	}

	public static RuntimeValue booleanValue(boolean v) {
		return new RuntimeValue(JottType.BOOLEAN, Boolean.valueOf(v));
	}

	public static RuntimeValue stringValue(String v) {
		return new RuntimeValue(JottType.STRING, v);
	}

	public static RuntimeValue voidValue() {
		return new RuntimeValue(JottType.VOID, null);
	}

	public JottType getType() {
		return type;
	}

	public int getInt() {
		return ((Integer) value).intValue();
	}

	public double getDouble() {
		if (type == JottType.INTEGER) {
			return ((Integer) value).doubleValue();
		}
		return ((Double) value).doubleValue();
	}

	public boolean getBoolean() {
		return ((Boolean) value).booleanValue();
	}

	public String getString() {
		return (String) value;
	}

	@Override
	public String toString() {
		if (type == JottType.STRING) {
			return getString();
		}
		if (type == JottType.BOOLEAN) {
			return getBoolean() ? "True" : "False";
		}
		if (type == JottType.INTEGER) {
			return Integer.toString(getInt());
		}
		if (type == JottType.DOUBLE) {
			double d = getDouble();
			if (Math.floor(d) == d) {
				return String.format("%.1f", d);
			}
			return Double.toString(d);
		}
		return "";
	}
}
