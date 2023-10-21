require "intermake";
require "../typechecker/datatypes";
require "../utils/diagnostic";
signature INCREMENTAL =
sig
structure InterMake : INTERMAKE
structure Diagnostic : DIAGNOSTIC
structure Datatypes : DATATYPES
sharing InterMake.Compiler.Absyn.Ident = Datatypes.Ident
sharing InterMake.Compiler.NewMap = Datatypes.NewMap
type ModuleId
datatype Context =
CONTEXT of
{topdec : int,
compiler_basis : InterMake.Compiler.basis,
inter_env : InterMake.Inter_EnvTypes.inter_env,
signatures : (InterMake.Compiler.Absyn.Ident.SigId,
InterMake.Compiler.Absyn.SigExp) Datatypes.NewMap.map}
datatype options =
OPTIONS of
{options : InterMake.Inter_EnvTypes.Options.options,
debugger : (MLWorks.Internal.Value.T -> MLWorks.Internal.Value.T) ->
(MLWorks.Internal.Value.T -> MLWorks.Internal.Value.T)}
val get_project : unit -> InterMake.Project
val set_project : InterMake.Project -> unit
val reset_project : unit -> unit
val delete_from_project : ModuleId -> unit
val remove_file_info : unit -> unit
type register_key
val add_update_fn : (unit -> unit) -> register_key
val remove_update_fn : register_key -> unit
val empty_context : Context
val initial : Context
val clear_debug_info : string * Context -> Context
val clear_debug_all_info : Context -> Context
val add_debug_info : InterMake.Inter_EnvTypes.Options.options * InterMake.Compiler.DebugInformation * Context -> Context
val topdec : Context -> int
val compiler_basis : Context -> InterMake.Compiler.basis
val parser_basis : Context -> InterMake.Compiler.ParserBasis
val type_basis : Context -> InterMake.Compiler.TypeBasis
val lambda_environment : Context -> InterMake.Compiler.Top_Env
val debug_info : Context -> InterMake.Compiler.DebugInformation
val inter_env : Context -> InterMake.Inter_EnvTypes.inter_env
val signatures : Context -> (InterMake.Compiler.Absyn.Ident.SigId, InterMake.Compiler.Absyn.SigExp) InterMake.Compiler.NewMap.map
val env : Context -> Datatypes.Env
type Result
val identifiers_from_result: Result -> InterMake.Compiler.Absyn.Ident.Identifier list
val pb_from_result: Result -> InterMake.Compiler.ParserBasis
val compile_source :
InterMake.Compiler.Info.options ->
options * Context * InterMake.Compiler.source ->
Result
exception NotAnExpression
val evaluate_exp_topdec :
InterMake.Compiler.Info.options ->
(options * Context * InterMake.Compiler.Absyn.TopDec) ->
(MLWorks.Internal.Value.T * Datatypes.Type)
val add_definitions :
InterMake.Inter_EnvTypes.Options.options * Context * Result -> Context
val load_mos :
InterMake.Compiler.Info.options
-> InterMake.Inter_EnvTypes.Options.options * Context *
InterMake.Project * ModuleId * ModuleId list *
InterMake.Compiler.Info.Location.T
-> Result option
val read_dependencies :
string -> InterMake.Compiler.Info.options -> ModuleId -> unit
val delete_module : InterMake.Compiler.Info.options -> ModuleId -> unit
val delete_all_modules : bool -> unit
val check_module :
InterMake.Compiler.Info.options -> ModuleId * string -> ModuleId list
val add_value :
Context * string * Datatypes.Typescheme * MLWorks.Internal.Value.T ->
Context * InterMake.Compiler.Absyn.Ident.Identifier list
val add_structure :
Context * string * Datatypes.Structure * MLWorks.Internal.Value.T ->
Context * InterMake.Compiler.Absyn.Ident.Identifier list
end;
