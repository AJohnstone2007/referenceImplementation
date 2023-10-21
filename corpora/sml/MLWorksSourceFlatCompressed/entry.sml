signature ENTRY =
sig
type Entry
type Context
type options
type Identifier
datatype BrowseOptions =
BROWSE_OPTIONS of
{show_sigs : bool ref,
show_funs : bool ref,
show_strs : bool ref,
show_types : bool ref,
show_exns : bool ref,
show_vars : bool ref,
show_conenvs : bool ref,
show_cons : bool ref
}
val new_options : unit -> BrowseOptions
val filter_entries : BrowseOptions -> Entry list -> Entry list
datatype SearchOptions =
SEARCH_OPTIONS of
{showSig : bool,
showStr : bool,
showFun : bool,
searchInitial : bool,
searchContext : bool,
showType : bool}
val is_tip : Entry -> bool
val browse_entry : bool -> Entry -> Entry list
val printEntry : options -> Entry -> string
val printEntry1 :
SearchOptions * options * Entry list -> (string * string) list
val massage : Entry -> Entry
val context2entry : Context -> (Entry list)
val get_id : Entry -> (string * bool)
val get_entry : Identifier * Context -> Entry option
end;
