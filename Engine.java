import java.util.Stack;

public class Engine {
	public class State {
		int fontSize = 12;
		boolean italic = false;

		public int getFontSize() { return fontSize; }
		public void setFontSize(int fontSize) { this.fontSize = fontSize; }

		public void setItalic(boolean state) { italic = state; }
		public boolean isItalic() { return italic; }

		public State clone() {
			State result = new State();
			result.setFontSize(getFontSize());

			return result;
		}
	}


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
		System.out.print(text);
	}
	
	public void par() { System.out.println(); }
	
	public void pard() {}
	
	public void rquote() {
		System.out.print('\'');
	}
	
	public void endash() {
		System.out.print("--");
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


	public void setFontSize(int fontSize) {
		getState().setFontSize(fontSize);
	}

	public State getState() {
		return stateStack.peek();
	}

	public int getFontSize() {
		return getState().getFontSize();
	}


	public void updateState() {}

	private Stack<State> stateStack = new Stack<State>();

}
