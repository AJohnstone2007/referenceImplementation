functor EquivalenceChecker(structure ModelChecker: MODELCHECKER
sharing ModelChecker.S.C.NS.N
= ModelChecker.S.A.ACT.N
sharing ModelChecker.S.F.ACT
= ModelChecker.S.A.ACT
sharing ModelChecker.S.F
= ModelChecker.S.C.F
sharing ModelChecker.S.C.NS
= ModelChecker.AS.NS
sharing ModelChecker.S.A
= ModelChecker.AS.A
sharing ModelChecker.S.F.P.A
= ModelChecker.S.A):EQUIVALENCECHECKER =
struct
structure MC = ModelChecker
open MC
open S
structure AT = AgentTable(structure Agent=A;structure PropVar=F.P)
fun act_filter [] = []
| act_filter ((a,A)::pl) =
if F.ACT.is_input(a) then
(F.ACT.name(a),A)::(act_filter pl)
else
act_filter pl
fun bar_filter [] = []
| bar_filter ((a,A)::pl) =
if F.ACT.is_output(a) then
(F.ACT.name(a),A)::(act_filter pl)
else
act_filter pl
fun tau_filter [] = []
| tau_filter ((x,A)::pl) =
if F.ACT.is_tau(x) then
A::(tau_filter pl)
else
tau_filter pl
fun cf2 at A ns e =
let val A1 = AS.normal_form A ns e
in cf3 at A1 (C.NS.restrict ns (A.free_names A1)) e
end
and
cf3 at A ns e =
let val x = F.ACT.N.next (A.free_names A)
in
if A.is_process A e
then
if AT.is_visited at A
then F.mk_rooted_var (AT.lookup at A) nil
else
(let val pv = AT.next_var at
in
(F.mk_rooted_gfp pv nil
(let val next_pairs = AS.commitments ns A e
in
F.mk_and
(F.mk_and
(F.mk_box (F.ACT.mk_input x)
(F.mk_big_or
(map (fn (y,A1) =>
F.mk_and (F.mk_eq y x)
(cf2 (AT.associate at A pv) A1 ns e))
(act_filter next_pairs))))
(F.mk_and
(F.mk_box (F.ACT.mk_output x)
(F.mk_big_or
(map (fn (y,A1) =>
F.mk_and (F.mk_eq y x)
(cf2 (AT.associate at A pv) A1 ns e))
(bar_filter next_pairs))))
(F.mk_box (F.ACT.mk_tau ())
(F.mk_big_or
(map (fn A1 =>
cf2 (AT.associate at A pv) A1 ns e)
(tau_filter next_pairs))))))
(F.mk_and
(F.mk_big_and
(map (fn (y,A1) =>
F.mk_diamond (F.ACT.mk_input x)
(F.mk_and (F.mk_eq y x)
(cf2 (AT.associate at A pv) A1 ns e)))
(act_filter next_pairs)))
(F.mk_and
(F.mk_big_and
(map (fn (y,A1) =>
F.mk_diamond (F.ACT.mk_output x)
(F.mk_and (F.mk_eq y x)
(cf2 (AT.associate at A pv) A1 ns e)))
(bar_filter next_pairs)))
(F.mk_big_and
(map
(fn A1 => F.mk_diamond (F.ACT.mk_tau())
(cf2 (AT.associate at A pv) A1 ns e))
(tau_filter next_pairs)))))
end)
nil)
end)
else
if A.is_concretion A e
then F.mk_sigma x
(F.mk_and (F.mk_eq x (A.concretion_left A e))
(cf2 at (A.concretion_right A e) ns e))
else
if A.is_bconcretion A e
then F.mk_bsigma x (cf2 at (A.bconcretion_right x A e)
(C.NS.add_distinct x ns) e)
else
if A.is_abstraction A e
then F.mk_pi x
(F.mk_big_and
(map
(fn ns1 => F.mk_or
(F.mk_not (C.diff x (C.mk_cond ns1)))
(cf2 at (A.abstraction_right x A e) ns1 e)) (C.NS.add_new x ns)))
else raise cannot_happen
end
fun characteristic_formula c A e =
F.mk_big_or
(map
(fn ns => F.mk_and (C.mk_form (C.mk_cond ns)) (cf2 AT.init A ns e))
(C.partition (A.free_names A) c))
fun equivalence_checker c A1 A2 e =
model_checker V.init
(mk_sequent(c,D.init,A1,characteristic_formula c A2 e)) e
end
;
