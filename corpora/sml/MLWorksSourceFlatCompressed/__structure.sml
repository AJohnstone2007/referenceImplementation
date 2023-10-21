require "structure";
require "aliens";
require "types";
require "__aliens";
require "__types";
require "^.utils.map";
require "^.utils.__btree";
require "^.basis.__char";
require "^.basis.__list";
require "^.basis.__string";
structure Structure_ : FOREIGN_STRUCTURE =
struct
structure Map = BTree_
structure FIAliens : FOREIGN_ALIENS = ForeignAliens_
structure FITypes : FOREIGN_TYPES = ForeignTypes_
structure FITypes = FITypes
type name = FITypes.name
type filename = FITypes.filename
type ('a,'b) Map = ('a,'b)Map.map
exception Undefined = Map.Undefined
type foreign_module = FIAliens.foreign_module
val get_module_later = FIAliens.get_module_later
val get_item_later = FIAliens.get_item_later
val get_module_now = FIAliens.get_module_now
val get_item_now = FIAliens.get_item_now
val get_item_names = FIAliens.get_item_names
val get_item_info = FIAliens.get_item_info
val lookup = Map.apply'
datatype load_mode = IMMEDIATE_LOAD | DEFERRED_LOAD
val files = ref([] : filename list)
fun filesLoaded () = !files
datatype value_type = CODE_VALUE | VAR_VALUE | UNKNOWN_VALUE
fun load_module (fname,IMMEDIATE_LOAD) = get_module_now(fname)
| load_module (fname,DEFERRED_LOAD) = get_module_later(fname)
abstype fStructure =
FCODESET of (filename * load_mode * foreign_module)
with
fun load_object_file(fname : filename, mode) =
let
fun adjoin (x,xs) =
let val mem : string -> bool = fn y=> (x=y)
in
if List.exists mem xs then xs else (x::xs)
end
val F_mod = load_module(fname,mode)
in
files := adjoin(fname,!files);
FCODESET(fname,mode,F_mod)
end
fun file_info(FCODESET(fname,mode,_)) = (fname,mode)
fun symbols(FCODESET(_,_,f_mod)) = get_item_names(f_mod)
fun symbol_info(FCODESET(_,_,f_mod),str) =
let val info_map = get_item_info(f_mod)
val info = lookup(info_map,str)
in
case Char.toLower (String.sub (info, 0)) of
#"c" => CODE_VALUE |
#"v" => VAR_VALUE |
_ => UNKNOWN_VALUE
end handle Undefined => UNKNOWN_VALUE
fun module(FCODESET(_,_,f_mod)) = f_mod
end
end;
