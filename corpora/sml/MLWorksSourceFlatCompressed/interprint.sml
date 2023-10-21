require "../main/compiler";
require "../utils/diagnostic";
signature INTERPRINT =
sig
structure Compiler : COMPILER
structure Diagnostic : DIAGNOSTIC
type Context
exception Undefined of Compiler.Absyn.Ident.Identifier
val strings :
Context * Compiler.Options.options
* Compiler.Absyn.Ident.Identifier list * Compiler.ParserBasis
-> (Compiler.Absyn.Ident.Identifier * string) list
end;
