require "debugger_types";
require "^.main.options";
signature DEBUGGER_UTILITIES =
sig
structure Options : OPTIONS
structure Debugger_Types : DEBUGGER_TYPES
val slim_down_a_type : Debugger_Types.Type -> Debugger_Types.Type
val is_type_polymorphic : Debugger_Types.Type -> bool
val generate_recipe : Options.options ->
Debugger_Types.Type * Debugger_Types.Type * string ->
Debugger_Types.Backend_Annotation
val apply_recipe : Debugger_Types.Backend_Annotation * Debugger_Types.Type
-> Debugger_Types.Type
exception ApplyRecipe of string
val handler_type : Debugger_Types.Type
val setup_function_type : Debugger_Types.Type
val is_nulltype : Debugger_Types.Type -> bool
val close_type : Debugger_Types.Type -> Debugger_Types.Type
end
;
