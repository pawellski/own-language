grammar OwnLanguage;

prog:		( stat? SEMICOLON )* 
	;

stat:		INT_KW ID				#declareInt
		| DOUBLE_KW ID				#declareDouble
		| INT_KW ID EQ expr0			#initializeInt
		| DOUBLE_KW ID EQ expr0			#initializeDouble
		| ID EQ expr0				#assign
		| PRINT_KW OPEN_BR expr0 CLOSE_BR	#print
		| READ_KW OPEN_BR ID CLOSE_BR		#read
	;

expr0:		expr1					#single0
		| expr1 ADD expr0			#add
		| expr1 SUB expr0			#sub
	;

expr1:		expr2					#single1
		| expr2 MUL expr1			#mul
		| expr2 DIV expr1			#div
	;

expr2:		int_val					#intval
		| double_val				#doubleval
		| ID					#id
		| OPEN_BR expr0 CLOSE_BR		#brackets
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

READ_KW:	'read'
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

