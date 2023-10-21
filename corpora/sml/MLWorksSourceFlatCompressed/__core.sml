require "types";
require "__types";
require "core";
structure ForeignCore_ : FOREIGN_CORE =
struct
structure FITypes : FOREIGN_TYPES = ForeignTypes_
structure FITypes = FITypes
open FITypes
val MLWcast = MLWorks.Internal.Value.cast
val MLWenvironment = MLWorks.Internal.Runtime.environment
val env = MLWenvironment
exception Unavailable
local
val open_symtab_file : string -> bool = env "open symtab file";
val next_symtab_entry : unit -> string = env "next symtab entry";
val close_symtab_file : unit -> unit = env "close symtab file";
in
fun get_item_list(sofar) =
let val next = next_symtab_entry()
in
if next = "" then sofar
else get_item_list(next :: sofar)
end
fun get_symtab(file) =
let val check = open_symtab_file(file)
in
if check
then let val content = get_item_list([])
in
close_symtab_file();
content
end
else raise Unavailable
end
end
datatype load_mode = LOAD_LATER | LOAD_NOW
abstype foreign_object = FOBJ of (string * (string list) * address)
with
val load_foreign_object : (string * load_mode) -> address
= env "load foreign object"
fun load_object(s:string,lm:load_mode) =
let val mem = load_foreign_object(s,lm)
val symtab = get_symtab(s)
in
FOBJ(s,symtab,mem)
end
fun list_content (FOBJ(_,obj_lst,_)) = obj_lst;
end
abstype foreign_value = FVAL of word32
with
val find_value : (foreign_object * string) -> foreign_value
= env "lookup foreign value"
val call_unit_fun : foreign_value -> unit
= env "call unit function"
val call_foreign_fun : (foreign_value * address * int * address) -> unit
= env "call foreign function"
end
end;
