import org.w3c.dom.*;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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

		if (state.isBold()) {
			result.append("font-weight: bold;");
		}

		return result.toString();
	}
}

public class DomEngine extends Engine {
	Document document;

	Element htmlNode;
	Element headNode;
	Element bodyNode;

	Element styleNode;

	Element currentPara;
	Element currentTarget;

	public DomEngine() throws Exception {
		document = DOMImplementationRegistry.newInstance().getDOMImplementation("XML 1.0").createDocument("http://www.w3.org/1999/xhtml", "html", null);
		if (document == null) {
			System.err.println("Couldn't find suitable DOM implementation!");
			throw new RuntimeException();
		}

		htmlNode = document.getDocumentElement(); // .createElement("html");
		headNode = document.createElement("head");
		bodyNode = document.createElement("body");

		// document.appendChild(getHtml());
		getHtml().appendChild(getHead());
		getHtml().appendChild(getBody());

		// document.setCodeset("UTF-8");

		styleNode = document.createElement("style");
		styleNode.setAttribute("type", "text/css");
		styleNode.appendChild(document.createTextNode("body { font-size: 12pt; width: 40em; margin: 0 auto; }\n"));
		getHead().appendChild(styleNode);
	}
	
	public void end() {
		try {
		TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(System.out));
		} catch(Exception e) {}
		// System.out.print(document.getDocumentElement().toString());
		super.end();
	}

	public void text(String text) {
		ensurePara();

		getCurrentTarget().appendChild(document.createTextNode(text));
	}

	public void rquote() {
		ensurePara();

		getCurrentTarget().appendChild(document.createEntityReference("rsquo"));
	}

	public void par() {
		if (getCurrentPara() == null) {
			Element para = document.createElement("p");
			para.appendChild(document.createTextNode("\u00a0"));
			getBody().appendChild(para);
		}

		setCurrentPara(null);
	}


	public void ensurePara() {
		if (getCurrentPara() == null) {
			getBody().appendChild(document.createTextNode("\n"));
			setCurrentPara(document.createElement("p"));
			getBody().appendChild(getCurrentPara());

			updateTarget();
		}
	}

	public void updateTarget() {
		String style = StateStyle.toString(getState());

		if (getCurrentTarget() != null) {
			Element currentTarget = getCurrentTarget();
			if (currentTarget.getTagName().equals("span") && currentTarget.getFirstChild() == null) {
				Node para = currentTarget.getParentNode();
				para.removeChild(currentTarget);
				if (para.getFirstChild() == null) {
					para.appendChild(document.createTextNode("\u00a0"));
				}
			}
		}

		if (style.length() > 0) {
			Element newTarget = document.createElement("span");
			newTarget.setAttribute("style", style);

			setCurrentTarget(newTarget);
			getCurrentPara().appendChild(getCurrentTarget());
		} else {
			setCurrentTarget(getCurrentPara());
		}
	}

	public void updateState() {
		if (getCurrentPara() == null) {
			ensurePara();
		} else {
			updateTarget();
		}
	}

	Element getHtml() {
		return htmlNode;
	}

	Element getHead() {
		return headNode;
	}

	Element getBody() {
		return bodyNode;
	}

	Element getCurrentPara() {
		return currentPara;
	}

	void setCurrentPara(Element para) {
		currentPara = para;
	}

	Element getCurrentTarget() {
		return currentTarget;
	}

	void setCurrentTarget(Element target) {
		currentTarget = target;
	}
}
