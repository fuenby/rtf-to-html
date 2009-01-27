import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Hashtable;
import java.util.Iterator;

public class Main {
	public static void main(String args[]) throws Exception {
		if (args.length == 0) {
			System.err.println("Usage: RtfConvert <rtf file>");
			System.exit(-1);
		}

		RtfLexer lexer = new RtfLexer(new ANTLRFileStream(args[0]));
		CommonTokenStream stream  = new CommonTokenStream(lexer);

		RtfParser parser = new RtfParser(stream);
		CommonTree tree = (CommonTree) parser.rtf().getTree();
		Engine engine = new DomEngine();

		CommonTreeNodeStream s = new CommonTreeNodeStream(tree);

		Convert convert = new Convert(s, engine);
		convert.rtf();
	}

}
