functor ElfLrValsFun (structure Token : TOKEN
structure Term : TERM
structure ElfAbsyn : ELF_ABSYN
sharing ElfAbsyn.Term = Term
structure Absyn : ABSYN
sharing Absyn.Term = Term) : Elf_LRVALS =
struct
structure ParserData=
struct
structure Header =
struct
structure ElfAbsyn = ElfAbsyn
structure Absyn = Absyn
open Absyn ElfAbsyn
end
structure LrTable = Token.LrTable
structure Token = Token
local open LrTable in
val table=let val actionRows =
"\
\\001\000\002\000\000\000\024\000\000\000\000\000\
\\001\000\003\000\035\000\005\000\056\000\010\000\034\000\011\000\033\000\000\000\
\\001\000\003\000\041\000\000\000\
\\001\000\004\000\019\000\006\000\018\000\008\000\017\000\012\000\016\000\
\\013\000\015\000\014\000\014\000\015\000\013\000\016\000\012\000\
\\017\000\011\000\000\000\
\\001\000\004\000\019\000\006\000\018\000\008\000\017\000\012\000\016\000\
\\014\000\014\000\015\000\013\000\016\000\012\000\017\000\011\000\000\000\
\\001\000\006\000\036\000\000\000\
\\001\000\007\000\055\000\000\000\
\\001\000\007\000\064\000\000\000\
\\001\000\009\000\053\000\000\000\
\\001\000\013\000\029\000\014\000\028\000\015\000\027\000\017\000\026\000\000\000\
\\001\000\013\000\029\000\014\000\028\000\015\000\027\000\017\000\026\000\
\\020\000\025\000\021\000\024\000\022\000\023\000\023\000\022\000\000\000\
\\001\000\018\000\004\000\019\000\003\000\000\000\
\\001\000\025\000\045\000\026\000\044\000\027\000\043\000\000\000\
\\001\000\028\000\047\000\000\000\
\\071\000\000\000\
\\072\000\000\000\
\\073\000\000\000\
\\074\000\000\000\
\\075\000\000\000\
\\076\000\000\000\
\\077\000\000\000\
\\078\000\000\000\
\\079\000\003\000\035\000\010\000\034\000\011\000\033\000\000\000\
\\080\000\000\000\
\\081\000\003\000\035\000\010\000\034\000\011\000\033\000\000\000\
\\082\000\000\000\
\\083\000\004\000\019\000\006\000\018\000\008\000\017\000\012\000\016\000\
\\014\000\014\000\015\000\013\000\016\000\012\000\017\000\011\000\000\000\
\\084\000\010\000\034\000\011\000\033\000\000\000\
\\085\000\011\000\033\000\000\000\
\\086\000\011\000\033\000\000\000\
\\087\000\000\000\
\\088\000\003\000\035\000\010\000\034\000\011\000\033\000\000\000\
\\089\000\003\000\035\000\010\000\034\000\011\000\033\000\000\000\
\\090\000\000\000\
\\091\000\000\000\
\\092\000\000\000\
\\093\000\000\000\
\\094\000\000\000\
\\095\000\000\000\
\\096\000\000\000\
\\097\000\000\000\
\\098\000\000\000\
\\099\000\000\000\
\\100\000\000\000\
\\101\000\000\000\
\\102\000\000\000\
\\103\000\000\000\
\\104\000\003\000\035\000\010\000\034\000\011\000\033\000\000\000\
\\105\000\003\000\054\000\000\000\
\\106\000\000\000\
\\107\000\013\000\029\000\014\000\028\000\015\000\027\000\017\000\026\000\000\000\
\\108\000\000\000\
\"
val actionRowNumbers =
"\011\000\003\000\010\000\033\000\
\\026\000\030\000\024\000\023\000\
\\015\000\040\000\039\000\038\000\
\\037\000\005\000\041\000\009\000\
\\009\000\004\000\002\000\014\000\
\\009\000\012\000\013\000\013\000\
\\045\000\044\000\043\000\046\000\
\\035\000\034\000\036\000\004\000\
\\004\000\004\000\009\000\008\000\
\\048\000\006\000\001\000\004\000\
\\009\000\013\000\013\000\013\000\
\\019\000\009\000\020\000\029\000\
\\028\000\027\000\007\000\004\000\
\\004\000\004\000\042\000\022\000\
\\021\000\050\000\018\000\017\000\
\\016\000\049\000\003\000\032\000\
\\047\000\031\000\051\000\025\000\
\\000\000"
val gotoT =
"\
\\001\000\068\000\000\000\
\\003\000\008\000\004\000\007\000\005\000\006\000\006\000\005\000\
\\007\000\004\000\009\000\003\000\000\000\
\\002\000\019\000\010\000\018\000\000\000\
\\000\000\
\\006\000\030\000\008\000\029\000\009\000\028\000\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\010\000\036\000\011\000\035\000\000\000\
\\010\000\036\000\011\000\037\000\000\000\
\\005\000\038\000\006\000\005\000\007\000\004\000\009\000\003\000\000\000\
\\000\000\
\\000\000\
\\010\000\040\000\000\000\
\\000\000\
\\012\000\044\000\000\000\
\\012\000\046\000\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\005\000\047\000\006\000\005\000\007\000\004\000\009\000\003\000\000\000\
\\005\000\048\000\006\000\005\000\007\000\004\000\009\000\003\000\000\000\
\\005\000\049\000\006\000\005\000\007\000\004\000\009\000\003\000\000\000\
\\010\000\036\000\011\000\050\000\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\005\000\055\000\006\000\005\000\007\000\004\000\009\000\003\000\000\000\
\\010\000\057\000\013\000\056\000\000\000\
\\012\000\058\000\000\000\
\\012\000\059\000\000\000\
\\012\000\060\000\000\000\
\\000\000\
\\010\000\057\000\013\000\061\000\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\005\000\063\000\006\000\005\000\007\000\004\000\009\000\003\000\000\000\
\\005\000\064\000\006\000\005\000\007\000\004\000\009\000\003\000\000\000\
\\005\000\065\000\006\000\005\000\007\000\004\000\009\000\003\000\000\000\
\\000\000\
\\000\000\
\\000\000\
\\010\000\057\000\013\000\066\000\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\004\000\067\000\005\000\006\000\006\000\005\000\007\000\004\000\
\\009\000\003\000\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\"
val numstates = 69
val numrules = 38
val s = ref "" and index = ref 0
val string_to_int = fn () => 
let val i = !index
in index := i+2; ordof(!s,i) + ordof(!s,i+1) * 256
end
val string_to_list = fn s' =>
    let val len = String.length s'
        fun f () =
           if !index < len then string_to_int() :: f()
           else nil
   in index := 0; s := s'; f ()
   end
