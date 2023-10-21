Shell.Options.set (Shell.Options.Language.requireReservedWord,false);
local
datatype Path = ABS of string list| REL of string list
fun strip (#"\t" :: rest) = strip rest
| strip (#" " :: rest) = strip rest
| strip l = l
fun getrequire l =
let
val chars = explode l
fun scan (#"r" :: #"e" :: #"q" :: #"u" :: #"i" :: #"r" :: #"e" :: rest) =
scan2 (strip rest)
| scan _ = NONE
and scan2 (#"\""::rest) =
scan3 (rest,[])
| scan2 _ = NONE
and scan3 ([],chars) = NONE
| scan3 (#"\""::rest,acc) =
SOME (implode (rev acc))
| scan3 (a::rest,acc) = scan3 (rest,a::acc)
in
scan (strip chars)
end
fun get_requires f =
let
val s = TextIO.openIn f
fun doline acc =
let
val line = TextIO.inputLine s
in
if line = "" then rev acc
else case getrequire line of
SOME r => doline (r ::acc)
| _ => doline acc
end
val res = doline []
in
TextIO.closeIn s;
res
end
handle e => (print ("get_requires for " ^ f ^ " failed\n"); raise e)
fun parsename s =
let
val chars = explode s
fun doit ([],[],acc) = acc
| doit ([],l,acc) = doit ([],[],implode (rev l) :: acc)
| doit ([#".",#"s",#"m",#"l"],l,acc) = doit ([],[],implode (rev l) :: acc)
| doit (#"."::rest,l,acc) = doit (rest,[],implode (rev l) :: acc)
| doit (#"/"::rest,l,acc) = doit (rest,[],implode (rev l) :: acc)
| doit (a::rest,l,acc) = doit (rest,a::l,acc)
in
case chars of
#"." :: #"." :: #"/" :: rest =>
ABS (doit (rest,[],[]))
| #"^" :: #"." :: rest =>
ABS (doit (rest,[],[]))
| #"$" :: #"." :: rest =>
ABS (doit (rest,[],[]))
| _ => REL (doit (chars,[],[]))
end
fun toString [] = ""
| toString [s] = s
| toString (a::b) = toString b ^ "/" ^ a
fun resolve_path dir file =
case parsename file of
ABS s => s
| REL s => (s @ dir)
fun read_dependencies (here,file) =
let
val requires = get_requires file
in
map (resolve_path here) requires
end
fun member (a,[]) = false
| member (a,b::c) = a=b orelse member (a,c)
fun iterate (f : 'a -> unit) [] = ()
| iterate f (a::b) = (f a; iterate f b)
exception Cdr
fun cdr [] = raise Cdr
| cdr (a::b) = b
fun scan file =
let
val seen = ref []
val result = ref []
fun one f =
let
val path = toString f ^ ".sml"
in
let
val name = OS.FileSys.realPath path
in
if member (name,!seen) then ()
else
(seen := name :: !seen;
let
val sub = read_dependencies (cdr f,name)
in
iterate one sub;
result := name :: !result
end)
end
handle e => (print ("scan for " ^ path ^ " failed\n"); raise e)
end
in
one file;
rev (!result)
end
in
fun require _ = ()
fun make f =
case parsename f of
REL l => map use (scan l)
| ABS l => map use (scan l)
end
;
