require "types";
signature FOREIGN_STRUCTURE =
sig
structure FITypes : FOREIGN_TYPES
type name = FITypes.name
type filename = FITypes.filename
type foreign_module
type fStructure
datatype load_mode = IMMEDIATE_LOAD | DEFERRED_LOAD
val load_object_file : filename * load_mode -> fStructure
val file_info : fStructure -> (filename * load_mode)
val filesLoaded : unit -> filename list
val symbols : fStructure -> name list
datatype value_type = CODE_VALUE | VAR_VALUE | UNKNOWN_VALUE
val symbol_info : fStructure * name -> value_type
val module : fStructure -> foreign_module
end;
