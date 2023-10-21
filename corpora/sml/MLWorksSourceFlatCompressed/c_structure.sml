require "types";
signature C_STRUCTURE =
sig
structure FITypes : FOREIGN_TYPES
eqtype name = FITypes.name
eqtype filename = FITypes.filename
type fStructure
type c_structure
val to_struct : c_structure -> fStructure
datatype load_mode = IMMEDIATE_LOAD | DEFERRED_LOAD
val loadObjectFile : filename * load_mode -> c_structure
val fileInfo : c_structure -> (filename * load_mode)
val filesLoaded : unit -> filename list
val symbols : c_structure -> name list
datatype value_type = CODE_VALUE | VAR_VALUE | UNKNOWN_VALUE
val symbolInfo : c_structure * name -> value_type
end;
