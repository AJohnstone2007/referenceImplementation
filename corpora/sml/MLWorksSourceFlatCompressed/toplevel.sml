require "../main/options";
require "../main/info";
require "../utils/diagnostic";
require "../lambda/environtypes";
signature TOPLEVEL =
sig
structure EnvironTypes : ENVIRONTYPES
structure Info : INFO
structure Options : OPTIONS
structure Diagnostic : DIAGNOSTIC
type ParserBasis
type TypeBasis
type ModuleId
datatype compiler_basis =
CB of (ParserBasis * TypeBasis * EnvironTypes.Top_Env)
val initial_compiler_basis : compiler_basis
val augment : compiler_basis * compiler_basis -> compiler_basis
val error_output_level : Info.severity ref
type Project
val compile_file' :
Info.options ->
Options.options * Project * ModuleId list ->
Project
val check_dependencies: Info.options -> Options.options -> string list -> unit
val list_objects: Info.options -> Options.options -> string list -> unit
val dump_objects: Info.options -> Options.options -> string -> unit
val compile_file: Info.options -> Options.options -> string list -> unit
val recompile_file: Info.options -> Options.options -> string list -> unit
val recompile_pervasive: Info.options -> Options.options -> unit
val build: Info.options -> Options.options -> unit -> unit
val show_build: Info.options -> Options.options -> unit -> unit
val print_timings: bool ref
end
;
