import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Hashtable;
import java.util.Iterator;

public class Main {
	static Hashtable<String, String> getHash() throws Exception {
		AntlrTokensLexer tokLex = new AntlrTokensLexer(new ANTLRFileStream("Rtf.tokens"));
		AntlrTokensParser tokParse = new AntlrTokensParser(new CommonTokenStream(tokLex));
		return tokParse.file();
	}

	public static void main(String args[]) throws Exception {
		if (args.length == 0) {
			System.err.println("Usage: test <rtf file>");
			System.exit(-1);
		}

		RtfLexer lexer = new RtfLexer(new ANTLRFileStream(args[0]));
		CommonTokenStream stream  = new CommonTokenStream(lexer);

		if (args.length > 1 && args[1].equals("-t")) {
			Hashtable<String, String> tokHash = getHash();

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
				Hashtable<String, String> tokHash = getHash();

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
