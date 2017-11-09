grammar crl_gram;

options {

	language=Java;
	TokenLabelType=CommonToken;
	output=AST;
	ASTLabelType=CommonTree;
	//backtrack=true;
}


tokens
{	
	RULES;
	J_Expression;
	Constraint;
	Condition;
	Conditions;
	Action;
	Actions;
	Set_mark;
	Java_string_expr;
	J_expr;
	SETTING;
	Import_item;
	Assignment;
	Set_mark;
	Set_text;
	New_label;
	IDENT;
}

/*@headers
{	
	package ru.icc.cells.ssdc.interpreeter.output;
}*/


//parser


crl
	:	import_stmt* crl_rule+ -> ^(SETTING import_stmt*) ^(RULES crl_rule+)
	;
	
	
import_stmt
	:	i=import_item -> Import_item [$i.value]
	;
import_item returns [String value]
@init{ $value=""; }
@after{ $value+=";"; }
	:	t=('import'|'package') { $value+=$t.text+" "; }
		t1=identifier { $value+=$t1.value; } ';'? EOL
	;
crl_rule
	:	'rule #' J_int_literal 'lock-on-active'? EOL
		'when' EOL 
		condition+
		'then' EOL
		action+
		'end' EOL? -> ^(J_int_literal ^(Conditions condition+) ^(Actions action+))
	;
condition
	:	query identifier (':' constraint (',' constraint)* (',' assignment)* )? EOL 
		-> ^(Condition query identifier constraint* assignment*)
	;
query
	:	Identifier //'cell'|'entry'|'label'|'category'|'no cells'|'no labels'|'no entries'|'no categories'
	;
	
constraint
	:	j_expr -> ^(Constraint j_expr)+
	;
assignment
	:	identifier ':' j_expr_ -> ^(Assignment identifier j_expr_)
	;
/*j_expr
	:	i=j_expr_ -> J_Expression [$i.value]//^(J_Expression j_expr_)
	;

j_expr_ returns [String value]
@init	{$value="";}
	:	 (i1=  ~(','|'"'|':'|'to'|'as'|'of'|'with'|EOL) { $value+=$i1.text; }
		 (i2=  ~(','|'"'|':'|'to'|'as'|'of'|'with'|EOL) { $value+=" "+$i2.text; })* )?
	;*/
	
/* 
j_expr returns [String value]
@init{ $value=""; }
	:	 ( i= ~(','|'"'|':'|'to'|'as'|'of'|'with'|EOL) { $value+=$i.text; } )+
	;
*/
j_expr returns [String value]
@init{ $value=""; }
	:	 ( i= ~(','|'"'|':'|'to'|EOL) { $value+=$i.text; } )+
	;
	
j_expr_
	:	j=j_expr -> ^(J_expr [$j.value])
	;

action
	:	action_ EOL -> action_
	;
action_
	:	set_mark
		|set_text
		|set_indent
		|split
		|merge
		|new_label
		|add_label
		|set_category
		|set_parent
		|new_entry
		|group
		|c_print
		|update
	;
set_mark
	:	'set mark' j_expr 'to' identifier -> ^(Set_mark identifier j_expr)
	;
set_text
	:	'set text' j_expr 'to' identifier -> ^(Set_text identifier j_expr)
	;
set_indent
	:	'set indent' J_int_literal 'to' identifier
	;
split
	:	'split' identifier
	;
merge
	:	'merge' identifier 'with' identifier
	;
new_entry
	:	'new entry' identifier ('as' j_expr)?
	;
set_value
	:	'set value' j_expr 'to' identifier 
	;
set_category
	:	'set category' identifier 'to' identifier
	;
set_parent
	:	'set parent' identifier 'to' identifier
	;
group
	:	'group' identifier 'with' identifier
	;
add_label
	:	'add label' identifier ('of' identifier)? 'to' identifier
	;
new_label
	:	'new label' identifier ('as' j_expr)? -> ^(New_label identifier j_expr?)
	;
update
	:	'update' identifier
	;
c_print
	:	('print'|'printf') j_expr
	;

identifier returns [String value]
@init { $value=""; }
	: 	t1=Identifier { $value+=$t1.text; } ('.' t2=Identifier { $value+="."+$t2.text; })* ('.' '*' { $value+=".*"; })? -> IDENT [$value]
	;

//lexer
WS
	:	 (' ')+ { $channel=HIDDEN; } 
	;
//S	:	' '	;
EOL
	:	('\n'|'\r')+
	;
J_int_literal
	:	DIGIT+
	;
Other_literals
	:	'='|'!'|'?'|'|'|'>'|'<'|'=='|'>='|'<='|'!='
	;
Identifier
	:	('$'|'_'|LETTER)('$'|'_'|LETTER|DIGIT)*
	;
String_lit
	:	'"' (.)* '"'
	;

Char_lit
	:	'\'' (.) '\''
	;
Breackits
	:	'('|')'
	;
/*lockonactive
	:	'lockonactive'
	;*/
fragment DIGIT
	:	'0'..'9'
	;
fragment LETTER
	:	'A'..'Z'|'a'..'z'
	;