require "^.basis.__list";
require "^.basis.__string";
require "^.basis.os_path";
require "^.basis.__char";
structure OSPath_ : OS_PATH =
struct
exception Path
exception InvalidArc
val dirSeparator = #"\\"
val extSeparator = #"."
val parentArc = ".."
val currentArc = "."
val volumeSeparator = #":"
fun isVolumePrefix s =
size s >= 2 andalso
String.sub(s,1) = volumeSeparator andalso
Char.isAlpha (String.sub(s,0))
fun validVolume {isAbs= isAbs, vol = vol} =
(vol = "") orelse
(size vol = 2 andalso isVolumePrefix vol)
fun invalidArc s = Char.contains s dirSeparator
fun fromString "" = {isAbs=false, vol="", arcs=[]}
| fromString s =
let
val (vol, s) =
if isVolumePrefix s then
(String.extract (s, 0, SOME 2), String.extract (s, 2, NONE))
else
("", s)
in
{isAbs=
size s > 0 andalso String.sub(s,0) = dirSeparator,
vol=vol,
arcs=
case String.fields (fn c=> c=dirSeparator) s of
""::xs => xs
| xs => xs
}
end
val sep = String.str dirSeparator
fun toString {isAbs, vol, arcs} =
if not (validVolume {isAbs=isAbs, vol=vol}) then
raise Path
else
case arcs of
[] => if isAbs then vol^sep else ""
| (arc::arcs) =>
let
val _ =
if invalidArc arc then
raise InvalidArc
else
()
val initialPath =
if isAbs then
concat [vol, sep, arc]
else if arc <> "" then
arc
else
raise Path
fun addArc (arc, path) =
if invalidArc arc then
raise InvalidArc
else
concat [path, sep, arc]
in
List.foldl addArc initialPath arcs
end
fun getVolume s =
if isVolumePrefix s then
substring(s, 0, 2)
else ""
fun getParent s =
let
val {isAbs, vol, arcs} = fromString s
fun scan1 [] = parentArc
| scan1 [""] = ""
| scan1 ["."] = parentArc
| scan1 [".."] = s^sep^parentArc
| scan1 [x] = currentArc
| scan1 [x, ""] = s^parentArc
| scan1 ["", x] = ""
| scan1 [x, y] = x
| scan1 (x::xs) = List.foldl (fn (arc, path)=> path^sep^arc)
x (scan2 ([],xs))
and scan2 (acc, []) = rev acc
| scan2 (acc, ["."]) = rev (parentArc::acc)
| scan2 (acc, [x, ""]) = rev (parentArc::x::acc)
| scan2 (acc, [x, y]) = rev (x::acc)
| scan2 (acc, x::xs) = scan2(x::acc, xs)
in
vol ^ (if isAbs then sep else "")^(scan1 arcs)
end
fun splitDirFile' (s, xmin, n, e) =
let
fun scan (n, e) =
if n < xmin then (xmin, xmin)
else if String.sub (s, n) = dirSeparator then
(if n = xmin then (n+1, n+1)
else
(n, n+1))
else
scan (n-1, e)
in
scan (n, e)
end
fun splitDirFile s =
let
val l = size s
val e = l - 1
val xmin = if isVolumePrefix s then 2 else 0
val (m, n) = splitDirFile' (s, xmin, l-1, l-1)
in
{ dir = substring (s, 0, m)
, file = substring (s, n, l-n)
}
end
fun joinDirFile {dir, file} =
let
val xmin = if isVolumePrefix dir then 2 else 0
val _ = if invalidArc file then raise InvalidArc else ()
in
if dir = "" then
file
else if size dir > xmin then
if String.sub(dir, size dir -1) = dirSeparator then
dir ^ file
else
dir ^ sep ^ file
else
dir ^ file
end
fun dir s =
let
val e = size s - 1
val xmin = if isVolumePrefix s then 2 else 0
val (m,_) = splitDirFile' (s, xmin, e, e)
in
substring(s, 0, m)
end
fun file s =
let
val sz = size s
val xmin = if isVolumePrefix s then 2 else 0
val (_, n) = splitDirFile' (s, xmin, sz-1, sz-1)
in
substring (s, n, sz-n)
end
fun splitBaseExt' (s, xmin, n, e) =
let
fun scan (n, e) =
if n <= xmin then
NONE
else
let
val c = String.sub(s, n)
in
if c = extSeparator then
if n = e
orelse String.sub (s, n-1) = dirSeparator then
NONE
else
SOME n
else if c = dirSeparator then
NONE
else
scan (n-1, e)
end
in
scan (n, e)
end
fun splitBaseExt s =
let
val e = size s - 1
val xmin = if isVolumePrefix s then 2 else 0
in
case splitBaseExt' (s, xmin, e, e) of
NONE => {base = s, ext = NONE}
| SOME n => { base = substring (s, 0, n)
, ext = SOME (substring (s, n+1, e-n))
}
end
fun joinBaseExt {base, ext = NONE} = base
| joinBaseExt {base, ext = SOME ""} = base
| joinBaseExt {base, ext = SOME ext} = base
^ (String.str extSeparator) ^ ext
fun base s =
let
val e = size s - 1
val xmin = if isVolumePrefix s then 2 else 0
in
case splitBaseExt' (s, xmin, e, e) of
NONE => s
| SOME n => substring (s, 0, n)
end
fun ext s =
let
val e = size s - 1
val xmin = if isVolumePrefix s then 2 else 0
in
case splitBaseExt' (s, xmin, e, e) of
NONE => NONE
| SOME n => SOME (substring (s, n+1, e-n))
end
fun mkCanonical s =
let
val {isAbs, vol, arcs} = fromString s
fun canon ([], 0, result) = result
| canon ([], n, result) =
if isAbs
then result
else canon ([], n-1, parentArc :: result)
| canon ("" :: arcs, level, result) =
canon (arcs, level, result)
| canon ("." :: arcs, level, result) =
canon (arcs, level, result)
| canon (".." :: arcs, level, result) =
canon (arcs, level + 1, result)
| canon (arc :: arcs, 0, result) =
canon (arcs, 0, arc :: result)
| canon (_ :: arcs, n, result) =
canon (arcs, n-1, result)
val arcs' = canon (rev arcs, 0, [])
val s' = toString {isAbs=isAbs, vol=vol, arcs=arcs'}
fun toLowerStr s = implode (map Char.toLower (explode s))
in
toLowerStr (if s' = "" then currentArc else s')
end
fun isCanonical s =
let
val {isAbs, vol, arcs} = fromString s
fun scan1 ["."] = not isAbs
| scan1 (".."::xs) = isAbs = false andalso scan2 xs
| scan1 (""::xs) = scan3 xs
| scan1 xs = scan3 xs
and scan2 (".."::xs) = scan2 xs
| scan2 xs = scan3 xs
and scan3 [] = true
| scan3 ("."::_) = false
| scan3 (".."::_) = false
| scan3 (""::_) = false
| scan3 (_::xs) = scan3 xs
in
scan1 arcs
end
fun isAbsolute "" = false
| isAbsolute s =
let
val xmin = if isVolumePrefix s then 2 else 0
in
if size s - xmin <= 0 then
false
else
String.sub(s, xmin) = dirSeparator
end
fun isRelative s = not (isAbsolute s)
local
fun strip_common_prefix (l, []:string list) = (l, [])
| strip_common_prefix ([],l) = ([],l)
| strip_common_prefix (l1 as h1::t1, l2 as h2::t2) =
if h1 = h2
then strip_common_prefix (t1, t2)
else (l1, l2)
fun mkRel ([], result) = result
| mkRel (_::t, result) = mkRel (t, parentArc :: result)
fun noroot [""] = []
| noroot x = x
fun diff (s1, s2) =
let
val canon1 = mkCanonical s1
val canon2 = s2
val {arcs = arcs1, ...} = fromString canon1
val {arcs = arcs2, ...} = fromString canon2
val (arcs1', arcs2') = strip_common_prefix (arcs1, arcs2)
val arcs = mkRel (arcs1', noroot arcs2')
in
toString {isAbs = false, vol = "", arcs = arcs}
end
fun isDir s = size s>1
andalso String.sub(s,size s-1)=dirSeparator
in
fun mkRelative {path, relativeTo} =
let
val r1 =
if isAbsolute relativeTo then
if isAbsolute path
then diff (relativeTo, path)
else path
else
raise Path
val r2 = if isDir path andalso not (isDir r1)
then r1^sep else r1
in
if path="" then "" else if r2="" then currentArc else r2
end
end
fun isRoot "" = false
| isRoot s =
case size s of
1 => String.sub(s, 0) = dirSeparator
| 3 => String.sub(s, 1) = volumeSeparator andalso
String.sub(s, 2) = dirSeparator
| _ => false
fun concat2backwards (t,s) =
let
val s =
if isVolumePrefix s then
if isVolumePrefix t then
let
val (c,d) = (Char.toLower (String.sub(s, 0)),
Char.toLower (String.sub(t, 0)))
in
if c <> d then raise Path
else
s
end
else
s
else
if isVolumePrefix t then
substring(t, 0, 2)^s
else
s
val xmint = if isVolumePrefix t then 2 else 0
val sz = size t
in
if isVolumePrefix t then
if (sz > 2 andalso String.sub(t, 2) = dirSeparator) then
raise Path
else
s^sep^String.extract(t, 2, NONE)
else
if (sz > 0 andalso String.sub(t, 0) = dirSeparator) then
raise Path
else
if s="" then t else s^sep^ t
end
fun concat [] = ""
| concat (h::t) = foldl concat2backwards h t;
fun mkAbsolute {path, relativeTo} =
if isAbsolute relativeTo then
if isAbsolute path then
path
else mkCanonical (concat [relativeTo, path])
else
raise Path
val unixSeparator = #"/"
val win32Separator = #"\\"
fun toUnixPath s =
let
fun trans #"\\" = unixSeparator
| trans x = x
in
String.implode(map trans (String.explode s))
end
fun fromUnixPath s =
let
fun trans #"/" = win32Separator
| trans x = x
in
String.implode(map trans (String.explode s))
end
end
;
