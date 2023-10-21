signature LISTS =
sig
exception Assoc
exception Find
exception Nth
exception Last
exception Hd and Tl
val hd : 'a list -> 'a
val tl : 'a list -> 'a list
val length : 'a list -> int
val last : 'a list -> 'a
val rev_append : 'a list * 'a list -> 'a list
val member : (''a * ''a list) -> bool
val difference : (''a list * ''a list) -> ''a list
val sublist : (''a list * ''a list) -> bool
val adjoin : (''a * ''a list) -> ''a list
val filter : ''a list -> ''a list
val rev_remove_dups : ''a list -> ''a list
exception Zip
val zip : 'a list * 'b list -> ('a * 'b) list
val unzip : ('a * 'b) list -> 'a list * 'b list
val nth : (int * 'a list) -> 'a
val nthtail : (int * 'a list) -> 'a list
val find : (''a * ''a list) -> int
val findp : ('a -> bool) -> 'a list -> 'a
val filterp : ('a -> bool) -> 'a list -> 'a list
val filter_outp : ('a -> bool) -> 'a list -> 'a list
val filter_length : ('a -> bool) -> 'a list -> int
val partition : ('a -> bool) -> 'a list -> ('a list * 'a list)
val number_from :
'a list * int * int * (int -> 'b) -> ('a * 'b) list * int
val number_from_by_one :
'a list * int * (int -> 'b) -> ('a * 'b) list * int
val forall : ('a -> bool) -> 'a list -> bool
val exists : ('a -> bool) -> 'a list -> bool
val assoc : ''a * (''a * 'b) list -> 'b
val assoc_returning_others : ''a * (''a * 'b) list -> (''a * 'b) list * 'b
val reducel : ('a * 'b -> 'a) -> ('a * 'b list) -> 'a
val reducer : ('a * 'b -> 'b) -> ('a list * 'b) -> 'b
val findOption : ('a -> 'b option) -> 'a list -> 'b option
val iterate : ('a -> 'b) -> 'a list -> unit
val qsort : ('a * 'a -> bool) -> 'a list -> 'a list
val msort : ('a * 'a -> bool) -> 'a list -> 'a list
val check_order : ('a * 'a -> bool) -> 'a list -> bool
val to_string : ('a -> string) -> 'a list -> string
end;
