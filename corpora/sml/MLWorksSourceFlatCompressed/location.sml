signature LOCATION =
sig
datatype T =
UNKNOWN |
FILE of string |
LINE of string * int |
POSITION of string * int * int |
EXTENT of {name:string, s_line:int, s_col:int, e_line:int, e_col: int}
exception InvalidLocation
val to_string : T -> string
val from_string : string -> T
val combine : T * T -> T
val first_col : int
val first_line : int
val file_of_location : T -> string
val extract : T * string -> int * int
end;
