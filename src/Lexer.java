import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import token.EofToken;
import token.Identifier;
import token.IntegerLiteral;
import token.Keyword;
import token.OperatorToken;
import token.TokName;
import token.Token;

public class Lexer {
	private CommandLine 		_cmdLine= null;
	private char[]				_chBuff= null;
	private int 				_buffIdx= 0;
	private HashSet<String>		_keywords= null;
	
	public Lexer(CommandLine cmdLine) throws FileNotFoundException {
		_cmdLine= cmdLine;
	}

	public void init() throws IOException {
		bufferInputFile(_cmdLine.getInputFilename());
		_buffIdx= 0;
		_keywords= new HashSet<String>(
				Arrays.asList(
			"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
			"continue", "default", "double", "do", "else", "enum", "extends", "false", "finally", "final",
			"float", "for", "goto", "if", "implements", "import", "instanceof", "interface", "int", "long",
			"native", "new", "null", "package", "private", "protected", "public", "return", "short",
			"static", "strictfp", "super", "switch", "synchronized", "this", "throws", "throw",
			"transient", "true", "try", "void", "volatile", "while")
		);
	}
	
	private void bufferInputFile(String filename) throws IOException {
		File file= new File(filename);
		FileReader fileReader= new FileReader(file);
		BufferedReader bufferedReader= new BufferedReader(fileReader);
		
		int fileLength= (int)file.length();
		_chBuff= new char[fileLength];
		bufferedReader.read(_chBuff);
		bufferedReader.close();
	}
	
	public Token nextToken() throws IOException {
		int fileLength= _chBuff.length;
		while (_buffIdx < fileLength) {
			//System.out.print(_chBuff[_buffIdx]);
			
			char c= _chBuff[_buffIdx];
			switch (c) {
			case ' ':
			case '\n':
			case '\r':
			case '\t':
				_buffIdx++;
				break;
			case '!':
				//!=, !,
				return processNOT(_chBuff, _buffIdx);
			case '(':
				_buffIdx++;
				return new Token(TokName.LBRACE, "(");
			case ')':
				_buffIdx++;
				return new Token(TokName.RBRACE, ")");
			case '*':
				//*=, *,
				return processMUL(_chBuff, _buffIdx);
			case '+':
				//++, +=, +, 
				return processPLUS(_chBuff, _buffIdx);
			case ',':
				_buffIdx++;
				return new Token(TokName.COMMA, ",");
			case '-':
				//-=, --, -, 
				return processMINUS(_chBuff, _buffIdx);
			case '.':
				_buffIdx++;
				return new OperatorToken(TokName.DOT, ".");
			case '/':
				// /=, /,
				Token t= processSLASH(_chBuff, _buffIdx);
				if (t != null) {
					return t;
				}
				break;
			case ':':
				_buffIdx++;
				return new Token(TokName.COLON, ":");
			case ';':
				_buffIdx++;
				return new Token(TokName.SEMICOLON, ";");
			case '<':
				//<<=, <<, <=, <,
				return processLESS(_chBuff, _buffIdx);
			case '=':
				// ==, =
				return processEQUAL(_chBuff, _buffIdx);
			case '>':
				//>>>=, >>>, >>=, >>, >=, >, 
				return processGT(_chBuff, _buffIdx);
			case '?':
				_buffIdx++;
				return new OperatorToken(TokName.QUESTMARK, "?");
			case '%':
				//%=, %,
				return processPERCENT(_chBuff, _buffIdx);
			case '&':
				// &=, &&, &, 
				return processAND(_chBuff, _buffIdx);
			case '[':
				_buffIdx++;
				return new Token(TokName.LBRACK, "[");
			case ']':
				_buffIdx++;
				return new Token(TokName.RBRACK, "]");
			case '^':
				// ^=, ^, 
				return processEXP(_chBuff, _buffIdx);
			case '{':
				_buffIdx++;
				return new Token(TokName.LPAREN, "{");
			case '}':
				_buffIdx++;
				return new Token(TokName.RPAREN, "}");
			case '~':
				_buffIdx++;
				return new OperatorToken(TokName.TILDE, "~");
			case '|':	
				//|=, ||, |
				return processOR(_chBuff, _buffIdx);
			default:
				if (	c >= 'a' && c <= 'z' ||
						c >= 'A' && c <= 'Z' ||
						c == '_') {
					return processIdentifier(_chBuff, _buffIdx);
				}
				else if (c >= '0' && c <= '9') {
					return processInteger(_chBuff, _buffIdx);
				}
			}
			
			if (_buffIdx == fileLength) {
				_buffIdx++;
				return new EofToken();
			}
		}
		return null;
	} 
	
