functor I_SortFUN (structure S : SORT
) : I_SORT =
struct
type Sort = S.Sort
type Sort_Store = S.Sort_Store
open S
local
fun read_sorts infn endfn ss =
let val s = infn ()
in if endfn s then ss
else let val sorts = Lex.lex_line s
in read_sorts infn endfn (foldl insert_sort ss
(map name_sort sorts))
end
end
in
fun enter_sorts ss = (write_terminal "Enter Sorts:\n" ;
read_sorts (fn () => prompt_line "") nl ss)
fun load_sorts infn = read_sorts infn Lex.end_check1
end
local
fun write_sorts outfn ss =
fold_over_sorts (fn () => fn s =>
(outfn (sort_name s) ; outfn "\n")) () ss
in
fun save_sorts outfn ss = (write_sorts outfn ss ; outfn Lex.end_marker)
val display_sorts = write_sorts (write std_out)
end
local
fun del_sorts ss =
let val s = prompt_line ""
in if nl s then ss
else let val sorts = Lex.lex_line s
in del_sorts (foldl delete_sort ss (map name_sort sorts))
end
end
in
fun delete_sorts ss = (write_terminal "Enter Sorts to Delete:\n"; del_sorts ss)
end
fun sort_error s = ("\""^s^"\" not a declared Sort name")
local
open Parse
in
fun sort_parser ss toks = change_errors
(case toks of Lex.Id s :: _ => sort_error s
| Lex.Num s :: _ => sort_error (makestring s)
| Lex.NL :: _ => "Newline unexpected"
| [] => ("Expecting Sort name - found end of input."))
(fold_over_sorts (fn p => fn s => $(sort_name s) >> name_sort || p)
fail ss)
toks
end
local
local
open Parse
in
fun sodec_parser f ss s =
let
val so = get_sort_order ss
val sortp = sort_parser ss
fun fail_sort s _ = (error_message (sort_error s); ss)
fun show s = (up 1; clear_line (); write_terminal s)
val change = insert_sort_order ss o (f so)
fun add_pair hi lo = (show (">>  "^sort_name lo^"  <  "^sort_name hi^"\n"); change (lo,hi))
fun get_hi lo = (show (">>  "^sort_name lo^"  <  ") ;
reader (sortp >> (C add_pair lo))
(get_next_line ())
)
fun get_lo hi = (show (">>  "^sort_name hi^"  >  ") ;
reader (sortp >> (add_pair hi))
(get_next_line ())
)
fun so_parser toks =
(sortp --
( sortp >> add_pair
|| $">" -- sortp >> (C add_pair o snd)
|| $"<" -- sortp >> (add_pair o snd)
|| $"<" -- id >> (fail_sort o snd)
|| $">" -- id >> (fail_sort o snd)
|| $"<" >> K get_hi
|| $">" >> K get_lo
|| id >> fail_sort
|| empty >> K get_hi
) >> uncurry R
) toks
in
reader so_parser s
handle (SynError m) => (error_message m; ss)
end
end
fun change_sort_ordering f ss =
let val s = prompt_line ""
in if nl s then ss
else change_sort_ordering f (sodec_parser f ss s)
end
in
fun enter_sort_ordering ss =
(write_terminal "Enter Subsort Declarations:\n" ; change_sort_ordering extend_sort_order ss)
fun delete_sort_ordering ss =
(write_terminal "Enter Sortsort Declarations:\n"; change_sort_ordering restrict_sort_order ss)
end
fun load_sort_ordering infn ss =
let val s = infn ()
in if Lex.end_check1 s
then ss
else let val new = Lex.lex_line s
in if length new = 3
then let val so = get_sort_order ss
val is_sort = is_declared_sort ss
fun add_sort lh = insert_sort_order ss (extend_sort_order so lh)
val lo = name_sort (hd new)
val dir = hd (tl new)
val hi = name_sort (hd (tl (tl new)))
in if dir = "<" andalso is_sort lo andalso is_sort hi
then load_sort_ordering infn (add_sort (lo,hi))
else
if dir = ">" andalso is_sort lo andalso is_sort hi
then load_sort_ordering infn (add_sort (hi,lo))
else (error_and_wait ("incorrect line in sort_ordering declaration in file : line " ^ s); load_sort_ordering infn ss)
end
else (error_and_wait ("incorrect line in sort_ordering declaration in file : line " ^ s); load_sort_ordering infn ss)
end
end
local
fun write_sort_ordering outfn ss =
(app (fn (s,t) => outfn (s ^ "   <   " ^ t ^ "\n"))
(name_sort_order (get_sort_order ss));())
in
fun save_sort_ordering outfn ss = (write_sort_ordering outfn ss ;
outfn Lex.end_marker)
val display_sort_ordering = write_sort_ordering write_terminal
end ;
local
val Sort_Menu = Menu.build_menu "Sort Options"
[
("a", "Add Sorts", enter_sorts ),
("d", "Delete Sorts", delete_sorts )
]
in
val sort_options =
Menu.display_menu_screen 1 Sort_Menu
display_sorts
"SORTS" "Sorts"
end
local
val Sort_Order_Menu = Menu.build_menu "Sort-Ordering Options"
[
("a","Add to Sort-Ordering", enter_sort_ordering ),
("d","Delete from Sort-Ordering",delete_sort_ordering )
]
in
val sort_order_options =
Menu.display_menu_screen 1 Sort_Order_Menu
display_sort_ordering
"SORT ORDERING" "Sort_ordering"
end
end
;
