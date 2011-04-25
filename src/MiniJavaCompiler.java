import java.io.FileNotFoundException;
import java.io.IOException;

import token.Token;


public class MiniJavaCompiler {

	private Lexer _lexer= null;
	private CommandLine _cmdLine= null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		CommandLine cmdLine= new CommandLine(args);
		if (!cmdLine.isValid()) {
			return;
		}
		
		MiniJavaCompiler comp= null;
		try {
			comp = createMiniJavaCompiler(cmdLine);
		} 
		catch (FileNotFoundException e) {
			System.err.println("file not found: " + cmdLine.getInputFilename());
			e.printStackTrace();
			return;
		}
		
		comp.process();
		
	}
	
	public static MiniJavaCompiler createMiniJavaCompiler(CommandLine cmdLine) 
	throws FileNotFoundException
	{
		Lexer lex= new Lexer(cmdLine);
		//Parser parser= new Parser();
		//...
		
		MiniJavaCompiler comp= new MiniJavaCompiler(lex, cmdLine);
		
		return comp;
	}
	
	private MiniJavaCompiler(Lexer lex, CommandLine cmdLine)
	{
		_lexer= lex;
		_cmdLine= cmdLine;
	}
	
	public void process() {
		try {
			_lexer.init();
			Token t= null;
			do {
				t= _lexer.nextToken();
				if (t != null) {
					System.out.println(t.toString());
				}
			} while (t != null);
		} catch (IOException e) {
			System.err.println("an error occured while parsing the file: " + _cmdLine.getInputFilename());
			e.printStackTrace();
		}
		
		//_parser.process();
	}
}
