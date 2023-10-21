require "lisp";
functor LispUtils() : LISP_UTILS =
struct
fun unwind_protect (body_fn) (coda_fn) =
let val res = body_fn ()
handle exn => (ignore(coda_fn()); raise exn)
in
ignore(coda_fn());
res
end
fun unwind_protect' (body_fn) (coda_fn) (arg) =
let val res = body_fn (arg)
handle exn => (ignore(coda_fn(arg)); raise exn)
in
ignore(coda_fn(arg));
res
end
datatype ('a)svref = SV of 'a ref list ref
fun svref (x) = SV (ref([ref x]))
fun set_svref(SV(rl),x) = (rl := (ref x :: !rl))
fun unset_svref(SV(rl),_) =
case !rl of
_ :: rest => rl := rest
| _ => ()
fun app f =
let fun loop (a::rest) = (ignore(f(a)) ; loop(rest))
| loop ([]) = ()
in
loop
end
fun letv (svref_l) (body_fn) =
( app set_svref svref_l;
unwind_protect body_fn (fn _ => app unset_svref svref_l)
)
fun letv' (svref_l) (body_fn) (arg) =
( app set_svref svref_l;
unwind_protect' body_fn (fn _ => app unset_svref svref_l) (arg)
)
exception UndefinedSpecialVariable
fun setv (SV(ref(rx :: _))) (x) = (rx := x)
| setv (_) (_) = raise UndefinedSpecialVariable
fun !! (SV(ref(ref(v) :: _))) = v
| !! (_) = raise UndefinedSpecialVariable
end
;
