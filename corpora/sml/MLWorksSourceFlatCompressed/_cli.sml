require "parser";
require "stream";
require "lexer";
require "code";
require "scheduler";
require "dynamics";
require "tracer";
require "interpreter";
require "cli";
functor Cli (structure Parser: PARSER
structure Stream: STREAM
structure Lexer: LEXER
structure Code: CODE
structure Scheduler: SCHEDULER
structure Interpreter: INTERPRETER
structure Dynamics: DYNAMICS
structure Tracer: TRACER
sharing type Code.agent = Scheduler.agent
and type Parser.token = Lexer.token
and type Parser.agent = Scheduler.agent
and type Parser.agent = Code.agent
and type Parser.object = Scheduler.object
and type Parser.object = Code.object
and type Dynamics.agent = Code.agent
and type Dynamics.object = Code.object
and type Dynamics.context = Scheduler.context
and type Code.word = Scheduler.word
and type Code.word = Tracer.word): CLI =
struct
local
open Lexer
open Code
open Scheduler
open Parser
open Interpreter
open Dynamics
in
local
val PROMPT = "\nJo> "
val PROMPT2 = "  > "
val PROMPT3 = "  ] "
val WELCOME = "WELCOME to Jo: version 2.0, July 1996.\nNote that select and query symbols have been swapped)\n"
val useTracer = ref false
fun absorbCR() =
let val ns = ref (Stream.nextSymbol(true))
in (while (!ns="\n") do ns:=(Stream.nextSymbol(true));
Stream.putSymbol(!ns);
Stream.putSymbol("\n"))
end handle Stream.Eof => ()
fun error(mesg) =
(print ("Woa! "^mesg^"  Skipping to full-stop.\n\n");
while nextToken()<>STOP do ())
fun printSolutions(cntxt,numvars,varlist) =
let
val sofar = ref 0
val vars = ref varlist
fun printVar(varname,num,cntxt) =
let
val obj = objectToWords(instantiate(!(valOf(cntxt,num)),cntxt))
in
(print (varname^"=");
Tracer.plainPrint(obj,(1+(size varname))))
end
in
while (!sofar)<numvars do
(let val (a,b) = (fn (a::b) => (a,b)
| nil => raise Fail "Impossible case CLI")
(!vars)
in (printVar(a,!sofar,cntxt);
sofar:=(!sofar)+1;
vars:=b)
end)
end
fun printSuspensions(nil) = print ("\nmaybe. \n")
| printSuspensions(a::b) =
let
fun printQlist(nil) = print "\n"
| printQlist(a::b) =
(Tracer.plainPrint(processToWords(a),2);
printQlist(b))
in
(print "**";
printQlist(a);
printSuspensions(b))
end
fun listClause(clause(name,args,_,_,body)) =
let
val headwords = (characters name)::
(openParen "(")::
objectListToWords(args)@
[closeParen ")"]
in
(Tracer.plainPrint(headwords,0);
print " is ";
Tracer.prettyPrint(agentToWords(body)@[terminator "."],4))
end
fun listPred(nil) = print "\n"
| listPred(a::b) = (listClause a;
listPred(b))
fun readFile() =
case nextToken() of
STOP => (ignore(parse("",1,PROMPT,PROMPT3,true,false));
print "\nloaded OK.\n")
| ATOM x => (
case nextToken() of
STOP => (ignore(parse(x,1024,"","",false,false));
print "\nloaded OK.\n";
flushStdIn();
openFile("",1,"","",false))
| X => error("Expected full-stop!")
)
| X => error("Expected filename.")
fun listCode() =
case nextToken() of
STOP => let val proggy = Code.retrieveAll()
in
(app listPred proggy; ())
end
| ATOM a => if nextToken()=STOP then listPred(Code.retrieve(a))
else error("Expected a full-stop.")
| _ => error("Expected a full-stop.")
fun wipeCode() =
case nextToken() of
STOP => Code.wipeAll()
| ATOM a => Code.wipe(a)
| _ => error("Expected a full stop.")
fun switchTrace(newvalue) =
case nextToken() of
STOP => useTracer:=newvalue
| _ => error("Expected a full stop.")
fun obey(command,continue) =
case command of
"load" => (readFile();absorbCR() )
| "bye" => if nextToken() = STOP then
(print "\nHALT.\n"; continue:=false) else
(error("Expected full-stop."))
| "list" => (listCode();absorbCR())
| "wipe" => (wipeCode();absorbCR())
| "trace" => (switchTrace(true);
print "\nTrace Switched ON.\n";
absorbCR())
| "def" => (ignore(parse("",1,PROMPT,PROMPT3,true,true));
absorbCR())
| "notrace" => (switchTrace(false);
print "\nTrace Switched OFF.\n";
absorbCR())
| X => error("Unknown Command: "^X^".")
fun go(0,failure,_) = print "\nnot understood.\n"
| go(numvars,a,varlist) =
let
val cntxt = Dynamics.buildCntxt(nil,numvars,nullCntxt(),
ref(!useTracer))
in
(if !useTracer then absorbCR() else ();
Scheduler.give(exec(a,cntxt,ref(!useTracer)));
Interpreter.interpret()
)
handle Interpreter.ProgramFailed _ =>
(printSolutions(cntxt,numvars,varlist);
print "\nno.\n")
| Code.NotFound procName =>
print ("Woa! Undefined constraint: "
^procName^".\n")
| Tracer.TraceAbort =>
(printSolutions(cntxt,numvars,varlist);
print "\nAborted.\n";
absorbCR())
| Scheduler.QueueEmpty =>
case suspended() of
nil => (printSolutions(cntxt,numvars,varlist);
print "\nyes.\n")
| s => (print "\n\n";
printSuspensions(s))
end
in
fun run() =
let val continue = ref true
in
(print WELCOME);
while (!continue) do
(print PROMPT;
openFile("",1,PROMPT,PROMPT2,false);
(case nextToken() of
COMMAND c => obey(c,continue)
| X => (putToken(X);
wipeSchedule();
deSuspend();
Dynamics.resetUnknownsCounter();
go(parseCLI());
if not(!useTracer) then absorbCR() else ())
)
handle RunError e => (print ("\nError! "^e^"\n");
absorbCR())
| NotFound n=> (print ("\nError! "^n^" Not found\n");
absorbCR())
| ParseError _ => (print "\nParse Error.\n";
absorbCR())
| MLWorks.Internal.Error.SysErr (e,_) =>
(print ("\nError! "^e^"\n");absorbCR())
| Fail message =>
(print ("\n Debug Info: "^message^"\n");
absorbCR())
)
end
end
end
end
;
