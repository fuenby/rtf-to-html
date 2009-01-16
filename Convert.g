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

hword: ~(TREE | TEXT | PARD | PAR | FS | F | I | LANG | B | RQUOTE | PLAIN) ;
hentity: hword | ^(hword entity*)  ;

header: hentity* ;

bstart: 
	TEXT { engine.text($TEXT.text); } | 
	PAR { engine.par(); } | 
	PARD | 
	FS NUMBER { engine.fs(Integer.parseInt($NUMBER.text)); } | 
	F NUMBER |
	I NUMBER { engine.i(false); } | 
	I { engine.i(true); } |
	B { engine.b(true); } |
	PLAIN { engine.plain(); } |
	B NUMBER { engine.b(false); } | 
	RQUOTE { engine.rquote(); } |
	LANG NUMBER | 
	^(TREE { engine.push(); } bentity* { engine.pop(); } ) ;
bentity: bstart ;
body: { engine.body(); } bstart bentity* { engine.endbody(); } ;
