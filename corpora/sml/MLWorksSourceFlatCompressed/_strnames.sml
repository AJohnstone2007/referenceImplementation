require "../utils/crash";
require "../utils/print";
require "../typechecker/datatypes";
require "stamp";
require "../typechecker/strnames";
functor Strnames(
structure Datatypes : DATATYPES
structure Stamp : STAMP
structure Crash : CRASH
structure Print : PRINT
sharing type Datatypes.Stamp = Stamp.Stamp
sharing type Datatypes.StampMap = Stamp.Map.T
) : STRNAMES =
struct
structure Datatypes = Datatypes
open Datatypes
local
fun metap (METASTRNAME _) = true
| metap (_) = false
in
fun string_strname (NULLNAME id) =
"NULLNAME" ^ Stamp.string_stamp id
| string_strname (STRNAME id) = "m" ^ Stamp.string_stamp id
| string_strname (METASTRNAME (ref name)) =
"metastr (" ^ string_strname name ^ ")"
end
fun uninstantiated (METASTRNAME (ref (NULLNAME _))) = true
| uninstantiated (METASTRNAME (ref name)) = uninstantiated name
| uninstantiated (_) = false
local
fun strip(m as METASTRNAME(ref name)) =
(case name of
NULLNAME _ => m
| _ => strip name)
| strip name = name
in
fun strname_eq(name, name') = strip name = strip name'
fun metastrname_eq(name, name') =
let
val name = strip name
in
case name of
METASTRNAME _ => name = strip name'
| _ => false
end
end
fun strname_ord (STRNAME id, STRNAME id') =
Stamp.stamp_lt (id,id')
| strname_ord (NULLNAME _,_) = false
| strname_ord (_,NULLNAME _) = true
| strname_ord (METASTRNAME (ref name),name') = strname_ord (name,name')
| strname_ord (name,METASTRNAME (ref name')) = strname_ord (name,name')
fun strip (name as METASTRNAME (ref (NULLNAME _))) = name
| strip (METASTRNAME (ref name)) = strip name
| strip name = name
fun create_strname_copy rigid =
let
fun copy (strname_copies,METASTRNAME (ref (NULLNAME id))) =
(case Stamp.Map.tryApply'(strname_copies, id) of
SOME _ => strname_copies
| NONE =>
let
val new_strname =
if rigid then STRNAME (Stamp.make_stamp())
else METASTRNAME (ref (NULLNAME (Stamp.make_stamp ())))
in
Stamp.Map.define(strname_copies, id, new_strname)
end)
| copy (strname_copies,METASTRNAME (ref strname)) =
copy (strname_copies,strname)
| copy (strname_copies,STRNAME stamp) =
(case Stamp.Map.tryApply'(strname_copies, stamp) of
SOME _ => strname_copies
| NONE =>
Stamp.Map.define(strname_copies, stamp, STRNAME (Stamp.make_stamp ())))
| copy (strname_copies,NULLNAME _) =
Crash.impossible "create_strname_copy"
in
copy
end
fun strname_copy (name as METASTRNAME (ref (NULLNAME id)), strname_copies) =
(case Stamp.Map.tryApply'(strname_copies, id) of
SOME newname => newname
| _ => name)
| strname_copy (METASTRNAME (ref strname), strname_copies) =
strname_copy (strname,strname_copies)
| strname_copy (name as STRNAME id,strname_copies) =
(case Stamp.Map.tryApply'(strname_copies, id) of
SOME newname => newname
| _ => name)
| strname_copy (NULLNAME _, _) = Crash.impossible "strname_copy"
end
;
