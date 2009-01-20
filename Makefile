SUFFIXES=.java .class .g

all: AntlrTokensLexer.class AntlrTokensParser.class Engine.class DomEngine.class RtfLexer.class RtfParser.class Convert.class Main.class
	jar cvfm RtfParser.jar manifest.txt *.class org/antlr/runtime/*.class org/antlr/runtime/tree/*.class

%.class: %.java
	javac $<

RtfLexer.java: Rtf.g
	java org.antlr.Tool $<

RtfParser.java: Rtf.g

Convert.java: Convert.g
	java org.antlr.Tool $<

AntlrTokensLexer.java: AntlrTokens.g
	java org.antlr.Tool $<

AntlrTokensParser.java: AntlrTokens.g

run: all
	java Main test.rtf > out.html 
# 2> err.txt