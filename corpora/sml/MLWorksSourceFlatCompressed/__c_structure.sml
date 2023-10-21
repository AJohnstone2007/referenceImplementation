require "types";
require "structure";
require "__types";
require "__structure";
require "c_structure";
structure CStructure_ : C_STRUCTURE =
struct
structure FITypes : FOREIGN_TYPES = ForeignTypes_
structure FIStructure : FOREIGN_STRUCTURE = Structure_
structure FITypes = FITypes
open FITypes
open FIStructure
type fStructure = fStructure
val FIload_object_file = FIStructure.load_object_file
val FIfile_info = FIStructure.file_info
val FIsymbols = FIStructure.symbols
val FIsymbol_info = FIStructure.symbol_info
abstype c_structure = AC of fStructure
with
val to_struct : c_structure -> fStructure = (fn (AC(x)) => x)
val loadObjectFile : filename * load_mode -> c_structure =
fn (fnm,lm) => AC(FIload_object_file(fnm,lm))
val fileInfo : c_structure -> (filename * load_mode) =
fn (AC(cinfo)) => FIfile_info(cinfo)
val symbols : c_structure -> name list =
fn (AC(cinfo)) => FIsymbols(cinfo)
val symbolInfo : c_structure * name -> value_type =
fn (AC(cinfo),nm) => FIsymbol_info(cinfo,nm)
end
end;
