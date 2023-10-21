require "../basics/absyn";
require "../main/options";
signature TOPDECPRINT =
sig
structure Absyn : ABSYN
structure Options : OPTIONS
val print_sigexp : Options.options
-> ('a * string -> 'a) -> ('a * int * Absyn.SigExp) -> 'a
val sigexp_to_string : Options.options -> Absyn.SigExp -> string
val strexp_to_string : Options.options -> Absyn.StrExp -> string
val strdec_to_string : Options.options -> Absyn.StrDec -> string
val topdec_to_string : Options.options -> Absyn.TopDec -> string
end;
