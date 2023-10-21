datatype btree =
LEAF
| N1 of btree
| N2
fun insert (N1 t1) = N1 t1
| insert LEAF = LEAF
| insert other_shape = other_shape
fun define' mapping =
(ignore(insert mapping); insert mapping)
;
define' LEAF
;
