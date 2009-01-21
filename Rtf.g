grammar Rtf;

options {
	output=AST;
}
tokens {
	TREE;
	BODY;
	HEADER;
}

@lexer::members {
	boolean afterControl = false;
}

rtf: '{'! RTF^ NUMBER { assert($NUMBER.text.equals("1")); } entity* '}'! ;

entity: 
	'{' entity* '}' -> ^(TREE entity*) |
	'{'! compound^ '}'! |
	unknown |
	text |
	word ;

text: (TEXT | NBSP | HEXCHAR | EMDASH | ENDASH | BULLET | SLASH | OPENBRACE | CLOSEBRACE)+ ;

word: (
	ANSI |
	LINE |
	ANSICPG |
	B |
	DEFF |
	DEFLANG |
	DEFLANGFE |
	DEFTAB |
	F |
	FALT |
	FNAME |
	FS |
	GENERATOR |
	I |
	LANG |
	PAR |
	PARD |
	PLAIN |
	PNSTART |
	RQUOTE |
	UC 
	//VIEWKIND
	) NUMBER? | fontfamily ;
	
fontfamily: FNIL | FROMAN | FSWISS | FMODERN | FSCRIPT | FDECOR | FTECH | FBIDI ;
compound: 
	// RTF^ NUMBER entity* |
	fonttbl | colortbl | stylesheet | info |
	//(COLORTBL | INFO | STYLESHEET)^ entity* |
	AUTHOR^ TEXT |
	OPERATOR^ TEXT |
	CREATIM^ YR NUMBER MO NUMBER DY NUMBER HR NUMBER MIN NUMBER (SEC NUMBER)? |
	REVTIM^ YR NUMBER MO NUMBER DY NUMBER HR NUMBER MIN NUMBER (SEC NUMBER)? ;
	
fonttbl: FONTTBL^ (fontinfo | '{'! fontinfo '}'!)+ ;
fontinfo: F^ NUMBER ( fontfamily | FCHARSET NUMBER | FPRQ NUMBER | unknown | TEXT)* ;

colortbl: COLORTBL^ ((RED NUMBER)? (GREEN NUMBER)? (BLUE NUMBER)? TEXT)* ;

stylesheet: STYLESHEET^ entity* ; 

info: INFO^ entity* ;

unknown: 
	control! |
	'{'! STAR! word^ entity* '}'! |
	'{'! STAR! CONTROL! (NUMBER!)? (entity!)* '}'! ;
	
control: CONTROL NUMBER? ;

SLASH: '\\\\' ;
STAR: '\\*' ;
OPENBRACE: '\\{' ;
CLOSEBRACE: '\\}' ;
NBSP: '\\~' ;
OTHER: '\\' ~('\n' | '\r' | '\\' | '\'' | '*' | '~' | '{' | '}' | 'a'..'z' | 'A'..'Z') { skip(); } ;



ANSI: '\\ansi' { afterControl = true; } ;
ANSICPG: '\\ansicpg' { afterControl = true; } ;
AUTHOR: '\\author' { afterControl = true; } ;
B: '\\b' { afterControl = true; } ;
BULLET: '\\bullet' { afterControl = true; } ;
COLORTBL: '\\colortbl' { afterControl = true; } ;
CREATIM: '\\creatim' { afterControl = true; } ;
DEFF: '\\deff' { afterControl = true; } ;
DEFLANG: '\\deflang' { afterControl = true; } ;
DEFLANGFE: '\\deflangfe' { afterControl = true; } ;
DEFTAB: '\\deftab' { afterControl = true; } ;
DY: '\\dy' { afterControl = true; } ;
EMDASH: '\\emdash' { afterControl = true; } ;
ENDASH: '\\endash' { afterControl = true; } ;
F: '\\f' { afterControl = true; } ;
FALT: '\\falt' { afterControl = true; } ;
FBIDI: '\\fbidi' { afterControl = true; } ;
FCHARSET: '\\fcharset' { afterControl = true; } ;
FDECOR: '\\fdecor' { afterControl = true; } ;
FMODERN: '\\fmodern' { afterControl = true; } ;
FNAME: '\\fname' { afterControl = true; } ;
FNIL: '\\fnil' { afterControl = true; } ;
FONTTBL: '\\fonttbl' { afterControl = true; } ;
FPRQ: '\\fprq' { afterControl = true; } ;
FROMAN: '\\froman' { afterControl = true; } ;
FS: '\\fs' { afterControl = true; } ;
FSCRIPT: '\\fscript' { afterControl = true; } ;
FSWISS: '\\fswiss' { afterControl = true; } ;
FTECH: '\\ftech' { afterControl = true; } ;
GENERATOR: '\\generator' { afterControl = true; } ;
HR: '\\hr' { afterControl = true; } ;
I: '\\i' { afterControl = true; } ;
INFO: '\\info' { afterControl = true; } ;
LANG: '\\lang' { afterControl = true; } ;
MIN: '\\min' { afterControl = true; } ;
MO: '\\mo' { afterControl = true; } ;
OPERATOR: '\\operator' { afterControl = true; } ;
PAR: ('\\par' | '\\\n' | '\\\r') { afterControl = true; } ;
PARD: '\\pard' { afterControl = true; } ;
PLAIN: '\\plain' { afterControl = true; } ;
PNSTART: '\\pnstart' { afterControl = true; } ;
REVTIM: '\\revtim' { afterControl = true; } ;
RQUOTE: '\\rquote' { afterControl = true; } ;
RED: '\\red' { afterControl = true; } ;
GREEN: '\\green' { afterControl = true; } ;
BLUE: '\\blue' { afterControl = true; } ;
RTF: '\\rtf' { afterControl = true; } ;
SEC: '\\sec' { afterControl = true; } ;
STYLESHEET: '\\stylesheet' { afterControl = true; } ;
UC: '\\uc' { afterControl = true; } ;
LINE: '\\line' { afterControl = true; } ;
//VIEWKIND: '\\viewkind' { afterControl = true; } ;
YR: '\\yr' { afterControl = true; } ;


CONTROL: '\\' ('a'..'z' | 'A'..'Z')+ { afterControl = true; System.err.println("Ignoring " + getText()); } ;

NUMBER: {afterControl}? => '-'? '0'..'9'+ ;
WS: {afterControl}? => ' ' { skip(); afterControl = false; } ;
NEWLINE: ('\n' | '\r') { skip(); afterControl = false; } ;

fragment HEX: '0'..'9' | 'a'..'f' ;
HEXCHAR: '\\' '\'' HEX HEX { afterControl = false; } ;

TEXT: 
	{!afterControl}? => ~('\\' | '{' | '}' | '\n' | '\r')+ |
	~(' ' | '0'..'9' | '-' | '\\' | '{' | '}' | '\n' | '\r') ~('\\' | '{' | '}' | '\n' | '\r')* { afterControl = false; } ;
