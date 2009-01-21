import java.util.Stack;
import java.nio.charset.Charset;
import java.nio.ByteBuffer;


public class Engine {
	public class ProgramState {
		private int paperWidth = 12240; // in twips (tenths of points)

		public ProgramState() {
		}

		public int getPaperWidth() { return paperWidth; }
		public void setPaperWidth(int paperWidth) { this.paperWidth = paperWidth; }
	}

	public class State {
		int fontSize = 12;
		boolean italic = false;
		boolean bold = false;

		public State() {
		}

		public int getFontSize() { return fontSize; }
		public State setFontSize(int fontSize) { this.fontSize = fontSize; return this; }

		public State setItalic(boolean state) { italic = state; return this; }
		public boolean isItalic() { return italic; }

		public State setBold(boolean state) { bold = state; return this; }
		public boolean isBold() { return bold; }

		public State clone() {
			State result = new State();
			result.setFontSize(getFontSize());
			result.setItalic(isItalic());
			result.setBold(isBold());

			return result;
		}
	}

	protected Charset codePage = Charset.forName("ISO-8859-1");
	private Stack<State> stateStack = new Stack<State>();
	private ProgramState programState = new ProgramState();


	public Engine() {
	}


	public void start() {
		stateStack.push(new State());
	}
	
	public void end() {}

	public void push() {
		stateStack.push(getState().clone());
		updateState();
	}

	public void pop() {
		stateStack.pop();
		updateState();
	}

	public void body() {}
	
	public void endbody() {}

	public void text(String text) {
		outText(decode(text));
	}
	
	public void par() { outText("\n"); }
	
	public void pard() {}
	
	public void rquote() {
		outText("'");
	}
	
	public void endash() {
		outText("--");
	}

	public void emdash() {
		outText("---");
	}

	public void fs(int rtfSize) {
		int ptSize = rtfSize / 2;
		if (getFontSize() != ptSize) {
			setFontSize(ptSize);
			updateState();
		}	
	}

	public void i(boolean state) {
		getState().setItalic(state);
		updateState();
	}

	public void b(boolean state) {
		getState().setBold(state);
		updateState();
	}

	public void plain() {
		getState().setItalic(false);
		getState().setBold(false);
		updateState();
	}

	public void line() {
		System.out.println();
	}

	public void ansicpg(int codepage) {
		String codepageName = "windows-" + Integer.toString(codepage);
		try {
			Charset newCharset = Charset.forName(codepageName);
			codePage = newCharset;
		} catch (Exception ex) {
			System.err.println("Cannot get decoder for " + codepageName);
		}
	}

	public void setFontSize(int fontSize) {
		getState().setFontSize(fontSize);
	}

	public State getState() {
		return stateStack.peek();
	}

	public ProgramState getProgramState() {
		return programState;
	}

	public int getFontSize() {
		return getState().getFontSize();
	}

	public String decode(String text) {
		return codePage.decode(ByteBuffer.wrap(text.getBytes())).toString();
	}

	public void updateState() {}

	public void outText(String text) {
		System.out.print(text);
	}

}
