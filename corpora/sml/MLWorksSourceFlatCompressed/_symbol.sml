require "symbol";
functor Symbol () : SYMBOL =
struct
type Symbol = string
fun symbol_name s = s
fun find_symbol s = s
val eq_symbol = (op=) : string * string -> bool
val symbol_lt :string * string -> bool = (op<)
val symbol_order : string * string -> bool = (op<=)
end;
