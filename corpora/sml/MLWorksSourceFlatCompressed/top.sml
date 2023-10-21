structure Top
=
struct
structure SL = SL()
structure V = Var()
structure E = Env(structure V=V;
structure SL = SL)
structure SN = StringName();
structure N = deBruijnName();
structure ST = StringTest(structure N=SN;
structure SL = SL)
structure T = Test(structure N=N;
structure SL=SL)
structure SAct = StringAction(structure N=SN);
structure Act = Action(structure N=N)
structure SA = StringAgent(structure T=ST;
structure Act=SAct)
structure D = Distinction(structure T = T;
structure SL = SL)
structure A = BruinAgent(structure SL = SL;
structure T=T;
structure Act=Act;
structure E=E;
structure V=V)
structure H = HashTable();
structure O = BruinOpenSem(structure B=A;
structure D=D;
structure H=H)
structure SP = StringPropVar(structure Agt=SA)
structure SF = StringFormula(structure Act=SAct;
structure PropVar = SP)
structure Bool = T
structure McAgent = McAgent(structure B=Bool;
structure Act=Act;
structure AG=A)
structure PV = PropVar(McAgent)
structure F = Formula(structure PropVar=PV;
structure Constant=Constant;
structure Action=Act)
structure NS = NameSubstitution(structure Name=N;
structure Boolean=Bool)
structure ASS = AgentSubSem(structure Agent=McAgent;
structure NameSubstitution = NS;
structure Boolean= Bool)
structure Cond = Condition(structure NameSubstitution = NS;
structure Formula = F)
structure DF = DefList(structure Formula = F)
structure SQ = Sequent(structure Formula = F;
structure Condition = Cond;
structure DefList = DF;
structure Agent = McAgent)
structure MC = ModelChecker(structure Sequent = SQ;
structure AgentSubSem = ASS)
structure EQ = EquivalenceChecker(structure ModelChecker = MC)
structure Cmd = Commands(structure A=SA;
structure F=SF)
structure PILrVals =
PILrValsFun(structure Token = LrParser.Token
structure Agent = SA
structure Commands = Cmd
structure Prop = SP
structure F = SF)
structure PILex =
PILexFun(structure Tokens = PILrVals.Tokens)
structure PIParser =
JoinWithArg(structure ParserData = PILrVals.ParserData
structure Lex = PILex
structure LrParser = LrParser)
exception Quit of A.agent E.env
val version = ref "Nice version"
fun read_line() =
let val line = input_line std_in
fun scan_line nil = nil
| scan_line (["\n"]) = nil
| scan_line (hd::rst) = hd::(scan_line rst)
in
implode(scan_line(explode line))
end
fun bbruin_agent(ag,fenv) =
let val fcount = if E.isempty (!fenv) then ref ~1
else ref(Lib.max (op <=)
(map (fn v => E.lookup(v,!fenv))
(E.getvars (!fenv))))
fun idx (n,e) = E.lookup(V.mkvar(SN.mkstr n),e)
handle E.Unbound v =>
E.lookup(v,!fenv) handle E.Unbound v =>
(inc fcount;
fenv := E.bind(v,~(!fcount),!fenv);
~(!fcount))
and bind (n,v,e) = E.bind(V.mkvar(SN.mkstr n),v,e)
fun bbruin_agent (SA.Nil,n,e) = A.mk_nil()
| bbruin_agent (SA.Abs(n,P),lvl,env) =
A.mk_abstraction(N.mkname(SN.mkstr n,lvl+1),
bbruin_agent(P,lvl+1,bind(n,lvl+1,env)))
| bbruin_agent (SA.Nu(n,P),lvl,env) =
A.mk_restriction(N.mkname(SN.mkstr n,lvl+1),
bbruin_agent(P,lvl+1,bind(n,lvl+1,env)))
| bbruin_agent (SA.Prefix(ac,A),lvl,env) =
A.mk_prefix(bbruin_act(ac,lvl,env),bbruin_agent(A,lvl,env))
| bbruin_agent (SA.Conc(n,P),lvl,env) =
A.mk_concretion(N.mkname(SN.mkstr n,lvl-idx(n,env)),
bbruin_agent(P,lvl,env))
| bbruin_agent (SA.Test(b,P),lvl,env) =
A.mk_match(bbruin_bool(b,lvl,env),
bbruin_agent(P,lvl,env))
| bbruin_agent (SA.Cond(b,P,Q),lvl,env) =
A.mk_conditional(bbruin_bool(b,lvl,env),
bbruin_agent(P,lvl,env),
bbruin_agent(Q,lvl,env))
| bbruin_agent (SA.Sum(PP),lvl,env) =
A.mk_sum(map (fn P=>bbruin_agent(P,lvl,env)) PP)
| bbruin_agent (SA.Parallel(PP),lvl,env) =
A.mk_parallel(map (fn P=>bbruin_agent(P,lvl,env)) PP)
| bbruin_agent (SA.Applic(P,n),lvl,env) =
A.mk_application(bbruin_agent(P,lvl,env),
N.mkname(SN.mkstr n,lvl-idx(n,env)))
| bbruin_agent (P as SA.AgentRef(I),_,_) =
A.mk_identifier(I)
and bbruin_act(SAct.Tau,lvl,e) = Act.mk_tau()
| bbruin_act(SAct.Input(n),lvl,e) =
Act.mk_input(N.mkname(SN.mkstr(n),lvl-idx(n,e)))
| bbruin_act(SAct.Output(n),lvl,e) =
Act.mk_output(N.mkname(SN.mkstr(n),lvl-idx(n,e)))
and bbruin_bool(b,lvl,e) =
let val s = ST.sigma(b)
in
fold (fn ((a,b),m) => T.join(T.match(N.mkname(SN.mkstr a,lvl-idx(a,e)),N.mkname(SN.mkstr b,lvl-idx(b,e))),m)) s T.True
end
in
bbruin_agent(ag,0,E.empty)
end
fun bbruin_nlist(nl,fenv) =
let val fcount = if E.isempty(!fenv) then ref ~1
else ref(Lib.max (op <=)
(map (fn v => E.lookup(v,!fenv))
(E.getvars (!fenv))))
fun idx (n) = ~(E.lookup(V.mkvar(SN.mkstr n),!fenv))
handle E.Unbound v =>
(inc fcount;
fenv := E.bind(v,~(!fcount),!fenv);
(!fcount))
in
map (fn n=>N.mkname(SN.mkstr n,idx n)) nl
end
fun bbruin_plist(nl,fenv) =
let val fcount = if E.isempty(!fenv) then ref ~1
else ref(Lib.max (op <=)
(map (fn v => E.lookup(v,!fenv))
(E.getvars (!fenv))))
fun idx (n) = ~(E.lookup(V.mkvar(SN.mkstr n),!fenv))
handle E.Unbound v =>
(inc fcount;
fenv := E.bind(v,~(!fcount),!fenv);
(!fcount))
in
map (fn (n,m)=>(N.mkname(SN.mkstr n,idx n),N.mkname(SN.mkstr m,idx m))) nl
end
fun bruijn_formula(f,fenv) =
let val fcount = if E.isempty (!fenv) then ref ~1
else ref(~(Lib.max (op >=)
(map (fn v => E.lookup(v,!fenv))
(E.getvars (!fenv)))))
fun nidx (n,(e,_)) =
E.lookup( V.mkvar(SN.mkstr n),!fenv) handle E.Unbound v =>
(inc fcount;
fenv := E.bind(v,~(!fcount),!fenv);
~(!fcount))
and nbind (n,v,(e,f)) = (E.bind(V.mkvar(SN.mkstr n),v,e),f)
fun pidx (n,(_,f)) = E.lookup(V.mkvar(SP.mkstr n),f)
and pbind (n,v,(e,f)) = (e,E.bind(V.mkvar(SP.mkstr n),v,f))
fun bbruin_formula(SF.True,n,m,e) = F.mk_true
| bbruin_formula(SF.False,n,m,e) = F.mk_false
| bbruin_formula(SF.IsEq(a,b),n,m,e) =
(F.mk_eq (bbruin_name(n,e) a) (bbruin_name(n,e) b))
| bbruin_formula(SF.IsNeq(a,b),n,m,e) =
(F.mk_ineq (bbruin_name(n,e) a) (bbruin_name(n,e) b))
| bbruin_formula(SF.And(l,r),n,m,e) =
(F.mk_and (bbruin_formula(l,n,m,e)) (bbruin_formula(r,n,m,e)))
| bbruin_formula(SF.Or(l,r),n,m,e) =
(F.mk_or (bbruin_formula(l,n,m,e)) (bbruin_formula(r,n,m,e)))
| bbruin_formula(SF.Diamond(a,f),lvl,m,env) =
(F.mk_diamond (bbruin_act(a,lvl,env)) (bbruin_formula(f,lvl,m,env)))
| bbruin_formula(SF.Box(a,f),n,m,e) =
(F.mk_box (bbruin_act(a,n,e)) (bbruin_formula(f,n,m,e)))
| bbruin_formula(SF.RootedVar(p,nl),n,m,e) =
(F.mk_rooted_var (bbruin_pvar (p,m,e)) (map (bbruin_name(n,e)) nl))
| bbruin_formula(SF.RootedGFP(p,fl,f,al),n,m,e) =
let val m' = m+1
val e' = pbind(p,m',(mkbound e 0 fl))
val l' = n+(length fl)
in
(F.mk_rooted_gfp (bbruin_pvar(p,m',e'))
(map (bbruin_name (l',e')) fl)
(bbruin_formula(f,l',m',e'))
(map (bbruin_name (n,e)) al))
end
| bbruin_formula(SF.RootedLFP(p,fl,f,al),n,m,e) =
let val m' = m+1
val e' = pbind(p,m',(mkbound e 0 fl))
val l' = n+(length fl)
in
(F.mk_rooted_lfp (bbruin_pvar (p,m',e'))
(map (bbruin_name (l',e')) fl)
(bbruin_formula(f,l',m',e'))
(map (bbruin_name (n,e)) al))
end
| bbruin_formula(SF.Sigma(a,f),n,m,e) =
(F.mk_sigma (bbruin_name (n,e) a) (bbruin_formula(f,n,m,e)))
| bbruin_formula(SF.BSigma(a,f),n,m,e) =
(F.mk_bsigma (bbruin_name (n,e) a) (bbruin_formula(f,n,m,e)))
| bbruin_formula(SF.Pi(a,f),n,m,e) =
(F.mk_pi (bbruin_name (n,e) a) (bbruin_formula(f,n,m,e)))
| bbruin_formula(SF.Exists(a,f),n,m,e) =
(F.mk_exists (bbruin_name (n,e) a) (bbruin_formula(f,n,m,e)))
| bbruin_formula(SF.Not(f),n,m,e) =
(F.mk_not (bbruin_formula(f,n,m,e)))
and mkbound e i ([]) = e
| mkbound e i (n::r) =
mkbound (nbind (n,i,e)) (i+1) r
and bbruin_name(lvl,env) n =
N.mkname(SN.mkstr n, 0-nidx(n,env))
and bbruin_pvar(p,lvl,env) =
PV.mk_propvar(lvl-pidx(p,env))
and bbruin_act(act,lvl,e) =
case act of
SAct.Tau => Act.mk_tau()
| SAct.Input(n) =>
Act.mk_input(bbruin_name(lvl,e) n)
| SAct.Output(n) =>
Act.mk_output(bbruin_name(lvl,e) n)
in
bbruin_formula(f,0,0,(E.empty,E.empty))
end
fun parse_agent_raw (str) =
let val eof = ref false
val lexer = PIParser.makeLexer (fn _ => if not (!eof)
then (eof := true;
str)
else "") (ref 0,ref 0)
val agtlexer = PIParser.Stream.cons(PILrVals.Tokens.DummyAGENT(0,0),lexer)
val print_error = fn (s,i:int,_) =>
output(std_out,"Error, line " ^
(makestring i) ^ ", " ^ s ^ "\n")
val a = case PIParser.parse(15,agtlexer,print_error,()) of
(Cmd.ParseAGENT a, _) => a | _ => raise Bind
in
a
end
and parse_agent(str) =
bbruin_agent(parse_agent_raw str,ref E.empty)
fun parse_formula_raw (str) =
let val eof = ref false
val lexer = PIParser.makeLexer (fn _ => if not (!eof)
then (eof := true;
str)
else "") (ref 0,ref 0)
val formlexer = PIParser.Stream.cons(PILrVals.Tokens.DummyFORMULA(0,0),lexer)
val print_error = fn (s,i:int,_) =>
output(std_out,"Error, line " ^
(makestring i) ^ ", " ^ s ^ "\n")
val f = case PIParser.parse(15,formlexer,print_error,()) of
(Cmd.ParseFORMULA f, _) => f | _ => raise Bind
in
f
end
and parse_formula(str) =
bruijn_formula(parse_formula_raw str,ref E.empty)
fun yes_or_no_p() =
let val _ = print "(yes or no) "
val line = read_line()
in
if line <= "yes" andalso "yes" <= line then true
else if line <= "no" andalso "no" <= line then false
else yes_or_no_p()
end
and y_or_n_p () =
let val _ = print "(y or n) "
val line = read_line()
in
if line <= "y" andalso "y" <= line then true
else if line <= "n" andalso "n" <= line then false
else y_or_n_p()
end
fun addenv ((Id,defn), env) =
let val forgot = A.free_names(defn)
fun pp [] = "??"
| pp [x] = pname(x)
| pp [a,b] = (pname a)^" and "^(pname b)
| pp (h::t) =
(pname h)^", "^(pp t)
and pname n =
let val s=(N.pretty_name n)
in
if size s > 0 then s
else (N.mkstr n)
end
and plural(1) = ""
| plural _ = "s"
in
if Lib.isnil(forgot) then
E.bind (V.mkvar Id, defn, env)
else
(print ("Error: Definition of "^Id^" has free name"^(plural (length forgot))^" "^(pp forgot)^"\n");
env)
end
and rmenv (str,env) =
E.unbind((V.mkvar str),env)
and printagent(id,env) =
let val agt = E.lookup(id,env)
in
print("agent "^(V.mkstr id)^" = "
^(makstr_top(agt))^"\n")
end
and printenv(env) =
if E.isempty(env) then print "The environment is empty.\n"
else
app (fn (id) => printagent(id,env)) (E.getvars env)
and makstr_top(P) =
let val f = (A.free_names P)
val fl = (N.fill(Lib.del_dups N.eq f))
val nl = fold (fn (n,l) => nl_add(n,l)) (Lib.sort N.le fl) []
in
A.makstr(P,nl)
end
and makstr_d_top(D) =
let val f = (D.names D)
val fl = (N.fill(Lib.del_dups N.eq f))
val nl = fold (fn (n,l) => nl_add(n,l)) (Lib.sort N.le fl) []
in
D.makstr(D,nl)
end
and makstr_triple(P,Q,D) =
let val f = (A.free_names P)@(A.free_names Q)@(D.names D)
val fl = (N.fill(Lib.del_dups N.eq f))
val nl = fold (fn (n,l) => nl_add(n,l)) (Lib.sort N.le fl) []
in
("< "^(A.makstr(P,nl))^",\n  "^(A.makstr(Q,nl))^" > "^(D.makstr(D,nl))^"\n")
end
and nl_add (n,l) =
let val s = N.pretty_name(n)
val i = length l
in
if size s > 0 andalso not(Lib.member (op =) (s,l)) then
s::l
else ("~v"^(makestring i))::l
end
and makstr_icomm_top sfun (C as O.Comm(c,a,p)) =
let val f = (A.free_names p)@(T.names c)@(Act.names a)
val fl = (N.fill(Lib.del_dups N.eq f))
val nl = fold (fn (n,l) => nl_add(n,l)) (Lib.sort N.le fl) []
in
sfun (C,nl)
end
val makstr_wcomm_top = makstr_icomm_top O.cw_makstr
val makstr_comm_top = makstr_icomm_top O.c_makstr
fun doeq bisim names (ag1,ag2,env) =
let val frn = Lib.del_dups N.eq ((A.free_names(ag1))@(A.free_names(ag2)))
val d = D.add_distinct_names(names,frn,D.EMPTY)
val eq = bisim(ag1, ag2, env, d)
fun ppair (p,q,d) =
if !Flags.tracelevel > 1 then
print ("< "^(A.mkstr p)^",\n  "^(A.mkstr q)^" > "^(D.mkstr d)^"\n")
else
print (makstr_triple(p,q,d))
val eqlen = length eq
in
if eqlen = 0 then print "The two agents are NOT related.\n"
else (print ("The two agents are related.\n"^
"Relation size = "^(makestring eqlen)^".\n");
if !Flags.interactive
andalso (print "Do you want to see it? ";
y_or_n_p())
then (print "R = \n";
app ppair eq;
if eqlen > 3
then print ("(that's length "^(makestring eqlen)^")\n")
else ())
else ())
end
handle (E.Unbound v) => print ("The identifier " ^ (V.mkstr v) ^
" is unbound\n")
val dwoeq = doeq O.weakbisimulation
val woeq = dwoeq []
val oeqd = doeq O.bisimulation
val oeq = oeqd []
fun trans(ag,defs) =
let val trns = O.commitments(ag,defs)
fun ptrans(tr) =
print ((makstr_comm_top tr)^"\n")
in
if null(trns) then
print "NO commitments.\n"
else
(print "Commitments:\n";
app ptrans trns;
if (length trns) > 5 then
print ("(that's "^(makestring (length trns))^" commitments)\n")
else ()
)
end
fun wtrans(ag,defs) =
let val trns = O.weakcomm(ag,defs)
fun ptrans(com) =
print ((makstr_wcomm_top com)^"\n");
in
if null(trns) then
print "NO commitments.\n"
else
(print "Weak commitments:\n";
app ptrans trns;
if (length trns) > 5 then
print ("(that's "^(makestring (length trns))^" commitments)\n")
else ())
end
fun pobses(ag,defs) =
print("NYI\n")
fun zpobses(ag,defs) =
let fun add(x,y) = (H.puthash(x,true,y); y)
fun member(x,y) = case H.gethash(x,y) of NONE => false | SOME z => z
val vis = H.maketable(3,A.hashval,A.eq,A.mk_nil(),true)
fun mks(c,a) =
let val f = (T.names c)@(Act.names a)
val fl = (N.fill(Lib.del_dups N.eq f))
val nl = fold (fn (n,l) => nl_add(n,l)) (Lib.sort N.le fl) []
in
(if T.eq(c,T.True) then ""
else (T.makstr (c,nl)))^(Act.makstr(a,nl))
end
fun pobs([],p,termp) =
print ("-> "^(makstr_top p)^
(if termp then "\n"
else "->...\n"))
| pobs(O.Comm(m,a,t)::tr,p,termp) =
(print ("-"^(mks(m,a)));
pobs(tr,p,termp))
fun traverse (p,trace) =
if A.is_process(p,defs) then
let val cm = O.commitments(p,defs)
in
if null cm then pobs(rev trace,p,true)
else if member(p,vis) then pobs (rev trace,p,false)
else
(ignore(add(p,vis));
app (fn (tt as O.Comm(_,_,t)) =>
traverse (t,tt::trace))
cm)
end
else if A.is_abstraction(p,defs) then
let val (vp,t') = A.abs_all(p,defs)
val nns = N.n_newNamesNotin(vp,(A.free_names p))
val tt = A.beta_reduce t' (nns,0)
in traverse(tt,trace)
end
else
let val (np,vp,t') = A.conc_all(p,defs)
val nns = N.n_newNamesNotin(np,A.free_names p)
val tt =
if np = 0 then t'
else A.beta_reduce t' (nns,0)
in traverse(tt,trace)
end
in
traverse(ag,[]);
()
end
fun gsize(ag,defs) =
let fun member(x,y) = case H.gethash(x,y) of NONE => false | SOME z => z
fun add(x,y) = (H.puthash(x,true,y); y)
val vtbl = H.maketable(3,A.hashval,A.eq,A.mk_nil(),true)
fun grsize(p,vis) =
if member(p,vis) then (0,vis)
else if A.is_process(p,defs) then
let val tr = O.commitments(p,defs)
val vlen = if Flags.trace() then H.entrycount vis else 0
in
(if vlen > 0 andalso vlen mod 100 = 0 then print ("current size "^(makestring vlen)^"\n"^(A.mkstr p)^"\n") else ();
fold (fn (O.Comm(_,c,t),(sum,rvis)) =>
if (Act.is_input(c) andalso A.is_abstraction(t,defs)) orelse
(Act.is_output(c) andalso A.is_concretion(t,defs)) orelse
(Act.is_tau(c) andalso A.is_process(t,defs)) then
let val (rsum, rrvis) = grsize(t,rvis)
in
(rsum+sum,rrvis)
end
else (sum,rvis)) tr (1,add(p,vis)))
end
else if A.is_abstraction(p,defs) then
let val (vp,t') = A.abs_all(p,defs)
val nns = N.n_newNamesNotin(vp,(A.free_names p))
val tt = A.beta_reduce t' (nns,0)
in grsize(tt,add(p,vis))
end
else
let val (np,vp,t') = A.conc_all(p,defs)
val nns = N.n_newNamesNotin(np,A.free_names p)
val tt =
if np = 0 then t'
else A.beta_reduce t' (nns,0)
in grsize(tt,add(p,vis))
end
val (s,_) = grsize(ag,vtbl)
in
if Flags.trace() then print ("*vtbl size (agents) = "^(makestring(H.entrycount vtbl))^"*\"states\" = "^(makestring s)^"\n") else ();
s
end
fun fdead(ag,defs) =
let fun vmember(x,y) = case H.gethash(x,y) of NONE => false | SOME z => z
fun vadd(x,y) = (H.puthash(x,true,y); y)
val vtbl = H.maketable(3,A.hashval,A.eq,A.mk_nil(),true)
fun commstr(O.Comm(m,a,p)) =
if T.eq(m,T.True) then "|>"^(Act.mkstr a)
else "|"^(T.mkstr m)^">"^(Act.mkstr a)
fun dead(p,vis,trace,b) =
if A.is_process(p,defs) then
let val tr = O.commitments(p,defs)
in
if null tr then (print("Deadlock found in "^(makstr_top p)^"\n reachable by "^(makestring (length trace))^" commitments\n");
if Flags.trace() then
print (" "^(Lib.mapconcat commstr (rev trace) "")^"\n")
else ();
(vadd(p,vis),true))
else if vmember(p,vis) then (vis,b)
else
fold (fn (tt as O.Comm(_,c,t),(rvis,bo)) =>
if (Act.is_input(c) andalso A.is_abstraction(t,defs)) orelse
(Act.is_output(c) andalso A.is_concretion(t,defs)) orelse
(Act.is_tau(c) andalso A.is_process(t,defs)) then
dead(t,rvis,tt::trace,b orelse bo)
else
(print("Sorting deadlock found in "^(makstr_top p)^"\n reachable by "^(makestring (length trace))^" commitments\n");
if Flags.trace() then
print (" "^(Lib.mapconcat commstr (rev trace) "")^"\n") else ();
(rvis,true))
) tr (vadd(p,vis),b)
end
else if A.is_abstraction(p,defs) then
let val (x,p') = A.abs_all(p,defs)
val nns = N.n_newNamesNotin(x,A.free_names p)
val tt = A.beta_reduce p' (nns,0)
in
dead(tt,vis,trace,b)
end
else
let val (n,x,p') = A.conc_all(p,defs)
val nns = N.n_newNamesNotin(n,A.free_names p)
val tt = A.beta_reduce p' (nns,0)
in
dead(tt,vis,trace,b)
end
in
let val (_,b) = dead(ag,vtbl,[],false)
in if b then ()
else print "No deadlocks found.\n"
end
end
fun ztep(ag,defs) =
let fun succ(O.Comm(c,a,p)) = p
fun add(x,y) = (H.puthash(x,true,y); y)
fun member(x,y) = case H.gethash(x,y) of NONE => false | SOME z => z
val vtbl = H.maketable(3,A.hashval,A.eq,A.mk_nil(),true)
fun nl_mak(nl,frn) = (Lib.mapconcat (fn n=>N.makstr(n,frn)) nl ",")
and nl_string(n,nl) =
if size(N.pretty_name(n)) > 0 then
N.pretty_name(n)
else
"~v"^(makestring(length nl))
fun nl_update([],nl) = nl
| nl_update(n::nr,[]) =
nl_update(nr,[nl_string (n,[])])
| nl_update(n::nr,nl) =
let fun nl_insert(s,i,[]) =
if i = 0 then [s]
else "V"::(nl_insert(s,i-1,[]))
| nl_insert (s,i,nl) =
if i = 0 then s::nl
else
hd(nl)::nl_insert(s,i-1,tl(nl))
in
if !Flags.tracelevel > 2 then print ("nl_update(["^(N.mkstr n)^","^(Lib.mapconcat N.mkstr nr ",")^"],["^(Lib.mapconcat (fn n=>n) nl ",")^"])\n") else ();
nl_update(nr,nl_insert(nl_string(n,nl),N.code(n),nl))
end
fun nfirst (0,_) = []
| nfirst (n,[]) = []
| nfirst (n,h::t) = h::(nfirst(n-1,t))
fun c_makstr(O.Comm(c,a,p),frn) =
"|"^(if T.eq(c,T.True) then "" else (T.makstr(c,frn)))
^">"^(Act.makstr(a,frn))^"."^(A.makstr (p,frn))
fun ptrans frn (com,n) =
(print ((makestring n)^": "^(c_makstr (com,frn))^"\n");
n+1)
fun makenum l =
if (size l) = 1 then
(ord l - ord "0")
else
(10*(ord l - ord "0"))
+(makenum (substring(l,1,(size l)-1)))
fun streq(a,b) = a <= b andalso b <= a
fun onestep(a,frn) =
if A.is_process(a,defs) then
let val tr = O.commitments(a,defs)
val _ = if member(a,vtbl) then
print("[Circular behaviour detected]\n")
else ignore(add(a,vtbl))
val x =
if null tr then
(print("No commitments for "^(A.makstr (a,frn))
^"\nQuitting.\n");~1)
else
fold (ptrans frn) (rev tr) 0
val line = if x >= 0 then (print "Step>";read_line()) else ""
in
if null tr then ()
else
if line <= "" andalso line >= "" then
onestep(succ (nth (tr,0)),frn)
else if ord line >= ord "0" andalso ord line <= ord "9"
then let val n = makenum line
in
if n < (length tr) then
onestep(succ (nth (tr, n)),frn)
else (print("Max transition = "^(makestring ((length tr)-1)^"\n"));
onestep(a,frn))
end
else if streq(line,"q") orelse streq(line,"quit")
then ()
else (print "What?\n";
onestep(a,frn))
end
else if A.is_concretion(a,defs) then
let val (n,x,p') = A.conc_all(a,defs)
val nns = N.n_newNamesNotin(n,A.free_names a)
val mx = if null(A.free_names a) then ~1
else N.code(Lib.max N.le (A.free_names a))+1
val nfrn = nl_update(Lib.sort N.le nns,nfirst(mx,frn))
val tt = A.beta_reduce p' (nns,0)
in
print("Concretion (^"^(nl_mak (nns,nfrn)^")["^(nl_mak (x,nfrn))^"]\n"));
onestep(tt,nfrn)
end
else
let val (x,p') = A.abs_all(a,defs)
val nns = N.n_newNamesNotin(x,A.free_names a)
val mx = if null(A.free_names a) then ~1
else N.code(Lib.max N.le (A.free_names a))+1
val nfrn = nl_update(Lib.sort N.le nns,nfirst(mx,frn))
val tt = A.beta_reduce p' (nns,0)
in
print("Abstraction (\\"^(nl_mak (nns,nfrn)^")\n"));
onestep(tt,nfrn)
end
in
(print ("* Valid responses are:\n"^
"  a number N >= 0 to select the Nth commitment,\n"^
"  <CR> to select commitment 0,\n"^
"  q to quit.\n");
onestep(ag,
fold (fn (n,l) => nl_add(n,l))
(Lib.sort N.le
(N.fill(Lib.del_dups N.eq (A.free_names ag))))
[]))
end
fun step(ag,defs) =
let fun succ(O.Comm(c,a,p)) = p
fun add(x,y) = (H.puthash(x,true,y); y)
fun member(x,y) = case H.gethash(x,y) of NONE => false | SOME z => z
val vtbl = H.maketable(3,A.hashval,A.eq,A.mk_nil(),true)
fun nl_mak(nl,frn) = (Lib.mapconcat (fn n=>N.makstr(n,frn)) nl ",")
and nl_string(n,nl) =
if size(N.pretty_name(n)) > 0 then
N.pretty_name(n)
else
"~v"^(makestring(length nl))
fun nl_update([],nl) = nl
| nl_update(n::nr,[]) =
nl_update(nr,[nl_string (n,[])])
| nl_update(n::nr,nl) =
let fun nl_insert(s,i,[]) =
if i = 0 then [s]
else "BUG"::(nl_insert(s,i-1,[]))
| nl_insert (s,i,nl) =
if i = 0 then s::nl
else
hd(nl)::nl_insert(s,i-1,tl(nl))
in
if !Flags.tracelevel > 2 then print ("nl_update(["^(N.mkstr n)^","^(Lib.mapconcat N.mkstr nr ",")^"],["^(Lib.mapconcat (fn n=>n) nl ",")^"])\n") else ();
nl_update(nr,nl_insert(nl_string(n,nl),N.code(n),nl))
end
fun nfirst (0,_) = []
| nfirst (n,[]) = []
| nfirst (n,h::t) = h::(nfirst(n-1,t))
fun c_makstr(O.Comm(c,a,p),frn) =
"|"^(if T.eq(c,T.True) then "" else (T.makstr(c,frn)))
^">"^(Act.makstr(a,frn))^"."^(A.makstr (p,frn))
fun ptrans frn (com,n) =
(print ((makestring n)^": "^(c_makstr (com,frn))^"\n");
n+1)
fun makenum l =
if (size l) = 1 then
(ord l - ord "0")
else
(10*(ord l - ord "0"))
+(makenum (substring(l,1,(size l)-1)))
fun streq(a,b) = a <= b andalso b <= a
fun onestep(a,frn) =
if A.is_process(a,defs) then
let val tr = O.commitments(a,defs)
val _ = if member(a,vtbl) then
print("[Circular behaviour detected]\n")
else ignore(add(a,vtbl))
val x =
if null tr then
(print("No commitments for "^(A.makstr (a,frn))
^"\nQuitting.\n");~1)
else
fold (ptrans frn) (rev tr) 0
val line = if x >= 0 then (print "Step>";read_line()) else ""
in
if null tr then ()
else
if line <= "" andalso line >= "" then
onestep(succ (nth (tr,0)),frn)
else if ord line >= ord "0" andalso ord line <= ord "9"
then let val n = makenum line
in
if n < (length tr) then
onestep(succ (nth (tr, n)),frn)
else (print("Max transition = "^(makestring ((length tr)-1)^"\n"));
onestep(a,frn))
end
else if streq(line,"q") orelse streq(line,"quit")
then ()
else (print "What?\n";
onestep(a,frn))
end
else if A.is_concretion(a,defs) then
let val (n,x,p') = A.conc_all(a,defs)
val nns = N.n_newNamesNotin(n,A.free_names a)
val mx = if null(A.free_names a) then ~1
else N.code(Lib.max N.le (A.free_names a))+1
val nfrn = nl_update(Lib.sort N.le nns,nfirst(mx,frn))
val tt = A.beta_reduce p' (nns,0)
in
print("Concretion (^"^(nl_mak (nns,nfrn)^")["^(nl_mak (x,nfrn))^"]\n"));
onestep(tt,nfrn)
end
else
let val (x,p') = A.abs_all(a,defs)
val nns = N.n_newNamesNotin(x,A.free_names a)
val mx = if null(A.free_names a) then ~1
else N.code(Lib.max N.le (A.free_names a))+1
val nfrn = nl_update(Lib.sort N.le nns,nfirst(mx,frn))
val tt = A.beta_reduce p' (nns,0)
in
print("Abstraction (\\"^(nl_mak (nns,nfrn)^")\n"));
onestep(tt,nfrn)
end
in
(print ("* Valid responses are:\n"^
"  a number N >= 0 to select the Nth commitment,\n"^
"  <CR> to select commitment 0,\n"^
"  q to quit.\n");
onestep(ag,
fold (fn (n,l) => nl_add(n,l))
(Lib.sort N.le
(N.fill(Lib.del_dups N.eq (A.free_names ag))))
[]))
end
fun cmdloop infun (env) =
let val lineNo = ref 0
val pCount = ref 0
fun mkcmdlexer inputfun =
PIParser.Stream.cons(PILrVals.Tokens.DummyCMD(0,0),
PIParser.makeLexer inputfun (lineNo,pCount))
fun eoftoken(lex) =
let val (tok,nlex) = PIParser.Stream.get(lex)
val eoftok = PILrVals.Tokens.EOF(0,0)
val cmdtok = PILrVals.Tokens.DummyCMD(0,0)
in
PIParser.sameToken(tok,eoftok)
orelse (PIParser.sameToken(tok,cmdtok)
andalso let val (tok,nlex) = PIParser.Stream.get(nlex)
in PIParser.sameToken(tok,eoftok) end)
end
fun parse () =
let val lex = mkcmdlexer infun
val print_error = fn (s,i:int,j:int) =>
output(std_out,
"Error"^
(if !Flags.interactive andalso i=0 andalso j=0
then ""
else if i = j then
(" in line "^(makestring i))
else
(" in lines "^(makestring i)^"-"^(makestring j)))
^": " ^ s ^ "\n")
in
if !Flags.interactive then
(print "MWB>";lineNo :=0;pCount:=0)
else ();
if eoftoken(lex) then (Cmd.ParseCMD(Cmd.Quit),lex)
else
PIParser.parse(if !Flags.interactive then 0 else 15,
lex,print_error,())
handle LrParser.ParseError => parse()
| PILex.LexError => (print "Lex error.\n";
parse())
end
fun loop env =
let val _ = if !Flags.interactive then Lib.capturetopcont() else ()
val result = parse()
fun cmdhandle(cmd,env) =
(case cmd of
Cmd.NULL =>
env
| Cmd.Help(c) =>
(print(help(c));
env)
| Cmd.Agent(id,agt) =>
(O.cleartbls();
addenv((id,bbruin_agent(agt,ref E.empty)),env))
| Cmd.Check(a,f) =>
let val e = ref E.empty
val ba = bbruin_agent(a,e)
val nba = A.std_form(ba,env)
val bf = bruijn_formula(f,e)
in
if Flags.trace() then
print("nf: "^(A.mkstr nba)^"\n")
else ();
if MC.naked_model_checker nba bf env then
print("Yes!\n")
else
print("No.\n");
env
end
| Cmd.Clear(id) =>
(O.cleartbls();
if id = "" then
(print "Clearing environment.\n";
E.empty)
else
rmenv(id,env))
| Cmd.Dead(a) =>
(fdead(A.std_form(bbruin_agent(a,ref E.empty),env),env);
env)
| Cmd.Environment(id) =>
(if id = "" then
printenv(env)
else
printagent(V.mkvar id,env);
env)
| Cmd.Eq(a1,a2) =>
let val fenv = ref E.empty
val p1 = A.std_form(bbruin_agent(a1,fenv),env)
val p2 = A.std_form(bbruin_agent(a2,fenv),env)
in
oeq(p1,p2,env);
env
end
| Cmd.EqD(a1, a2, nl) =>
let val fenv = ref E.empty
val fnl = bbruin_nlist(nl,fenv)
val p1 = A.std_form(bbruin_agent(a1,fenv),env)
val p2 = A.std_form(bbruin_agent(a2,fenv),env)
in
oeqd fnl (p1,p2,env);
env
end
| Cmd.Input(file) =>
let fun rfile file old =
let val s = open_in(file)
val e' = cmdloop (fn _ => input_line s) env
in
(close_in s;
Flags.interactive := old;
e')
end
val oldi = !Flags.interactive
val _ = Flags.interactive := false
in
(rfile file oldi)
handle (Io {function=str, ...}) =>
(print ("Error: "^str^"\n");
Flags.interactive := oldi;
env)
end
| Cmd.Size(a) =>
(print ((makestring (gsize(A.std_form(bbruin_agent(a,ref E.empty),env), env)))^"\n");
env)
| Cmd.Step(a) =>
(step(A.std_form(bbruin_agent(a,ref E.empty),env),env);
env)
| Cmd.Ztep(a) =>
(ztep(A.std_form(bbruin_agent(a,ref E.empty),env),env);
env)
| Cmd.Transitions(a) =>
(trans(bbruin_agent(a,ref E.empty),env);
env)
| Cmd.Wtransitions(a) =>
(wtrans(bbruin_agent(a,ref E.empty),env);
env)
| Cmd.Weq(a1,a2) =>
let val fenv = ref E.empty
val p1 = A.std_form(bbruin_agent(a1,fenv),env)
val p2 = A.std_form(bbruin_agent(a2,fenv),env)
in
woeq(p1,p2,env);
env
end
| Cmd.WeqD(p,q,nl) =>
let val fenv = ref E.empty
val fnl = bbruin_nlist(nl,fenv)
val a = A.std_form(bbruin_agent(p,fenv),env)
val b = A.std_form(bbruin_agent(q,fenv),env)
in
dwoeq fnl (a,b,env);
env
end
| Cmd.Quit =>
raise Quit env
| Cmd.Time(cmd) =>
let val oldi = !Flags.interactive
val _ = Flags.interactive := false
val env' = cmdhandle(cmd,env) handle (Quit e) => e
val _ = Flags.interactive := oldi
in
env'
end
| Cmd.Traces(ag) =>
(zpobses(bbruin_agent (ag,ref E.empty),env);
env)
| Cmd.Set(setting) =>
(setit(setting); env)
| Cmd.Show(setting) =>
(showit(setting); env)
)
in
case result of
(Cmd.ParseCMD(cmd),_) =>
((loop (cmdhandle(cmd,env)))
handle E.Unbound v =>
(print ("Unbound identifier: "^(V.mkstr v)^"\n");
loop env)
| Quit e => e)
| _ => (print("Parse error.  Try again.\n");
loop env)
end
handle Lib.Interrupt =>
(print "\n*Interrupt*\n";
Flags.interactive := true;
loop env)
| (A.WrongArgs str) =>
(print (str^"\n");
loop env)
| (O.SemanticsError(s,a)) =>
(print("Semantic Error:\n "^s^"\n in agent: "^
(makstr_top a)^"\n");
loop env)
in
loop env
end
and toplevel(vrsn) =
let
in
print ("\n The Mobility Workbench\n"^
" ("^vrsn^")\n\n");
ignore(cmdloop (fn _ => input_line std_in) E.empty);
O.cleartbls();
()
end
handle (Lib.disaster str) =>
print ("Program error: "^str^
"\nPlease write to Bjorn.Victor@DoCS.UU.SE about it.\n")
| exn =>
print("Program error: Uncaught exception "^(System.exn_name exn)^
"\nPlease write to Bjorn.Victor@DoCS.UU.SE about it.\n")
and help(Cmd.HNULL) =
"Commands are:\n"^
" agent clear deadlocks env eq eqd input set show step size time weq weqd\n"^
" help quit\n"^
"Type \"help CMD\" to get help on the command CMD.\n\n"^
"The syntax for agents is what you'd expect, plus that tau is t, nil is 0,\n"^
"(nu x) is (^x), input prefix is a(x), output is 'a<x>,\n"^
"(lambda x) is (\\x) and concretions are [x]\n"
| help(Cmd.HAll) =
(app print (map help [Cmd.HAgent,Cmd.HClear,Cmd.HDead,Cmd.HEnv,
Cmd.HEq,Cmd.HEqd,Cmd.HInput,Cmd.HStep,
Cmd.HSize,Cmd.HTime,Cmd.HWeq,Cmd.HWeqd,
Cmd.HSet,Cmd.HShow,Cmd.HQuit]);
"")
| help(Cmd.HAgent) =
"agent P(x) = def\n"^
"agent P = abstr\n"^
"\t\tdefine the agent identifier P.  The definition must be closed.\n"
| help(Cmd.HClear) =
"clear P\t\tremoves the definition of agent identifier P\n"^
"clear\t\tremoves ALL agent definitions\n"
| help(Cmd.HDead) =
"deadlocks A\tshow deadlocks in the agent A\n"
| help(Cmd.HEnv) =
"env P\t\tshows the definition of agent identifier P\n"^
"env\t\tshows ALL agent definitions\n"
| help(Cmd.HEq) =
"eq A B\t\tcheck whether agents A and B are strong open equivalent\n"
| help(Cmd.HEqd) =
"eqd (n,m,..,o) A B\n"^
"\t\tcheck whether agents A and B are strong open equivalent,\n"^
"\t\tgiven that (n,m,..,o) are distinct from all free names\n"^
"in A and B\n"
| help(Cmd.HInput) =
"input \"file\"\tread commands from file\n"
| help(Cmd.HStep) =
"step P\t\tsimulate P step by step\n"
| help(Cmd.HSize) =
"size P\t\tgive a low estimate of the graph size of P\n"
| help(Cmd.HTime) =
"time CMD ...\texecute the command CMD and print timing info\n"
| help(Cmd.HWeq) =
"weq A B\t\tcheck whether agents A and B are weak open equivalent\n"
| help(Cmd.HWeqd) =
"weqd (n,m,..,o) A B\n"^
"\t\tcheck whether agents A and B are weak open equivalent,\n"^
"\t\tgiven that (n,m,..,o) are distinct from all free names\n"^
"in A and B\n"
| help(Cmd.HQuit) =
"quit\t\tterminates MWB\n"
| help(Cmd.HSet) =
"set STTNG\tset the setting STTNG. See \"set ?\"\n"
| help(Cmd.HShow) =
"show STTNG\tshow the setting STTNG. See \"show ?\"\n"^
"show\t\tshows all settings\n"
| help(_) =
"Sorry, no specific help on that subject.\n"^(help Cmd.HNULL)
and setit(Cmd.Debug(n)) =
(Flags.tracelevel := n;
())
| setit(Cmd.Threshold(f)) =
(if f > 100 orelse f <= 0 then
print ("Bogus threshold value; please use a value between 1 and 100.\n")
else
(H.set_rehash_threshold(real(f)/100.0);
print("New rehash threshold = "^(makestring(floor(100.0*(H.get_rehash_threshold()))))^"\n")))
| setit(Cmd.Rewrite(b)) =
(Flags.rewrite := b; showit(Cmd.SRewrite))
| setit(Cmd.Remember(b)) =
(O.enabletbls(b); showit(Cmd.SRemember))
| setit(Cmd.SetHelp) = print
("set debug n\t\tsets debug level n (positive integer)\n"^
"set threshold n\t\tset rehash threshold n (1 to 100)\n"^
"set rewrite b\t\tset agent rewriting on or off\n"^
"set remember b\t\tset commitment remembering on or off\n"^
"set ?\t\t\tshow this text\n")
and showit(Cmd.SDebug) =
print("Debug level = "^(makestring(!Flags.tracelevel))^"\n")
| showit(Cmd.SThreshold) =
print("Rehash threshold = "^(makestring(floor(100.0*(H.get_rehash_threshold()))))^"\n")
| showit(Cmd.SRewrite) =
print("Agent rewriting is "^(if !Flags.rewrite then "on" else "off")^"\n")
| showit(Cmd.SRemember) =
print("Commitment remembering is "^(if O.enabledtbls() then "on" else "off")^"\n")
| showit(Cmd.STables) =
O.desctbls()
| showit(Cmd.SVersion) = print ((!version)^"\n")
| showit(Cmd.SHelp) = print
("show debug\t\tshow debug level\n"^
"show threshold\t\tshow rehash threshold\n"^
"show rewrite\t\tshow agent rewriting setting\n"^
"show remember\t\tshow commitment remembering setting\n"^
"show version\t\tshow MWB version\n"^
"show tables\t\tshow commitment tables\n"^
"show all\t\tshow most of above things\n"^
"show ?\t\t\tshow this text\n")
| showit(Cmd.SAll) =
app showit [Cmd.SVersion,Cmd.SDebug,Cmd.SThreshold,Cmd.SRewrite,Cmd.SRemember]
end
;
