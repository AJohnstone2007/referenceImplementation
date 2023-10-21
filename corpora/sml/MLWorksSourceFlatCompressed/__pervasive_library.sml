require " __builtin_library";
signature ARRAY =
sig
eqtype 'a array
exception Size
exception Subscript
val array: int * '_a -> '_a array
val arrayoflist: '_a list -> '_a array
val tabulate: int * (int -> '_a) -> '_a array
val sub: 'a array * int -> 'a
val update: 'a array * int * 'a -> unit
val length: 'a array -> int
end
signature EXTENDED_ARRAY =
sig
eqtype 'a array
exception Size
exception Subscript
exception Find
val array : int * '_a -> '_a array
val length : 'a array -> int
val update : 'a array * int * 'a -> unit
val sub : 'a array * int -> 'a
val arrayoflist : '_a list -> '_a array
val tabulate : int * (int -> '_a) -> '_a array
val from_list : '_a list -> '_a array
val to_list : 'a array -> 'a list
val fill : 'a array * 'a -> unit
val map : ('a -> '_b) -> 'a array -> '_b array
val map_index : (int * 'a -> '_b) -> 'a array -> '_b array
val iterate : ('a -> unit) -> 'a array -> unit
val iterate_index : (int * 'a -> unit) -> 'a array -> unit
val rev : '_a array -> '_a array
val duplicate : '_a array -> '_a array
val subarray : '_a array * int * int -> '_a array
val append : '_a array * '_a array -> '_a array
val reducel : ('a * 'b -> 'a) -> ('a * 'b array) -> 'a
val reducer : ('a * 'b -> 'b) -> ('a array * 'b) -> 'b
val reducel_index : (int * 'a * 'b -> 'a) -> ('a * 'b array) -> 'a
val reducer_index : (int * 'a * 'b -> 'b) -> ('a array * 'b) -> 'b
val copy : 'a array * int * int * 'a array * int -> unit
val fill_range : 'a array * int * int * 'a -> unit
val find : ('a -> bool) -> 'a array -> int
val find_default : (('a -> bool) * int) -> 'a array -> int
val maxLen : int
end;
signature VECTOR =
sig
eqtype 'a vector
exception Size
exception Subscript
val vector: 'a list -> 'a vector
val tabulate: int * (int -> 'a) -> 'a vector
val sub: 'a vector * int -> 'a
val length: 'a vector -> int
val maxLen : int
end
signature BYTEARRAY =
sig
eqtype bytearray
exception Range of int
exception Size
exception Subscript
exception Substring
exception Find
val array : int * int -> bytearray
val length : bytearray -> int
val update : bytearray * int * int -> unit
val sub : bytearray * int -> int
val arrayoflist : int list -> bytearray
val tabulate : int * (int -> int) -> bytearray
val from_list : int list -> bytearray
val to_list : bytearray -> int list
val from_string : string -> bytearray
val to_string : bytearray -> string
val fill : bytearray * int -> unit
val map : (int -> int) -> bytearray -> bytearray
val map_index : (int * int -> int) -> bytearray -> bytearray
val iterate : (int -> unit) -> bytearray -> unit
val iterate_index : (int * int -> unit) -> bytearray -> unit
val rev : bytearray -> bytearray
val duplicate : bytearray -> bytearray
val subarray : bytearray * int * int -> bytearray
val substring : bytearray * int * int -> string
val append : bytearray * bytearray -> bytearray
val reducel : ('a * int -> 'a) -> ('a * bytearray) -> 'a
val reducer : (int * 'a -> 'a) -> (bytearray * 'a) -> 'a
val reducel_index : (int * 'a * int -> 'a) -> ('a * bytearray) -> 'a
val reducer_index : (int * int * 'a -> 'a) -> (bytearray * 'a) -> 'a
val copy : bytearray * int * int * bytearray * int -> unit
val fill_range : bytearray * int * int * int -> unit
val find : (int -> bool) -> bytearray -> int
val find_default : ((int -> bool) * int) -> bytearray -> int
val maxLen : int
end;
signature FLOATARRAY =
sig
eqtype floatarray
exception Range of int
exception Size
exception Subscript
exception Find
val array : int * real -> floatarray
val length : floatarray -> int
val update : floatarray * int * real -> unit
val sub : floatarray * int -> real
val arrayoflist : real list -> floatarray
val tabulate : int * (int -> real) -> floatarray
val from_list : real list -> floatarray
val to_list : floatarray -> real list
val fill : floatarray * real -> unit
val map : (real -> real) -> floatarray -> floatarray
val map_index : (int * real -> real) -> floatarray -> floatarray
val iterate : (real -> unit) -> floatarray -> unit
val iterate_index : (int * real -> unit) -> floatarray -> unit
val rev : floatarray -> floatarray
val duplicate : floatarray -> floatarray
val subarray : floatarray * int * int -> floatarray
val append : floatarray * floatarray -> floatarray
val reducel : ('a * real -> 'a) -> ('a * floatarray) -> 'a
val reducer : (real * 'a -> 'a) -> (floatarray * 'a) -> 'a
val reducel_index : (int * 'a * real -> 'a) -> ('a * floatarray) -> 'a
val reducer_index : (int * real * 'a -> 'a) -> (floatarray * 'a) -> 'a
val copy : floatarray * int * int * floatarray * int -> unit
val fill_range : floatarray * int * int * real -> unit
val find : (real -> bool) -> floatarray -> int
val find_default : ((real -> bool) * int) -> floatarray -> int
val maxLen : int
end;
signature STRING =
sig
exception Substring
exception Chr
exception Ord
val maxLen : int
val explode : string -> string list
val implode : string list -> string
val chr : int -> string
val ord : string -> int
val substring : string * int * int -> string
val < : string * string -> bool
val > : string * string -> bool
val <= : string * string -> bool
val >= : string * string -> bool
val ordof : string * int -> int
val ml_string : string * int -> string
val implode_char : int list -> string
end;
signature BITS =
sig
val andb : int * int -> int
val orb : int * int -> int
val xorb : int * int -> int
val lshift : int * int -> int
val rshift : int * int -> int
val arshift : int * int -> int
val notb : int -> int
end;
signature GENERAL =
sig
eqtype unit
type exn
exception Bind
exception Match
exception Subscript
exception Size
exception Overflow
exception Domain
exception Div
exception Chr
exception Fail of string
exception Empty
val exnName : exn -> string
val exnMessage : exn -> string
datatype order = LESS | EQUAL | GREATER
val <> : (''a * ''a) -> bool
val ! : 'a ref -> 'a
val := : ('a ref * 'a) -> unit
val o : (('b -> 'c) * ('a -> 'b)) -> 'a -> 'c
val before : ('a * unit) -> 'a
val ignore : 'a -> unit
end
signature MLWORKS =
sig
structure String : STRING
exception Interrupt
structure Deliver :
sig
datatype app_style = CONSOLE | WINDOWS
type deliverer = string * (unit -> unit) * app_style -> unit
type delivery_hook = deliverer -> deliverer
val deliver : deliverer
val with_delivery_hook : delivery_hook -> ('a -> 'b) -> 'a -> 'b
val add_delivery_hook : delivery_hook -> unit
val exitFn : (unit -> unit) ref
end
val arguments : unit -> string list
val name: unit -> string
structure Threads :
sig
type 'a thread
exception Threads of string
val fork : ('a -> 'b) -> 'a -> 'b thread
val yield : unit -> unit
datatype 'a result =
Running
| Waiting
| Sleeping
| Result of 'a
| Exception of exn
| Died
| Killed
| Expired
val result : 'a thread -> 'a result
val sleep : 'a thread -> unit
val wake : 'a thread -> unit
structure Internal :
sig
eqtype thread_id
val id : unit -> thread_id
val get_id : 'a thread -> thread_id
val children : thread_id -> thread_id list
val parent : thread_id -> thread_id
val all : unit -> thread_id list
val kill : thread_id -> unit
val raise_in : thread_id * exn -> unit
val yield_to : thread_id -> unit
val state : thread_id -> unit result
val get_num : thread_id -> int
val set_handler : (int -> unit) -> unit
val reset_fatal_status : unit -> unit
structure Preemption :
sig
val start : unit -> unit
val stop : unit -> unit
val on : unit -> bool
val get_interval : unit -> int
val set_interval : int -> unit
val enter_critical_section: unit -> unit
val exit_critical_section: unit -> unit
val in_critical_section: unit -> bool
end
end
end
structure Internal :
sig
exception Save of string
val save : string * (unit -> 'a) -> unit -> 'a
val execSave : string * (unit -> 'a) -> unit -> 'a
val real_to_string : real * int -> string
exception StringToReal
val string_to_real : string -> real
val text_preprocess : ((int -> string) -> int -> string) ref
structure Types :
sig
type int8
type word8
type int16
type word16
type int32
type word32
datatype 'a option = SOME of 'a | NONE
datatype time = TIME of int * int * int
end
structure Error :
sig
type syserror
exception SysErr of string * syserror Types.option
val errorMsg: syserror -> string
val errorName: syserror -> string
val syserror: string -> syserror Types.option
end
structure IO :
sig
exception Io of {cause: exn, name: string, function: string}
datatype file_desc = FILE_DESC of int
val write : file_desc * string * int * int -> int
val read : file_desc * int -> string
val seek : file_desc * int * int -> int
val close : file_desc -> unit
val can_input : file_desc -> int
end
structure StandardIO :
sig
type IOData = {input: {descriptor: IO.file_desc Types.option,
get: int -> string,
get_pos: (unit -> int) Types.option,
set_pos: (int -> unit) Types.option,
can_input: (unit-> bool) Types.option,
close: unit->unit},
output: {descriptor: IO.file_desc Types.option,
put: {buf:string,i:int,sz:int Types.option} -> int,
get_pos: (unit -> int) Types.option,
set_pos: (int -> unit) Types.option,
can_output: (unit-> bool) Types.option,
close: unit->unit},
error: {descriptor: IO.file_desc Types.option,
put: {buf:string,i:int,sz:int Types.option} -> int,
get_pos: (unit -> int) Types.option,
set_pos: (int -> unit) Types.option,
can_output: (unit->bool) Types.option,
close: unit-> unit},
access: (unit->unit)->unit}
val currentIO: unit -> IOData
val redirectIO: IOData -> unit
val resetIO: unit -> unit
val print : string -> unit
val printError : string -> unit
end
structure Images :
sig
exception Table of string
val clean : unit -> unit
val save : string * (unit -> 'a) -> unit -> 'a
val table : string -> string list
end
structure Bits : BITS
structure Word32 :
sig
val word32_orb: Types.word32 * Types.word32 -> Types.word32
val word32_xorb: Types.word32 * Types.word32 -> Types.word32
val word32_andb: Types.word32 * Types.word32 -> Types.word32
val word32_notb: Types.word32 -> Types.word32
val word32_lshift: Types.word32 * word -> Types.word32
val word32_rshift: Types.word32 * word -> Types.word32
val word32_arshift: Types.word32 * word -> Types.word32
end;
structure Word :
sig
val word_orb: word * word -> word
val word_xorb: word * word -> word
val word_andb: word * word -> word
val word_notb: word -> word
val word_lshift: word * word -> word
val word_rshift: word * word -> word
val word_arshift: word * word -> word
end;
structure Array : ARRAY
structure ByteArray : BYTEARRAY
structure FloatArray: FLOATARRAY
structure ExtendedArray : EXTENDED_ARRAY
structure Vector : VECTOR
structure Value :
sig
type ml_value
type T = ml_value
exception Value of string
val cast : 'a -> 'b
val ccast : 'a -> 'b
val list_to_tuple : T list -> T
val tuple_to_list : T -> T list
val string_to_real : string -> real
val real_to_string : real -> string
val real_equal : real * real -> bool
val arctan : real -> real
val cos : real -> real
val exp : real -> real
val sin : real -> real
val sqrt : real -> real
val unsafe_plus : int * int -> int
val unsafe_minus : int * int -> int
val unsafe_array_sub : '_a Array.array * int -> '_a
val unsafe_array_update : '_a Array.array * int * '_a -> unit
val unsafe_bytearray_sub : ByteArray.bytearray * int -> int
val unsafe_bytearray_update : ByteArray.bytearray * int * int -> unit
val unsafe_floatarray_sub : FloatArray.floatarray * int -> real
val unsafe_floatarray_update : FloatArray.floatarray * int * real -> unit
val unsafe_record_sub : 'a * int -> 'b
val unsafe_record_update : 'a * int * 'b -> unit
val unsafe_string_sub : string * int -> int
val unsafe_string_update : string * int * int -> unit
val alloc_pair : unit -> ml_value
val alloc_vector : int -> ml_value
val alloc_string : int -> string
datatype print_options =
DEFAULT |
OPTIONS of {depth_max : int,
string_length_max : int,
indent : bool,
tags : bool}
val print : print_options * ml_value -> unit
val pointer : T * int -> T
val primary : T -> int
val header : T -> int * int
val update_header : T * int * int -> unit
val sub : T * int -> T
val update : T * int * T -> unit
val sub_byte : T * int -> int
val update_byte : T * int * int -> unit
val exn_name : exn -> string
val exn_argument : exn -> T
val code_name : T -> string
val update_exn : exn * exn ref -> unit
val update_exn_cons : ('a -> exn) * ('a -> exn) ref -> unit
structure Frame :
sig
eqtype frame
val sub : frame * int -> T
val update : frame * int * T -> unit
val current : unit -> frame
val is_ml_frame : frame -> bool
val frame_call : (frame -> 'a) -> 'a
val frame_next : frame -> bool * frame * int
val frame_offset : frame * int -> T
val frame_double : frame * int -> T
val frame_allocations : frame -> bool
end
end
structure Trace :
sig
exception Trace of string
val intercept : ('a -> 'b) * (Value.Frame.frame -> unit) -> unit
val replace : ('a -> 'b) * (Value.Frame.frame -> unit) -> unit
val restore : ('a -> 'b) -> unit
val restore_all : unit -> unit
datatype status = INTERCEPT | NONE | REPLACE | UNTRACEABLE
val status : ('a -> 'b) -> status
end
structure Runtime :
sig
exception Unbound of string
val environment : string -> 'a
val modules : (string * Value.T * Value.T) list ref
structure Loader :
sig
exception Load of string
val load_module : string -> (string * Value.T)
val load_wordset :
int *
{a_names:string list,
b:{a_clos:int, b_spills:int, c_saves:int, d_code:string} list,
c_leafs:bool list, d_intercept:int list,
e_stack_parameters: int list} ->
(int * Value.T) list
end;
structure Memory :
sig
val gc_message_level : int ref
val max_stack_blocks : int ref
val collect : int -> unit
val collect_all : unit -> unit
val collections : unit -> int * int
val promote_all : unit -> unit
end;
structure Event :
sig
datatype T = SIGNAL of int
exception Signal of string
val signal : int * (int -> unit) -> unit
val stack_overflow_handler : (unit -> unit) -> unit
val interrupt_handler : (unit -> unit) -> unit
end;
end
structure Dynamic :
sig
type dynamic
type type_rep
exception Coerce of type_rep * type_rep
val generalises_ref : ((type_rep * type_rep) -> bool) ref
val coerce : (dynamic * type_rep) -> Value.ml_value
end
structure Exit :
sig
eqtype key
type status = Types.word32
val success : status
val failure : status
val atExit : (unit -> unit) -> key
val removeAtExit : key -> unit
val exit : status -> 'a
val terminate : status -> 'a
end
structure Debugger :
sig
val break_hook : (string -> unit) ref
val break : string -> unit
end
end
structure Profile :
sig
type manner
type function_id = string
type cost_centre_profile = unit
datatype object_kind =
RECORD
| PAIR
| CLOSURE
| STRING
| ARRAY
| BYTEARRAY
| OTHER
| TOTAL
datatype large_size =
Large_Size of
{megabytes : int,
bytes : int}
datatype object_count =
Object_Count of
{number : int,
size : large_size,
overhead : int}
datatype function_space_profile =
Function_Space_Profile of
{allocated : large_size,
copied : large_size,
copies : large_size list,
allocation : (object_kind * object_count) list list}
datatype function_caller =
Function_Caller of
{id: function_id,
found: int,
top: int,
scans: int,
callers: function_caller list}
datatype function_time_profile =
Function_Time_Profile of
{found: int,
top: int,
scans: int,
depth: int,
self: int,
callers: function_caller list}
datatype function_profile =
Function_Profile of
{id: function_id,
call_count: int,
time: function_time_profile,
space: function_space_profile}
datatype general_header =
General of
{data_allocated: int,
period: Internal.Types.time,
suspended: Internal.Types.time}
datatype call_header =
Call of {functions : int}
datatype time_header =
Time of
{data_allocated: int,
functions: int,
scans: int,
gc_ticks: int,
profile_ticks: int,
frames: real,
ml_frames: real,
max_ml_stack_depth: int}
datatype space_header =
Space of
{data_allocated: int,
functions: int,
collections: int,
total_profiled : function_space_profile}
type cost_header = unit
datatype profile =
Profile of
{general: general_header,
call: call_header,
time: time_header,
space: space_header,
cost: cost_header,
functions: function_profile list,
centres: cost_centre_profile list}
datatype options =
Options of
{scan : int,
selector : function_id -> manner}
datatype 'a result =
Result of 'a
| Exception of exn
exception ProfileError of string
val profile : options -> ('a -> 'b) -> 'a -> ('b result * profile)
val make_manner :
{time : bool,
space : bool,
calls : bool,
copies : bool,
depth : int,
breakdown : object_kind list} -> manner
end
end ;
structure FullPervasiveLibrary_ :
sig
structure MLWorks : MLWORKS
datatype option = datatype MLWorks.Internal.Types.option
structure General: GENERAL
exception Option
include GENERAL
eqtype 'a array
eqtype 'a vector
val chr : int -> char
val ord : char -> int
val floor : real -> int
val ceil : real -> int
val trunc : real -> int
val round : real -> int
val real : int -> real
val / : real * real -> real
val @ : 'a list * 'a list -> 'a list
val ^ : string * string -> string
val map : ('a -> 'b) -> 'a list -> 'b list
val not : bool -> bool
val rev : 'a list -> 'a list
val size : string -> int
val str : char -> string
val concat : string list -> string
val implode : char list -> string
val explode : string -> char list
val substring : string * int * int -> string
val null : 'a list -> bool
val hd : 'a list -> 'a
val tl : 'a list -> 'a list
val length : 'a list -> int
val app : ('a -> unit) -> 'a list -> unit
val foldr : ('a * 'b -> 'b) -> 'b -> 'a list -> 'b
val foldl : ('a * 'b -> 'b) -> 'b -> 'a list -> 'b
val print : string -> unit
val vector : 'a list -> 'a vector
val isSome : 'a option -> bool
val valOf : 'a option -> 'a
val getOpt : 'a option * 'a -> 'a
end
=
struct
open BuiltinLibrary_
val chr = char_chr
val ord = char_ord
exception Unimplemented of string
exception Substring
fun ignore x = ()
structure MLWorks : MLWORKS =
struct
exception Interrupt
val arguments : unit -> string list = call_c "system os arguments"
val name: unit -> string = call_c "system os name"
structure String : STRING =
struct
open BuiltinLibrary_
exception Substring = Substring
val maxLen = 16777195
val unsafe_substring : (string * int * int) -> string =
call_c "string unsafe substring"
fun substring (s, i, n) =
if n < 0 then raise Substring
else if i < 0 then raise Substring
else if i > size s - n then raise Substring
else if n > 12 then unsafe_substring (s, i, n)
else
let
val new_s = alloc_string (n + 1)
fun copy 0 = new_s
| copy j =
let
val j' = j - 1
in
string_unsafe_update
(new_s, j', string_unsafe_sub (s, i+j'));
copy j'
end
in
string_unsafe_update (new_s, n, 0);
copy n
end;
val c_implode_char : int list * int -> string =
call_c "string c implode char"
fun implode_char cl : string =
let
fun copyall ([],start,to) = to
| copyall (c::cl,start,to) =
(string_unsafe_update (to,start,c);
copyall (cl,start+1,to))
fun get_size (a::rest,sz) = get_size (rest,1 + sz)
| get_size ([],sz) =
if sz > 30 then c_implode_char (cl,sz)
else
let
val result = alloc_string (sz+1)
val _ = string_unsafe_update (result,sz,0)
in
copyall (cl,0,result)
end
in
get_size (cl,0)
end
fun ml_string (s,max_size) =
let
val string_abbrev = "\\..."
val (s,abbrev) =
if max_size < 0 orelse size s <= max_size
then (s,false)
else if max_size < size string_abbrev
then ("",true)
else (substring (s,0,max_size - size string_abbrev),true)
fun to_digit n = chr (n +ord "0")
fun aux ([],result) = implode (rev result)
| aux (char::rest,result) =
let val newres =
case char of
"\n" => "\\n"::result
| "\t" => "\\t"::result
| "\"" => "\\\""::result
| "\\" => "\\\\"::result
| c =>
let val n = ord c
in
if n < 32 orelse n >= 127 then
let
val n1 = n div 10
in
(to_digit (n mod 10))::
(to_digit (n1 mod 10))::
(to_digit (n1 div 10))::
"\\" :: result
end
else
c::result
end
in
aux (rest, newres)
end
in
aux (explode s,[]) ^ (if abbrev then string_abbrev else "")
end
val op < : string * string -> bool = string_less
val op > : string * string -> bool = string_greater
val op <= : string * string -> bool = string_less_equal
val op >= : string * string -> bool = string_greater_equal
end
structure Deliver =
struct
datatype app_style = CONSOLE | WINDOWS
type deliverer = string * (unit -> unit) * app_style -> unit
type delivery_hook = deliverer -> deliverer
local
val c_deliver : deliverer = call_c "function deliver"
val delivery_hooks : delivery_hook list ref = ref []
fun true_deliverer (f,[]) = f
| true_deliverer (f,h::hs) = true_deliverer (h f, hs)
in
fun add_delivery_hook (h : delivery_hook) =
delivery_hooks := (h::(!delivery_hooks))
fun with_delivery_hook h f x =
let
val hs = !delivery_hooks
val _ = delivery_hooks := (h::hs)
fun restore () = delivery_hooks := hs
val result = f x handle exn => (restore (); raise exn)
in
(restore (); result)
end
val exitFn = ref (fn () => ())
fun deliver args =
let
val old_deliverer = true_deliverer(c_deliver, !delivery_hooks)
val deliverer =
(fn (s, f, b) =>
old_deliverer (s, fn () => (ignore(f()); (!exitFn) ()), b) )
val hooks = !delivery_hooks
in
delivery_hooks := [];
deliverer args
handle exn =>
(delivery_hooks := hooks; raise exn);
delivery_hooks := hooks
end
end
end
structure Internal =
struct
exception Save of string
structure Types =
struct
type int8 = int8
type word8 = word8
type int16 = int16
type word16 = word16
type int32 = int32
type word32 = word32
datatype 'a option = SOME of 'a | NONE
datatype time = TIME of int * int * int
end
val save : string * (unit -> 'a) -> unit -> 'a =
fn x => call_c "image save" x
val execSave = fn x => call_c "exec image save" x
val real_to_string : real * int -> string = call_c "real to string"
exception StringToReal
val string_to_real : string -> real = call_c "real from string"
val text_preprocess = ref (fn (f : int -> string ) => f)
structure Error =
struct
type syserror = int
exception SysErr of string * syserror Types.option
val errorMsg : syserror -> string = call_c "OS.errorMsg"
val errorName : syserror -> string = call_c "OS.errorName"
val syserror : string -> syserror Types.option
= call_c "OS.syserror"
end
structure IO =
struct
exception Io of {name : string, function : string, cause : exn}
datatype file_desc = FILE_DESC of int
val write: file_desc * string * int * int-> int
= call_c "system io write"
val read: file_desc * int -> string
= call_c "system io read"
val seek: file_desc * int * int -> int
= call_c "system io seek"
val close: file_desc -> unit
= call_c "system io close"
val can_input: file_desc -> int
= call_c "system io can input"
end
structure StandardIO =
struct
type IOData = {input:{descriptor: IO.file_desc Types.option,
get: int -> string,
get_pos: (unit -> int) Types.option,
set_pos: (int -> unit) Types.option,
close: unit -> unit,
can_input: (unit -> bool) Types.option},
output: {descriptor: IO.file_desc Types.option,
put: {buf:string, i:int, sz: int Types.option}
-> int,
get_pos: (unit -> int) Types.option,
set_pos: (int -> unit) Types.option,
can_output: (unit->bool) Types.option,
close: unit-> unit},
error: {descriptor: IO.file_desc Types.option,
put: {buf:string, i:int, sz: int Types.option}
-> int,
get_pos: (unit -> int) Types.option,
set_pos: (int -> unit) Types.option,
can_output: (unit->bool) Types.option,
close: unit->unit},
access: (unit->unit)->unit}
local
val stdIn: IO.file_desc ref =
call_c "system io standard input";
val stdOut: IO.file_desc ref =
call_c "system io standard output";
val stdErr: IO.file_desc ref =
call_c "system io standard error";
val terminalIO : IOData =
{output={descriptor=Types.SOME (!stdOut),
put=fn {buf,i,sz=Types.NONE} =>
IO.write(!stdOut,buf,i,size buf-i)
| {buf,i,sz=Types.SOME n} =>
IO.write(!stdOut,buf,i,n),
get_pos = Types.SOME(fn () =>
IO.seek(!stdOut,0,1)),
set_pos = Types.SOME
(fn i => (ignore(IO.seek (!stdOut,i,0));())),
can_output = Types.NONE,
close = fn () => IO.close (!stdOut)},
error={descriptor=Types.SOME (!stdErr),
put=fn {buf,i,sz=Types.NONE} =>
IO.write(!stdErr,buf,i,size buf-i)
| {buf,i,sz=Types.SOME n} =>
IO.write(!stdErr,buf,i,n),
get_pos=
Types.SOME
(fn () => IO.seek(!stdErr,0,1)),
set_pos= Types.SOME
(fn i => (ignore(IO.seek(!stdErr,i,0)); ())),
can_output = Types.NONE,
close= fn () => IO.close (!stdErr)},
input={descriptor=Types.SOME (!stdIn),
get= fn i => IO.read (!stdIn,i),
get_pos= Types.SOME
(fn () => IO.seek(!stdIn,0,1)),
set_pos= Types.SOME
(fn i => (ignore(IO.seek(!stdIn,i,0));())),
close= fn() => IO.close (!stdIn),
can_input=
Types.SOME
(fn()=>IO.can_input (!stdIn)>0)},
access = fn f =>f()}
val currIO: IOData ref = ref terminalIO
in
fun currentIO () = !currIO
fun redirectIO (newIO:IOData) =
(currIO:=newIO)
fun resetIO() = currIO:=terminalIO
fun print s = (ignore(#put(#output(currentIO()))
{buf=s,i=0,sz=Types.NONE}); ())
fun printError s = (ignore(#put(#error(currentIO()))
{buf=s,i=0,sz=Types.NONE});())
end
end
structure Exit =
struct
type key = int
type status = word32
val success : status = 0w0
val failure : status = 0w1
fun terminate (x:status):'a = call_c "system os exit" x
val exitActions = ref [] : (key * (unit -> unit)) list ref
val exiting = ref false : bool ref
fun exit exitCode =
let
fun exit' [] = terminate exitCode
| exit' ((_,action)::actions) =
(exitActions := actions;
(action () handle _ => ());
exit' actions)
in
exiting := true;
exit' (!exitActions)
end
val key = ref 0
val firstExitFn = ref true
fun atExit action =
if !exiting
then 0
else
(if !firstExitFn then
(Deliver.exitFn := (fn () => (ignore(exit success); ()));
firstExitFn := false)
else ();
key := !key + 1;
exitActions := (!key, action)::(!exitActions);
!key)
fun removeAtExit key =
if !exiting then ()
else
let fun rem [] = []
| rem ((p as (key',_))::t) =
if key = key' then t else p :: (rem t)
in exitActions := rem(!exitActions)
end
end
structure Debugger =
struct
fun default_break s = StandardIO.print("Break at " ^ s ^ "\n")
val break_hook = ref default_break
fun break s = (!break_hook) s
end
structure Images =
struct
exception Table of string
val clean : unit -> unit = call_c "clean code vectors"
val save = save
val table : string -> string list = call_c "image table"
end
structure Bits : BITS =
struct
open BuiltinLibrary_
end
structure Word =
struct
open BuiltinLibrary_
val orb = word_orb
val xorb = word_xorb
val andb = word_andb
val notb = word_notb
val << = word_lshift
val >> = word_rshift
val ~>> = word_arshift
end
structure Word32 =
struct
val word32_orb = word32_orb
val word32_xorb = word32_xorb
val word32_andb = word32_andb
val word32_notb = word32_notb
val word32_lshift = word32_lshift
val word32_rshift = word32_rshift
val word32_arshift = word32_arshift
end
structure FloatArray: FLOATARRAY =
struct
type floatarray = BuiltinLibrary_.floatarray
exception Range = BuiltinLibrary_.Range
exception Size = BuiltinLibrary_.Size
exception Subscript = BuiltinLibrary_.Subscript
val array = BuiltinLibrary_.floatarray
val length = BuiltinLibrary_.floatarray_length
val update = BuiltinLibrary_.floatarray_update
val unsafe_update = BuiltinLibrary_.floatarray_unsafe_update
val sub = BuiltinLibrary_.floatarray_sub
val unsafe_sub = BuiltinLibrary_.floatarray_unsafe_sub
val maxLen = 1048575
fun tabulate (l, f) =
let
val a = array (l, 0.0)
fun init n =
if n = l then a
else
(unsafe_update (a, n, f n);
init (n+1))
in
init 0
end
fun from_list list =
let
fun list_length (n, []) = n
| list_length (n, _::xs) = list_length (n+1, xs)
val new = array (list_length (0, list), 0.0)
fun fill (_, []) = new
| fill (n, x::xs) =
(unsafe_update (new, n, x);
fill (n+1, xs))
in
fill (0, list)
end
val arrayoflist = from_list
fun fill (a, x) =
let
fun fill' 0 = ()
| fill' n =
let
val n' = n-1
in
(unsafe_update (a, n', x);
fill' n')
end
in
fill' (length a)
end
fun map f a =
let
val l = length a
val new = array (l, 0.0)
fun map' n =
if n = l then
new
else
(unsafe_update (new, n, f (unsafe_sub (a, n)));
map' (n+1))
in
map' 0
end
fun map_index f a =
let
val l = length a
val new = array (l, 0.0)
fun map' n =
if n = l then
new
else
(unsafe_update (new, n, f (n, unsafe_sub (a, n)));
map' (n+1))
in
map' 0
end
fun to_list a =
let
fun to_list' (0, list) = list
| to_list' (n, list) =
let
val n' = n-1
in
to_list' (n', unsafe_sub (a, n') :: list)
end
in
to_list' (length a, nil)
end
fun iterate f a =
let
val l = length a
fun iterate' n =
if n = l then
()
else
(ignore(f (unsafe_sub (a, n)));
iterate' (n+1))
in
iterate' 0
end
fun iterate_index f a =
let
val l = length a
fun iterate' n =
if n = l then
()
else
(ignore(f (n, unsafe_sub (a, n)));
iterate' (n+1))
in
iterate' 0
end
fun rev a =
let
val l = length a
val new = array (l, 0.0)
fun rev' 0 = new
| rev' n =
let
val n' = n-1
in
(unsafe_update (new, n', unsafe_sub (a, l-n));
rev' n')
end
in
rev' l
end
fun duplicate a =
let
val l = length a
val new = array (l, 0.0)
fun duplicate' 0 = new
| duplicate' n =
let
val n' = n-1
in
(unsafe_update (new, n', unsafe_sub (a, n'));
duplicate' n')
end
in
duplicate' l
end
fun subarray (a, start, finish) =
let
val l = length a
in
if start < 0 orelse start > l orelse finish > l orelse
start > finish then
raise Subscript
else
let
val l' = finish - start
val new = array (l', 0.0)
fun copy 0 = new
| copy n =
let
val n' = n-1
in
(unsafe_update (new, n', unsafe_sub (a, start+n'));
copy n')
end
in
copy l'
end
end
fun append (array1, array2) =
let
val l1 = length array1
val l2 = length array2
val new = array (l1+l2, 0.0)
fun copy1 0 = new
| copy1 n =
let
val n' = n-1
in
(unsafe_update (new, n', unsafe_sub (array1, n'));
copy1 n')
end
fun copy2 0 = copy1 l1
| copy2 n =
let
val n' = n-1
in
(unsafe_update (new, n'+l1, unsafe_sub (array2, n'));
copy2 n')
end
in
copy2 l2
end
fun reducel f (i, a) =
let
val l = length a
fun reducel' (i, n) =
if n = l then
i
else
reducel' (f (i, unsafe_sub (a, n)), n+1)
in
reducel' (i, 0)
end
fun reducer f (a, i) =
let
val l = length a
fun reducer' (0, i) = i
| reducer' (n, i) =
let
val n' = n-1
in
reducer' (n', f (unsafe_sub (a, n'), i))
end
in
reducer' (l, i)
end
fun reducel_index f (i, a) =
let
val l = length a
fun reducel' (i, n) =
if n = l then
i
else
reducel' (f (n, i, unsafe_sub (a, n)), n+1)
in
reducel' (i, 0)
end
fun reducer_index f (a, i) =
let
val l = length a
fun reducer' (0, i) = i
| reducer' (n, i) =
let
val n' = n-1
in
reducer' (n', f (n', unsafe_sub (a, n'), i))
end
in
reducer' (l, i)
end
fun copy (from, start, finish, to, start') =
let
val l1 = length from
val l2 = length to
in
if start < 0 orelse start > l1 orelse finish > l1 orelse
start > finish orelse
start' < 0 orelse start' + finish - start > l2 then
raise Subscript
else
let
fun copydown 0 = ()
| copydown n =
let
val n' = n-1
in
(unsafe_update (to, start'+n',
unsafe_sub (from, start+n'));
copydown n')
end
fun copyup i =
if i = finish then ()
else
(unsafe_update (to,i-start+start',
unsafe_sub (from,i));
copyup (i + 1))
in
if start < start'
then copydown (finish - start)
else copyup start
end
end
fun fill_range (a, start, finish, x) =
let
val l = length a
in
if start < 0 orelse start > l orelse finish > l orelse
start > finish then
raise Subscript
else
let
fun fill' 0 = ()
| fill' n =
let
val n' = n-1
in
(unsafe_update (a, start+n', x);
fill' n')
end
in
fill' (finish - start)
end
end
exception Find
fun find predicate a =
let
val l = length a
fun find' n =
if n = l then
raise Find
else
if predicate (unsafe_sub (a, n)) then n
else find' (n+1)
in
find' 0
end
fun find_default (predicate, default) a =
let
val l = length a
fun find' n =
if n = l then
default
else
if predicate (unsafe_sub (a, n)) then n
else find' (n+1)
in
find' 0
end
end
structure ByteArray : BYTEARRAY =
struct
open BuiltinLibrary_
exception Substring = Substring
type bytearray = bytearray
val maxLen = 16777196
val array = BuiltinLibrary_.bytearray
val length = BuiltinLibrary_.bytearray_length
val update = BuiltinLibrary_.bytearray_update
val unsafe_update = BuiltinLibrary_.bytearray_unsafe_update
val sub = BuiltinLibrary_.bytearray_sub
val unsafe_sub = BuiltinLibrary_.bytearray_unsafe_sub
val to_string : bytearray -> string =
call_c "bytearray to string"
val from_string : string -> bytearray =
call_c "bytearray from string"
val substring : bytearray * int * int -> string =
call_c "bytearray substring"
fun tabulate (l, f) =
let
val a = array (l, 0)
fun init n =
if n = l then a
else
(unsafe_update (a, n, f n);
init (n+1))
in
init 0
end
fun from_list list =
let
fun list_length (n, []) = n
| list_length (n, _::xs) = list_length (n+1, xs)
val new = array (list_length (0, list), 0)
fun fill (_, []) = new
| fill (n, x::xs) =
(unsafe_update (new, n, x);
fill (n+1, xs))
in
fill (0, list)
end
val arrayoflist = from_list
fun fill (a, x) =
let
fun fill' 0 = ()
| fill' n =
let
val n' = n-1
in
(unsafe_update (a, n', x);
fill' n')
end
in
fill' (length a)
end
fun map f a =
let
val l = length a
val new = array (l, 0)
fun map' n =
if n = l then
new
else
(unsafe_update (new, n, f (unsafe_sub (a, n)));
map' (n+1))
in
map' 0
end
fun map_index f a =
let
val l = length a
val new = array (l, 0)
fun map' n =
if n = l then
new
else
(unsafe_update (new, n, f (n, unsafe_sub (a, n)));
map' (n+1))
in
map' 0
end
fun to_list a =
let
fun to_list' (0, list) = list
| to_list' (n, list) =
let
val n' = n-1
in
to_list' (n', unsafe_sub (a, n') :: list)
end
in
to_list' (length a, nil)
end
fun iterate f a =
let
val l = length a
fun iterate' n =
if n = l then
()
else
(ignore(f (unsafe_sub (a, n)));
iterate' (n+1))
in
iterate' 0
end
fun iterate_index f a =
let
val l = length a
fun iterate' n =
if n = l then
()
else
(ignore(f (n, unsafe_sub (a, n)));
iterate' (n+1))
in
iterate' 0
end
fun rev a =
let
val l = length a
val new = array (l, 0)
fun rev' 0 = new
| rev' n =
let
val n' = n-1
in
(unsafe_update (new, n', unsafe_sub (a, l-n));
rev' n')
end
in
rev' l
end
fun duplicate a =
let
val l = length a
val new = array (l, 0)
fun duplicate' 0 = new
| duplicate' n =
let
val n' = n-1
in
(unsafe_update (new, n', unsafe_sub (a, n'));
duplicate' n')
end
in
duplicate' l
end
fun subarray (a, start, finish) =
let
val l = length a
in
if start < 0 orelse start > l orelse finish > l orelse
start > finish then
raise Subscript
else
let
val l' = finish - start
val new = array (l', 0)
fun copy 0 = new
| copy n =
let
val n' = n-1
in
(unsafe_update (new, n', unsafe_sub (a, start+n'));
copy n')
end
in
copy l'
end
end
fun append (array1, array2) =
let
val l1 = length array1
val l2 = length array2
val new = array (l1+l2, 0)
fun copy1 0 = new
| copy1 n =
let
val n' = n-1
in
(unsafe_update (new, n', unsafe_sub (array1, n'));
copy1 n')
end
fun copy2 0 = copy1 l1
| copy2 n =
let
val n' = n-1
in
(unsafe_update (new, n'+l1, unsafe_sub (array2, n'));
copy2 n')
end
in
copy2 l2
end
fun reducel f (i, a) =
let
val l = length a
fun reducel' (i, n) =
if n = l then
i
else
reducel' (f (i, unsafe_sub (a, n)), n+1)
in
reducel' (i, 0)
end
fun reducer f (a, i) =
let
val l = length a
fun reducer' (0, i) = i
| reducer' (n, i) =
let
val n' = n-1
in
reducer' (n', f (unsafe_sub (a, n'), i))
end
in
reducer' (l, i)
end
fun reducel_index f (i, a) =
let
val l = length a
fun reducel' (i, n) =
if n = l then
i
else
reducel' (f (n, i, unsafe_sub (a, n)), n+1)
in
reducel' (i, 0)
end
fun reducer_index f (a, i) =
let
val l = length a
fun reducer' (0, i) = i
| reducer' (n, i) =
let
val n' = n-1
in
reducer' (n', f (n', unsafe_sub (a, n'), i))
end
in
reducer' (l, i)
end
fun copy (from, start, finish, to, start') =
let
val l1 = length from
val l2 = length to
in
if start < 0 orelse start > l1 orelse finish > l1 orelse
start > finish orelse
start' < 0 orelse start' + finish - start > l2 then
raise Subscript
else
let
fun copydown 0 = ()
| copydown n =
let
val n' = n-1
in
(unsafe_update (to, start'+n',
unsafe_sub (from, start+n'));
copydown n')
end
fun copyup i =
if i = finish then ()
else
(unsafe_update (to,i-start+start',
unsafe_sub (from,i));
copyup (i + 1))
in
if start < start'
then copydown (finish - start)
else copyup start
end
end
fun fill_range (a, start, finish, x) =
let
val l = length a
in
if start < 0 orelse start > l orelse finish > l orelse
start > finish then
raise Subscript
else
let
fun fill' 0 = ()
| fill' n =
let
val n' = n-1
in
(unsafe_update (a, start+n', x);
fill' n')
end
in
fill' (finish - start)
end
end
exception Find
fun find predicate a =
let
val l = length a
fun find' n =
if n = l then
raise Find
else
if predicate (unsafe_sub (a, n)) then n else find' (n+1)
in
find' 0
end
fun find_default (predicate, default) a =
let
val l = length a
fun find' n =
if n = l then
default
else
if predicate (unsafe_sub (a, n)) then n else find' (n+1)
in
find' 0
end
end
structure ExtendedArray : EXTENDED_ARRAY =
struct
open BuiltinLibrary_
type 'a array = 'a array
val maxLen = 4194297
fun tabulate (l, f) =
let
val a = array (l, 0)
fun init n =
if n = l then a
else
(unsafe_update (a, n, f n);
init (n+1))
in
init 0
end
fun from_list list =
let
fun list_length (n, []) = n
| list_length (n, _::xs) = list_length (n+1, xs)
val new = array (list_length (0, list), 0)
fun fill (_, []) = new
| fill (n, x::xs) =
(unsafe_update (new, n, x);
fill (n+1, xs))
in
fill (0, list)
end
val arrayoflist = from_list
fun fill (a, x) =
let
fun fill' 0 = ()
| fill' n =
let
val n' = n-1
in
(unsafe_update (a, n', x);
fill' n')
end
in
fill' (length a)
end
fun map f a =
let
val l = length a
val new = array (l, 0)
fun map' n =
if n = l then
new
else
(unsafe_update (new, n, f (unsafe_sub (a, n)));
map' (n+1))
in
map' 0
end
fun map_index f a =
let
val l = length a
val new = array (l, 0)
fun map' n =
if n = l then
new
else
(unsafe_update (new, n, f (n, unsafe_sub (a, n)));
map' (n+1))
in
map' 0
end
fun to_list a =
let
fun to_list' (0, list) = list
| to_list' (n, list) =
let
val n' = n-1
in
to_list' (n', unsafe_sub (a, n') :: list)
end
in
to_list' (length a, nil)
end
fun iterate f a =
let
val l = length a
fun iterate' n =
if n = l then
()
else
(ignore(f (unsafe_sub (a, n)));
iterate' (n+1))
in
iterate' 0
end
fun iterate_index f a =
let
val l = length a
fun iterate' n =
if n = l then
()
else
(ignore(f (n, unsafe_sub (a, n)));
iterate' (n+1))
in
iterate' 0
end
fun rev a =
let
val l = length a
val new = array (l, 0)
fun rev' 0 = new
| rev' n =
let
val n' = n-1
in
(unsafe_update (new, n', unsafe_sub (a, l-n));
rev' n')
end
in
rev' l
end
fun duplicate a =
let
val l = length a
val new = array (l, 0)
fun duplicate' 0 = new
| duplicate' n =
let
val n' = n-1
in
(unsafe_update (new, n', unsafe_sub (a, n'));
duplicate' n')
end
in
duplicate' l
end
exception Subarray of int * int
fun subarray (a, start, finish) =
let
val l = length a
in
if start < 0 orelse start > l orelse finish > l orelse
start > finish then
raise Subscript
else
let
val l' = finish - start
val new = array (l', 0)
fun copy 0 = new
| copy n =
let
val n' = n-1
in
(unsafe_update (new, n', unsafe_sub (a, start+n'));
copy n')
end
in
copy l'
end
end
fun append (array1, array2) =
let
val l1 = length array1
val l2 = length array2
val new = array (l1+l2, 0)
fun copy1 0 = new
| copy1 n =
let
val n' = n-1
in
(unsafe_update (new, n', unsafe_sub (array1, n'));
copy1 n')
end
fun copy2 0 = copy1 l1
| copy2 n =
let
val n' = n-1
in
(unsafe_update (new, n'+l1, unsafe_sub (array2, n'));
copy2 n')
end
in
copy2 l2
end
fun reducel f (i, a) =
let
val l = length a
fun reducel' (i, n) =
if n = l then
i
else
reducel' (f (i, unsafe_sub (a, n)), n+1)
in
reducel' (i, 0)
end
fun reducer f (a, i) =
let
val l = length a
fun reducer' (0, i) = i
| reducer' (n, i) =
let
val n' = n-1
in
reducer' (n', f (unsafe_sub (a, n'), i))
end
in
reducer' (l, i)
end
fun reducel_index f (i, a) =
let
val l = length a
fun reducel' (i, n) =
if n = l then
i
else
reducel' (f (n, i, unsafe_sub (a, n)), n+1)
in
reducel' (i, 0)
end
fun reducer_index f (a, i) =
let
val l = length a
fun reducer' (0, i) = i
| reducer' (n, i) =
let
val n' = n-1
in
reducer' (n', f (n', unsafe_sub (a, n'), i))
end
in
reducer' (l, i)
end
fun copy (from, start, finish, to, start') =
let
val l1 = length from
val l2 = length to
in
if start < 0 orelse start > l1 orelse finish > l1 orelse
start > finish orelse
start' < 0 orelse start' + finish - start > l2 then
raise Subscript
else
let
fun copydown 0 = ()
| copydown n =
let
val n' = n-1
in
(unsafe_update (to, start'+n', unsafe_sub (from, start+n'));
copydown n')
end
fun copyup i =
if i = finish then ()
else
(unsafe_update (to,i-start+start',unsafe_sub (from,i));
copyup (i + 1))
in
if start < start'
then copydown (finish - start)
else copyup start
end
end
fun fill_range (a, start, finish, x) =
let
val l = length a
in
if start < 0 orelse start > l orelse finish > l orelse
start > finish then
raise Subscript
else
let
fun fill' 0 = ()
| fill' n =
let
val n' = n-1
in
(unsafe_update (a, start+n', x);
fill' n')
end
in
fill' (finish - start)
end
end
exception Find
fun find predicate a =
let
val l = length a
fun find' n =
if n = l then
raise Find
else
if predicate (unsafe_sub (a, n)) then n else find' (n+1)
in
find' 0
end
fun find_default (predicate, default) a =
let
val l = length a
fun find' n =
if n = l then
default
else
if predicate (unsafe_sub (a, n)) then n else find' (n+1)
in
find' 0
end
end
structure Array : ARRAY = ExtendedArray
structure Vector : VECTOR =
struct
open BuiltinLibrary_
type 'a vector = 'a vector
val maxLen = 4194299
fun length' ([],n) = n
| length' (a::b,n) = length' (b,unsafe_int_plus (n,1))
fun length l = length' (l,0)
fun update ([],n,v) = ()
| update (a::rest,n,v) =
(record_unsafe_update (v,n,a);
update (rest,unsafe_int_plus (n,1),v))
fun vector (l : 'a list) : 'a vector =
let
val len = length l
val result = alloc_vector len
val _ = update (l,0,result)
in
result
end
fun tabulate(i, f) =
if i < 0 then
raise Size
else
let
fun make_list(done, j) =
if j >= i then done
else
make_list(f j :: done, j+1)
in
vector (rev (make_list([], 0)))
end
val sub = vector_sub
val length = vector_length
end
structure Value =
struct
open BuiltinLibrary_
type T = ml_value
exception Value of string
val cast = cast
val ccast = fn x => call_c "value cast" x
val list_to_tuple : ml_value list -> ml_value = call_c "value list to tuple"
val tuple_to_list : ml_value -> ml_value list = call_c "value tuple to list"
val string_to_real : string -> real = call_c "value string to real"
val real_to_string : real -> string = call_c "value real to string"
val real_equal : real * real -> bool = real_equal
val arctan : real -> real = arctan
val cos : real -> real = cos
val exp : real -> real = exp
val sin : real -> real = sin
val sqrt : real -> real = sqrt
val unsafe_plus = unsafe_int_plus
val unsafe_minus = unsafe_int_minus
val unsafe_array_sub = unsafe_sub
val unsafe_array_update = unsafe_update
val unsafe_bytearray_sub = bytearray_unsafe_sub
val unsafe_bytearray_update = bytearray_unsafe_update
val unsafe_floatarray_sub = floatarray_unsafe_sub
val unsafe_floatarray_update = floatarray_unsafe_update
val unsafe_record_sub = record_unsafe_sub
val unsafe_record_update = record_unsafe_update
val unsafe_string_sub = string_unsafe_sub
val unsafe_string_update = string_unsafe_update
val alloc_pair = alloc_pair
val alloc_string = alloc_string
val alloc_vector = alloc_vector
datatype print_options =
DEFAULT |
OPTIONS of {depth_max : int,
string_length_max : int,
indent : bool,
tags : bool}
val print : print_options * T -> unit = call_c "value print"
val primary : T -> int = call_c "value primary"
val header : T -> int * int = call_c "value header"
val pointer : T * int -> T = call_c "value pointer"
val update : T * int * T -> unit = call_c "value update value"
val sub : T * int -> T = call_c "value sub value"
val update_byte : T * int * int -> unit = call_c "value update byte"
val sub_byte : T * int -> int = call_c "value sub byte"
val update_header : T * int * int -> unit = call_c "value update header"
val code_name : ml_value -> string = call_c "value code name"
val exn_name : exn -> string = call_c "value exn name"
val exn_argument : exn -> T = call_c "value exn argument"
fun update_exn_sub(exn1, exn2) =
let
val constructor1 = unsafe_record_sub(exn1, 0)
val constructor2 = unsafe_record_sub(exn2, 0)
val unique = unsafe_record_sub(constructor2, 0)
in
unsafe_record_update(constructor1, 0, unique)
end
fun update_exn(exn1, ref exn2) =
update_exn_sub(exn1, exn2)
fun update_exn_cons(exn1_cons, ref exn2_cons) =
update_exn_sub(exn1_cons(cast 0), exn2_cons(cast 0))
structure Frame =
struct
datatype frame = FRAME of int
val sub : frame * int -> ml_value = call_c "stack frame sub"
val update : frame * int * ml_value -> unit = call_c "stack frame update"
val current : unit -> frame = call_c "stack frame current"
val is_ml_frame : frame -> bool = call_c "stack is ml frame"
val frame_call = fn x => call_c "debugger frame call" x
val frame_next : frame -> (bool * frame * int) = call_c "debugger frame next"
val frame_offset = fn x => call_c "debugger frame offset" x
val frame_double = fn x => call_c "debugger frame double" x
val frame_allocations : frame -> bool = call_c "debugger frame allocations"
end
end
structure Trace =
struct
exception Trace of string
val intercept = fn x => call_c "trace intercept" x
val replace = fn x => call_c "trace replace" x
val restore = fn x => call_c "trace restore" x
val restore_all : unit -> unit = call_c "trace restore all"
datatype status = INTERCEPT | NONE | REPLACE | UNTRACEABLE
val status = fn x => call_c "trace status" x
end
structure Dynamic =
struct
type dynamic = dynamic
type type_rep = type_rep
exception Coerce of type_rep * type_rep
val generalises_ref : (type_rep * type_rep -> bool) ref =
ref (fn _ => false)
local
fun generalises data = (!generalises_ref) data
val get_type = Value.cast (fn (a,b) => b)
val get_value = Value.cast (fn (a,b) => a)
in
fun coerce (d : dynamic, t:type_rep) : ml_value =
if generalises (get_type d,t) then
get_value d
else
raise Coerce(get_type d,t)
end
end
structure Runtime =
struct
exception Unbound of string
val environment = call_c
val modules : (string * ml_value * ml_value) list ref = call_c "system module root"
structure Loader =
struct
exception Load of string
val load_module : string -> (string * ml_value) = call_c "system load link"
val load_wordset : int * {a_names : string list,
b: {a_clos: int,
b_spills: int,
c_saves: int,
d_code : string} list,
c_leafs : bool list,
d_intercept : int list,
e_stack_parameters : int list} ->
(int * ml_value) list = call_c "system load wordset"
end
structure Memory =
struct
val max_stack_blocks : int ref = call_c "mem max stack blocks"
val gc_message_level : int ref = call_c "gc message level"
val collect : int -> unit = call_c "gc collect generation"
val collect_all : unit -> unit = call_c "gc collect all"
val promote_all : unit -> unit = call_c "gc promote all"
val collections : unit -> (int * int) = call_c "gc collections"
end
structure Event =
struct
datatype T = SIGNAL of int
exception Signal of string
val signal : int * (int -> unit) -> unit = call_c "event signal"
val stack_overflow_handler : (unit -> unit) -> unit = call_c "event signal stack overflow"
val interrupt_handler : (unit -> unit) -> unit = call_c "event signal interrupt"
end
end
end
structure Threads =
struct
exception Threads of string
datatype 'a result =
Running
| Waiting
| Sleeping
| Result of 'a
| Exception of exn
| Died
| Killed
| Expired
datatype 'a thread =
Thread of Internal.Value.T result ref * int
local
val identity = Internal.Value.cast
in
fun result (Thread (r,i)) =
Internal.Value.cast(
let val y = Internal.Value.cast (!r)
in if y=8 then 3 else y
end)
val c_fork = (fn x => call_c "thread fork" x) : (unit -> 'b) -> 'b thread
fun fork (f : 'a -> 'b) a = c_fork (fn () => f a)
val yield = (call_c "thread yield") : unit -> unit
val sleep = (fn x => call_c "thread sleep" x) : 'a thread -> unit
val wake = (fn x => call_c "thread wake" x) : 'a thread -> unit
structure Internal =
struct
type thread_id = unit thread
val id = fn x => call_c "thread current thread" x
fun get_id t = (identity t) : thread_id
val children = fn x => call_c "thread children" x
val parent = fn x => call_c "thread parent" x
val all = fn x => call_c "thread all threads" x
val kill = fn x => call_c "thread kill" x
val raise_in = fn x => call_c "thread raise" x
val yield_to = fn x => call_c "thread yield to" x
val set_handler = fn x => call_c "thread set fatal handler" x
val reset_fatal_status = fn x => call_c "thread reset fatal status" x
val get_num = fn x => call_c "thread number" x
fun state t =
case (result t) of
Running => Running
| Waiting => Waiting
| Sleeping => Sleeping
| Result _ => Result ()
| Exception e => Exception e
| Died => Died
| Killed => Killed
| Expired => Expired
structure Preemption =
struct
val start : unit -> unit
= call_c "thread start preemption"
val stop : unit -> unit
= fn () =>
let val s: unit->unit =
call_c "thread stop preemption"
in #access(Internal.StandardIO.currentIO()) s
end
val on : unit -> bool
= call_c "thread preempting"
val get_interval : unit -> int
= call_c "thread get preemption interval"
val set_interval : int -> unit
= call_c "thread set preemption interval"
val enter_critical_section : unit -> unit
= call_c "thread start critical section"
val exit_critical_section : unit -> unit
= call_c "thread stop critical section"
val in_critical_section: unit -> bool
= call_c "thread critical"
end
end
end
end
structure Profile =
struct
type manner = int
type function_id = string
type cost_centre_profile = unit
datatype object_kind =
RECORD
| PAIR
| CLOSURE
| STRING
| ARRAY
| BYTEARRAY
| OTHER
| TOTAL
datatype large_size =
Large_Size of
{megabytes : int,
bytes : int}
datatype object_count =
Object_Count of
{number : int,
size : large_size,
overhead : int}
type object_breakdown = (object_kind * object_count) list
datatype function_space_profile =
Function_Space_Profile of
{allocated : large_size,
copied : large_size,
copies : large_size list,
allocation : object_breakdown list}
datatype function_caller =
Function_Caller of
{id: function_id,
found: int,
top: int,
scans: int,
callers: function_caller list}
datatype function_time_profile =
Function_Time_Profile of
{found: int,
top: int,
scans: int,
depth: int,
self: int,
callers: function_caller list}
datatype function_profile =
Function_Profile of
{id: function_id,
call_count: int,
time: function_time_profile,
space: function_space_profile}
datatype general_header =
General of
{data_allocated: int,
period: Internal.Types.time,
suspended: Internal.Types.time}
datatype call_header =
Call of {functions : int}
datatype time_header =
Time of
{data_allocated: int,
functions: int,
scans: int,
gc_ticks: int,
profile_ticks: int,
frames: real,
ml_frames: real,
max_ml_stack_depth: int}
datatype space_header =
Space of
{data_allocated: int,
functions: int,
collections: int,
total_profiled : function_space_profile}
type cost_header = unit
datatype profile =
Profile of
{general: general_header,
call: call_header,
time: time_header,
space: space_header,
cost: cost_header,
functions: function_profile list,
centres: cost_centre_profile list}
datatype options =
Options of
{scan : int,
selector : function_id -> manner}
datatype 'a result =
Result of 'a
| Exception of exn
exception ProfileError of string
local
val rts_profile = (fn x => call_c "profile" x) :
int * (string->int) * (unit->'b) -> ('b * profile)
in
fun profile (Options {scan, selector}) f a =
let
fun wrapper () = (Result (f a)) handle e => Exception e
fun sel x = selector x handle _ => 0
in
rts_profile (scan, sel, wrapper)
end
end
local
fun manner_object ARRAY = 1
| manner_object BYTEARRAY = 2
| manner_object CLOSURE = 4
| manner_object OTHER = 8
| manner_object PAIR = 16
| manner_object RECORD = 32
| manner_object STRING = 64
| manner_object TOTAL = 128
fun breakdown_manner [] = 0
| breakdown_manner (x::xs) =
Internal.Bits.orb(manner_object x,breakdown_manner xs)
in
fun make_manner {time, space, copies, calls, depth, breakdown} =
(if calls then 1 else 0) +
(if time then 2 else 0) +
(if space then 4 else 0) +
(if copies then 8 else 0) +
Internal.Bits.lshift(depth,16) +
Internal.Bits.lshift(breakdown_manner breakdown, 8)
end
end
end
local
val exns_initialised = call_c "exception exns_initialised"
val SizeExn = call_c "exception Size"
val DivExn = call_c "exception Div"
val OverflowExn = call_c "exception Overflow"
val ProfileExn = call_c "exception Profile"
val SaveExn = call_c "exception Save"
val LoadExn = call_c "exception Load"
val SignalExn = call_c "exception Signal"
val StringToRealExn = call_c "exception StringToReal"
val SubstringExn = call_c "exception Substring"
val SysErrExn = call_c "exception syserr"
val TraceExn = call_c "exception Trace"
val UnboundExn = call_c "exception Unbound"
val ValueExn = call_c "exception Value"
val ThreadsExn = call_c "exception Threads"
in
val _ =
if !exns_initialised then ()
else
(exns_initialised := true;
SizeExn := Size;
DivExn := Div;
LoadExn := MLWorks.Internal.Runtime.Loader.Load "";
OverflowExn := Overflow;
ProfileExn := MLWorks.Profile.ProfileError "";
SaveExn := MLWorks.Internal.Save "";
SignalExn := MLWorks.Internal.Runtime.Event.Signal "";
StringToRealExn := MLWorks.Internal.StringToReal;
SubstringExn := MLWorks.String.Substring;
SysErrExn := MLWorks.Internal.Error.SysErr ("",MLWorks.Internal.Types.NONE);
TraceExn := MLWorks.Internal.Trace.Trace "";
UnboundExn := MLWorks.Internal.Runtime.Unbound "";
ValueExn := MLWorks.Internal.Value.Value "";
ThreadsExn := MLWorks.Threads.Threads "")
end
val ceil : real -> int = ~ o floor o ~
val trunc: real -> int = fn x => if x>0.0 then floor x else ceil x
val round: real -> int = fn x=>
let
val near = floor x
val diff = abs(real near - x)
in
if diff < 0.5 then
near
else
if diff > 0.5 orelse near mod 2 = 1
then near + 1
else near
end
val maxSize = MLWorks.String.maxLen
fun unsafe_alloc_string (n:int) : string =
let val alloc_s = alloc_string n
in
string_unsafe_update (alloc_s, n-1, 0);
alloc_s
end
fun alloc_string (n:int) : string =
if n > maxSize then
raise Size
else
unsafe_alloc_string n
fun implode (cl : char list) : string =
let
val cl : int list = cast cl
fun copyall ([],start,to) = to
| copyall (c::cl,start,to) =
(string_unsafe_update (to,start, c);
copyall (cl,start+1,to))
fun get_size (a::rest,sz) = get_size (rest,1 + sz)
| get_size ([],sz) =
if sz > 30 then call_c "string c implode char" (cl,sz)
else
let
val result = unsafe_alloc_string (sz+1)
val _ = string_unsafe_update (result,sz,0)
in
copyall (cl,0,result)
end
in
get_size (cl,0)
end
fun concat [] = ""
| concat xs = call_c "string implode" xs
fun str (c:char) : string =
let val alloc_s = unsafe_alloc_string (1+1)
in
string_unsafe_update(alloc_s, 0, (cast c):int);
alloc_s
end
fun explode (s:string) : char list =
let fun aux (i, acc) =
if i >= 0 then
aux (i-1, (cast (string_unsafe_sub(s, i)):char)::acc)
else
acc
in
aux (size s -1, [])
end
fun substring (s, i, n) =
if i < 0 orelse n < 0 orelse (size s - i) < n then raise Subscript
else if n <= 12 then
let
val alloc_s = unsafe_alloc_string (n+1)
fun copy i' =
if i' < 0 then alloc_s
else
(string_unsafe_update (alloc_s, i', string_unsafe_sub (s, i+i'));
copy (i'-1))
in
copy (n-1)
end
else
call_c "string unsafe substring" (s, i, n)
datatype option = datatype MLWorks.Internal.Types.option
exception Option
fun isSome (SOME _) = true | isSome _ = false
fun valOf (SOME x) = x | valOf NONE = raise Option
fun getOpt (NONE, d) = d
| getOpt ((SOME x), _) = x
structure General : GENERAL =
struct
open BuiltinLibrary_
type exn = exn
type unit = unit
exception Domain
exception Fail of string
exception Empty
datatype order = LESS | EQUAL | GREATER
fun exnName e =
let val full_name = MLWorks.Internal.Value.exn_name e
fun split [] = []
| split ("[" :: _) = []
| split (h :: t) = h :: (split t)
in implode(split(explode full_name)) end;
fun exnMessage (MLWorks.Internal.Error.SysErr(s, NONE)) =
"SysErr: " ^ s
| exnMessage (MLWorks.Internal.Error.SysErr(s, SOME n)) =
"SysErr: " ^ (MLWorks.Internal.Error.errorMsg n)
| exnMessage (Fail s) =
"Fail: " ^ s
| exnMessage (MLWorks.Internal.IO.Io {name,function,cause}) =
"Io: " ^ function ^ " failed on file " ^ name ^ " with "
^ exnMessage cause
| exnMessage e = exnName e
val (op<>) : ''a * ''a -> bool = (op<>)
val (op :=) : ('a ref * 'a) -> unit = (op :=)
val ignore : 'a -> unit = fn (_) => ()
fun (x before _) = x
end
open General
fun null [] = true
| null _ = false
fun hd [] = raise Empty
| hd (a::_) = a
fun tl [] = raise Empty
| tl (_::a) = a
fun length l =
let
fun loop ([], acc) = acc
| loop (_::t, acc) = loop (t, 1+acc)
in
loop (l, 0)
end
fun app f [] = ()
| app f (h :: t) = (ignore(f h); app f t)
fun foldl f i list =
let
fun red (acc, []) = acc
| red (acc, (x::xs)) = red (f (x, acc), xs)
in
red (i, list)
end
fun foldr f i list =
let
fun red (acc, []) = acc
| red (acc, x::xs) = red (f (x,acc), xs)
in
red (i, rev list)
end
val print = MLWorks.Internal.StandardIO.print
val vector = MLWorks.Internal.Vector.vector
end
open FullPervasiveLibrary_;
