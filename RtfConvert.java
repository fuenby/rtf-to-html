import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Hashtable;
import java.util.Iterator;

public class RtfConvert {
	public static void main(String args[]) throws Exception {
		if (args.length == 0) {
			System.err.println("Usage: RtfConvert [--output html|txt] <source.rtf>");
			System.exit(-1);
		}

		String sourceName;
		Engine engine;
		if (args[0].equals("--output")) {
			if (args[1].equals("txt")) {
				engine = new Engine();
			} else if (args[1].equals("html")) {
				engine = new DomEngine();
			} else {
				throw new RuntimeException("Wrong output format; has to be either txt or html.");
			}
			sourceName = args[2];
		} else {
			engine = new DomEngine();
			sourceName = args[0];
		}

		RtfLexer lexer = new RtfLexer(new ANTLRFileStream(sourceName));
		CommonTokenStream stream  = new CommonTokenStream(lexer);

		RtfParser parser = new RtfParser(stream);
		CommonTree tree = (CommonTree) parser.rtf().getTree();

		CommonTreeNodeStream s = new CommonTreeNodeStream(tree);

		Convert convert = new Convert(s, engine);
		convert.rtf();
	}

}
