signature NAME =
sig
type name
val mkName : string -> name
val sameName : (name * name) -> bool
val stringOf : name -> string
val hashOf : name -> int
type 'a name_tbl
val mkNameTbl : (int * exn) -> '1a name_tbl
val numItems : 'a name_tbl -> int
val listItems : '_a name_tbl -> (name * '_a) list
val insert : '2a name_tbl -> (name * '2a) -> unit
val remove : 'a name_tbl -> name -> 'a
val find : 'a name_tbl -> name -> 'a
val peek : 'a name_tbl -> name -> 'a option
val apply : ((name * 'a) -> 'b) -> 'a name_tbl -> unit
val filter : ((name * 'a) -> bool) -> 'a name_tbl -> unit
val map : ((name * 'a) -> '2b) -> 'a name_tbl -> '2b name_tbl
val transform : ('a -> '2b) -> 'a name_tbl -> '2b name_tbl
val copy : '1a name_tbl -> '1a name_tbl
end
;
