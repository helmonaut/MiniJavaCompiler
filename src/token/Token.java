package token;

public class Token {
	
	protected TokName _name;
	protected String _value;
	
	public Token(TokName name, String value) {
		_name= name;
		_value= value;
	}
	
	public String toString() {
		return _value;
	}
}
