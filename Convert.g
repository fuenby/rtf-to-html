tree grammar Convert;

options {
	tokenVocab = Rtf;
	ASTLabelType=CommonTree;
}

rtf: ^(RTF NUMBER .*) ;
