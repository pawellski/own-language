grammar OwnLanguage;

prog:		( stat? SEMICOLON )* 
	;

stat:		INT_KW ID				#declareInt
		| DOUBLE_KW ID				#declareDouble
		| INT_KW ID EQ INT			#assignInt
		| DOUBLE_KW ID EQ DOUBLE		#assignDouble
		| PRINT_KW value			#print
		| SCAN_KW value				#scan
	;

value:		ID
		| INT
		| DOUBLE
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
	
SEMICOLON:	';'
	;

WS:		(' '|'\t'|NEWLINE)+ { skip(); }
	;

NEWLINE:	'\r'? '\n'
	;

