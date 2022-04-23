grammar OwnLanguage;

prog:		( stat? SEMICOLON )* 
	;

stat:		INT_KW ID				#declareInt
		| DOUBLE_KW ID				#declareDouble
		| INT_KW ID EQ int_val			#assignInt
		| DOUBLE_KW ID EQ double_val		#assignDouble
		| PRINT_KW value			#print
		| SCAN_KW value				#scan
	;

int_val:	INT
		| toint value
	;

double_val:	DOUBLE
		| todouble value
	;

value:		ID
		| INT
		| DOUBLE
	;

toint:		OPEN_BR INT_KW CLOSE_BR
	;

todouble:	OPEN_BR DOUBLE_KW CLOSE_BR
	;

SCAN_KW:	'scan' 
	;

PRINT_KW:	'print' 
	;

INT_KW:		'int'
	;

DOUBLE_KW:	'double'
	;

ID:		('a'..'z'|'A'..'Z')+
	;

DOUBLE:		INT '.' INT
	;

INT:		'0'..'9'+
	;

EQ:		'='
	;

OPEN_BR:	'('
	;

CLOSE_BR:	')'
	;
	
SEMICOLON:	';'
	;

WS:		(' '|'\t'|NEWLINE)+ { skip(); }
	;

NEWLINE:	'\r'? '\n'
	;

