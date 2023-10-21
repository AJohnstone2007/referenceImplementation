functor OpSymbFUN (structure S : SORT
structure P : PRETTY
) : OPSYMB =
struct
structure Pretty = P
type Sort = S.Sort
datatype Attr = NONE | ASSOC | COMM | AC
abstype OpSig = OpSig of (Sort list * Sort * bool * Attr)
with
fun mk_OpSig (sl,s) = OpSig (sl,s,false,NONE)
fun get_arg_sorts (OpSig(sl,_,_,_)) = sl
fun get_result_sort (OpSig(_,s,_,_)) = s
fun get_type (OpSig(sl,s,_,_)) = (sl,s)
fun is_generator (OpSig(_,_,b,_)) = b
fun set_generator (OpSig(sl,s,_,a)) = OpSig(sl,s,true,a)
fun unset_generator (OpSig(sl,s,_,a)) = OpSig(sl,s,false,a)
fun is_commutative (OpSig(_,_,_,a)) = a = COMM
fun set_commutative (OpSig(sl,s,b,_)) = OpSig(sl,s,b,COMM)
fun unset_commutative (OpSig(sl,s,b,COMM)) = OpSig(sl,s,b,NONE)
| unset_commutative (OpSig(sl,s,b,AC)) = OpSig(sl,s,b,ASSOC)
| unset_commutative s = s
fun is_associative (OpSig(_,_,_,a)) = a = ASSOC
fun set_associative (OpSig(sl,s,b,_)) = OpSig(sl,s,b,ASSOC)
fun unset_associative (OpSig(sl,s,b,ASSOC)) = OpSig(sl,s,b,NONE)
| unset_associative (OpSig(sl,s,b,AC)) = OpSig(sl,s,b,COMM)
| unset_associative s = s
fun is_ac (OpSig(_,_,_,a)) = a = AC
fun set_ac (OpSig(sl,s,b,_)) = OpSig(sl,s,b,AC)
fun unset_ac (OpSig(sl,s,b,AC)) = OpSig(sl,s,b,NONE)
| unset_ac s = s
fun eq_OpSig (OpSig(sl,r,_,_)) (OpSig(sl',r',_,_)) =
length sl = length sl'
andalso
S.SortEq r r'
andalso
forall_pairs S.SortEq sl sl'
fun show_OpSig (OpSig(sl,s,b,a)) =
stringlist S.sort_name (""," "," -> ") sl ^ (S.sort_name s) ^
(if b
then case a of ASSOC => "     (GEN ASSOC)"
| COMM => "     (GEN COMM)"
| AC => "     (GEN ASSOC COMM)"
| NONE => "     (GEN)"
else case a of ASSOC => "     (ASSOC)"
| COMM => "     (COMM)"
| AC => "     (ASSOC COMM)"
| NONE => "")
end ;
abstype OpSigSet = SigSet of OpSig list
with
local
val mk_single = SigSet o singleton
in
val EmptyOpSigSet = SigSet []
val mk_OpSigSet = mk_single o mk_OpSig
fun get_OpSigs (SigSet s) = s
fun get_flagged_signatures (SigSet s) = s
fun null_OpSigSet (SigSet s) = null s
val numargs = length o get_arg_sorts o hd o get_OpSigs
fun remove_OpSig (SigSet sigset) opsig = SigSet (remove eq_OpSig sigset opsig)
fun merge_OpSigSets (SigSet s) (SigSet s') =
if null_OpSigSet (SigSet s) orelse
null_OpSigSet (SigSet s') orelse
numargs (SigSet s) = numargs (SigSet s')
then SigSet (union eq_OpSig s s' )
else (error_message "Arity Clash in Signature Merging" ; SigSet s)
local
fun change_opsig f opsig SS =
merge_OpSigSets (mk_single (f opsig)) (remove_OpSig SS opsig)
in
val set_as_generator = (change_opsig set_generator) o mk_OpSig
val unset_as_generator = (change_opsig unset_generator) o mk_OpSig
val set_as_commutative = (change_opsig set_commutative) o mk_OpSig
val unset_as_commutative = (change_opsig unset_commutative) o mk_OpSig
val set_as_associative = (change_opsig set_associative) o mk_OpSig
val unset_as_associative = (change_opsig unset_associative) o mk_OpSig
val set_as_ac = (change_opsig set_ac) o mk_OpSig
val unset_as_ac = (change_opsig unset_ac) o mk_OpSig
end
fun generators_of S (SigSet Sset) =
let fun gens_of oldSset newSs =
if null oldSset
then newSs
else let val opsig = hd oldSset
in gens_of (tl oldSset)
(if is_generator opsig andalso S.SortEq S (get_result_sort opsig)
then merge_OpSigSets newSs (mk_single opsig)
else newSs)
end
in gens_of Sset (SigSet [])
end
end
fun show_OpSigSet (SigSet Sset) = map show_OpSig Sset
end ;
abstype OpId = OpId of int
with
local
val Op_Id_Number = ref 0
in
fun OpIdeq (OpId f) (OpId g) = f = g
fun new_OpId () = (inc Op_Id_Number ; OpId (!Op_Id_Number))
fun ord_o (OpId f) (OpId g) = f <= g
fun Oord (OpId f) (OpId g) =
if f < g then LT else if g < f then GT else EQ
end
end ;
abstype Form = Form of string list
with
fun mk_form sl = Form (map (fn "_" => "" | s => s) sl)
fun get_form (Form sl) = sl
fun unform (Form sl) =
let fun f (a::l) = if a = "" then " _ "^f l else a^f l
| f [] = ""
in f sl
end
fun Formeq (Form sl) (Form sl') = sl = sl'
end
local
structure OOL = OrdList2FUN (struct type T = (OpId * OpSigSet * Form * int * (P.T list -> P.T))
fun order (i1,_,_,_,_) (i2,_,_,_,_) = Oord i1 i2
end)
open Maybe OOL
in
abstype Op_Store = Op_Store of OrdList
with
val Empty_Op_Store = Op_Store EMPTY
fun E ops = (ops, EmptyOpSigSet, mk_form [], 0, K (P.str ""))
fun search_op ops (ops',_,_,_,_) = OpIdeq ops ops'
fun search_form (sl:Form) (_,_,sl',_,_) = Formeq sl sl'
fun lookup_op (Op_Store opst) = lookup opst o E
fun lookup_form (Op_Store opst) = search search_form opst
fun remove_op (Op_Store opst) = Op_Store o remove opst o E
fun insert_entry (Op_Store opst) = Op_Store o (insert opst)
fun reinsert ss (sy,si,f,a,p) = insert_entry (remove_op ss sy) (sy,si,f,a,p)
fun arity op_store opid =
(case lookup_op op_store opid of
Match (_,_,_,a,_) => a
| NoMatch => raise Error.MERILL_ERROR "No Arity for Given Operator" )
fun operator_sig op_store opid =
(case lookup_op op_store opid of
Match (_,s,_,_,_) => s
| NoMatch => raise Error.MERILL_ERROR "No Signature for Given Operator" )
fun display_format op_store opid =
(case lookup_op op_store opid of
Match (_,_,f,_,_) => f
| NoMatch => raise Error.MERILL_ERROR "No Displayable Form for Given Operator")
fun pretty_form op_store opid =
(case lookup_op op_store opid of
Match (_,_,_,_,f) => f
| NoMatch => raise Error.MERILL_ERROR "No Pretty Form for Given Operator")
val remove_operator = remove_op
fun find_operator op_store form =
(case lookup_form op_store form of
Match (s,_,_,_,_) => OK s
| NoMatch => Error ("No Symbol for Form "^ unform form ))
fun insert_op_opid op_store opid sign form newp =
(case lookup_op op_store opid of
Match(sy,si,f,a,p) =>
if a = numargs sign
then reinsert op_store (sy,merge_OpSigSets si sign,form,a,newp)
else (Error.error_message "Incompatible Arity - Symbol Not Added" ; op_store)
| NoMatch => insert_entry op_store (opid,sign,form,numargs sign,newp))
fun insert_op_form op_store form newp sign =
(case lookup_form op_store form of
Match (sy,si,f,a,p) =>
if a = numargs sign
then reinsert op_store (sy,merge_OpSigSets si sign,form,a,newp)
else (Error.error_message "Incompatible Arity - Symbol Not Added" ; op_store)
| NoMatch => insert_entry op_store (new_OpId (),sign,form,numargs sign,newp))
fun change_opsig op_store opid sign =
(case lookup_op op_store opid of
Match (sy,si,f,a,p) =>
if a = numargs sign
then reinsert op_store (sy,sign,f,a,p)
else (Error.error_message "Incompatible Arity - Signature not modified" ; op_store)
| NoMatch => (Error.error_message "Invalid Symbol" ; op_store))
fun foldOps f b (Op_Store op_store) =
fold f b op_store
val all_forms = foldOps (fn l => (snoc l o (fn (_,s,f,_,_) => (f,s)))) []
val all_ops = foldOps (fn l => (snoc l o #1)) []
fun fold_over_ops f = foldOps (fn b => C f b o #1)
fun C_Operator op_store opid =
exists is_commutative (get_OpSigs (operator_sig op_store opid))
handle Error.MERILL_ERROR m => (Error.error_message m ; false)
fun AC_Operator op_store opid =
exists is_ac (get_OpSigs (operator_sig op_store opid ))
handle Error.MERILL_ERROR m => (Error.error_message m ; false)
end
end
fun show_operator FS s = unform (display_format FS s)
end
;