	private Token processOR(char[] chBuff, int i) {
		//|=, ||, |
		if (chBuff[i+1] == '=') {
			_buffIdx = i+2;
			return new OperatorToken(TokName.OREQUAL, "|=");
		}
		else if (chBuff[i+1] == '|') {
			_buffIdx = i+2;
			return new OperatorToken(TokName.OROR, "||");
		}
		
		_buffIdx= i+1;
		return new OperatorToken(TokName.OR, "|");
	}

	private Token processEXP(char[] chBuff, int i) {
		// ^=, ^,
		if (chBuff[i+1] == '=') {
			_buffIdx = i+2;
			return new OperatorToken(TokName.EXPEQUAL, "^⁼");
		}
			
		_buffIdx = i+1;
		return new OperatorToken(TokName.EXP, "^");
	}

	private Token processAND(char[] chBuff, int i) {
		// &=, &&, &,
		if (chBuff[i+1] == '=') {
			_buffIdx=  i+2;
			return new OperatorToken(TokName.ANDEQUAL, "&=");
		}
		else if (chBuff[i+1] == '&') {
			_buffIdx= i+2;
			return new OperatorToken(TokName.ANDAND, "&&");
		}
		
		_buffIdx = i+1;
		return new OperatorToken(TokName.AND, "&");
	}

	private Token processPERCENT(char[] chBuff, int i) {
		//%=, %
		if (chBuff[i+1] == '=') {
			_buffIdx = i+2;
			return new OperatorToken(TokName.PERCENTEQUAL, "%=");
		}
		_buffIdx = i+1;
		return new OperatorToken(TokName.PERCENT, "%");
	}

	private Token processGT(char[] chBuff, int i) {
		//>>>=,			i+3 
		//>>>, >>=, 	i+2
		//>>, >=, 		i+1
		//>,
		if (chBuff[i+1] == '>') {
			if (chBuff[i+2] == '>') {
				if (chBuff[i+3] == '=') {
					_buffIdx = i+4;
					return new OperatorToken(TokName.GTGTGTEQUAL, ">>>=");
				}
				_buffIdx = i+3;
				return new OperatorToken(TokName.GTGTGT, ">>>");
			}
			else if (chBuff[i+2] == '=') {
				_buffIdx = i+3;
				return new OperatorToken(TokName.GTGTEQUAL, ">>=");
			}
			_buffIdx= i+2;
			return new OperatorToken(TokName.GTGT, ">>");
		}
		else if (chBuff[i+1] == '=') {
			_buffIdx = i+2;
			return new OperatorToken(TokName.GTEQUAL, ">=");
		}
		_buffIdx= i+1;
		return new OperatorToken(TokName.GT, ">");
	}

	private Token processEQUAL(char[] chBuff, int i) {
		// ==, =
		if (chBuff[i+1] == '=') {
			_buffIdx = i+2;
			return new OperatorToken(TokName.EQUALEQUAL, "==");
		}
		_buffIdx = i+1;
		return new OperatorToken(TokName.EQUAL, "=");
	}

	private Token processLESS(char[] chBuff, int i) {
		// <<=, <<, <=, <,
		if (chBuff[i+1] == '<') {
			if (chBuff[i+2] == '=') {
				_buffIdx = i+3;
				return new OperatorToken(TokName.LESSLESSEQUAL, "<<=");
			}
			_buffIdx = i+2;
			return new OperatorToken(TokName.LESSLESS, "<<");
		}
		else if (chBuff[i+1] == '=') {
			_buffIdx= i+2;
			return new OperatorToken(TokName.LESSEQUAL, "<=");
		}
		
		_buffIdx= i+1;
		return new OperatorToken(TokName.LESS, "<");
	}

