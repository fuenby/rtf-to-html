SUFFIXES=.java .class .g

all: AntlrTokensLexer.class AntlrTokensParser.class Engine.class HtmlEngine.class RtfLexer.class RtfParser.class Convert.class Main.class

%.class: %.java
	javac -classpath "%CLASSPATH%;." $<

RtfLexer.java: Rtf.g
	java -classpath "%CLASSPATH%;." org.antlr.Tool $<

RtfParser.java: Rtf.g

Convert.java: Convert.g
	java -classpath "%CLASSPATH%;." org.antlr.Tool $<

AntlrTokensLexer.java: AntlrTokens.g
	java -classpath "%CLASSPATH%;." org.antlr.Tool $<

AntlrTokensParser.java: AntlrTokens.g

run: all
	java -classpath "%CLASSPATH%;." Main test.rtf > out.html 2> err.txt