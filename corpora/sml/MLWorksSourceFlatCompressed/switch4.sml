datatype btree =
LEAF
fun insert LEAF = LEAF
fun define' mapping =
(ignore(insert mapping); insert mapping)
;
define' LEAF;
datatype btree =
Node of int
fun insert (Node t1) = Node t1
fun define' mapping =
(ignore(insert mapping); insert mapping)
;
define' (Node 3);