val string_to_pairlist = fn (conv_key,conv_entry) =>
     let fun f () =
         case string_to_int()
         of 0 => EMPTY
          | n => PAIR(conv_key (n-1),conv_entry (string_to_int()),f())
     in f
     end
val string_to_pairlist_default = fn (conv_key,conv_entry) =>
    let val conv_row = string_to_pairlist(conv_key,conv_entry)
    in fn () =>
       let val default = conv_entry(string_to_int())
           val row = conv_row()
       in (row,default)
       end
   end
val string_to_table = fn (convert_row,s') =>
    let val len = String.length s'
 	 fun f ()=
	    if !index < len then convert_row() :: f()
	    else nil
     in (s := s'; index := 0; f ())
     end
local
  val memo = Array.array(numstates+numrules,ERROR)
  val _ =let fun g i=(Array.update(memo,i,REDUCE(i-numstates)); g(i+1))
	fun f i =
	     if i=numstates then g i
	     else (Array.update(memo,i,SHIFT (STATE i)); f (i+1))
	   in f 0 handle Subscript => ()
	   end
in
val entry_to_action = fn 0 => ACCEPT | 1 => ERROR | j => Array.sub(memo,(j-2))
end
val gotoT=Array.arrayoflist(string_to_table(string_to_pairlist(NT,STATE),gotoT))
val actionRows=string_to_table(string_to_pairlist_default(T,entry_to_action),actionRows)
val actionRowNumbers = string_to_list actionRowNumbers
val actionT = let val actionRowLookUp=
let val a=Array.arrayoflist(actionRows) in fn i=>Array.sub(a,i) end
in Array.arrayoflist(map actionRowLookUp actionRowNumbers)
end
in LrTable.mkLrTable {actions=actionT,gotos=gotoT,numRules=numrules,
numStates=numstates,initialState=STATE 0}
end
end
local open Header in
type pos = int
type arg = unit
structure MlyValue = 
struct
datatype svalue = VOID | ntVOID of unit | NUM of  (int)
 | QUID of  (string) | UCID of  (string) | LCID of  (string)
 | idlist of  (string list) | fixity of  (Absyn.afixity)
 | varbd of  (Absyn.avarbind) | id of  (string)
 | atom of  (Absyn.aatom) | rand of  (Absyn.aatom)
 | termseq of  (Absyn.atermseq) | bnd of  (Absyn.aterm)
 | term of  (Absyn.aterm) | qterm of  (Absyn.aterm)
 | query of  (string list*Term.term)
 | sigentry of  (string list*Term.varbind)
 | start of  (ElfAbsyn.parse_result)
end
type svalue = MlyValue.svalue
type result = ElfAbsyn.parse_result
end
structure EC=
struct
open LrTable
val is_keyword =
fn _ => false
val preferred_insert =
fn (T 1) => true | _ => false
val preferred_subst =
fn  _ => nil
val noShift = 
fn (T 0) => true | _ => false
val showTerminal =
fn (T 0) => "EOF"
  | (T 1) => "DOT"
  | (T 2) => "COLON"
  | (T 3) => "LPAREN"
  | (T 4) => "RPAREN"
  | (T 5) => "LBRACKET"
  | (T 6) => "RBRACKET"
  | (T 7) => "LBRACE"
  | (T 8) => "RBRACE"
  | (T 9) => "BACKARROW"
  | (T 10) => "ARROW"
  | (T 11) => "TYPE"
  | (T 12) => "SIGMA"
  | (T 13) => "LCID"
  | (T 14) => "UCID"
  | (T 15) => "QUID"
  | (T 16) => "UNDERSCORE"
  | (T 17) => "SIGENTRY"
  | (T 18) => "QUERY"
  | (T 19) => "POSTFIX"
  | (T 20) => "PREFIX"
  | (T 21) => "INFIX"
  | (T 22) => "NAME"
  | (T 23) => "EOFPRAGMA"
  | (T 24) => "LEFT"
  | (T 25) => "RIGHT"
  | (T 26) => "NONE_"
  | (T 27) => "NUM"
  | _ => "bogus-term"
val errtermvalue=
let open Header in
fn _:Token.LrTable.term => MlyValue.VOID
end
val terms = (T 0) :: (T 1) :: (T 2) :: (T 3) :: (T 4) :: (T 5) :: (T 6
) :: (T 7) :: (T 8) :: (T 9) :: (T 10) :: (T 11) :: (T 12) :: (T 16)
 :: (T 17) :: (T 18) :: (T 19) :: (T 20) :: (T 21) :: (T 22) :: (T 23)
 :: (T 24) :: (T 25) :: (T 26) :: nil
end
structure Actions =
struct 
exception mlyAction of int
val actions = 
let open Header
in
fn (i392,defaultPos:int,stack,
    ():arg) =>
case (i392,stack)
of (0,(_:Token.LrTable.state,
       (MlyValue.sigentry sigentry,sigentryleft,sigentryright as 
sigentry1right))::(_,(_,SIGENTRY1left,_))::rest671) => let val result=
MlyValue.start((ParsedSigentry(sigentry,(sigentryleft,sigentryright)))
)
 in (LrTable.NT 0,(result,SIGENTRY1left,sigentry1right),rest671) end
| (1,(_,(MlyValue.query query,_,query1right))::(_,(_,QUERY1left,_))::
rest671) => let val result=MlyValue.start((ParsedQuery(query)))
 in (LrTable.NT 0,(result,QUERY1left,query1right),rest671) end
| (2,(_,(MlyValue.fixity fixity,_,fixityright as fixity1right))::_::(_
,(_,INFIXleft,_))::(_,(_,SIGENTRY1left,_))::rest671) => let val result
=MlyValue.start((
ParsedFixity(mk_fix(Term.Infix(Term.Left),fixity)
				                   (INFIXleft,fixityright))
))
 in (LrTable.NT 0,(result,SIGENTRY1left,fixity1right),rest671) end
| (3,(_,(MlyValue.fixity fixity,_,fixityright as fixity1right))::_::(_
,(_,INFIXleft,_))::(_,(_,SIGENTRY1left,_))::rest671) => let val result
=MlyValue.start((
ParsedFixity(mk_fix(Term.Infix(Term.Right),fixity)
				    		  (INFIXleft,fixityright))
))
 in (LrTable.NT 0,(result,SIGENTRY1left,fixity1right),rest671) end
| (4,(_,(MlyValue.fixity fixity,_,fixityright as fixity1right))::_::(_
,(_,INFIXleft,_))::(_,(_,SIGENTRY1left,_))::rest671) => let val result
=MlyValue.start((
ParsedFixity(mk_fix(Term.Infix(Term.None),fixity)
				    	          (INFIXleft,fixityright))
))
 in (LrTable.NT 0,(result,SIGENTRY1left,fixity1right),rest671) end
| (5,(_,(MlyValue.fixity fixity,_,fixityright as fixity1right))::(_,(_
,PREFIXleft,_))::(_,(_,SIGENTRY1left,_))::rest671) => let val result=
MlyValue.start((
ParsedFixity(mk_fix(Term.Prefix,fixity)
				    	          (PREFIXleft,fixityright))
))
 in (LrTable.NT 0,(result,SIGENTRY1left,fixity1right),rest671) end
| (6,(_,(MlyValue.fixity fixity,_,fixityright as fixity1right))::(_,(_
,POSTFIXleft,_))::(_,(_,SIGENTRY1left,_))::rest671) => let val result=
MlyValue.start((
ParsedFixity(mk_fix(Term.Postfix,fixity)
				 	       (POSTFIXleft,fixityright))
))
 in (LrTable.NT 0,(result,SIGENTRY1left,fixity1right),rest671) end
| (7,(_,(MlyValue.idlist idlist,_,idlist1right))::(_,(MlyValue.id id,_
,idright))::(_,(_,NAMEleft,_))::(_,(_,SIGENTRY1left,_))::rest671) => 
let val result=MlyValue.start((
ParsedNamePref(mk_name_pref(id,idlist)
				 		 (NAMEleft,idright))))
 in (LrTable.NT 0,(result,SIGENTRY1left,idlist1right),rest671) end
| (8,(_,(MlyValue.term term,_,term1right))::_::(_,(MlyValue.id id,
id1left,_))::rest671) => let val result=MlyValue.sigentry((
to_varbind(mk_varbind(id,term))))
 in (LrTable.NT 1,(result,id1left,term1right),rest671) end
| (9,(_,(MlyValue.qterm qterm,qterm1left,qterm1right))::rest671) => 
let val result=MlyValue.query((to_term(qterm)))
 in (LrTable.NT 2,(result,qterm1left,qterm1right),rest671) end
| (10,(_,(MlyValue.term term,term1left,term1right))::rest671) => let 
val result=MlyValue.qterm((term))
 in (LrTable.NT 3,(result,term1left,term1right),rest671) end
| (11,(_,(MlyValue.qterm qterm,_,qtermright as qterm1right))::_::(_,(
MlyValue.varbd varbd,_,_))::(_,(_,LBRACKETleft,_))::(_,(_,SIGMAleft
 as SIGMA1left,SIGMAright))::rest671) => let val result=MlyValue.qterm
((
mk_quant("sigma",mk_abst(varbd,qterm)(LBRACKETleft,qtermright))
					 (SIGMAleft,SIGMAright)
					 (SIGMAleft,qtermright)
))
 in (LrTable.NT 3,(result,SIGMA1left,qterm1right),rest671) end
| (12,(_,(MlyValue.termseq termseq,termseqleft as termseq1left,
termseqright as termseq1right))::rest671) => let val result=
MlyValue.term((
seq_to_term(termseq)
					    (termseqleft,termseqright)))
 in (LrTable.NT 4,(result,termseq1left,termseq1right),rest671) end
| (13,(_,(MlyValue.term term2,_,term2right))::_::(_,(MlyValue.term 
term1,term1left,_))::rest671) => let val result=MlyValue.term((
mk_hastype(term1,term2)(term1left,term2right)))
 in (LrTable.NT 4,(result,term1left,term2right),rest671) end
| (14,(_,(MlyValue.term term2,_,term2right))::_::(_,(MlyValue.term 
term1,term1left,_))::rest671) => let val result=MlyValue.term((
mk_arrow(term2,term1)(term1left,term2right)))
 in (LrTable.NT 4,(result,term1left,term2right),rest671) end
| (15,(_,(MlyValue.term term2,_,term2right))::_::(_,(MlyValue.term 
term1,term1left,_))::rest671) => let val result=MlyValue.term((
mk_arrow(term1,term2)(term1left,term2right)))
 in (LrTable.NT 4,(result,term1left,term2right),rest671) end
| (16,(_,(MlyValue.bnd bnd,bnd1left,bnd1right))::rest671) => let val 
result=MlyValue.term((bnd))
 in (LrTable.NT 4,(result,bnd1left,bnd1right),rest671) end
| (17,(_,(MlyValue.term term,_,termright as term1right))::_::(_,(
MlyValue.varbd varbd,_,_))::(_,(_,LBRACKETleft as LBRACKET1left,_))::
rest671) => let val result=MlyValue.bnd((
mk_abst(varbd,term)(LBRACKETleft,termright)))
 in (LrTable.NT 5,(result,LBRACKET1left,term1right),rest671) end
| (18,(_,(MlyValue.term term,_,termright as term1right))::_::(_,(
MlyValue.varbd varbd,_,_))::(_,(_,LBRACEleft as LBRACE1left,_))::
rest671) => let val result=MlyValue.bnd((
mk_pi(varbd,term)(LBRACEleft,termright)))
 in (LrTable.NT 5,(result,LBRACE1left,term1right),rest671) end
| (19,(_,(MlyValue.atom atom,atomleft as atom1left,atomright as 
atom1right))::rest671) => let val result=MlyValue.termseq((
mk_oneseq(atom)(atomleft,atomright)))
 in (LrTable.NT 6,(result,atom1left,atom1right),rest671) end
| (20,(_,(MlyValue.rand rand,_,rand1right))::(_,(MlyValue.termseq 
termseq,termseqleft as termseq1left,termseqright))::rest671) => let 
val result=MlyValue.termseq((
mk_termseq(termseq,rand)
					   (termseqleft,termseqright)))
 in (LrTable.NT 6,(result,termseq1left,rand1right),rest671) end
| (21,(_,(MlyValue.atom atom,atom1left,atom1right))::rest671) => let 
val result=MlyValue.rand((atom))
 in (LrTable.NT 7,(result,atom1left,atom1right),rest671) end
| (22,(_,(MlyValue.bnd bnd,bnd1left,bnd1right))::rest671) => let val 
result=MlyValue.rand((term_to_atom(bnd)))
 in (LrTable.NT 7,(result,bnd1left,bnd1right),rest671) end
| (23,(_,(MlyValue.LCID LCID,LCIDleft as LCID1left,LCIDright as 
LCID1right))::rest671) => let val result=MlyValue.atom((
mk_bv_const(LCID)(LCIDleft,LCIDright)))
 in (LrTable.NT 8,(result,LCID1left,LCID1right),rest671) end
| (24,(_,(MlyValue.UCID UCID,UCIDleft as UCID1left,UCIDright as 
UCID1right))::rest671) => let val result=MlyValue.atom((
mk_bv_const_fv(UCID)(UCIDleft,UCIDright)))
 in (LrTable.NT 8,(result,UCID1left,UCID1right),rest671) end
| (25,(_,(MlyValue.QUID QUID,QUIDleft as QUID1left,QUIDright as 
QUID1right))::rest671) => let val result=MlyValue.atom((
term_to_atom(atom_to_term(mk_bv_const(QUID)(QUIDleft,QUIDright)))))
 in (LrTable.NT 8,(result,QUID1left,QUID1right),rest671) end
| (26,(_,(_,UNDERSCOREleft as UNDERSCORE1left,UNDERSCOREright as 
UNDERSCORE1right))::rest671) => let val result=MlyValue.atom((
term_to_atom(mk_uscore(UNDERSCOREleft,UNDERSCOREright))))
 in (LrTable.NT 8,(result,UNDERSCORE1left,UNDERSCORE1right),rest671)
 end
| (27,(_,(_,TYPEleft as TYPE1left,TYPEright as TYPE1right))::rest671)
 => let val result=MlyValue.atom((
term_to_atom(mk_ttype(TYPEleft,TYPEright))))
 in (LrTable.NT 8,(result,TYPE1left,TYPE1right),rest671) end
| (28,(_,(_,_,RPARENright as RPAREN1right))::(_,(MlyValue.term term,_,
_))::(_,(_,LPARENleft as LPAREN1left,_))::rest671) => let val result=
MlyValue.atom((term_to_atom(mk_mark(term)(LPARENleft,RPARENright))))
 in (LrTable.NT 8,(result,LPAREN1left,RPAREN1right),rest671) end
| (29,(_,(MlyValue.LCID LCID,LCID1left,LCID1right))::rest671) => let 
val result=MlyValue.id((LCID))
 in (LrTable.NT 9,(result,LCID1left,LCID1right),rest671) end
| (30,(_,(MlyValue.UCID UCID,UCID1left,UCID1right))::rest671) => let 
val result=MlyValue.id((UCID))
 in (LrTable.NT 9,(result,UCID1left,UCID1right),rest671) end
| (31,(_,(_,UNDERSCORE1left,UNDERSCORE1right))::rest671) => let val 
result=MlyValue.id((mk_uscore_string))
 in (LrTable.NT 9,(result,UNDERSCORE1left,UNDERSCORE1right),rest671)
 end
| (32,(_,(_,SIGMA1left,SIGMA1right))::rest671) => let val result=
MlyValue.id(("sigma"))
 in (LrTable.NT 9,(result,SIGMA1left,SIGMA1right),rest671) end
| (33,(_,(MlyValue.term term,_,term1right))::_::(_,(MlyValue.id id,
id1left,_))::rest671) => let val result=MlyValue.varbd((
mk_varbind(id,term)))
 in (LrTable.NT 10,(result,id1left,term1right),rest671) end
| (34,(_,(MlyValue.id id,id1left,idright as id1right))::rest671) => 
let val result=MlyValue.varbd((
mk_varbind(id,mk_uscore(idright,idright))))
 in (LrTable.NT 10,(result,id1left,id1right),rest671) end
| (35,(_,(MlyValue.idlist idlist,_,idlist1right))::(_,(MlyValue.NUM 
NUM,NUMleft as NUM1left,NUMright))::rest671) => let val result=
MlyValue.fixity((mk_fixity(NUM,idlist)(NUMleft,NUMright)))
 in (LrTable.NT 11,(result,NUM1left,idlist1right),rest671) end
| (36,(_,(MlyValue.id id,id1left,id1right))::rest671) => let val 
result=MlyValue.idlist((id::nil))
 in (LrTable.NT 12,(result,id1left,id1right),rest671) end
| (37,(_,(MlyValue.idlist idlist,_,idlist1right))::(_,(MlyValue.id id,
id1left,_))::rest671) => let val result=MlyValue.idlist((id::idlist))
 in (LrTable.NT 12,(result,id1left,idlist1right),rest671) end
| _ => raise (mlyAction i392)
end
val void = MlyValue.VOID
val extract = fn a => (fn MlyValue.start x => x
| _ => let exception ParseInternal
	in raise ParseInternal end) a 
end
end
structure Tokens : Elf_TOKENS =
struct
type svalue = ParserData.svalue
type ('a,'b) token = ('a,'b) Token.token
fun EOF (p1,p2) = Token.TOKEN (ParserData.LrTable.T 0,(
ParserData.MlyValue.VOID,p1,p2))
fun DOT (p1,p2) = Token.TOKEN (ParserData.LrTable.T 1,(
ParserData.MlyValue.VOID,p1,p2))
fun COLON (p1,p2) = Token.TOKEN (ParserData.LrTable.T 2,(
ParserData.MlyValue.VOID,p1,p2))
fun LPAREN (p1,p2) = Token.TOKEN (ParserData.LrTable.T 3,(
ParserData.MlyValue.VOID,p1,p2))
fun RPAREN (p1,p2) = Token.TOKEN (ParserData.LrTable.T 4,(
ParserData.MlyValue.VOID,p1,p2))
fun LBRACKET (p1,p2) = Token.TOKEN (ParserData.LrTable.T 5,(
ParserData.MlyValue.VOID,p1,p2))
fun RBRACKET (p1,p2) = Token.TOKEN (ParserData.LrTable.T 6,(
ParserData.MlyValue.VOID,p1,p2))
fun LBRACE (p1,p2) = Token.TOKEN (ParserData.LrTable.T 7,(
ParserData.MlyValue.VOID,p1,p2))
fun RBRACE (p1,p2) = Token.TOKEN (ParserData.LrTable.T 8,(
ParserData.MlyValue.VOID,p1,p2))
fun BACKARROW (p1,p2) = Token.TOKEN (ParserData.LrTable.T 9,(
ParserData.MlyValue.VOID,p1,p2))
fun ARROW (p1,p2) = Token.TOKEN (ParserData.LrTable.T 10,(
ParserData.MlyValue.VOID,p1,p2))
fun TYPE (p1,p2) = Token.TOKEN (ParserData.LrTable.T 11,(
ParserData.MlyValue.VOID,p1,p2))
fun SIGMA (p1,p2) = Token.TOKEN (ParserData.LrTable.T 12,(
ParserData.MlyValue.VOID,p1,p2))
fun LCID (i,p1,p2) = Token.TOKEN (ParserData.LrTable.T 13,(
ParserData.MlyValue.LCID i,p1,p2))
fun UCID (i,p1,p2) = Token.TOKEN (ParserData.LrTable.T 14,(
ParserData.MlyValue.UCID i,p1,p2))
fun QUID (i,p1,p2) = Token.TOKEN (ParserData.LrTable.T 15,(
ParserData.MlyValue.QUID i,p1,p2))
fun UNDERSCORE (p1,p2) = Token.TOKEN (ParserData.LrTable.T 16,(
ParserData.MlyValue.VOID,p1,p2))
fun SIGENTRY (p1,p2) = Token.TOKEN (ParserData.LrTable.T 17,(
ParserData.MlyValue.VOID,p1,p2))
fun QUERY (p1,p2) = Token.TOKEN (ParserData.LrTable.T 18,(
ParserData.MlyValue.VOID,p1,p2))
fun POSTFIX (p1,p2) = Token.TOKEN (ParserData.LrTable.T 19,(
ParserData.MlyValue.VOID,p1,p2))
fun PREFIX (p1,p2) = Token.TOKEN (ParserData.LrTable.T 20,(
ParserData.MlyValue.VOID,p1,p2))
fun INFIX (p1,p2) = Token.TOKEN (ParserData.LrTable.T 21,(
ParserData.MlyValue.VOID,p1,p2))
fun NAME (p1,p2) = Token.TOKEN (ParserData.LrTable.T 22,(
ParserData.MlyValue.VOID,p1,p2))
fun EOFPRAGMA (p1,p2) = Token.TOKEN (ParserData.LrTable.T 23,(
ParserData.MlyValue.VOID,p1,p2))
fun LEFT (p1,p2) = Token.TOKEN (ParserData.LrTable.T 24,(
ParserData.MlyValue.VOID,p1,p2))
fun RIGHT (p1,p2) = Token.TOKEN (ParserData.LrTable.T 25,(
ParserData.MlyValue.VOID,p1,p2))
fun NONE_ (p1,p2) = Token.TOKEN (ParserData.LrTable.T 26,(
ParserData.MlyValue.VOID,p1,p2))
fun NUM (i,p1,p2) = Token.TOKEN (ParserData.LrTable.T 27,(
ParserData.MlyValue.NUM i,p1,p2))
end
end
;
