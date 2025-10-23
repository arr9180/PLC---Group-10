package provided;

/**
 * Represents the semantic data types in Jott.
 */
public enum JottType {
	INTEGER,
	DOUBLE,
	BOOLEAN,
	STRING,
	VOID,
	ANY;

	public static JottType fromString(String text) {
		if ("Integer".equals(text)) {
			return INTEGER;
		}
		if ("Double".equals(text)) {
			return DOUBLE;
		}
		if ("Boolean".equals(text)) {
			return BOOLEAN;
		}
		if ("String".equals(text)) {
			return STRING;
		}
		if ("Void".equals(text)) {
			return VOID;
		}
		return null;
	}

	public boolean isNumeric() {
		return this == INTEGER || this == DOUBLE;
	}
}
