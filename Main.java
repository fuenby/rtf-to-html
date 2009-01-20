import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Hashtable;
import java.util.Iterator;

public class Main {
	public static void main(String args[]) throws Exception {
		if (args.length == 0) {
			System.err.println("Usage: test <rtf file>");
			System.exit(-1);
		}

		AntlrTokensLexer tokLex = new AntlrTokensLexer(new ANTLRFileStream("Rtf.tokens"));
		AntlrTokensParser tokParse = new AntlrTokensParser(new CommonTokenStream(tokLex));
		Hashtable<String, String> tokHash = tokParse.file();
		// System.out.println();

		RtfLexer lexer = new RtfLexer(new ANTLRFileStream(args[0]));
		CommonTokenStream stream  = new CommonTokenStream(lexer);

		if (args.length > 1 && args[1].equals("-t")) {
			for (Object o : stream.getTokens()) {
				CommonToken t = (CommonToken) o;
				String tokName = tokHash.get(Integer.toString(t.getType()));
				System.out.print(tokName + (tokName.charAt(0) == '\'' ? "" : "='" + t.getText() + "'") + " ");
			}
		} else {
			RtfParser parser = new RtfParser(stream);
			CommonTree tree = (CommonTree) parser.rtf().getTree();
			Engine engine = new DomEngine();

			CommonTreeNodeStream s = new CommonTreeNodeStream(tree);
				
			if (args.length > 1 && args[1].equals("-r")) {
				Iterator it = s.iterator();
				while (it.hasNext()) {
					CommonToken t = (CommonToken) ((CommonTree) it.next()).getToken();
					String tokName = tokHash.get(Integer.toString(t.getType()));
					System.out.print(tokName + (tokName != null && tokName.charAt(0) == '\'' ? "" : "='" + t.getText() + "'") + " ");
				}

				// System.out.println(s.toTokenString(0, 100));
			} else {
				Convert convert = new Convert(s, engine);
				convert.rtf();
			}	
		}
	}

}
