datatype btree =
LEAF
| N1 of int
| N2 of btree
fun insert (N1 t1) = N1 23
| insert LEAF = LEAF
| insert other_shape = other_shape
fun define' mapping =
(ignore(insert mapping); insert mapping)
;
define' LEAF
;
