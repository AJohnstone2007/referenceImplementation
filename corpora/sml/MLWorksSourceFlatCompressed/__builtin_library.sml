structure BuiltinLibrary_ :
sig
eqtype ml_value
eqtype 'a array
eqtype 'a vector
eqtype bytearray
eqtype floatarray
type dynamic
type type_rep
type int8
type word8
type int16
type word16
type int32
type word32
exception Bind
exception Chr
exception Div
exception Exp
exception Interrupt
exception Io of string
exception Ln
exception Match
exception Ord
exception Overflow
exception Range of int
exception Size
exception Sqrt
exception Subscript
val ! : 'a ref -> 'a
val / : real * real -> real
val := : 'a ref * 'a -> unit
val <> : ''a * ''a -> bool
val @ : 'a list * 'a list -> 'a list
val ^ : string * string -> string
val alloc_pair : unit -> 'a
val alloc_string : int -> string
val alloc_vector : int -> 'a
val andb : int * int -> int
val arctan : real -> real
val array : int * '_a -> '_b
val arshift : int * int -> int
val bytearray : int * int -> bytearray
val bytearray_length : bytearray -> int
val bytearray_sub : bytearray * int -> int
val bytearray_unsafe_sub : bytearray * int -> int
val bytearray_unsafe_update : bytearray * int * int -> unit
val bytearray_update : bytearray * int * int -> unit
val floatarray : int * real -> floatarray
val floatarray_length : floatarray -> int
val floatarray_sub : floatarray * int -> real
val floatarray_unsafe_sub : floatarray * int -> real
val floatarray_unsafe_update : floatarray * int * real -> unit
val floatarray_update : floatarray * int * real -> unit
val call_c : string -> 'a
val call_ml_value : ml_value -> ml_value list
val cast : 'a -> 'b
val chr : int -> string
val char_chr : int -> char
val char_equal : char * char -> bool
val char_not_equal : char * char -> bool
val char_less : char * char -> bool
val char_less_equal : char * char -> bool
val char_greater : char * char -> bool
val char_greater_equal : char * char -> bool
val cos : real -> real
val exp : real -> real
val explode : string -> string list
val external_equality : ''a * ''a -> bool
val get_implicit : int -> ml_value
val floor : real -> int
val implode : string list -> string
val inline_equality : ''a * ''a -> bool
val int_abs : int -> int
val int_div : int * int -> int
val int_equal : int * int -> bool
val int_greater : int * int -> bool
val int_greater_or_equal : int * int -> bool
val int_less : int * int -> bool
val int_less_or_equal : int * int -> bool
val int_minus : int * int -> int
val int_mod : int * int -> int
val int_multiply : int * int -> int
val int_negate : int -> int
val int_not_equal : int * int -> bool
val int_plus : int * int -> int
val int32_abs : int32 -> int32
val int32_div : int32 * int32 -> int32
val int32_equal : int32 * int32 -> bool
val int32_greater : int32 * int32 -> bool
val int32_greater_equal : int32 * int32 -> bool
val int32_less : int32 * int32 -> bool
val int32_less_equal : int32 * int32 -> bool
val int32_minus : int32 * int32 -> int32
val int32_mod : int32 * int32 -> int32
val int32_multiply : int32 * int32 -> int32
val int32_negate : int32 -> int32
val int32_not_equal : int32 * int32 -> bool
val int32_plus : int32 * int32 -> int32
val length : 'a array -> int
val ln : real -> real
val load_exn : string -> unit
val load_funct : string -> unit
val load_string : string -> unit
val load_struct : string -> unit
val load_var : string -> unit
val lshift : int * int -> int
val make_ml_value : 'a -> ml_value
val make_ml_value_tuple : ml_value list -> ml_value
val map : ('a -> 'b) -> 'a list -> 'b list
val umap : (('a -> 'b) * 'a list) -> 'b list
val ml_require : string -> ml_value
val ml_value_from_offset : ml_value * int -> ml_value
val not : bool -> bool
val notb : int -> int
val o : ('b -> 'c) * ('a -> 'b) -> ('a -> 'c)
val orb : int * int -> int
val ord : string -> int
val char_ord : char -> int
val ordof : string * int -> int
val real : int -> real
val real_abs : real -> real
val real_equal : real * real -> bool
val real_greater : real * real -> bool
val real_greater_or_equal : real * real -> bool
val real_less : real * real -> bool
val real_less_or_equal : real * real -> bool
val real_minus : real * real -> real
val real_multiply : real * real -> real
val real_negate : real -> real
val real_not_equal : real * real -> bool
val real_plus : real * real -> real
val record_unsafe_sub : 'a * int -> 'b
val record_unsafe_update : 'a * int * 'b -> unit
val rev : 'a list -> 'a list
val rshift : int * int -> int
val sin : real -> real
val size : string -> int
val sqrt : real -> real
val string_equal : string * string -> bool
val string_not_equal : string * string -> bool
val string_less : string * string -> bool
val string_less_equal : string * string -> bool
val string_greater : string * string -> bool
val string_greater_equal : string * string -> bool
val string_unsafe_sub : string * int -> int
val string_unsafe_update : string * int * int -> unit
val sub : 'a array * int -> 'a
val unsafe_int_plus : int * int -> int
val unsafe_int_minus : int * int -> int
val unsafe_sub : 'a array * int -> 'a
val unsafe_update : 'a array * int * 'a -> unit
val update : 'a array * int * 'a -> unit
val vector : 'a list -> 'a vector
val vector_length : 'a vector -> int
val vector_sub : 'a vector * int -> 'a
val word32_orb : word32 * word32 -> word32
val word32_xorb : word32 * word32 -> word32
val word32_andb : word32 * word32 -> word32
val word32_notb : word32 -> word32
val word32_lshift : word32 * word -> word32
val word32_rshift : word32 * word -> word32
val word32_arshift : word32 * word -> word32
val word32_plus : word32 * word32 -> word32
val word32_minus : word32 * word32 -> word32
val word32_star : word32 * word32 -> word32
val word32_div : word32 * word32 -> word32
val word32_mod : word32 * word32 -> word32
val word32_equal : word32 * word32 -> bool
val word32_not_equal : word32 * word32 -> bool
val word32_less : word32 * word32 -> bool
val word32_less_equal : word32 * word32 -> bool
val word32_greater : word32 * word32 -> bool
val word32_greater_equal : word32 * word32 -> bool
val word_orb : word * word -> word
val word_xorb : word * word -> word
val word_andb : word * word -> word
val word_notb : word -> word
val word_lshift : word * word -> word
val word_rshift : word * word -> word
val word_arshift : word * word -> word
val word_plus : word * word -> word
val word_minus : word * word -> word
val word_star : word * word -> word
val word_div : word * word -> word
val word_mod : word * word -> word
val word_equal : word * word -> bool
val word_not_equal : word * word -> bool
val word_less : word * word -> bool
val word_less_equal : word * word -> bool
val word_greater : word * word -> bool
val word_greater_equal : word * word -> bool
val xorb : int * int -> int
end
=
struct
local
exception LibraryError of string
val yes : string -> 'a = call_c
fun no message = fn _ => raise LibraryError message
val obsolete = no
in
exception Bind
exception Chr
exception Div
exception Exp
exception Interrupt
exception Io of string
exception Ln
exception Match
exception Ord
exception Overflow
exception Range of int
exception Size
exception Sqrt
exception Subscript
type ml_value = ml_value
type 'a array = 'a array
type 'a vector = 'a vector
type bytearray = bytearray
type floatarray = floatarray
type dynamic = dynamic
type type_rep = type_rep
type int8 = int8
type word8 = word8
type int16 = int16
type word16 = word16
type int32 = int32
type word32 = word32
local
infix 6 + -
infix 4 <> < > <= >= =
val int_equal : int * int -> bool = int_equal
val op <> : int * int -> bool = int_not_equal
val op > : int * int -> bool = int_greater
val op >= : int * int -> bool = int_greater_or_equal
val op < : int * int -> bool = int_less
val op <= : int * int -> bool = int_less_or_equal
val op + : int * int -> int = unsafe_int_plus
val op - : int * int -> int = unsafe_int_minus
val alloc_vector : int -> 'a vector = alloc_vector
val vector_unsafe_sub : 'a vector * int -> 'a = record_unsafe_sub
val vector_unsafe_update : 'a vector * int * 'a -> unit = record_unsafe_update
val alloc_string : int -> string = alloc_string
val size : string -> int = size
val string_unsafe_update : string * int * int -> unit = string_unsafe_update
val string_unsafe_sub : string * int -> int = string_unsafe_sub
val cast : 'a -> 'b = cast
val length : 'a array -> int = length
val unsafe_sub : '_a array * int -> '_a = unsafe_sub
val unsafe_update : '_a array * int * '_a -> unit = unsafe_update
val real : int -> real = real
in
local
fun aux2 ([],_,res) = res
| aux2 (a::b,cell,res) =
let val _ = record_unsafe_update (cell,0,a)
in aux2 (b,record_unsafe_sub (cell,1),res) end
fun aux1 ([],res,x) = aux2 (x,res,res)
| aux1 (a::b,y,x) =
let
val cell = cast (0,y)
in
aux1 (b,cell,x)
end
in
fun @ (nil,l) = l
| @ (l,nil) = l
| @ ([a],l) = a::l
| @ ([a,b],l) = a::b::l
| @ ([a,b,c],l) = a::b::c::l
| @ ([a,b,c,d],l) = a::b::c::d::l
| @ ([a,b,c,d,e],l) = a::b::c::d::e::l
| @ ([a,b,c,d,e,f],l) = a::b::c::d::e::f::l
| @ ([a,b,c,d,e,f,g],l) = a::b::c::d::e::f::g::l
| @ ([a,b,c,d,e,f,g,h],l) = a::b::c::d::e::f::g::h::l
| @ (a::b::c::d::e::f::g::h::r,l) = a::b::c::d::e::f::g::h:: aux1(r,l,r)
end
local
fun make_chars (0,acc) = acc
| make_chars (n,acc) =
let
val s = alloc_string 2
in
string_unsafe_update (s,0,n-1);
string_unsafe_update (s,1,0);
make_chars (n-1,s::acc)
end
val char_list = make_chars (256,[])
val chars = alloc_vector 256
fun update ([],n) = ()
| update (c::rest,n) =
(vector_unsafe_update (chars,n,c);
update (rest,n+1))
val _ = update (char_list,0)
in
fun explode s : string list =
let
fun aux (n,acc,chars) =
if n <= 1
then
if int_equal (n,1)
then vector_unsafe_sub (chars,string_unsafe_sub (s,0)) :: acc
else acc
else
let
val n' = n-1
val n'' = n-2
in
aux (n'',
vector_unsafe_sub (chars,string_unsafe_sub (s,n'')) ::
vector_unsafe_sub (chars,string_unsafe_sub (s,n')) ::
acc,
chars)
end
in
aux (size s, [], chars)
end
end
end
fun not true = false
| not false = true
local
fun rev' (done, []) = done
| rev' (done, x::xs) = rev' (x::done, xs)
in
fun rev [] = []
| rev (l as [_]) = l
| rev l = rev' ([], l)
fun map f [] = []
| map f [x] = [f x]
| map f [x,y] = [f x, f y]
| map f [x,y,z] = [f x, f y, f z]
| map f [x,y,z,w] = [f x, f y, f z, f w]
| map f (x :: y :: z :: w :: rest) =
let
fun map_sub([], done) = rev' ([], done)
| map_sub(x :: xs, done) = map_sub (xs, f x :: done)
in
f x :: f y :: f z :: f w :: map_sub(rest, [])
end
fun umap (f, []) = []
| umap (f, [x]) = [f x]
| umap (f, [x,y]) = [f x, f y]
| umap (f, [x,y,z]) = [f x, f y, f z]
| umap (f, [x,y,z,w]) = [f x, f y, f z, f w]
| umap (f,(x :: y :: z :: w :: rest)) =
let
fun map_sub([], done) = rev' ([], done)
| map_sub(x :: xs, done) = map_sub (xs, f x :: done)
in
f x :: f y :: f z :: f w :: map_sub(rest, [])
end
end
val ! : 'a ref -> 'a = fn x => no "deref" x
val / : real * real -> real = no "real divide"
val := : 'a ref * 'a -> unit = fn x => no "becomes" x
val <> : ''a * ''a -> bool = fn x => yes "polymorphic inequality" x
val ^ : string * string ->string = yes "string concatenate"
val o : ('b -> 'c) * ('a -> 'b) -> 'a -> 'c = (fn (f,g) => fn x => f(g(x)))
val alloc_pair : unit -> 'a = fn x => no "alloc pair" x
val alloc_string : int -> string = no "alloc string"
val alloc_vector : int -> 'a = fn x => no "alloc_vector" x
val andb : int * int -> int = no "integer bit and"
val arctan : real -> real = yes "real arctan"
val array : int * '_a -> '_b = fn x => no "array" x
val arshift : int * int -> int = no "integer bit shift right arithmetic"
val bytearray : int * int -> bytearray = no "bytearray"
val bytearray_length : bytearray ->int = no "bytearray length"
val bytearray_sub : bytearray * int -> int = no "bytearray sub"
val bytearray_unsafe_sub : bytearray * int -> int =
no "bytearray sub unsafe"
val bytearray_unsafe_update : bytearray * int * int -> unit =
no "bytearray update unsafe"
val bytearray_update : bytearray * int * int -> unit =
no "bytearray update"
val floatarray : int * real -> floatarray =
no "floatarray"
val floatarray_length : floatarray -> int =
no "floatarray length"
val floatarray_sub : floatarray * int -> real =
no "floatarray sub"
val floatarray_unsafe_sub : floatarray * int -> real =
no "floatarray sub unsafe"
val floatarray_unsafe_update : floatarray * int * real -> unit =
no "floatarray update unsafe"
val floatarray_update : floatarray * int * real -> unit =
no "floatarray update"
val call_c : string -> 'a = fn x => no "call_c" x
val call_ml_value : ml_value -> ml_value list = obsolete "system call"
val cast : 'a -> 'b = fn x => no "cast" x
val chr : int -> string = no "string chr"
val char_chr : int -> char = no "char chr"
val char_equal : char * char -> bool = no "char equal"
val char_not_equal : char * char -> bool =no "char not equal"
val char_less : char * char -> bool = no "char less"
val char_greater : char * char -> bool = no "char greater"
val char_less_equal : char * char -> bool = no "char less equal"
val char_greater_equal : char * char -> bool = no "char greater equal"
val cos : real -> real = yes "real cos"
val exp : real -> real = yes "real exp"
val external_equality : ''a * ''a -> bool = fn x => yes "polymorphic equality" x
val get_implicit : int -> ml_value = no "get implicit"
val floor : real -> int = no "real floor"
val implode : string list -> string = yes "string implode"
val inline_equality : ''a * ''a -> bool =fn x => no "inline equality" x
val int_abs : int -> int = no "integer abs"
val int_div : int * int -> int = yes "integer divide"
val int_equal : int * int -> bool = no "integer equal"
val int_greater : int * int -> bool = no "integer greater"
val int_greater_or_equal : int * int -> bool = no "integer greater or equal"
val int_less : int * int -> bool = no "integer less"
val int_less_or_equal : int * int -> bool = no "integer less or equal"
val int_minus : int * int -> int = no "integer minus"
val int_mod : int * int -> int = yes "integer modulo"
val int_multiply : int * int -> int = yes "integer multiply"
val int_negate : int -> int = no "integer negate"
val int_not_equal : int * int -> bool = no "integer not equal"
val int_plus : int * int -> int = no "integer plus"
val unsafe_int_plus : int * int -> int = no "unsafe integer plus"
val unsafe_int_minus : int * int -> int = no "unsafe integer minus"
val int32_abs : int32 -> int32 = no "int32 abs"
val int32_div : int32 * int32 -> int32 = yes "int32 divide"
val int32_equal : int32 * int32 -> bool = no "int32 equal"
val int32_greater : int32 * int32 -> bool = no "int32 greater"
val int32_greater_equal : int32 * int32 -> bool = no "int32 greater or equal"
val int32_less : int32 * int32 -> bool = no "int32 less"
val int32_less_equal : int32 * int32 -> bool = no "int32 less or equal"
val int32_minus : int32 * int32 -> int32 = no "int32 minus"
val int32_mod : int32 * int32 -> int32 = yes "int32 modulo"
val int32_multiply : int32 * int32 -> int32 = yes "int32 multiply"
val int32_negate : int32 -> int32 = no "int32 negate"
val int32_not_equal : int32 * int32 -> bool = no "int32 not equal"
val int32_plus : int32 * int32 -> int32 = no "int32 plus"
val length : 'a array -> int = fn x => no "array length" x
val ln : real -> real = yes "real ln"
val load_exn : string -> unit = no "load exn"
val load_funct : string -> unit = no "load funct"
val load_string : string -> unit = no "load string"
val load_struct : string -> unit = no "load struct"
val load_var : string -> unit = no "load var"
val lshift : int * int -> int =no "integer bit shift left"
val make_ml_value : 'a -> ml_value = fn x => obsolete "system make ml value" x
val make_ml_value_tuple : ml_value list -> ml_value =obsolete "system tuple"
val ml_require : string -> ml_value = obsolete "system module require"
val ml_value_from_offset : ml_value * int -> ml_value =no "system ml value from offset"
val notb : int -> int = no "integer bit not"
val orb : int * int -> int = no "integer bit or"
val ord : string -> int = no "string ord"
val char_ord : char -> int = no "string ord"
val ordof : string * int -> int = no "string ordof"
val real : int -> real = no "real"
val real_abs : real -> real = no "real absolute"
val real_equal : real * real -> bool = no "real equal"
val real_greater : real * real -> bool = no "real greater"
val real_greater_or_equal : real * real -> bool = no "real greater or equal"
val real_less : real * real -> bool = no "real less"
val real_less_or_equal : real * real -> bool = no "real less or equal"
val real_minus : real * real -> real = no "real minus"
val real_multiply : real * real -> real = no "real multiply"
val real_negate : real -> real = no "real negate"
val real_not_equal : real * real -> bool = no "real not equal"
val real_plus : real * real -> real = no "real plus"
val record_unsafe_sub : 'a * int -> 'b = fn x => no "record unsafe sub" x
val record_unsafe_update : 'a * int * 'b -> unit = fn x => no "record unsafe update" x
val rshift : int * int -> int = no "integer shift right"
val sin : real -> real = yes "real sin"
val size : string -> int = no "string size"
val sqrt : real -> real = yes "real square root"
val string_equal : string * string -> bool = yes "string equal"
val string_not_equal : string * string -> bool = yes "string not equal"
val string_less : string * string -> bool = yes "string less"
val string_greater : string * string -> bool = yes "string greater"
val string_unsafe_sub : string * int -> int = no "string unsafe sub"
val string_unsafe_update : string * int * int -> unit = no "string unsafe update"
val sub : 'a array * int -> 'a = fn x => no "array sub" x
val unsafe_sub : 'a array * int -> 'a = fn x => no "array sub unsafe" x
val unsafe_update : 'a array * int * 'a -> unit = fn x => no "array update unsafe" x
val update : 'a array * int * 'a -> unit = fn x => no "array update" x
val vector : 'a list -> 'a vector = fn x => no "vector" x
val vector_length : 'a vector -> int = fn x => no "vector length" x
val vector_sub : 'a vector * int -> 'a = fn x => no "vector sub" x
val word32_orb : word32 * word32 -> word32 = no "word32 orb"
val word32_xorb : word32 * word32 -> word32 = no "word32 xorb"
val word32_andb : word32 * word32 -> word32 = no "word32 andb"
val word32_notb : word32 -> word32 = no "word32 notb"
val word32_lshift : word32 * word -> word32 = no "word32 lshift"
val word32_rshift : word32 * word -> word32 = no "word32 rshift"
val word32_arshift : word32 * word -> word32 = no "word32 arshift"
val word32_plus : word32 * word32 -> word32 = no "word32 plus"
val word32_minus : word32 * word32 -> word32 = no "word32 minus"
val word32_star : word32 * word32 -> word32 = yes "word32 times"
val word32_div : word32 * word32 -> word32 = yes "word32 div"
val word32_mod : word32 * word32 -> word32 = yes "word32 mod"
val word32_equal : word32 * word32 -> bool = no "word32 equal"
val word32_not_equal : word32 * word32 -> bool = no "word32 not equal"
val word32_less : word32 * word32 -> bool = no "word32 less"
val word32_less_equal : word32 * word32 -> bool = no "word32 less or equal"
val word32_greater : word32 * word32 -> bool = no "word32 greater"
val word32_greater_equal : word32 * word32 -> bool = no "word32 greater or equal"
val word_orb : word * word -> word = no "word orb"
val word_xorb : word * word -> word = no "word xorb"
val word_andb : word * word -> word = no "word andb"
val word_notb : word -> word = no "word notb"
val word_lshift : word * word -> word = no "word lshift"
val word_rshift : word * word -> word = no "word rshift"
val word_arshift : word * word -> word = no "word arshift"
val word_plus : word * word -> word = no "word plus"
val word_minus : word * word -> word = no "word minus"
val word_star : word * word -> word = yes "word multiply"
val word_div : word * word -> word = yes "word divide"
val word_mod : word * word -> word = yes "word modulus"
val word_equal : word * word -> bool = no "word equal"
val word_not_equal : word * word -> bool = no "word not equal"
val word_less : word * word -> bool = no "word less"
val word_less_equal : word * word -> bool = no "word less or equal"
val word_greater : word * word -> bool = no "word greater"
val word_greater_equal : word * word -> bool = no "word greater or equal"
val xorb : int * int -> int = no "integer bit or exclusive"
fun string_greater_equal arg = not(string_less arg)
fun string_less_equal arg = not(string_greater arg)
end
end
;
