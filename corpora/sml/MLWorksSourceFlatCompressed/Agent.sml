functor McAgent(structure B : TEST
structure Act : ACTION
structure AG : AGENT
sharing AG.Act = Act
sharing AG.T.N = Act.N
sharing AG.T = B) : McAGENT =
struct
structure ACT = Act
structure B = B
structure N = B.N
type agent = AG.agent
type env = AG.env
val mkstr = AG.mkstr
val mk_nil = AG.mk_nil()
fun mk_sum a b = AG.mk_sum[a,b]
fun mk_prefix a p = AG.mk_prefix(a,p)
fun mk_par p q = AG.mk_parallel[p,q]
fun mk_conditional b p q = AG.mk_conditional(b,p,q)
fun mk_abstraction n p = AG.mk_abstraction(n,p)
fun mk_application p n = AG.mk_application(p,n)
fun mk_restriction n p = AG.mk_restriction(n,p)
fun mk_concretion n p = AG.mk_concretion(n,p)
fun mk_bconcretion n p = AG.mk_restriction(n,AG.mk_concretion(n,p))
fun mk_identifier s = AG.mk_identifier s
fun eq p q = AG.eq(p,q)
fun is_nil a e = AG.is_nil(a,e)
fun is_sum a e = AG.is_sum(a,e)
fun is_prefix a e = AG.is_prefix(a,e)
fun is_par a e = AG.is_parallel(a,e)
fun is_conditional a e = AG.is_conditional (a,e)
fun is_application a e = AG.is_application(a,e)
fun is_restriction a e = AG.is_restriction(a,e)
fun is_identifier a e = AG.is_identifier(a,e)
fun is_process a e = AG.is_process(a,e)
fun is_concretion a e = AG.is_concretion(a,e)
fun is_bconcretion a e =
AG.is_restriction(a,e) andalso AG.is_concretion(a,e)
fun is_abstraction a e = AG.is_abstraction(a,e)
val free_names = AG.free_names
fun sum_left a e = Lib.hd(AG.sum_summands(a,e))
fun sum_right a e =
let val r = Lib.tl(AG.sum_summands(a,e))
in
if Lib.isnil(Lib.tl(r)) then Lib.hd(r)
else AG.mk_sum(r)
end
fun prefix_left a e = AG.prefix_act(a,e)
fun prefix_right a e = AG.prefix_agent(a,e)
fun par_left a e = Lib.hd(AG.parallel_pars(a,e))
fun par_right a e =
let val r = Lib.tl(AG.parallel_pars(a,e))
in
if Lib.isnil(Lib.tl(r)) then Lib.hd(r)
else AG.mk_parallel(r)
end
fun get_boolean a e = AG.conditional_test(a,e)
fun cond_positive a e = AG.conditional_positive(a,e)
fun cond_negative a e = AG.conditional_negative(a,e)
fun appl_fun a e = AG.application_abstr(a,e)
fun appl_arg a e = AG.application_arg(a,e)
fun restriction_right n a e = AG.restriction_right(a,n,e)
fun concretion_left a e = AG.concretion_name(a,e)
fun concretion_right a e = AG.concretion_agent(a,e)
fun bconcretion_right n a e =
AG.concretion_agent(AG.restriction_right(a,n,e),e)
fun abstraction_right n a e = AG.abstraction_right(a,n,e)
fun pseudo_appl p q e = AG.pseudo_apply(p,q,e)
fun identifier_def p e = AG.identifier_def (p,e)
end
;
