require "../parser/parserenv";
require "../typechecker/basistypes";
require "../lambda/environtypes";
require "../debugger/debugger_types";
require "../system/__time";
signature ENCAPSULATE =
sig
structure ParserEnv : PARSERENV
structure BasisTypes : BASISTYPES
structure EnvironTypes : ENVIRONTYPES
structure Debugger_Types : DEBUGGER_TYPES
type Module
sharing ParserEnv.Ident = BasisTypes.Datatypes.Ident
sharing type EnvironTypes.LambdaTypes.Type = Debugger_Types.Type =
BasisTypes.Datatypes.Type
val do_timings : bool ref
val clean_basis : BasisTypes.Basis -> unit
val decode_type_basis :
{type_env : string,
file_name : string,
sub_modules : (string, (string * int * int))ParserEnv.Map.map,
decode_debug_information : bool,
pervasive_env : BasisTypes.Datatypes.Env} ->
BasisTypes.Basis * Debugger_Types.information
val output_file : bool ->
{filename : string,
code : Module,
stamps : int,
parser_env : ParserEnv.pB,
type_basis : BasisTypes.Basis,
debug_info : Debugger_Types.information,
require_list : (string * int * int) list,
lambda_env : EnvironTypes.Top_Env,
mod_name : string,
time_stamp : Time.time,
consistency :
{mod_name : string, time : Time.time} list}
-> unit
val input_code : string -> Module
exception BadInput of string
val input_info : string ->
{stamps : int,
mod_name : string,
time_stamp: Time.time,
consistency :
{mod_name : string, time : Time.time} list}
val input_all : string ->
{parser_env : string,
type_env : string,
lambda_env : string,
stamps : int,
time_stamp : Time.time,
mod_name : string,
consistency :
{mod_name : string, time : Time.time} list}
val decode_all :
{parser_env : string,
lambda_env : string,
type_env : string,
file_name : string,
sub_modules : (string, (string * int * int))ParserEnv.Map.map,
decode_debug_information : bool,
pervasive_env : BasisTypes.Datatypes.Env} ->
ParserEnv.pB * EnvironTypes.Top_Env *
BasisTypes.Basis * Debugger_Types.information
val input_debug_info :
{file_name : string,
sub_modules : (string, (string * int * int))ParserEnv.Map.map} ->
Debugger_Types.information
val code_offset : string -> int
end
;
