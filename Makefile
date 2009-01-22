SUFFIXES=.java .class .g

all: Engine.class DomEngine.class RtfLexer.class RtfParser.class Convert.class Main.class
	jar cvfm RtfParser.jar manifest.txt *.class 

%.class: %.java
	javac $<

RtfLexer.java: Rtf.g
	java org.antlr.Tool $<

RtfParser.java: Rtf.g

Convert.java: Convert.g
	java org.antlr.Tool $<

run: all
	java Main test.rtf > out.html 
# 2> err.txt