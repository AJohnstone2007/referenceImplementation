require "sigs";
require "$.basis.__int";
functor mkVerbose(structure Errs : LR_ERRS) : VERBOSE =
struct
structure Errs = Errs
open Errs Errs.LrTable
val mkPrintAction = fn print =>
let val printInt = print o (Int.toString : int -> string)
in fn (SHIFT (STATE i)) =>
(ignore(print "\tshift ");
ignore(printInt i);
(print "\n"))
| (REDUCE rulenum) =>
(ignore(print "\treduce by rule ");
ignore(printInt rulenum);
print "\n")
| ACCEPT => print "\taccept\n"
| ERROR => print "\terror\n"
end
val mkPrintGoto = fn (printNonterm,print) =>
let val printInt = print o (Int.toString : int -> string)
in fn (nonterm,STATE i) =>
(ignore(print "\t");
ignore(printNonterm nonterm);
ignore(print "\tgoto ");
ignore(printInt i);
print "\n")
end
val mkPrintTermAction = fn (printTerm,print) =>
let val printAction = mkPrintAction print
in fn (term,action) =>
(ignore(print "\t");
ignore(printTerm term);
printAction action)
end
val mkPrintGoto = fn (printNonterm,print) =>
fn (nonterm,STATE i) =>
let val printInt = print o (Int.toString : int -> string)
in (ignore(print "\t");
ignore(printNonterm nonterm);
ignore(print "\tgoto ");
ignore(printInt i);
print "\n")
end
val mkPrintError = fn (printTerm,printRule,print) =>
let val printInt = print o (Int.toString : int -> string)
val printState = fn STATE s => (ignore(print " state "); printInt s)
in fn (RR (term,state,r1,r2)) =>
(ignore(print "error: ");
ignore(printState state);
ignore(print ": reduce/reduce conflict between rule ");
ignore(printInt r1);
ignore(print " and rule ");
ignore(printInt r2);
ignore(print " on ");
ignore(printTerm term);
print "\n")
| (SR (term,state,r1)) =>
(ignore(print "error: ");
ignore(printState state);
ignore(print ": shift/reduce conflict ");
ignore(print "(shift ");
ignore(printTerm term);
ignore(print ", reduce by rule ");
ignore(printInt r1);
print ")\n")
| NOT_REDUCED i =>
(ignore(print "warning: rule <");
ignore(printRule i);
print "> will never be reduced\n")
| START i =>
(ignore(print "warning: start symbol appears on the rhs of ");
ignore(print "<");
ignore(printRule i);
print ">\n")
| NS (term,i) =>
(ignore(print "warning: non-shiftable terminal ");
ignore(printTerm term);
ignore(print "appears on the rhs of ");
ignore(print "<");
ignore(printRule i);
print ">\n")
end
structure PairList : sig
val app : ('a * 'b -> unit) -> ('a,'b) pairlist -> unit
val length : ('a,'b) pairlist -> int
end
=
struct
val app = fn f =>
let fun g EMPTY = ()
| g (PAIR(a,b,r)) = (ignore(f(a,b)); g r)
in g
end
val length = fn l =>
let fun g(EMPTY,len) = len
| g(PAIR(_,_,r),len) = g(r,len+1)
in g(l,0)
end
end
val printVerbose =
fn {termToString,nontermToString,table,stateErrs,entries:int,
print,printRule,errs,printCores} =>
let
val printTerm = print o termToString
val printNonterm = print o nontermToString
val printCore = printCores print
val printTermAction = mkPrintTermAction(printTerm,print)
val printAction = mkPrintAction print
val printGoto = mkPrintGoto(printNonterm,print)
val printError = mkPrintError(printTerm,printRule print,print)
val gotos = LrTable.describeGoto table
val actions = LrTable.describeActions table
val states = numStates table
val gotoTableSize = ref 0
val actionTableSize = ref 0
val _ = if length errs > 0
then (printSummary print errs;
print "\n";
app printError errs)
else ()
fun loop i =
if i=states then ()
else let val s = STATE i
in (ignore(app printError (stateErrs s));
ignore(print "\n");
ignore(printCore s);
let val (actionList,default) = actions s
val gotoList = gotos s
in (PairList.app printTermAction actionList;
print "\n";
PairList.app printGoto gotoList;
print "\n";
print "\t.";
printAction default;
print "\n";
gotoTableSize:=(!gotoTableSize)+
PairList.length gotoList;
actionTableSize := (!actionTableSize) +
PairList.length actionList + 1
)
end;
loop (i+1))
end
in loop 0;
print (Int.toString entries ^ " of " ^
Int.toString (!actionTableSize)^
" action table entries left after compaction\n");
print (Int.toString (!gotoTableSize)^ " goto table entries\n")
end
end;
