require "code";
require "stream";
require "tracer";
require "__lowlevel";
functor Tracer(structure Code: CODE
structure Stream: STREAM):TRACER =
struct
open Code;
open Stream;
exception TraceAbort
fun prettyPrint(wordList,firstIndent) =
let
exception StackErr
fun push(s,e) = s:=e::(!s)
fun top(ref(nil)) = raise StackErr
| top(ref(a::b)) = a
fun pop(ref(nil)) = raise StackErr
| pop(s as ref(a::b)) = s:=b
val printStack = ref []: int list ref
val wordStack = ref []: word list ref
val posn = ref firstIndent
val indent = ref firstIndent
val continue = ref true
val currComb = ref ""
val words = ref wordList
fun printTabs(0) = ()
| printTabs(n) = (print " ";
printTabs(n-1))
and pr x = (if ((!posn)+(size x)>80) andalso
((!indent)+(size x))<=80
then newLine()
else ();
print x;
posn:= !posn+(size x))
and newLine() = (print "\n";
posn:= !indent;
printTabs(!indent))
in
(while !continue do
case !words
of nil => continue:=false
| a::b => (words:=b;
case a
of combinator c
=> if !currComb=""
then (currComb:=c;
newLine();
pr(c^" "))
else if !currComb=c
then (newLine(); pr(c^" "))
else (push(printStack,!indent);
indent:= !indent + size(!currComb);
currComb:=c;
newLine(); pr(c^" "))
| terminator t => pr(t)
| separator s => pr(s)
| characters c => pr(c)
| openParen p => (push(printStack, !indent);
indent:= !posn;
pr(p))
| closeParen c => (pr(c);
indent:=top(printStack);
pop(printStack))
);
pr "\n")
end
fun plainPrint(wordList,indent) =
let
val posn = ref indent
val words = ref wordList
val continue = ref true
fun printTabs(0) = ()
| printTabs(n) = (print" ";
printTabs(n-1))
and pr x = (if (!posn)+(size x)>80
then newLine()
else ();
print x;
posn:= !posn+(size x))
and newLine() = (print "\n";
printTabs(indent);
posn:=indent)
in
(while !continue do
(case !words
of nil => continue:=false
| a::b => (words:=b;
case a
of combinator c => pr(" "^c^" ")
| terminator t => pr(t)
| separator s => pr(s)
| characters c => pr(c)
| openParen p => pr(p)
| closeParen c => pr(c)
)
);
pr "\n")
end
fun panel(traceProc) =
(print " ? ";
case nextSymbol(false)
of "q" => raise TraceAbort
| "i" => (ignore(nextSymbol(false)); traceProc:=false)
| "?" => (ignore(nextSymbol(false));
print "\n\tq --- quit program";
print "\n\ti --- ignore process (and children)";
print "\n\t? --- display this info.\n";
panel(traceProc))
| _ => ()
)
end
;
