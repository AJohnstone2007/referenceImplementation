functor Formula(structure PropVar: PROPVAR
structure Constant: CONSTANT
structure Action: ACTION): FORMULA =
struct
structure CST = Constant
structure ACT = Action
structure P = PropVar
datatype variable = free of ACT.N.name |
bound of int
datatype modal_cases = unbarred | barred | invisible
datatype formula = True
| False
| IsEq of variable * variable
| IsNeq of variable * variable
| And of formula * formula
| Or of formula * formula
| Diamond of modal_cases * formula
| Box of modal_cases * formula
| RootedVar of P.propvar * variable list
| RootedGFP of
P.propvar * int * formula * variable list
| RootedLFP of
P.propvar * int * formula * variable list
| RootedCon of CST.constant * variable list
| Sigma of formula
| BSigma of formula
| Pi of formula
| Exists of formula
datatype fixed_point_formula =
GFP of P.propvar * int * formula
| LFP of P.propvar * int * formula
exception not_closed_formula of string
exception not_syntactically_monotone of string
exception Illformed of string
exception bug
exception dual_not_yet_implemented
fun mkstr (True) = "TT"
| mkstr (False) = "FF"
| mkstr (IsEq(v,w)) = (vmkstr v)^"="^(vmkstr w)
| mkstr (IsNeq (v,w))= (vmkstr v)^"#"^(vmkstr w)
| mkstr (And (f1,f2))= (mkstr f1)^"&"^(mkstr f2)
| mkstr (Or (f1,f2)) = (mkstr f1)^"|"^(mkstr f2)
| mkstr (Diamond (m,f)) = "<"^(mmkstr m)^">("^(mkstr f)^")"
| mkstr (Box(m,f)) = "["^(mmkstr m)^"]("^(mkstr f)^")"
| mkstr (RootedVar (p,vl)) =
(P.mkstr p)^"("^(Lib.mapconcat vmkstr vl ",")^")"
| mkstr (RootedGFP (p,n,f,vl)) =
let fun mkl 0 = ""
| mkl 1 = "v"^(makestring n)
| mkl n = "v"^(makestring n)^","^(mkl (n-1))
in
"(mu "^(P.mkstr p)^"("^(mkl n)^")."^(mkstr f)^")"
^"("^(Lib.mapconcat vmkstr vl ",")^")"
end
| mkstr (RootedLFP (p,n,f,vl)) =
let fun mkl 0 = ""
| mkl 1 = "v"^(makestring n)
| mkl n = "v"^(makestring n)^","^(mkl (n-1))
in
"(nu "^(P.mkstr p)^"("^(mkl n)^")."^(mkstr f)^")"
^"("^(Lib.mapconcat vmkstr vl ",")^")"
end
| mkstr (RootedCon(c,vl)) =
(CST.mkstr c)^"("^(Lib.mapconcat vmkstr vl ",")^")"
| mkstr (Sigma f) = "Sigma ("^(mkstr f)^")"
| mkstr (BSigma f) = "BSigma ("^(mkstr f)^")"
| mkstr (Pi f) = "Pi ("^(mkstr f)^")"
| mkstr (Exists f) = "Exists ("^(mkstr f)^")"
and vmkstr (free n) = (ACT.N.mkstr n)
| vmkstr (bound n) = "b"^(makestring n)
and mmkstr (unbarred) = "x"
| mmkstr (barred) = "'x"
| mmkstr (invisible) = "t"
fun var_subst x y (free z) = if ACT.N.eq (y, z) then (free x) else (free z) |
var_subst x y (bound n) = bound n
fun subst x y (True) = True |
subst x y (False) = False |
subst x y (IsEq(v,w)) = IsEq(var_subst x y v,var_subst x y w) |
subst x y (IsNeq(v,w)) = IsNeq(var_subst x y v,var_subst x y w) |
subst x y (And(F1,F2)) = And(subst x y F1,subst x y F2) |
subst x y (Or(F1,F2)) = Or(subst x y F1,subst x y F2) |
subst x y (Diamond(a_type,F)) = Diamond(a_type,subst x y F) |
subst x y (Box(a_type,F)) = Box(a_type,subst x y F) |
subst x y (RootedVar(X,vl)) = RootedVar(X,map (var_subst x y) vl) |
subst x y (RootedGFP(X,n,F,vl)) =
RootedGFP(X,n,F,map (var_subst x y) vl) |
subst x y (RootedLFP(X,n,F,vl)) =
RootedLFP(X,n,F,map (var_subst x y) vl) |
subst x y (RootedCon(U,vl)) = RootedCon(U,map (var_subst x y) vl) |
subst x y (Sigma F) = Sigma (subst x y F) |
subst x y (BSigma F) = BSigma (subst x y F) |
subst x y (Pi F) = Pi (subst x y F) |
subst x y (Exists F) = Exists (subst x y F)
fun bind_name l x (free y) = if ACT.N.eq (x, y) then bound l else free y |
bind_name l x (bound n) = bound n
fun bind l x (True) = True |
bind l x (False) = False |
bind l x (IsEq(v,w)) = IsEq(bind_name l x v,bind_name l x w) |
bind l x (IsNeq(v,w)) = IsNeq(bind_name l x v,bind_name l x w) |
bind l x (And(F1,F2)) = And(bind l x F1,bind l x F2) |
bind l x (Or(F1,F2)) = Or(bind l x F1,bind l x F2) |
bind l x (Diamond(a_type,F)) = Diamond(a_type,bind (l+1) x F) |
bind l x (Box(a_type,F)) = Box(a_type,bind (l+1) x F) |
bind l x (RootedVar(X,vl)) = RootedVar(X,map (bind_name l x) vl) |
bind l x (RootedGFP(X,n,F,vl)) =
RootedGFP(X,n,F,map (bind_name l x) vl) |
bind l x (RootedLFP(X,n,F,vl)) =
RootedLFP(X,n,F,map (bind_name l x) vl) |
bind l x (RootedCon(U,vl)) =
RootedCon(U,map (bind_name l x) vl) |
bind l x (Sigma F) = Sigma (bind (l+1) x F) |
bind l x (BSigma F) = BSigma (bind (l+1) x F) |
bind l x (Pi F) = Pi (bind (l+1) x F) |
bind l x (Exists F) = Exists (bind (l+1) x F)
fun bind_list l nil F = F |
bind_list l (x::nl) F = bind l x (bind_list l nl F)
val mk_true = True
val mk_false = False
fun mk_eq x y = IsEq(free x,free y)
fun mk_ineq x y = IsNeq(free x,free y)
fun mk_and F1 True = F1
| mk_and True F2 = F2
| mk_and F1 False = False
| mk_and False F2 = False
| mk_and F1 F2 = And(F1,F2)
fun mk_big_and nil = True |
mk_big_and (F::nil) = F |
mk_big_and (F::fl) = mk_and F (mk_big_and fl)
fun mk_or F1 True = True
| mk_or True F2 = True
| mk_or False F2 = F2
| mk_or F1 False = F1
| mk_or F1 F2 = Or(F1,F2)
fun mk_big_or nil = False |
mk_big_or (F::nil) = F |
mk_big_or (F::fl) = mk_or F (mk_big_or fl)
fun mk_diamond x F =
if ACT.is_input(x) then
Diamond(unbarred,bind_list 1 [ACT.name x] F)
else if ACT.is_output(x) then
Diamond(barred,bind_list 1 [ACT.name x] F)
else
Diamond(invisible,F)
fun mk_box x F =
if ACT.is_input(x) then
Box(unbarred,bind_list 1 [ACT.name x] F)
else if ACT.is_output(x) then
Box(barred,bind_list 1 [ACT.name x] F)
else
Box(invisible,F)
fun mk_rooted_var X nl = RootedVar(X,(map free nl))
fun mk_rooted_gfp X formal_params F actual_params =
let val l = length formal_params
in
RootedGFP(X,l,bind_list 1 formal_params F,map free actual_params)
end
fun mk_rooted_lfp X formal_params F actual_params =
let val l = length formal_params
in
RootedLFP(X,l,bind_list 1 formal_params F,map free actual_params)
end
fun mk_rooted_con U nl = RootedCon(U,map free nl)
fun mk_sigma x F = Sigma(bind_list 1 [x] F)
fun mk_bsigma x F = BSigma(bind_list 1 [x] F)
fun mk_pi x F = Pi(bind_list 1 [x] F)
fun mk_exists x F = Exists(bind_list 1 [x] F)
fun mk_not (True) = False |
mk_not (False) = True |
mk_not (IsEq(v,w)) = IsNeq(v,w) |
mk_not (IsNeq(v,w)) = IsEq(v,w) |
mk_not (And(F1,F2)) = Or(mk_not F1,mk_not F2) |
mk_not (Or(F1,F2)) = And(mk_not F1,mk_not F2) |
mk_not _ = raise dual_not_yet_implemented
fun is_true (True) = true |
is_true _ = false
fun is_false (False) = true |
is_false _ = false
fun is_eq (IsEq(_,_)) = true |
is_eq _ = false
fun is_neq (IsNeq(_,_)) = true |
is_neq _ = false
fun is_and (And(_,_)) = true |
is_and _ = false
fun is_or (Or(_,_)) = true |
is_or _ = false
fun is_diamond_unbarred (Diamond(unbarred,_)) = true |
is_diamond_unbarred _ = false
fun is_diamond_barred (Diamond(barred,_)) = true |
is_diamond_barred _ = false
fun is_diamond_tau (Diamond(invisible,_)) = true |
is_diamond_tau _ = false
fun is_box_unbarred (Box(unbarred,_)) = true |
is_box_unbarred _ = false
fun is_box_barred (Box(barred,_)) = true |
is_box_barred _ = false
fun is_box_tau (Box(invisible,_)) = true |
is_box_tau _ = false
fun is_rooted_var (RootedVar(_,_)) = true |
is_rooted_var _ = false
fun is_rooted_gfp (RootedGFP(_,_,_,_)) = true |
is_rooted_gfp _ = false
fun is_rooted_lfp (RootedLFP(_,_,_,_)) = true |
is_rooted_lfp _ = false
fun is_rooted_con (RootedCon(_,_)) = true |
is_rooted_con _ = false
fun is_sigma (Sigma _) = true |
is_sigma _ = false
fun is_bsigma (BSigma _) = true |
is_bsigma _ = false
fun is_pi (Pi _) = true |
is_pi _ = false
fun is_exists (Exists _) = true |
is_exists _ = false
fun is_GFP (GFP(_,_,_)) = true |
is_GFP _ = false
fun eq_left (IsEq(free x,free y)) = x |
eq_left (IsNeq(free x,free y)) = x
| eq_left _ = raise Match
fun eq_right (IsEq(free x,free y)) = y |
eq_right (IsNeq(free x,free y)) = y
| eq_right _ = raise Match
fun select_left (And(F1,F2)) = F1 |
select_left (Or(F1,F2)) = F1
| select_left _ = raise Match
fun select_right (And(F1,F2)) = F2 |
select_right (Or(F1,F2)) = F2 |
select_right (Diamond(invisible,F)) = F |
select_right (Box(invisible,F)) = F
| select_right _ = raise Match
fun instantiate_name n x (free y) = free y |
instantiate_name m x (bound n) =
if n = m then free x else bound n
fun instantiate x (True) n = True |
instantiate x (False) n = False |
instantiate x (IsEq(v,w)) n =
IsEq(instantiate_name n x v,instantiate_name n x w) |
instantiate x (IsNeq(v,w)) n =
IsNeq(instantiate_name n x v,instantiate_name n x w) |
instantiate x (And(F1,F2)) n = And(instantiate x F1 n,instantiate x F2 n) |
instantiate x (Or(F1,F2)) n = Or(instantiate x F1 n,instantiate x F2 n) |
instantiate x (Diamond(a_type,F)) n = Diamond(a_type,instantiate x F (n+1)) |
instantiate x (Box(a_type,F)) n = Box(a_type,instantiate x F (n+1)) |
instantiate x (RootedVar(X,vl)) n =
RootedVar(X,map (instantiate_name n x) vl) |
instantiate x (RootedGFP(X,n,F,vl)) l =
RootedGFP(X,n,F,map (instantiate_name l x) vl) |
instantiate x (RootedLFP(X,n,F,vl)) l =
RootedLFP(X,n,F,map (instantiate_name l x) vl) |
instantiate x (RootedCon(U,vl)) n =
RootedCon(U,map (instantiate_name n x) vl) |
instantiate x (Sigma F) n = Sigma (instantiate x F (n+1)) |
instantiate x (BSigma F) n = BSigma (instantiate x F (n+1)) |
instantiate x (Pi F) n = Pi (instantiate x F (n+1)) |
instantiate x (Exists F) n = Exists (instantiate x F (n+1))
fun instantiate_list n nil F = F |
instantiate_list n (x::nl) F = instantiate_list n nl (instantiate x F n)
fun successor x (Diamond(unbarred,F)) = instantiate x F 1 |
successor x (Diamond(barred,F)) = instantiate x F 1 |
successor x (Box(unbarred,F)) = instantiate x F 1 |
successor x (Box(barred,F)) = instantiate x F 1 |
successor x (Sigma F) = instantiate x F 1 |
successor x (BSigma F) = instantiate x F 1 |
successor x (Pi F) = instantiate x F 1 |
successor x (Exists F) = instantiate x F 1
| successor x _ = raise Match
fun select_name nil = nil |
select_name ((free x)::vl) = x::(select_name vl)
| select_name _ = raise Match
fun const_subst U X (True) = True |
const_subst U X (False) = False |
const_subst U X (IsEq(v,w)) = IsEq(v,w) |
const_subst U X (IsNeq(v,w)) = IsNeq(v,w) |
const_subst U X (And(F1,F2)) =
And(const_subst U X F1,const_subst U X F2) |
const_subst U X (Or(F1,F2)) =
Or(const_subst U X F1,const_subst U X F2) |
const_subst U X (Diamond(a_type,F)) =
Diamond(a_type,const_subst U X F) |
const_subst U X (Box(a_type,F)) =
Box(a_type,const_subst U X F) |
const_subst U X (RootedVar(Y,vl)) =
if P.eq X Y then RootedCon(U,vl) else RootedVar(Y,vl) |
const_subst U X (RootedGFP(Y,n,F,vl)) =
RootedGFP(Y,n,const_subst U X F,vl) |
const_subst U X (RootedLFP(Y,n,F,vl)) =
RootedLFP(Y,n,const_subst U X F,vl) |
const_subst U X (RootedCon(V,vl)) = RootedCon(V,vl) |
const_subst U X (Sigma F) = Sigma(const_subst U X F) |
const_subst U X (BSigma F) = BSigma(const_subst U X F) |
const_subst U X (Pi F) = Pi (const_subst U X F) |
const_subst U X (Exists F) = Exists (const_subst U X F)
fun get_propvar (GFP(X,n,F)) = X |
get_propvar (LFP(X,n,F)) = X
fun get_arity (GFP(X,n,F)) = n |
get_arity (LFP(X,n,F)) = n
fun get_body _ _ = raise Lib.disaster "F.get_body called"
fun root (GFP(X,n,F)) nl = RootedGFP(X,n,F,map free nl) |
root (LFP(X,n,F)) nl = RootedLFP(X,n,F,map free nl)
fun unroot (RootedGFP(X,n,F,nl)) = GFP(X,n,F) |
unroot (RootedLFP(X,n,F,nl)) = LFP(X,n,F)
| unroot _ = raise Match
fun params (RootedCon(U,vl)) = select_name vl
| params (RootedLFP(_,_,_,nl)) = select_name nl
| params (RootedGFP(_,_,_,nl)) = select_name nl
| params _ = raise Match
fun unfold U (RootedGFP(X,n,F,vl)) =
const_subst U X (instantiate_list 1 (select_name vl) F) |
unfold U (RootedLFP(X,n,F,vl)) =
const_subst U X (instantiate_list 1 (select_name vl) F)
| unfold U _ = raise Match
fun f_constants (True) = nil |
f_constants (False) = nil |
f_constants (IsEq(v,w)) = nil |
f_constants (IsNeq(v,w)) = nil |
f_constants (And(F1,F2)) = (f_constants F1) @ (f_constants F2) |
f_constants (Or(F1,F2)) = (f_constants F1) @ (f_constants F2) |
f_constants (Diamond(a_type,F)) = f_constants F |
f_constants (Box(a_type,F)) = f_constants F |
f_constants (RootedVar(X,vl)) = nil |
f_constants (RootedGFP(X,n,F,vl)) = f_constants F |
f_constants (RootedLFP(X,n,F,vl)) = f_constants F |
f_constants (RootedCon(U,vl)) = [U] |
f_constants (Sigma F) = f_constants F |
f_constants (BSigma F) = f_constants F |
f_constants (Pi F) = f_constants F |
f_constants (Exists F) = f_constants F
fun constants (GFP(X,n,F)) = f_constants F |
constants (LFP(X,n,F)) = f_constants F
fun constant (RootedCon(U,_)) = U
| constant _ = raise Match
fun var_free_names (free x) = [x] |
var_free_names (bound n) = nil
fun vl_free_names nil = nil |
vl_free_names ((free x)::vl) = x::(vl_free_names vl) |
vl_free_names ((bound n)::vl) = vl_free_names vl
fun free_names (True) = nil |
free_names (False) = nil |
free_names (IsEq(v,w)) = (var_free_names v)@(var_free_names w) |
free_names (IsNeq(v,w)) = (var_free_names v)@(var_free_names w) |
free_names (And(F1,F2)) = (free_names F1) @ (free_names F2) |
free_names (Or(F1,F2)) = (free_names F1) @ (free_names F2) |
free_names (Diamond(a_type,F)) = free_names F |
free_names (Box(a_type,F)) = free_names F |
free_names (RootedVar(X,vl)) = vl_free_names vl |
free_names (RootedGFP(X,n,F,vl)) = vl_free_names vl |
free_names (RootedLFP(X,n,F,vl)) = vl_free_names vl |
free_names (RootedCon(U,vl)) = vl_free_names vl |
free_names (Sigma F) = free_names F |
free_names (BSigma F) = free_names F |
free_names (Pi F) = free_names F |
free_names (Exists F) = free_names F
end;
