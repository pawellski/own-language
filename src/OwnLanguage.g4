grammar OwnLanguage;

prog:		block
	;

block:		( stat? NEWLINE )*
	;

stat:		declaration				#declare
		| type ID EQ expr0			#initialize
		| assignment				#assign
		| printType OPEN_BR printval CLOSE_BR	#print
		| read_stat				#read
		| IF_KW OPEN_BR cond CLOSE_BR
		  OPEN_CBR blockif CLOSE_CBR		#if
	;

type:		numType
		| STRING_KW
	;

numType:	INT_KW
		| DOUBLE_KW
	;

printType:	PRINT_KW
		| PRINTLN_KW
	;

declaration:	type ID					#declareVariable
		| numType arrayid			#declareArray
	;

assignment:	ID EQ expr0				#assignId
		| arrayid EQ expr0			#assignArrayId
	;

read_stat:	READ_KW OPEN_BR ID CLOSE_BR		#readId
		| READ_KW OPEN_BR arrayid CLOSE_BR	#readArrayId
	;

blockif:	block
	;

cond:		expr0 comp expr0
	;

comp:		'=='
		| '!='
		| '>='
		| '<='
		| '>'
		| '<'
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
		| STRING				#string
		| arrayid				#arrayId
		| OPEN_BR expr0 CLOSE_BR		#brackets
	;

int_val:	INT					#int
		| conv_toint INT			#intToInt
		| conv_toint DOUBLE			#doubleToInt
		| conv_toint ID				#idToInt
		| conv_toint arrayid			#arrayIdToInt
	;

double_val:	DOUBLE					#double
		| conv_todouble INT			#intToDouble
		| conv_todouble DOUBLE			#doubleToDouble
		| conv_todouble ID			#idToDouble
		| conv_todouble arrayid			#arrayIdToDouble
	;

printval:	expr0
		| expr0 COMMA printval
	;

arrayid:	ID OPEN_SBR INT CLOSE_SBR
	;

conv_toint:	OPEN_BR INT_KW CLOSE_BR
	;

conv_todouble:	OPEN_BR DOUBLE_KW CLOSE_BR
	;

READ_KW:	'read'
	;

PRINT_KW:	'print' 
	;

PRINTLN_KW:	'println'
	;

INT_KW:		'int'
	;

DOUBLE_KW:	'double'
	;

STRING_KW:	'string'
	;

IF_KW:		'if'
	;

ID:		('a'..'z'|'A'..'Z')+
	;

DOUBLE:		'-'?'0'..'9'+'.''0'..'9'+
	;

INT:		'-'?'0'..'9'+
	;

STRING:		'"' ( ~('"') )* '"'
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

OPEN_SBR:	'['
	;

CLOSE_SBR:	']'
	;

OPEN_CBR:	'{'
	;

CLOSE_CBR:	'}'
	;

COMMA:		','
	;

SEMICOLON:	';'
	;

WS:		(' '|'\t')+ { skip(); }
	;

NEWLINE:	'\r'? '\n'
	;

