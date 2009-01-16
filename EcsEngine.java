import org.apache.ecs.*;
import org.apache.ecs.xhtml.*;
import java.util.Stack;

	class StateStyle {
		Engine.State state;
		
		public StateStyle(Engine.State state) {
			this.state = state;
		}

		public String toString() {
			return toString(state);
		}

		public static String toString(Engine.State state) {
			StringBuilder result = new StringBuilder();

			if (state.getFontSize() != 12) {
				result.append("font-size: ");
				result.append(state.getFontSize());
				result.append("pt;");
			}

			if (state.isItalic()) {
				result.append("font-style: italic;");
			}

			return result.toString();
		}
	}

public class EcsEngine extends Engine {
	XhtmlDocument document = new XhtmlDocument();

	html htmlNode = new html();
	head headNode = new head();
	body bodyNode = new body();

	style styleNode = new style();

	p currentPara;
	MultiPartElement currentTarget;


	public EcsEngine() {
		document.setDoctype(new Doctype.XHtml10Strict());
		document.setHtml(getHtml());
		document.setHead(getHead());
		document.setBody(getBody());
		document.setCodeset("UTF-8");

		styleNode.addAttribute("type", "text/css");
		styleNode.addElement("body { font-size: 12pt; }\n");
		getHead().addElement(styleNode);
	}
	
	public void end() {
		getHtml().setPrettyPrint(true);
		getBody().setPrettyPrint(true);

		System.out.print(document.toString());
		super.end();
	}

	public void text(String text) {
		ensurePara();

		getCurrentTarget().addElement(text);
	}

	public void rquote() {
		ensurePara();

		getCurrentTarget().addElement(Entities.RSQUO);
	}

	public void par() {
		setCurrentPara(null);
	}


	public void ensurePara() {
		if (getCurrentPara() == null) {
			setCurrentPara(new p());
			getBody().addElement(getCurrentPara());

			updateState();
		}
	}

	public void updateState() {
		String style = StateStyle.toString(getState());
		if (style.length() > 0) {
			span newTarget = new span();
			newTarget.addAttribute("style", style);

			setCurrentTarget(newTarget);
			getCurrentPara().addElement(getCurrentTarget());
		} else {
			setCurrentTarget(getCurrentPara());
		}
	}

	html getHtml() {
		return htmlNode;
	}

	head getHead() {
		return headNode;
	}

	body getBody() {
		return bodyNode;
	}

	p getCurrentPara() {
		return currentPara;
	}

	void setCurrentPara(p para) {
		currentPara = para;
	}

	MultiPartElement getCurrentTarget() {
		return currentTarget;
	}

	void setCurrentTarget(MultiPartElement target) {
		currentTarget = target;
	}
}