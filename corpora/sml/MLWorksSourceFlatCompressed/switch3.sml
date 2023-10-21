datatype Action =
Shift
| Reduce of int * int * int
| Funcall of int * int * Action * Action
| NoAction
fun convert_action a =
if a = ~1 then
NoAction
else
Reduce (2,3,4)
fun is_reduction n =
case (convert_action n) of
Reduce _ => true
| _ => false
fun f _ = is_reduction 3
datatype Action =
Shift
| Reduce of int * int * int
| Funcall of int * int * Action * Action
| NoAction
fun convert_action a =
if a = ~1 then
NoAction
else
Reduce (2,3,4)
fun is_reduction n =
case (convert_action n) of
Shift => true
| _ => false
fun f _ = is_reduction 3
;
