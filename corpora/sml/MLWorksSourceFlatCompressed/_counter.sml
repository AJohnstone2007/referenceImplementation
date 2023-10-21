require "counter";
functor Counter (): COUNTER =
struct
local
val count = ref 0
in
fun counter () =
let val ref x = count
in
(count := (x + 1);
x)
end
fun previous_count x =
(x-1)
fun next_count x =
(x+1)
fun reset_counter n =
(count := n;
())
fun read_counter () = !count
end
end
;
