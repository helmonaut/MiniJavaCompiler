package token;


public class IntegerLiteral extends Token {
	public IntegerLiteral(TokName name, String value) {
		super(name, value);
	}
	
	public String toString() {
		return "integer literal " + _value;
	}
}
