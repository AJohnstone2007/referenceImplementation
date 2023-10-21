require "absyn";
require "../main/options";
signature ABSYNPRINT =
sig
structure Absyn : ABSYN
structure Options: OPTIONS
type 'a Sexpr
val detreeTy : Options.options -> Absyn.Ty -> string Sexpr
val detreePat : Options.options -> Absyn.Pat -> string Sexpr
val detreeDec : Options.options -> Absyn.Dec -> string Sexpr
val detreeExp : Options.options -> Absyn.Exp -> string Sexpr
val printTy : Options.options -> Absyn.Ty -> string
val printPat : Options.options -> Absyn.Pat -> string
val printDec : Options.options -> Absyn.Dec -> string
val printExp : Options.options -> Absyn.Exp -> string
val unparseTy : Options.print_options -> Absyn.Ty -> string
val unparsePat : bool -> Options.print_options -> Absyn.Pat -> string
val unparseExp : Options.print_options -> Absyn.Exp -> string
end
;
