signature CODE_MODULE =
sig
datatype wordset =
WORD_SET of
{a_names:string list,
b:{a_clos:int, b_spills:int, c_saves:int, d_code:string} list,
c_leafs:bool list,
d_intercept:int list,
e_stack_parameters:int list}
datatype module_element =
REAL of int * string |
STRING of int * string |
MLVALUE of int * MLWorks.Internal.Value.ml_value |
WORDSET of wordset |
EXTERNAL of int * string |
VAR of int * string |
EXN of int * string |
STRUCT of int * string |
FUNCT of int * string
datatype Module = MODULE of module_element list
end;
