
public class CommandLine {

	private final String LEX_TEST= "--lextest";

	public enum Action {UNDEFINED, TEST_LEXER};
	
	private Action _action;
	private String _inputFilename;
	public String getInputFilename() {
		return _inputFilename;
	}
	
	public CommandLine(String[] args) {
		processCommandLine(args);
	}

	private void processCommandLine(String[] args) {
		_action= Action.UNDEFINED;
		
		if (args.length != 2) {
			printUsage();
			return;
		}
			
		String action= args[0];
		if (action.equals(LEX_TEST)) {
			_action= Action.TEST_LEXER;
			_inputFilename= args[1];
		}
		else {
			printUsage();
		}
	}

	public boolean isValid() {
		return _action != Action.UNDEFINED;
	}
	
	private static void printUsage() {
		System.out.println("usage:");
		System.out.println("java MiniJavaCompiler --lextest CompilerInput");
	}
}
