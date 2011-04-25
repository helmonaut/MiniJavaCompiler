package token;


public class Identifier extends Token {
	public Identifier (TokName name, String value) {
		super(name, value);
	}
	
	public String toString() {
		return "identifier " + _value;
	}
}
