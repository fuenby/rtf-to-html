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

hword: 
	DEFF NUMBER { engine.deff($NUMBER.text); } |
	ANSI |
	ANSICPG NUMBER { engine.ansicpg(Integer.parseInt($NUMBER.text)); } |
	DEFLANG NUMBER |
	DEFLANGFE NUMBER |
	DEFTAB NUMBER |
	UC NUMBER ;
hentity: hword | ^((COLORTBL | STYLESHEET | INFO | GENERATOR) entity*) | fonttbl ;

fonttbl: ^(FONTTBL fontdesc*) ;
fontdesc: ^(F NUMBER TEXT) { engine.font($NUMBER.text, new Engine.Font($TEXT.text.substring(0, $TEXT.text.length() - 1))); } ;

header: hentity* ;

bstart: 
	TEXT { engine.text($TEXT.text); } | 
	LINE { engine.line(); } | 
	NBSP { engine.outText("\u00a0"); } | 
	HEXCHAR {
		int code = Integer.parseInt($HEXCHAR.text.substring(2), 16);
		engine.charCode(code); 
		} |
	QC { engine.qc(); } |
	BULLET { engine.outText("\u2022"); } |
	SLASH { engine.outText("\\"); } | 
	OPENBRACE { engine.outText("{"); } | 
	CLOSEBRACE { engine.outText("}"); } |
	PAR { engine.par(); } | 
	PARD { engine.pard(); } | 
	FS NUMBER { engine.fs(Integer.parseInt($NUMBER.text)); } | 
	F NUMBER { engine.f($NUMBER.text); } |
	I NUMBER { engine.i(false); } | 
	I { engine.i(true); } |
	B { engine.b(true); } |
	FI NUMBER { engine.fi(Integer.parseInt($NUMBER.text)); } |
	PLAIN { engine.plain(); } |
	EMDASH { engine.emdash(); } | 
	ENDASH { engine.endash(); } | 
	B NUMBER { engine.b(false); } | 
	RQUOTE { engine.rquote(); } |
	LANG NUMBER | 
	^(TREE { engine.push(); } bentity* { engine.pop(); } ) ;
bentity: bstart ;
body: { engine.body(); } bstart bentity* { engine.endbody(); } ;
