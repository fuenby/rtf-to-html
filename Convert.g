tree grammar Convert;

options {
	tokenVocab = Rtf;
	ASTLabelType=CommonTree;
}

@members {
	Engine engine;

	public Convert(TreeNodeStream input, Engine engine) {
		this(input);
		this.engine = engine;
	}
}

rtf: { engine.start(); } ^(RTF NUMBER header body) { engine.end(); } ;

entity: . | ^(. entity*) ;

hword: FONTTBL | DEFF | COLORTBL | INFO | ANSI | ANSICPG NUMBER { engine.ansicpg(Integer.parseInt($NUMBER.text)); } | DEFLANG | NUMBER | DEFLANGFE | DEFTAB | STYLESHEET ;
hentity: hword | ^(hword entity*)  ;

header: hentity* ;

bstart: 
	TEXT { engine.text($TEXT.text); } | 
	NBSP { engine.text(" "); } | 
	HEXCHAR { engine.text("#"); } |
	PAR { engine.par(); } | 
	PARD | 
	FS NUMBER { engine.fs(Integer.parseInt($NUMBER.text)); } | 
	F NUMBER |
	I NUMBER { engine.i(false); } | 
	I { engine.i(true); } |
	B { engine.b(true); } |
	PLAIN { engine.plain(); } |
	ENDASH { engine.endash(); } | 
	B NUMBER { engine.b(false); } | 
	RQUOTE { engine.rquote(); } |
	LANG NUMBER | 
	^(TREE { engine.push(); } bentity* { engine.pop(); } ) ;
bentity: bstart ;
body: { engine.body(); } bstart bentity* { engine.endbody(); } ;
