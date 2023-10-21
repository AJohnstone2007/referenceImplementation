require "../utils/diagnostic";
require "../utils/map";
require "../main/info";
require "../main/options";
require "../basics/absyn";
require "pervasives";
signature COMPILER =
sig
structure Info : INFO
structure Absyn : ABSYN
structure Pervasives : PERVASIVES
structure Diagnostic : DIAGNOSTIC
structure Options : OPTIONS
structure NewMap : MAP
sharing Info.Location = Absyn.Ident.Location
type Top_Env
type DebuggerEnv
type LambdaExp
type Module
type DebugInformation
type TypeBasis
type ParserBasis
datatype basis =
BASIS of {parser_basis : ParserBasis,
type_basis : TypeBasis,
lambda_environment : Top_Env,
debugger_environment : DebuggerEnv,
debug_info : DebugInformation}
val augment : Options.options * basis * basis -> basis
val adjust_compiler_basis_debug_info : basis * DebugInformation -> basis
val get_basis_debug_info : basis -> DebugInformation
val clear_debug_info : string * basis -> basis
val clear_debug_all_info : basis -> basis
val make_external : basis -> basis
val add_debug_info : Options.options * DebugInformation * basis -> basis
val empty_basis : basis
val initial_basis : basis
val initial_basis_for_builtin_library : basis
val builtin_lambda_environment : Top_Env
val extract_identifiers : Absyn.Ident.Identifier list * TypeBasis -> Absyn.Ident.Identifier list
datatype id_cache = ID_CACHE of {stamp_start:int,
stamp_no:int}
datatype result =
RESULT of {basis : basis,
signatures : (Absyn.Ident.SigId, Absyn.SigExp) NewMap.map,
code : Module option,
id_cache : id_cache}
type tokenstream
datatype source =
TOKENSTREAM of tokenstream |
TOKENSTREAM1 of tokenstream |
TOPDEC of string * Absyn.TopDec * ParserBasis
val compile :
Info.options *
Options.options ->
('a * string * Info.Location.T ->
'a * string * basis) ->
'a * basis * bool ->
(bool * source) ->
'a * result
val remove_str : basis * Absyn.Ident.StrId -> basis
end
;
