grammar OwnLanguage;

prog:		( stat? SEMICOLON )* 
	;

stat:		INT_KW ID				#declareInt
		| DOUBLE_KW ID				#declareDouble
		| INT_KW ID EQ expr0int			#initializeInt
		| DOUBLE_KW ID EQ expr0dbl		#initializeDouble
		| ID EQ expr0				#assign
		| PRINT_KW value			#print
		| SCAN_KW value				#scan
	;

expr0:		expr0int
		| expr0dbl
	;

expr0int:	expr1int				#single0int
		| expr1int ADD expr0int			#addint
		| expr1int SUB expr0int			#subint
	;

expr1int:	expr2int				#single1int
		| expr2int MUL expr1int			#mulint
		| expr2int DIV expr1int			#divint
	;

expr2int:	int_val					#intval
		| OPEN_BR expr0int CLOSE_BR		#bracketsint
	;

expr0dbl:	expr1dbl				#single0dbl
		| expr1dbl ADD expr0dbl			#adddbl
		| expr1dbl SUB expr0dbl			#subdbl
	;

expr1dbl:	expr2dbl				#single1dbl
		| expr2dbl MUL expr1dbl			#muldbl
		| expr2dbl DIV expr1dbl			#divdbl
	;

expr2dbl:	double_val				#doubleval
		| OPEN_BR expr0dbl CLOSE_BR		#bracketsdbl
	;

int_val:	INT					#int
		| conv_toint INT			#intToInt
		| conv_toint DOUBLE			#doubleToInt
		| conv_toint ID				#idToInt
	;

double_val:	DOUBLE					#double
		| conv_todouble INT			#intToDouble
		| conv_todouble DOUBLE			#doubleToDouble
		| conv_todouble ID			#idToDouble
	;

value:		ID
		| INT
		| DOUBLE
	;

conv_toint:	OPEN_BR INT_KW CLOSE_BR
	;

conv_todouble:	OPEN_BR DOUBLE_KW CLOSE_BR
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

ADD:		'+'
	;

SUB:		'-'
	;

MUL:		'*'
	;

DIV:		'/'
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

