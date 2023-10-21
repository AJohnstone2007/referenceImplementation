require "^.basis.__list";
require "custom";
functor CustomEditor () : CUSTOM_EDITOR =
struct
val emptyEntry = ("",[])
type dBentry = (string * string list)
val editorsOneWay : (string * string) list ref = ref []
val editorsTwoWay : (string * dBentry) list ref = ref []
fun split P lst =
let fun sp (a::l,r) =
if P(a) then (SOME(a), List.revAppend (r,l)) else sp(l,a::r)
| sp ([],_) = (NONE,lst)
in
sp(lst,[])
end
fun findFirst P lst =
let fun fF (a::l) =
if P(a) then SOME(a) else fF(l)
| fF ([]) = NONE
in
fF(lst)
end
fun names edlist () = map (fn (s,_) => s) (!edlist)
fun removeCommand ed =
let
val (itm,rest) = split (fn (s,_) => (s = ed)) (!editorsOneWay)
val result =
case itm of
NONE => ""
| SOME (_,r) => r
in
editorsOneWay := rest;
result
end
fun removeDialog ed =
let
val (itm,rest) = split (fn (s,_) => (s = ed)) (!editorsTwoWay)
val result =
case itm of
NONE => emptyEntry
| SOME((_,r)) => r
in
editorsTwoWay := rest;
result
end
fun getCommandEntry ed =
let
val itm = findFirst (fn (s,_) => (s = ed)) (!editorsOneWay)
in
case itm of
NONE => ""
| SOME (_,r) => r
end
fun getDialogEntry ed =
let
val itm = findFirst (fn (s,_) => (s = ed)) (!editorsTwoWay)
in
case itm of
NONE => emptyEntry
| SOME (_,r) => r
end
fun addCommand (ed,cmd) =
let
val (itm,rest) = split (fn (s,_) => (s = ed)) (!editorsOneWay)
val new_itm = (ed,cmd)
in
editorsOneWay := new_itm :: rest
end
fun addConnectDialog (ed,con_type,cmds) =
let
val (itm,rest) = split (fn (s,_) => (s = ed)) (!editorsTwoWay)
val new_itm = (ed,(con_type,cmds))
in
editorsTwoWay := (new_itm :: rest)
end
val commandNames = names editorsOneWay
val dialogNames = names editorsTwoWay
end;