	private Token processSLASH(char[] chBuff, int i) {
		// // /* /=, /
		if (chBuff[i+1] == '/') {
			int incr= 1;
			while ( (i + incr + 1) < chBuff.length &&
					chBuff[i + incr + 1] != '\n' ) {
				incr++;
			}
			_buffIdx= i + incr + 1;
			return null;
		}
		else if (chBuff[i+1] == '*') {
			int incr= 1;
			while ( (i + incr + 1) < chBuff.length) {
//				char c= chBuff[i + incr + 1];
//				System.out.print(c);
				if (chBuff[i + incr + 1] == '*' ) {
					if ( (i + incr + 2) < chBuff.length &&
							chBuff[i + incr + 2] == '/') {
						_buffIdx = i + incr + 3;
						return null;						
					}
				}
				incr++;
			}
			return null;
		}
		else if (chBuff[i+1] == '=') {
			_buffIdx = i+2;
			return new OperatorToken(TokName.SLASHEQUAL, "/=");
		}

		_buffIdx = i+1;
		return new OperatorToken(TokName.SLASH, "/");
	}

	private Token processMINUS(char[] chBuff, int i) {
		//-=, --, -,
		if (chBuff[i+1] == '=') {
			_buffIdx= i+2;
			return new OperatorToken(TokName.MINUSEQUAL, "-=");
		}
		else if (chBuff[i+1] == '-') {
			_buffIdx= i+2;
			return new OperatorToken(TokName.MINUSMINUS, "--");
		}
		_buffIdx= i+1;
		return new OperatorToken(TokName.MINUS, "-");
	}
	
	
	private Token processPLUS(char[] chBuff, int i) {
		//++, +=, +,
		char c= chBuff[i+1];
		if (c == '+') {
			_buffIdx= i+2;
			return new OperatorToken(TokName.PLUSPLUS, "++");
		}
		else if (c == '=') {
			_buffIdx= i+2;
			return new OperatorToken(TokName.PLUSEQUAL, "+=");
		}
		_buffIdx= i+1;
		return new OperatorToken(TokName.PLUS, "+");
	}
	
	private Token processMUL(char[] chBuff, int i)
	{
		//*=, *,
		if (chBuff[i+1] == '=')
		{
			_buffIdx = i+2;
			return new OperatorToken(TokName.MULTEQUAL, "*=");
		}
		_buffIdx= i+1;
		return new OperatorToken(TokName.MULT, "*");
	}

	private Token processNOT(char[] chBuff, int i) {
		//!=, !,
		if (chBuff[i+1] == '=') {
			_buffIdx= i+2;
			return new OperatorToken(TokName.NOTEQUAL, "!=");
		}
		_buffIdx= i+1;
		return new OperatorToken(TokName.NOT, "!");
	}
	
	
	private Token processInteger(char[] chBuff, int i) {
		int incr= 0;
		String value = new String();
		value+= chBuff[i];
		while (	chBuff[i + incr + 1] >= '0' && 
				chBuff[i + incr +1] <= '9')
		{
			incr++;
			value+= chBuff[i + incr];
		}
		_buffIdx= i + incr + 1;
		return new IntegerLiteral(TokName.INTEGERLITERAL, value);
	}
	
	private Token processIdentifier(char[] chBuff, int i) {
		int incr= 0;
		String value= new String();
		value+= chBuff[i];
		
		while (	chBuff[i + incr + 1] >= 'a' && chBuff[i + incr + 1] <= 'z' ||
				chBuff[i + incr + 1] >= 'A' && chBuff[i + incr + 1] <= 'Z' ||
				chBuff[i + incr + 1] == '_') {
			incr++;
			value+= chBuff[i + incr];
		}
		_buffIdx= i+incr+1;
		
		if (_keywords.contains(value)) {
			return new Keyword(TokName.KEYWORD, value);
		}
	
		return new Identifier(TokName.IDENTIFIER, value);
	}
}
