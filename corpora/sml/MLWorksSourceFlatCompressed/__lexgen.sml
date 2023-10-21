require "$.basis.__int";
require "$.basis.__list";
require "$.basis.__string";
require "$.basis.__char";
require "$.basis.__array";
require "$.basis.__text_io";
require "$.basis.__io";
require "$.system.__os";
fun stripRequires instring =
let
val inp = TextIO.openString instring
fun lineByLine acc =
let
val line = TextIO.inputLine inp
in
if String.isPrefix "require" line
then lineByLine (acc^line)
else (acc,line^(TextIO.inputAll inp))
end
in
lineByLine ""
end
functor RedBlack(B : sig type key
val > : key*key->bool
end):
sig type tree
type key
val empty : tree
val insert : key * tree -> tree
val lookup : key * tree -> key
exception notfound of key
end =
struct
open B
datatype color = RED | BLACK
datatype tree = empty | tree of key * color * tree * tree
exception notfound of key
fun insert (key,t) =
let fun f empty = tree(key,RED,empty,empty)
| f (tree(k,BLACK,l,r)) =
if key>k
then case f r
of r as tree(rk,RED, rl as tree(rlk,RED,rll,rlr),rr) =>
(case l
of tree(lk,RED,ll,lr) =>
tree(k,RED,tree(lk,BLACK,ll,lr),
tree(rk,BLACK,rl,rr))
| _ => tree(rlk,BLACK,tree(k,RED,l,rll),
tree(rk,RED,rlr,rr)))
| r as tree(rk,RED,rl, rr as tree(rrk,RED,rrl,rrr)) =>
(case l
of tree(lk,RED,ll,lr) =>
tree(k,RED,tree(lk,BLACK,ll,lr),
tree(rk,BLACK,rl,rr))
| _ => tree(rk,BLACK,tree(k,RED,l,rl),rr))
| r => tree(k,BLACK,l,r)
else if k>key
then case f l
of l as tree(lk,RED,ll, lr as tree(lrk,RED,lrl,lrr)) =>
(case r
of tree(rk,RED,rl,rr) =>
tree(k,RED,tree(lk,BLACK,ll,lr),
tree(rk,BLACK,rl,rr))
| _ => tree(lrk,BLACK,tree(lk,RED,ll,lrl),
tree(k,RED,lrr,r)))
| l as tree(lk,RED, ll as tree(llk,RED,lll,llr), lr) =>
(case r
of tree(rk,RED,rl,rr) =>
tree(k,RED,tree(lk,BLACK,ll,lr),
tree(rk,BLACK,rl,rr))
| _ => tree(lk,BLACK,ll,tree(k,RED,lr,r)))
| l => tree(k,BLACK,l,r)
else tree(key,BLACK,l,r)
| f (tree(k,RED,l,r)) =
if key>k then tree(k,RED,l, f r)
else if k>key then tree(k,RED, f l, r)
else tree(key,RED,l,r)
in case f t
of tree(k,RED, l as tree(_,RED,_,_), r) => tree(k,BLACK,l,r)
| tree(k,RED, l, r as tree(_,RED,_,_)) => tree(k,BLACK,l,r)
| t => t
end
fun lookup (key,t) =
let fun look empty = raise (notfound key)
| look (tree(k,_,l,r)) =
if k>key then look l
else if key>k then look r
else k
in look t
end
end
signature LEXGEN =
sig
val lexGen: string -> unit
end
structure LexGen: LEXGEN =
struct
open Array List
infix 9 sub
datatype token = CHARS of bool array | QMARK | STAR | PLUS | BAR
| LP | RP | CARAT | DOLLAR | SLASH | STATE of string list
| REPS of int * int | ID of string | ACTION of string
| BOF | EOF | ASSIGN | SEMI | ARROW | LEXMARK | LEXSTATES |
COUNT | REJECT | FULLCHARSET | STRUCT | HEADER | ARG
datatype exp = EPS | CLASS of bool array * int | CLOSURE of exp
| ALT of exp * exp | CAT of exp * exp | TRAIL of int
| END of int
val CharFormat = ref false;
val UsesTrailingContext = ref false;
val UsesPrevNewLine = ref false;
val CountNewLines = ref false;
val HaveReject = ref false;
val CharSetSize = ref 129;
val StrName = ref "Mlex"
val HeaderCode = ref ""
val HeaderDecl = ref false
val ArgCode = ref (NONE: string option)
val StrDecl = ref false
val ResetFlags = fn () => (CountNewLines := false; HaveReject := false;
UsesTrailingContext := false;
CharSetSize := 129; StrName := "Mlex";
HeaderCode := ""; HeaderDecl:= false;
ArgCode := NONE;
StrDecl := false)
val LexOut = ref(TextIO.stdOut)
fun say x = TextIO.output(!LexOut, x)
fun union(a,b) = let val rec merge = fn
(nil,nil,z) => z
| (nil,el::more,z) => merge(nil,more,el::z)
| (el::more,nil,z) => merge(more,nil,el::z)
| (x::morex,y::morey,z) => if (x:int)=(y:int)
then merge(morex,morey,x::z)
else if x>y then merge(morex,y::morey,x::z)
else merge(x::morex,morey,y::z)
in merge(rev a,rev b,nil)
end
val rec nullable = fn
EPS => true
| CLASS(_) => false
| CLOSURE(_) => true
| ALT(n1,n2) => nullable(n1) orelse nullable(n2)
| CAT(n1,n2) => nullable(n1) andalso nullable(n2)
| TRAIL(_) => true
| END(_) => false
and firstpos = fn
EPS => nil
| CLASS(_,i) => [i]
| CLOSURE(n) => firstpos(n)
| ALT(n1,n2) => union(firstpos(n1),firstpos(n2))
| CAT(n1,n2) => if nullable(n1) then union(firstpos(n1),firstpos(n2))
else firstpos(n1)
| TRAIL(i) => [i]
| END(i) => [i]
and lastpos = fn
EPS => nil
| CLASS(_,i) => [i]
| CLOSURE(n) => lastpos(n)
| ALT(n1,n2) => union(lastpos(n1),lastpos(n2))
| CAT(n1,n2) => if nullable(n2) then union(lastpos(n1),lastpos(n2))
else lastpos(n2)
| TRAIL(i) => [i]
| END(i) => [i]
;
fun ++(x) : int = (x := !x + 1; !x);
structure dict =
struct
type 'a relation = 'a * 'a -> bool
abstype ('b,'a) dictionary = DATA of { Table : ('b * 'a) list,
Leq : 'b * 'b -> bool }
with
exception LOOKUP
fun create Leqfunc = DATA { Table = nil, Leq = Leqfunc }
fun lookup (DATA { Table = entrylist, Leq = leq }) key =
let fun search [] = raise LOOKUP
| search((k,item)::entries) =
if leq(key,k)
then if leq(k,key) then item else raise LOOKUP
else search entries
in search entrylist
end
fun enter (DATA { Table = entrylist, Leq = leq })
(newentry as (key : 'b,item :'a)) : ('b,'a) dictionary =
let val gt = fn a => fn b => not (leq(a,b))
val eq = fn k => fn k' => (leq(k,k')) andalso (leq(k',k))
fun update nil = [ newentry ]
| update ((entry as (k,_))::entries) =
if (eq key k) then newentry::entries
else if gt k key then newentry::(entry::entries)
else entry::(update entries)
in DATA { Table = update entrylist, Leq = leq }
end
fun listofdict (DATA { Table = entrylist,Leq = leq}) =
let fun f (nil,r) = rev r
| f (a::b,r) = f (b,a::r)
in f(entrylist,nil)
end
end
end
open dict;
val LineNum = ref 1;
abstype ibuf =
BUF of TextIO.instream * {b : string ref, p : int ref}
with
fun make_ibuf(s) = BUF (s, {b=ref"", p = ref 0})
fun close_ibuf (BUF (s,_)) = TextIO.closeIn(s)
exception eof
fun getch (a as (BUF(s,{b,p}))) =
if (!p = (size (!b)))
then (b := TextIO.inputN(s,1024);
p := 0;
if (size (!b))=0
then raise eof
else getch a)
else (let val ch = String.sub(!b,!p)
in (if ch = #"\n"
then LineNum := !LineNum + 1
else ();
p := !p + 1;
ch)
end)
fun ungetch(BUF(s,{b,p})) = (
p := !p - 1;
if String.sub(!b,!p) = #"\n"
then LineNum := !LineNum - 1
else ())
end;
exception Error
fun prErr x = (
TextIO.output (TextIO.stdErr, String.concat [
"ml-lex: error, line ", (Int.toString (!LineNum)), ": ", x, "\n"
]);
raise Error)
fun prSynErr x = (
TextIO.output (TextIO.stdErr, String.concat [
"ml-lex: syntax error, line ", (Int.toString (!LineNum)), ": ", x, "\n"
]);
raise Error)
exception SyntaxError;
exception LexError;
val LexBuf = ref(make_ibuf(TextIO.stdIn));
val LexState = ref 0;
val NextTok = ref BOF;
val inquote = ref false;
fun AdvanceTok () : unit = let
fun isLetter c =
((c >= #"a") andalso (c <= #"z")) orelse
((c >= #"A") andalso (c <= #"Z"))
fun isDigit c = (c >= #"0") andalso (c <= #"9")
fun isIdentChr c =
((isLetter c) orelse (isDigit c) orelse (c = #"_") orelse (c = #"'"))
fun atoi s = let
fun num (c::r, n) = if isDigit c
then num (r, 10*n + (Char.ord c - Char.ord #"0"))
else n
| num ([], n) = n
in
num (explode s, 0)
end
fun skipws () = (case nextch()
of #" " => skipws()
| #"\t" => skipws()
| #"\n" => skipws()
| x => x
)
and nextch () = getch(!LexBuf)
and escaped () = (case nextch()
of #"b" => #"\008"
| #"n" => #"\n"
| #"t" => #"\t"
| #"h" => #"\128"
| x => let
fun err t = prErr("illegal ascii escape '"^(implode(rev t))^"'")
fun cvt c = (Char.ord c - Char.ord #"0")
fun f (n, c, t) = if c=3
then if n >= (!CharSetSize)
then err t
else Char.chr n
else let val ch=nextch()
in
if isDigit ch
then f(n*10+(cvt ch), c+1, ch::t)
else err t
end
in
if isDigit x then f(cvt x, 1, [x]) else x
end
)
and onechar x = let val c = array(!CharSetSize, false)
in
update(c, Char.ord(x), true); CHARS(c)
end
in case !LexState of 0 => let val makeTok = fn () =>
case skipws()
of #"%" => (case nextch() of
#"%" => LEXMARK
| a => let fun f s =
let val a = nextch()
in if isLetter a then f(a::s)
else (ungetch(!LexBuf);
implode(rev s))
end
val command = f [a]
in if command = "reject" then REJECT
else if command = "count" then COUNT
else if command = "full" then FULLCHARSET
else if command = "s" then LEXSTATES
else if command = "S" then LEXSTATES
else if command = "structure" then STRUCT
else if command = "header" then HEADER
else if command = "arg" then ARG
else prErr "unknown % operator "
end
)
| #";" => SEMI
| ch => if isLetter(ch) then
let fun getID matched =
let val x = nextch()
in if (isIdentChr x)
then getID (x::matched)
else (ungetch(!LexBuf); implode(rev matched))
end
in ID(getID [ch])
end
else (prSynErr ("bad character: " ^ String.str ch))
in NextTok := makeTok()
end
| 1 => let val rec makeTok = fn () =>
if !inquote then case nextch() of
#"\\" => onechar(escaped())
| #"\"" => (inquote := false; makeTok())
| x => onechar(x)
else case skipws() of
#"?" => QMARK
| #"*" => STAR
| #"+" => PLUS
| #"|" => BAR
| #"(" => LP
| #")" => RP
| #"^" => CARAT
| #"$" => DOLLAR
| #"/" => SLASH
| #";" => SEMI
| #"." => let val c = array(!CharSetSize,true) in
update(c,10,false); CHARS(c)
end
| #"=" => let val c = nextch() in
if c = #">" then ARROW else (ungetch(!LexBuf); ASSIGN)
end
| #"[" => let val rec classch = fn () => let val x = skipws()
in if x = #"\\" then escaped() else x
end;
val first = classch();
val flag = (first <> #"^");
val c = array(!CharSetSize,not flag);
fun add NONE = ()
| add (SOME x) = update(c, Char.ord(x), flag)
and range (x, y) = if x>y
then (prErr "bad char. range")
else let
val i = ref(Char.ord(x)) and j = Char.ord(y)
in while !i<=j do (
add (SOME(Char.chr(!i)));
i := !i + 1)
end
and getClass last = (case classch()
of #"]" => (add(last); c)
| #"-" => (case last
of NONE => getClass(SOME #"-")
| (SOME last') => let val x = classch()
in
if x = #"]"
then (add(last); add(SOME #"-"); c)
else (range(last',x); getClass(NONE))
end
)
| x => (add(last); getClass(SOME x))
)
in CHARS(getClass(if first = #"^" then NONE else SOME first))
end
| #"<" => let val rec get_state = fn (prev,matched) =>
case nextch() of
#">" => matched::prev
| #"," => get_state(matched::prev,"")
| x => if isIdentChr(x)
then get_state(prev,matched ^ String.str x)
else (prSynErr "bad start state list")
in STATE(get_state(nil,""))
end
| #"{" => let val ch = nextch() in if isLetter(ch) then
let fun getID matched = (case nextch()
of #"}" => matched
| x => if (isIdentChr x) then
getID(matched ^ String.str x)
else (prErr "invalid char. class name")
)
in ID(getID(String.str ch))
end
else if isDigit(ch) then
let fun get_r (matched, r1) = (case nextch()
of #"}" => let val n = atoi(matched) in
if r1 = ~1 then (n,n) else (r1,n)
end
| #"," => if r1 = ~1 then get_r("",atoi(matched))
else (prErr "invalid repetitions spec.")
| x => if isDigit(x)
then get_r(matched ^ String.str x,r1)
else (prErr "invalid char in repetitions spec")
)
in REPS(get_r(String.str ch,~1))
end
else (prErr "bad repetitions spec")
end
| #"%" => if nextch() = #"%" then LEXMARK else
(ungetch(!LexBuf); onechar (#"%"))
| #"\\" => onechar(escaped())
| #"\"" => (inquote := true; makeTok())
| ch => onechar(ch)
in NextTok := makeTok()
end
| 2 => NextTok :=
(case skipws()
of #"(" => let
fun GetAct (lpct,x) = (case getch(!LexBuf)
of #"(" => GetAct (lpct+1, #"("::x)
| #")" => if lpct = 0 then (implode (rev x))
else GetAct(lpct-1, #")"::x)
| y => GetAct(lpct,y::x)
)
in ACTION (GetAct (0,nil))
end
| #";" => SEMI
| c => (prSynErr ("invalid character " ^ String.str c)))
| _ => raise LexError
end
handle eof => NextTok := EOF ;
fun GetTok (_:unit) : token =
let val t = !NextTok in AdvanceTok(); t
end;
val SymTab = ref (create String.<=) : (string,exp) dictionary ref
fun GetExp () : exp =
let val rec optional = fn e => ALT(EPS,e)
and lookup' = fn name =>
lookup(!SymTab) name
handle LOOKUP => prErr ("bad regular expression name: "^
name)
and newline = fn () => let val c = array(!CharSetSize,false) in
update(c,10,true); c
end
and endline = fn e => trail(e,CLASS(newline(),0))
and trail = fn (e1,e2) => CAT(CAT(e1,TRAIL(0)),e2)
and closure1 = fn e => CAT(e,CLOSURE(e))
and repeat = fn (min,max,e) => let val rec rep = fn
(0,0) => EPS
| (0,1) => ALT(e,EPS)
| (0,i) => CAT(rep(0,1),rep(0,i-1))
| (i,j) => CAT(e,rep(i-1,j-1))
in rep(min,max)
end
and exp0 = fn () => case GetTok() of
CHARS(c) => exp1(CLASS(c,0))
| LP => let val e = exp0() in
if !NextTok = RP then
(AdvanceTok(); exp1(e))
else (prSynErr "missing '('") end
| ID(name) => exp1(lookup' name)
| _ => raise SyntaxError
and exp1 = fn (e) => case !NextTok of
SEMI => e
| ARROW => e
| EOF => e
| LP => exp2(e,exp0())
| RP => e
| t => (AdvanceTok(); case t of
QMARK => exp1(optional(e))
| STAR => exp1(CLOSURE(e))
| PLUS => exp1(closure1(e))
| CHARS(c) => exp2(e,CLASS(c,0))
| BAR => ALT(e,exp0())
| DOLLAR => (UsesTrailingContext := true; endline(e))
| SLASH => (UsesTrailingContext := true;
trail(e,exp0()))
| REPS(i,j) => exp1(repeat(i,j,e))
| ID(name) => exp2(e,lookup' name)
| _ => raise SyntaxError)
and exp2 = fn (e1,e2) => case !NextTok of
SEMI => CAT(e1,e2)
| ARROW => CAT(e1,e2)
| EOF => CAT(e1,e2)
| LP => exp2(CAT(e1,e2),exp0())
| RP => CAT(e1,e2)
| t => (AdvanceTok(); case t of
QMARK => exp1(CAT(e1,optional(e2)))
| STAR => exp1(CAT(e1,CLOSURE(e2)))
| PLUS => exp1(CAT(e1,closure1(e2)))
| CHARS(c) => exp2(CAT(e1,e2),CLASS(c,0))
| BAR => ALT(CAT(e1,e2),exp0())
| DOLLAR => (UsesTrailingContext := true;
endline(CAT(e1,e2)))
| SLASH => (UsesTrailingContext := true;
trail(CAT(e1,e2),exp0()))
| REPS(i,j) => exp1(CAT(e1,repeat(i,j,e2)))
| ID(name) => exp2(CAT(e1,e2),lookup' name)
| _ => raise SyntaxError)
in exp0()
end;
val StateTab = ref(create(String.<=)) : (string,int) dictionary ref
val StateNum = ref 0;
fun GetStates () : int list =
let fun add nil sl = sl
| add (x::y) sl = add y (union ([lookup (!StateTab)(x)
handle LOOKUP =>
prErr ("bad state name: "^x)
],sl))
fun addall i sl =
if i <= !StateNum then addall (i+2) (union ([i],sl))
else sl
fun incall (x::y) = (x+1)::incall y
| incall nil = nil
fun addincs nil = nil
| addincs (x::y) = x::(x+1)::addincs y
val state_list =
case !NextTok of
STATE s => (AdvanceTok(); LexState := 1; add s nil)
| _ => addall 1 nil
in case !NextTok
of CARAT => (LexState := 1; AdvanceTok(); UsesPrevNewLine := true;
incall state_list)
| _ => addincs state_list
end
val LeafNum = ref ~1;
fun renum(e : exp) : exp =
let val rec label = fn
EPS => EPS
| CLASS(x,_) => CLASS(x,++LeafNum)
| CLOSURE(e) => CLOSURE(label(e))
| ALT(e1,e2) => ALT(label(e1),label(e2))
| CAT(e1,e2) => CAT(label(e1),label(e2))
| TRAIL(i) => TRAIL(++LeafNum)
| END(i) => END(++LeafNum)
in label(e)
end;
exception ParseError;
fun parse() : (string * (int list * exp) list * ((string,string) dictionary)) =
let val Accept = ref (create String.<=) : (string,string) dictionary ref
val rec ParseRtns = fn l => case getch(!LexBuf) of
#"%" => let val c = getch(!LexBuf) in
if c = #"%" then (implode (rev l))
else ParseRtns(c :: #"%" :: l)
end
| c => ParseRtns(c::l)
and ParseDefs = fn () =>
(LexState:=0; AdvanceTok(); case !NextTok of
LEXMARK => ()
| LEXSTATES =>
let fun f () = (case !NextTok of (ID i) =>
(StateTab := enter(!StateTab)(i,++StateNum);
++StateNum; AdvanceTok(); f())
| _ => ())
in AdvanceTok(); f ();
if !NextTok=SEMI then ParseDefs() else
(prSynErr "expected ';'")
end
| ID x => (LexState:=1; AdvanceTok(); if GetTok() = ASSIGN
then (SymTab := enter(!SymTab)(x,GetExp());
if !NextTok = SEMI then ParseDefs()
else (prSynErr "expected ';'"))
else raise SyntaxError)
| REJECT => (HaveReject := true; ParseDefs())
| COUNT => (CountNewLines := true; ParseDefs())
| FULLCHARSET => (CharSetSize := 256; ParseDefs())
| HEADER => (LexState := 2; AdvanceTok();
case GetTok()
of ACTION s =>
if (!StrDecl) then
(prErr "cannot have both %s and %header \
				    \declarations")
else if (!HeaderDecl) then
(prErr "duplicate %header declarations")
else
(HeaderCode := s; LexState := 0;
HeaderDecl := true; ParseDefs())
| _ => raise SyntaxError)
| ARG => (LexState := 2; AdvanceTok();
case GetTok()
of ACTION s =>
(case !ArgCode
of SOME _ => prErr "duplicate %arg declarations"
| NONE => ArgCode := SOME s;
LexState := 0;
ParseDefs())
| _ => raise SyntaxError)
| STRUCT => (AdvanceTok();
case !NextTok of
(ID i) =>
if (!HeaderDecl) then
(prErr "cannot have both %s and %header \
				    \declarations")
else if (!StrDecl) then
(prErr "duplicate %s declarations")
else StrName := i
| _ => (prErr "expected ID");
ParseDefs())
| _ => raise SyntaxError)
and ParseRules =
fn rules => (LexState:=1; AdvanceTok(); case !NextTok of
LEXMARK => rules
| EOF => rules
| _ =>
let val s = GetStates()
val e = renum(CAT(GetExp(),END(0)))
in
if !NextTok = ARROW then
(LexState:=2; AdvanceTok();
case GetTok() of ACTION(act) =>
if !NextTok=SEMI then
(Accept:=enter(!Accept) (Int.toString (!LeafNum),act);
ParseRules((s,e)::rules))
else (prSynErr "expected ';'")
| _ => raise SyntaxError)
else (prSynErr "expected '=>'")
end)
in let val usercode = ParseRtns nil
in (ParseDefs(); (usercode,ParseRules(nil),!Accept))
end
end handle SyntaxError => (prSynErr "")
fun makebegin () : unit =
let fun make nil = ()
| make ((x,n:int)::y)=(say "val "; say x; say " = " ;
say "STARTSTATE ";
say (Int.toString n); say ";\n"; make y)
in say "\n(* start state definitions *)\n\n"; make(listofdict(!StateTab))
end
structure L =
struct
nonfix >
type key = int list * string
fun > ((key,item:string),(key',item')) =
let fun f ((a:int)::a') (b::b') = if Int.> (a,b) then true
else if a=b then f a' b'
else false
| f _ _ = false
in f key key'
end
end
structure RB = RedBlack(L)
fun maketable (fins:(int * (int list)) list,
tcs :(int * (int list)) list,
tcpairs: (int * int) list,
trans : (int*(int list)) list) : unit =
let datatype elem = N of int | T of int | D of int
val count = ref 0
val _ = (if length(trans)<256 then CharFormat := true
else CharFormat := false;
if !UsesTrailingContext then
(say "\ndatatype yyfinstate = N of int | \
			   \ T of int | D of int\n")
else say "\ndatatype yyfinstate = N of int";
say "\ntype statedata = {fin : yyfinstate list, trans: ";
case !CharFormat of
true => say "string}"
| false => say "int Vector.vector}";
say "\n(* transition & final state table *)\nval tab = let\n";
case !CharFormat of
true => ()
| false =>
(say "fun decode s k =\n";
say "  let val k' = k + k\n";
say "      val hi = Char.ord(String.sub(s, k'))\n";
say "      val lo = Char.ord(String.sub(s, k' + 1))\n";
say "  in hi * 256 + lo end\n"))
val newfins =
let fun IsEndLeaf t =
let fun f ((l,e)::r) = if (e=t) then true else f r
| f nil = false in f tcpairs end
fun GetEndLeaf t =
let fun f ((tl,el)::r) = if (tl=t) then el else f r
| f [] = raise General.Fail "Impossible"
in f tcpairs
end
fun GetTrConLeaves s =
let fun f ((s',l)::r) = if (s = s') then l else f r
| f nil = nil
in f tcs
end
fun sort_leaves s =
let fun insert (x:int) (a::b) =
if (x <= a) then x::(a::b)
else a::(insert x b)
| insert x nil = [x]
in List.foldr (fn (x,r) => insert x r) [] s
end
fun conv a = if (IsEndLeaf a) then (D a) else (N a)
fun merge (a::a',b::b') =
if (a <= b) then (conv a)::merge(a',b::b')
else (T b)::(merge(a::a',b'))
| merge (a::a',nil) = (conv a)::(merge (a',nil))
| merge (nil,b::b') = (T b)::(merge (b',nil))
| merge (nil,nil) = nil
in map (fn (x,l) =>
rev (merge (l,
sort_leaves (map (fn x => GetEndLeaf x) (GetTrConLeaves x)))))
fins
end
val rs =
let open RB
fun makeItems x =
let fun emit8(x, pos) =
let val d1 = Int.quot(x,100)
val r1 = x - d1 * 100
val d2 = Int.quot(r1,10)
val d3 = r1 - d2 * 10
val s = implode [
Char.chr(d1 + 48), Char.chr(d2 + 48),
Char.chr(d3 + 48)
]
in
case pos
of 16 => (say "\\\n\\\\"; say s; 1)
| _ => (say "\\"; say s; pos+1)
end
fun emit16(x, pos) =
let val hi8 = Int.quot(x,256)
val lo8 = x - hi8 * 256
in
emit8(lo8, emit8(hi8, pos))
end
fun makestring([], _, _) = ()
| makestring(x::xs, emitter, pos) =
makestring(xs, emitter, emitter(x, pos))
in case !CharFormat of
true => (say " =\n\""; makestring(x,emit8,0); say "\"\n")
| false => (say " = Vector.tabulate("; say (Int.toString(length x));
say ", decode\n\""; makestring(x,emit16,0); say "\")\n")
end
fun makeEntry(nil,rs,t) = rev rs
| makeEntry(((l:int,x)::y),rs,t) =
let val name = "s" ^ (Int.toString l)
in let val (r,n) = lookup ((x,name),t)
in makeEntry(y,(n::rs),t)
end handle notfound _ => (count := !count+1;
say "val "; say name; makeItems x;
makeEntry(y,(name::rs),(insert ((x,name),t))))
end
in (makeEntry(trans,nil,empty))
end
fun makeTable(nil,nil) = ()
| makeTable(a::a',b::b') =
let fun makeItems nil = ()
| makeItems (hd::tl) =
let val (t,n) =
case hd of
(N i) => ("(N ",i)
| (T i) => ("(T ",i)
| (D i) => ("(D ",i)
in (say t; say (Int.toString n); say ")";
if null tl
then ()
else (say ","; makeItems tl))
end
in (say "{fin = ["; makeItems b;
say "], trans = "; say a; say "}";
if null a'
then ()
else (say ",\n"; makeTable(a',b')))
end
| makeTable _ = raise General.Fail "Impossible"
fun msg x = TextIO.output(TextIO.stdOut, x)
in (say "in Vector.fromList\n["; makeTable(rs,newfins); say "]\nend\n";
msg ("\nNumber of states = " ^ (Int.toString (length trans)));
msg ("\nNumber of distinct rows = " ^ (Int.toString (!count)));
msg ("\nApprox. memory size of trans. table = " ^
(Int.toString (!count*(!CharSetSize)*(if !CharFormat then 1 else 8))));
msg " bytes\n")
end
fun makeaccept ends =
let fun startline f = if f then say "  " else say "| "
fun make(nil,f) = (startline f; say "_ => raise Internal.LexerError\n")
| make((x,a)::y,f) = (startline f; say x; say " => (";
say a; say ")\n"; make(y,false))
in make (listofdict(ends),true)
end
fun leafdata(e:(int list * exp) list) =
let val fp = array(!LeafNum + 1,nil)
and leaf = array(!LeafNum + 1,EPS)
and tcpairs = ref nil
and trailmark = ref ~1;
val rec add = fn
(nil,x) => ()
| (hd::tl,x) => (update(fp,hd,union(fp sub hd,x));
add(tl,x))
and moredata = fn
CLOSURE(e1) =>
(moredata(e1); add(lastpos(e1),firstpos(e1)))
| ALT(e1,e2) => (moredata(e1); moredata(e2))
| CAT(e1,e2) => (moredata(e1); moredata(e2);
add(lastpos(e1),firstpos(e2)))
| CLASS(x,i) => update(leaf,i,CLASS(x,i))
| TRAIL(i) => (update(leaf,i,TRAIL(i)); if !trailmark = ~1
then trailmark := i else ())
| END(i) => (update(leaf,i,END(i)); if !trailmark <> ~1
then (tcpairs := (!trailmark,i)::(!tcpairs);
trailmark := ~1) else ())
| _ => ()
and makedata = fn
nil => ()
| (_,x)::tl => (moredata(x);makedata(tl))
in trailmark := ~1; makedata(e); (fp,leaf,!tcpairs)
end;
fun makedfa(rules) =
let val StateTab = ref (create(String.<=)) : (string,int) dictionary ref
val fintab = ref (create(Int.<=)) : (int,(int list)) dictionary ref
val transtab = ref (create(Int.<=)) : (int,int list) dictionary ref
val tctab = ref (create(Int.<=)) : (int,(int list)) dictionary ref
val (fp, leaf, tcpairs) = leafdata(rules);
fun visit (state,statenum) =
let val transitions = gettrans(state) in
fintab := enter(!fintab)(statenum,getfin(state));
tctab := enter(!tctab)(statenum,gettc(state));
transtab := enter(!transtab)(statenum,transitions)
end
and visitstarts (states) =
let fun vs nil i = ()
| vs (hd::tl) i = (visit (hd,i); vs tl (i+1))
in vs states 0
end
and hashstate(s: int list) =
let val rec hs =
fn (nil,z) => z
| ((x:int)::y,z) => hs(y,z ^ " " ^ (Int.toString x))
in hs(s,"")
end
and find(s) = lookup(!StateTab)(hashstate(s))
and add(s,n) = StateTab := enter(!StateTab)(hashstate(s),n)
and getstate (state) =
find(state)
handle LOOKUP => let val n = ++StateNum in
add(state,n); visit(state,n); n
end
and getfin state =
let fun f nil fins = fins
| f (hd::tl) fins =
case (leaf sub hd)
of END _ => f tl (hd::fins)
| _ => f tl fins
in f state nil
end
and gettc state =
let fun f nil fins = fins
| f (hd::tl) fins =
case (leaf sub hd)
of TRAIL _ => f tl (hd::fins)
| _ => f tl fins
in f state nil
end
and gettrans (state) =
let fun loop c tlist =
let fun cktrans nil r = r
| cktrans (hd::tl) r =
case (leaf sub hd) of
CLASS(i,_)=>
(if (i sub c) then cktrans tl (union(r,fp sub hd))
else cktrans tl r handle Subscript =>
cktrans tl r
)
| _ => cktrans tl r
in if c >= 0 then
let val v=cktrans state nil
in loop (c-1) (if v=nil then 0::tlist else (getstate v)::tlist)
end
else tlist
end
in loop ((!CharSetSize) - 1) nil
end
and startstates() =
let val startarray = array(!StateNum + 1, nil);
fun listofarray(a,n) =
let fun f i l = if i >= 0 then f (i-1) ((a sub i)::l) else l
in f (n-1) nil end
val rec makess = fn
nil => ()
| (startlist,e)::tl => (fix(startlist,firstpos(e));makess(tl))
and fix = fn
(nil,_) => ()
| (s::tl,firsts) => (update(startarray,s,
union(firsts,startarray sub s));
fix(tl,firsts))
in makess(rules);listofarray(startarray, !StateNum + 1)
end
in visitstarts(startstates());
(listofdict(!fintab),listofdict(!transtab),listofdict(!tctab),tcpairs)
end
val skel_hd =
"   struct\n\
\    structure UserDeclarations =\n\
\      struct\n\
\"

val skel_mid2 =
" | Internal.D k => action (i,(acts::l),k::rs)\n\
\ | Internal.T k =>\n\
\ let fun f (a::b,r) =\n\
\ if a=k\n\
\ then action(i,(((Internal.N a)::acts)::l),(b@r))\n\
\ else f (b,a::r)\n\
\ | f (nil,r) = action(i,(acts::l),rs)\n\
\ in f (rs,nil)\n\
\ end\n\
\"

fun lexGen(infile) =
    let 
      val {dir,file} = OS.Path.splitDirFile infile
      val file = implode (map (fn c => if c= #"." then #"_" else c)
                          (explode file)) ^ ".sml"
      val outfile = OS.Path.joinDirFile {dir=dir,file=file}
      fun PrintLexer (ends) =
    let val sayln = fn x => (say x; say "\n")
     in case !ArgCode 
	 of NONE => (sayln "fun lex () : Internal.result =";
		     sayln "let fun continue() = lex() in")
	  | SOME s => (say "fun lex "; say "(yyarg as ("; say s; sayln ")) =";
		       sayln "let fun continue() : Internal.result = ");
	 say " let fun scan (s,AcceptingLeaves : Internal.yyfinstate";
	 sayln " list list,l,i0) =";
	 if !UsesTrailingContext
	     then say "\tlet fun action (i,nil,rs)"
	     else say "\tlet fun action (i,nil)";
	 sayln " = raise LexError";
	 if !UsesTrailingContext
	     then sayln "\t| action (i,nil::l,rs) = action(i-1,l,rs)"
	     else sayln "\t| action (i,nil::l) = action (i-1,l)";
	 if !UsesTrailingContext
	     then sayln "\t| action (i,(node::acts)::l,rs) ="
	     else sayln "\t| action (i,(node::acts)::l) =";
	 sayln "\t\tcase node of";
	 sayln "\t\t Internal.N yyk => ";
	 sayln "\t\t\t(let val yytext = substring(!yyb,i0,i-i0)\n\
\\t\t\t val yypos = i0+ !yygone";
	 if !CountNewLines 
	    then (sayln "\t\t\tval _ = yylineno := CharVector.foldl";
	  	  sayln "\t\t\t\t(fn (#\"\\n\", n) => n+1 | (_, n) => n) 0 yytext")
else ();
if !HaveReject
then (say "\t\t\tfun REJECT() = action(i,acts::l";
if !UsesTrailingContext
then sayln ",rs)" else sayln ")")
else ();
sayln "\t\t\topen UserDeclarations Internal.StartStates";
sayln " in (yybufpos := i; case yyk of ";
sayln "";
sayln "\t\t\t(* Application actions *)\n";
makeaccept(ends);
say "\n\t\t) end ";
say ")\n\n";
if (!UsesTrailingContext) then say skel_mid2 else ();
sayln "\tval {fin,trans} = Vector.sub(Internal.tab, s)";
sayln "\tval NewAcceptingLeaves = fin::AcceptingLeaves";
sayln "\tin if l = !yybl then";
sayln "\t     if trans = #trans(Vector.sub(Internal.tab,0))";
sayln "\t       then action(l,NewAcceptingLeaves";
if !UsesTrailingContext then say ",nil" else ();
say ") else";
sayln "\t    let val newchars= if !yydone then \"\" else yyinput 1024";
sayln "\t    in if (size newchars)=0";
sayln "\t\t  then (yydone := true;";
say "\t\t        if (l=i0) then UserDeclarations.eof ";
sayln (case !ArgCode of NONE => "()" | SOME _ => "yyarg");
say "\t\t                  else action(l,NewAcceptingLeaves";
if !UsesTrailingContext then
sayln ",nil))" else sayln "))";
sayln "\t\t  else (if i0=l then yyb := newchars";
sayln "\t\t     else yyb := substring(!yyb,i0,l-i0)^newchars;";
sayln "\t\t     yygone := !yygone+i0;";
sayln "\t\t     yybl := size (!yyb);";
sayln "\t\t     scan (s,AcceptingLeaves,l-i0,0))";
sayln "\t    end";
sayln "\t  else let val NewChar = Char.ord(String.sub(!yyb,l))";
say "\t\tval NewState = ";
case (!CharFormat,!CharSetSize)
of (true,129) => sayln "if NewChar<128 then Char.ord(String.sub(trans,NewChar)) else Char.ord(String.sub(trans,128))"
| (true,256) => sayln "Char.ord(String.sub(trans,NewChar))"
| (false,129) => sayln "if NewChar<128 then Vector.sub(trans, NewChar) else Vector.sub(trans, 128)"
| (false,256) => sayln "Vector.sub(trans, NewChar)"
| _ => raise General.Fail "Impossible";
say "\t\tin if NewState=0 then action(l,NewAcceptingLeaves";
if !UsesTrailingContext then sayln ",nil)" else sayln ")";
sayln "\t\telse scan(NewState,NewAcceptingLeaves,l+1,i0)";
sayln "\tend";
sayln "\tend";
if !UsesPrevNewLine then () else sayln "(*";
sayln "\tval start= if substring(!yyb,!yybufpos-1,1)=\"\\n\"";
sayln "then !yybegin+1 else !yybegin";
if !UsesPrevNewLine then () else sayln "*)";
say "\tin scan(";
if !UsesPrevNewLine then say "start"
else say "!yybegin (* start *)";
sayln ",nil,!yybufpos,!yybufpos)";
sayln "    end";
sayln (case !ArgCode of NONE => "end" | SOME _ => "in continue end");
sayln "  in lex";
sayln "  end";
sayln "end"
end
in (UsesPrevNewLine := false;
ResetFlags();
LexBuf := make_ibuf(TextIO.openIn infile);
NextTok := BOF;
inquote := false;
LexOut := TextIO.openOut(outfile);
StateNum := 2;
LineNum := 1;
StateTab := enter(create(String.<=))("INITIAL",1);
LeafNum := ~1;
let
val (user_code,rules,ends) =
parse() handle x =>
(close_ibuf(!LexBuf);
TextIO.closeOut(!LexOut);
raise x)
val (user_requires,user_code) = stripRequires user_code;
val (fins,trans,tctab,tcpairs) = makedfa(rules)
val _ = if !UsesTrailingContext then
(close_ibuf(!LexBuf);
TextIO.closeOut(!LexOut);
prErr "lookahead is unimplemented")
else ()
in
say "(* MLWorks specific additions *)\n";
say "require \"$.basis.__vector\";\n";
say "require \"$.basis.__char\";\n";
say "require \"$.basis.__string\";\n";
say (user_requires^"\n");
say "(* Normal code *)\n";
if (!HeaderDecl)
then say (!HeaderCode)
else say ("structure " ^ (!StrName));
say "=\n";
say skel_hd;
say user_code;
say "end (* end of user routines *)\n";
say "exception LexError (* raised if illegal leaf ";
say "action tried *)\n";
say "structure Internal =\n\tstruct\n";
maketable(fins,tctab,tcpairs,trans);
say "structure StartStates =\n\tstruct\n";
say "\tdatatype yystartstate = STARTSTATE of int\n";
makebegin();
say "\nend\n";
say "type result = UserDeclarations.lexresult\n";
say "\texception LexerError (* raised if illegal leaf ";
say "action tried *)\n";
say "end\n\n";
if !CountNewLines then say "val yylineno = ref 0\n\n" else ();
say "fun makeLexer yyinput = \n";
say "let \n";
say "\tval yyb = ref \"\\n\" \t\t(* buffer *)\n\
	  \\tval yybl = ref 1\t\t(*buffer length *)\n\
	  \\tval yybufpos = ref 1\t\t(* location of next character to use *)\n\
	  \\tval yygone = ref 1\t\t(* position in file of beginning of buffer *)\n\
	  \\tval yydone = ref false\t\t(* eof found yet? *)\n\
	  \\tval yybegin = ref 1\t\t(*Current 'start state' for lexer *)\n\
  	  \\n\tval YYBEGIN = fn (Internal.StartStates.STARTSTATE x) =>\n\
	  \\t\t yybegin := x\n\n";
PrintLexer(ends);
close_ibuf(!LexBuf);
TextIO.closeOut(!LexOut)
end)
end
handle IO.Io{name,function,cause=OS.SysErr(s,NONE)} =>
print ("Error: "^s^"\n")
| IO.Io{name,function,cause=OS.SysErr(s,SOME e)} =>
print ("Error: "^(OS.errorMsg e)^"\n")
end
;
