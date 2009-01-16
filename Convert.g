tree grammar Convert;

options {
	tokenVocab = Rtf;
	ASTLabelType=CommonTree;
}

@members {
Engine engine = new EcsEngine();
}

rtf: { engine.start(); } ^(RTF NUMBER header body) { engine.end(); } ;

entity: . | ^(. entity*) ;

hword: ~(TREE | TEXT | PARD | PAR | FS | F | I | LANG | B | RQUOTE) ;
hentity: hword | ^(hword entity*)  ;

header: hentity* ;

bstart: 
	TEXT { engine.text($TEXT.text); } | 
	PAR { engine.par(); } | 
	PARD | 
	FS NUMBER | 
	F NUMBER |
	I NUMBER { engine.i(false); } | 
	I { engine.i(true); } |
	B NUMBER? | 
	RQUOTE { engine.rquote(); } |
	LANG NUMBER | 
	^(TREE { engine.push(); } bentity* { engine.pop(); } ) ;
bentity: bstart ;
body: { engine.body(); } bstart bentity* { engine.endbody(); } ;
