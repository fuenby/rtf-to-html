import java.util.Hashtable;
import java.util.Stack;
import java.nio.charset.Charset;
import java.nio.ByteBuffer;


public class Engine {
	public class ProgramState {
		private int paperWidth = 12240; // in twips (twentieths of points)
		private int leftMargin = 1800;
		private int rightMargin = 1800;

		public ProgramState() {
		}

		public int getPaperWidth() { return paperWidth; }
		public void setPaperWidth(int paperWidth) { this.paperWidth = paperWidth; }

		public int getLeftMargin() { return leftMargin; }

		public int getRightMargin() { return rightMargin; }
	}

	public class State {
		int fontSize = 12;
		boolean italic = false;
		boolean bold = false;
		Font font;

		public State() {
		}

		public int getFontSize() { return fontSize; }
		public State setFontSize(int fontSize) { this.fontSize = fontSize; return this; }

		public State setItalic(boolean state) { italic = state; return this; }
		public boolean isItalic() { return italic; }

		public State setBold(boolean state) { bold = state; return this; }
		public boolean isBold() { return bold; }

		public State setFont(Font font) { this.font = font; return this; }
		public Font getFont() { return font; }

		public State clone() {
			State result = new State();
			result.setFontSize(getFontSize());
			result.setItalic(isItalic());
			result.setBold(isBold());
			result.setFont(getFont());

			return result;
		}
	}

	public static class ParaState {
		public enum Align { LEFT, RIGHT, JUSTIFY, CENTER };

		private Align align = Align.LEFT;
		private int firstLineIndent = 720;

		public Align getAlign() { return align; }
		public ParaState setAlign(Align align) { this.align = align; return this; }

		public int getFirstLineIndent() { return firstLineIndent; }
		public ParaState setFirstLineIndent(int firstLineIndent) { this.firstLineIndent = firstLineIndent; return this; }
	}

	public static class Font {
		String name;

		public Font(String name) {
			this.name = name;
		}

		public String getFontName() { return name; }
	}

	protected Charset codePage = Charset.forName("ISO-8859-1");
	private Stack<State> stateStack = new Stack<State>();
	private ProgramState programState = new ProgramState();
	private Hashtable<String, Font> fonts = new Hashtable<String, Font>();
	private State defaultState = new State();
	private String defFontNumber = "";
	private ParaState paraState = new ParaState();

	public Engine() {
	}


	public void start() {
		stateStack.push(getDefState());
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

	public void qc() { // TODO:
		getParaState().setAlign(ParaState.Align.CENTER);
		updateParaState();
	}

	public void font(String number, Font font) {
		fonts.put(number, font);
		if (defFontNumber.equals(number)) {
			getDefState().setFont(font);
		}

		// System.err.println("Font " + number + ": " + font.getFontName());
	}

	public void text(String text) {
		outText(decode(text));
	}

	public void charCode(int code) {
		outText(codePage.decode(ByteBuffer.wrap(new byte[] { (byte) code })).toString());
	}
	
	public void par() { outText("\n"); }
	
	public void pard() {
		setParaState(new ParaState());
	}
	
	public void rquote() {
		outText("'");
	}
	
	public void endash() {
		outText("--");
	}

	public void emdash() {
		outText("---");
	}

	public void fi(int firstLineIndent) {
		getParaState().setFirstLineIndent(firstLineIndent);

		updateParaState();
	}

	public void fs(int rtfSize) {
		int ptSize = rtfSize / 2;
		if (getFontSize() != ptSize) {
			setFontSize(ptSize);
			updateState();
		}	
	}

	public void f(String fontNumber) {
		getState().setFont(fonts.get(fontNumber));
		updateState();
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

	public void deff(String number) {
		defFontNumber = number;
	}

	public void setFontSize(int fontSize) {
		getState().setFontSize(fontSize);
	}

	public State getState() {
		return stateStack.peek();
	}

	public State getDefState() {
		return defaultState;
	}

	public ParaState getParaState() {
		return paraState;
	}

	public Engine setParaState(ParaState paraState) {
		this.paraState = paraState;
		return this;
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

	public void updateParaState() {}

	public void outText(String text) {
		System.out.print(text);
	}

}
