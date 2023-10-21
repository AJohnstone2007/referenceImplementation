signature SYMBOL =
sig
eqtype Symbol
val symbol_name : Symbol -> string
val find_symbol : string -> Symbol
val eq_symbol : Symbol * Symbol -> bool
val symbol_lt : Symbol * Symbol -> bool
val symbol_order: Symbol * Symbol -> bool
end
;
