require "base";
require "stream";
require "lrtable";
require "$.basis.__text_io";
require "$.basis.__int";
require "$.basis.__list";
signature FIFO =
sig type 'a queue
val empty : 'a queue
exception Empty
val get : 'a queue -> 'a * 'a queue
val put : 'a * 'a queue -> 'a queue
end
structure LrParser :> LR_PARSER =
struct
structure LrTable = LrTable
structure Stream = Stream
structure Token : TOKEN =
struct
structure LrTable = LrTable
datatype ('a,'b) token = TOKEN of LrTable.term * ('a * 'b * 'b)
val sameToken = fn (TOKEN(t,_),TOKEN(t',_)) => t=t'
end
open LrTable
open Token
val DEBUG1 = false
val DEBUG2 = false
exception ParseError
exception ParseImpossible of int
structure Fifo :> FIFO =
struct
type 'a queue = ('a list * 'a list)
val empty = (nil,nil)
exception Empty
fun get(a::x, y) = (a, (x,y))
| get(nil, nil) = raise Empty
| get(nil, y) = get(rev y, nil)
fun put(a,(x,y)) = (x,a::y)
end
type ('a,'b) elem = (state * ('a * 'b * 'b))
type ('a,'b) stack = ('a,'b) elem list
type ('a,'b) lexv = ('a,'b) token
type ('a,'b) lexpair = ('a,'b) lexv * (('a,'b) lexv Stream.stream)
type ('a,'b) distanceParse =
('a,'b) lexpair *
('a,'b) stack *
(('a,'b) stack * ('a,'b) lexpair) Fifo.queue *
int ->
('a,'b) lexpair *
('a,'b) stack *
(('a,'b) stack * ('a,'b) lexpair) Fifo.queue *
int *
action option
type ('a,'b) ecRecord =
{is_keyword : term -> bool,
preferred_change : (term list * term list) list,
error : string * 'b * 'b -> unit,
errtermvalue : term -> 'a,
terms : term list,
showTerminal : term -> string,
noShift : term -> bool}
local
val println = fn s => (print s; print "\n")
val showState = fn (STATE s) => "STATE " ^ (Int.toString s)
in
fun printStack(stack: ('a,'b) stack, n: int) =
case stack
of (state,_) :: rest =>
(print("\t" ^ Int.toString n ^ ": ");
println(showState state);
printStack(rest, n+1))
| nil => ()
fun prAction showTerminal
(stack as (state,_) :: _, next as (TOKEN (term,_),_), action) =
(println "Parse: state stack:";
printStack(stack, 0);
print("       state="
^ showState state
^ " next="
^ showTerminal term
^ " action="
);
case action
of SHIFT state => println ("SHIFT " ^ (showState state))
| REDUCE i => println ("REDUCE " ^ (Int.toString i))
| ERROR => println "ERROR"
| ACCEPT => println "ACCEPT")
| prAction _ (_,_,action) = ()
end
val ssParse =
fn (table,showTerminal,saction,fixError,arg) =>
let val prAction = prAction showTerminal
val action = LrTable.action table
val goto = LrTable.goto table
fun parseStep(args as
(lexPair as (TOKEN (terminal, value as (_,leftPos,_)),
lexer
),
stack as (state,_) :: _,
queue)) =
let val nextAction = action (state,terminal)
val _ = if DEBUG1 then prAction(stack,lexPair,nextAction)
else ()
in case nextAction
of SHIFT s =>
let val newStack = (s,value) :: stack
val newLexPair = Stream.get lexer
val (_,newQueue) =Fifo.get(Fifo.put((newStack,newLexPair),
queue))
in parseStep(newLexPair,(s,value)::stack,newQueue)
end
| REDUCE i =>
(case saction(i,leftPos,stack,arg)
of (nonterm,value,stack as (state,_) :: _) =>
parseStep(lexPair,(goto(state,nonterm),value)::stack,
queue)
| _ => raise (ParseImpossible 197))
| ERROR => parseStep(fixError args)
| ACCEPT =>
(case stack
of (_,(topvalue,_,_)) :: _ =>
let val (token,restLexer) = lexPair
in (topvalue,Stream.cons(token,restLexer))
end
| _ => raise (ParseImpossible 202))
end
| parseStep _ = raise (ParseImpossible 204)
in parseStep
end
val distanceParse =
fn (table,showTerminal,saction,arg) =>
let val prAction = prAction showTerminal
val action = LrTable.action table
val goto = LrTable.goto table
fun parseStep(lexPair,stack,queue,0) = (lexPair,stack,queue,0,NONE)
| parseStep(lexPair as (TOKEN (terminal, value as (_,leftPos,_)),
lexer
),
stack as (state,_) :: _,
queue,distance) =
let val nextAction = action(state,terminal)
val _ = if DEBUG1 then prAction(stack,lexPair,nextAction)
else ()
in case nextAction
of SHIFT s =>
let val newStack = (s,value) :: stack
val newLexPair = Stream.get lexer
in parseStep(newLexPair,(s,value)::stack,
Fifo.put((newStack,newLexPair),queue),distance-1)
end
| REDUCE i =>
(case saction(i,leftPos,stack,arg)
of (nonterm,value,stack as (state,_) :: _) =>
parseStep(lexPair,(goto(state,nonterm),value)::stack,
queue,distance)
| _ => raise (ParseImpossible 240))
| ERROR => (lexPair,stack,queue,distance,SOME nextAction)
| ACCEPT => (lexPair,stack,queue,distance,SOME nextAction)
end
| parseStep _ = raise (ParseImpossible 242)
in parseStep : ('_a,'_b) distanceParse
end
fun mkFixError({is_keyword,terms,errtermvalue,
preferred_change,noShift,
showTerminal,error,...} : ('_a,'_b) ecRecord,
distanceParse : ('_a,'_b) distanceParse,
minAdvance,maxAdvance)
(lexv as (TOKEN (term,value as (_,leftPos,_)),_),stack,queue) =
let val _ = if DEBUG2 then
error("syntax error found at " ^ (showTerminal term),
leftPos,leftPos)
else ()
fun tokAt(t,p) = TOKEN(t,(errtermvalue t,p,p))
val minDelta = 3
val stateList =
let fun f q = let val (elem,newQueue) = Fifo.get q
in elem :: (f newQueue)
end handle Fifo.Empty => nil
in f queue
end
val (_, numStateList) =
List.foldr (fn (a,(num,r)) => (num+1,(a,num)::r)) (0, []) stateList
datatype ('a,'b) change = CHANGE of
{pos : int, distance : int, leftPos: 'b, rightPos: 'b,
new : ('a,'b) lexv list, orig : ('a,'b) lexv list}
val showTerms = concat o map (fn TOKEN(t,_) => " " ^ showTerminal t)
val printChange = fn c =>
let val CHANGE {distance,new,orig,pos,...} = c
in (print ("{distance= " ^ (Int.toString distance));
print (",orig ="); print(showTerms orig);
print (",new ="); print(showTerms new);
print (",pos= " ^ (Int.toString pos));
print "}\n")
end
val printChangeList = app printChange
fun parse (lexPair,stack,queuePos : int) =
case distanceParse(lexPair,stack,Fifo.empty,queuePos+maxAdvance+1)
of (_,_,_,distance,SOME ACCEPT) =>
if maxAdvance-distance-1 >= 0
then maxAdvance
else maxAdvance-distance-1
| (_,_,_,distance,_) => maxAdvance - distance - 1
fun catList l f = List.foldr (fn(a,r)=> f a @ r) [] l
fun keywordsDelta new = if List.exists (fn(TOKEN(t,_))=>is_keyword t) new
then minDelta else 0
fun tryChange{lex,stack,pos,leftPos,rightPos,orig,new} =
let val lex' = List.foldr (fn (t',p)=>(t',Stream.cons p)) lex new
val distance = parse(lex',stack,pos+length new-length orig)
in if distance >= minAdvance + keywordsDelta new
then [CHANGE{pos=pos,leftPos=leftPos,rightPos=rightPos,
distance=distance,orig=orig,new=new}]
else []
end
fun tryDelete n ((stack,lexPair as (TOKEN(term,(_,l,r)),_)),qPos) =
let fun del(0,accum,left,right,lexPair) =
tryChange{lex=lexPair,stack=stack,
pos=qPos,leftPos=left,rightPos=right,
orig=rev accum, new=[]}
| del(n,accum,left,right,(tok as TOKEN(term,(_,_,r)),lexer)) =
if noShift term then []
else del(n-1,tok::accum,left,r,Stream.get lexer)
in del(n,[],l,r,lexPair)
end
fun tryInsert((stack,lexPair as (TOKEN(_,(_,l,_)),_)),queuePos) =
catList terms (fn t =>
tryChange{lex=lexPair,stack=stack,
pos=queuePos,orig=[],new=[tokAt(t,l)],
leftPos=l,rightPos=l})
fun trySubst ((stack,lexPair as (orig as TOKEN (term,(_,l,r)),lexer)),
queuePos) =
if noShift term then []
else
catList terms (fn t =>
tryChange{lex=Stream.get lexer,stack=stack,
pos=queuePos,
leftPos=l,rightPos=r,orig=[orig],
new=[tokAt(t,r)]})
fun do_delete(nil,lp as (TOKEN(_,(_,l,_)),_)) = SOME(nil,l,l,lp)
| do_delete([t],(tok as TOKEN(t',(_,l,r)),lp')) =
if t=t'
then SOME([tok],l,r,Stream.get lp')
else NONE
| do_delete(t::rest,(tok as TOKEN(t',(_,l,r)),lp')) =
if t=t'
then case do_delete(rest,Stream.get lp')
of SOME(deleted,l',r',lp'') =>
SOME(tok::deleted,l,r',lp'')
| NONE => NONE
else NONE
fun tryPreferred((stack,lexPair),queuePos) =
catList preferred_change (fn (delete,insert) =>
if List.exists noShift delete then []
else case do_delete(delete,lexPair)
of SOME(deleted,l,r,lp) =>
tryChange{lex=lp,stack=stack,pos=queuePos,
leftPos=l,rightPos=r,orig=deleted,
new=map (fn t=>(tokAt(t,r))) insert}
| NONE => [])
val changes = catList numStateList tryPreferred @
catList numStateList tryInsert @
catList numStateList trySubst @
catList numStateList (tryDelete 1) @
catList numStateList (tryDelete 2) @
catList numStateList (tryDelete 3)
val findMaxDist = fn l =>
foldr (fn (CHANGE {distance,...},high) => Int.max(distance,high)) 0 l
val maxDist = findMaxDist changes
val changes = catList changes
(fn(c as CHANGE{distance,...}) =>
if distance=maxDist then [c] else [])
in case changes
of (l as change :: _) =>
let fun print_msg (CHANGE {new,orig,leftPos,rightPos,...}) =
let val s =
case (orig,new)
of (_::_,[]) => "deleting " ^ (showTerms orig)
| ([],_::_) => "inserting " ^ (showTerms new)
| _ => "replacing " ^ (showTerms orig) ^
" with " ^ (showTerms new)
in error ("syntax error: " ^ s,leftPos,rightPos)
end
val _ =
(if length l > 1 andalso DEBUG2 then
(print "multiple fixes possible; could fix it by:\n";
app print_msg l;
print "chosen correction:\n")
else ();
print_msg change)
val findNth = fn n =>
let fun f (h::t,0) = (h,rev t)
| f (h::t,n) = f(t,n-1)
| f (nil,_) = let exception FindNth
in raise FindNth
end
in f (rev stateList,n)
end
val CHANGE {pos,orig,new,...} = change
val (last,queueFront) = findNth pos
val (stack,lexPair) = last
val lp1 = foldl(fn (_,(_,r)) => Stream.get r) lexPair orig
val lp2 = foldr(fn(t,r)=>(t,Stream.cons r)) lp1 new
val restQueue =
Fifo.put((stack,lp2),
foldl Fifo.put Fifo.empty queueFront)
val (lexPair,stack,queue,_,_) =
distanceParse(lp2,stack,restQueue,pos)
in (lexPair,stack,queue)
end
| nil => (error("syntax error found at " ^ (showTerminal term),
leftPos,leftPos); raise ParseError)
end
val parse = fn {arg,table,lexer,saction,void,lookahead,
ec=ec as {showTerminal,...} : ('_a,'_b) ecRecord} =>
let val distance = 15
val minAdvance = 1
val maxAdvance = Int.max(lookahead,0)
val lexPair = Stream.get lexer
val (TOKEN (_,(_,leftPos,_)),_) = lexPair
val startStack = [(initialState table,(void,leftPos,leftPos))]
val startQueue = Fifo.put((startStack,lexPair),Fifo.empty)
val distanceParse = distanceParse(table,showTerminal,saction,arg)
val fixError = mkFixError(ec,distanceParse,minAdvance,maxAdvance)
val ssParse = ssParse(table,showTerminal,saction,fixError,arg)
fun loop (lexPair,stack,queue,_,SOME ACCEPT) =
ssParse(lexPair,stack,queue)
| loop (lexPair,stack,queue,0,_) = ssParse(lexPair,stack,queue)
| loop (lexPair,stack,queue,distance,SOME ERROR) =
let val (lexPair,stack,queue) = fixError(lexPair,stack,queue)
in loop (distanceParse(lexPair,stack,queue,distance))
end
| loop _ = let exception ParseInternal
in raise ParseInternal
end
in loop (distanceParse(lexPair,startStack,startQueue,distance))
end
end;
