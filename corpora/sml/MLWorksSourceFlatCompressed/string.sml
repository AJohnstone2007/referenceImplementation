local
val print = fn s => fn res => (print(s^":"^res^"\n"))
fun check' s f = print s ((if f () then "OK" else "WRONG") handle exn => "FAIL:"^General.exnName exn)
fun checkexn' s exn f =
let val result = (ignore(f ()); "FAIL") handle ex =>
if General.exnName ex = General.exnName exn then
"OKEXN"
else
"BADEXN:" ^ (General.exnName ex)
in
print s result
end
fun iterate (x,y) = if x>y then [] else x::iterate(x+1,y)
val s1 = ""
val s2 = "ABCDE\tFGHI"
val ABCDE = map Char.chr (iterate (65, 69))
val test1 = check' "test1" (fn _=>(map String.size [s1, s2]) = [0, 10])
val test2 = check' "test2" (fn _=>String.sub(s2, 6) = Char.chr 70 andalso
String.sub(s2, 9) = Char.chr 73)
val test3 = checkexn' "test3" Subscript (fn _=>String.sub(s1, 0))
val test4 = checkexn' "test4" Subscript (fn _=>String.sub(s2, ~1))
val test5 = checkexn' "test5" Subscript (fn _=>String.sub(s2, 10))
fun foo (acc, 0) = acc
| foo (acc, n) =
if n < 0 orelse n > Char.maxOrd then foo (#"_"::acc, n-1)
else foo (Char.chr n :: acc, n-1)
val test7 = check' "test7"
(fn _=>
String.isPrefix "abc" "abcd" andalso
String.isPrefix "abc" "abc" andalso
not (String.isPrefix "abcd" "abc") andalso
not (String.isPrefix "abxd" "abcdef") andalso
String.substring("abcd", 2, 2) = "cd" andalso
String.substring("abcd", 0, 4) = "abcd")
val test8 = checkexn' "test8" Subscript
(fn _=>String.substring ("abcd", ~1, 2))
val test9 = checkexn' "test9" Subscript
(fn _=>String.substring ("abcd", 0, 5))
val isDelimiter = fn c=> c = #"a"
val test10 = check' "test10"
(fn _=>
String.fields isDelimiter "aaa" = ["", "", "", ""] andalso
String.fields isDelimiter "aa1" = ["", "", "1"] andalso
String.fields isDelimiter "1aa" = ["1", "", ""] andalso
String.fields isDelimiter "1a2a3" = ["1", "2", "3"] andalso
String.fields isDelimiter "1a2a" = ["1", "2", ""] andalso
String.fields isDelimiter "a1a2" = ["", "1", "2"])
val test11 = check' "test11"
(fn _=>
String.extract ("abc", 0, NONE) = "abc" andalso
String.extract ("abc",1, NONE) = "bc" andalso
String.extract ("abc",1, SOME 1) = "b" andalso
String.extract ("abc",3, NONE) = "" andalso
String.extract ("abc", 3, SOME 0) = "")
val test11a = checkexn' "test11a" Subscript
(fn _=>String.extract ("abc", ~1, NONE))
val test11b = checkexn' "test11b" Subscript
(fn _=>String.extract ("abc", 4, NONE))
val test11c = checkexn' "test11c" Subscript
(fn _=>String.extract ("abc", 3, SOME 1))
val test11d = checkexn' "test11d" Subscript
(fn _=>String.extract ("abc",3, SOME 2))
val test11e = checkexn' "test11e" Subscript
(fn _=>String.extract ("abc", 0, SOME ~1))
val test11f = checkexn' "test11f" Subscript
(fn _=>String.extract ("abc", 1, SOME (1+2)))
val test11g = checkexn' "test11g" Subscript
(fn _=>String.extract(s2, ~1, SOME 0))
val test11h = checkexn' "test11h" Subscript
(fn _=>String.extract("", ~1, SOME 0))
val test11i = checkexn' "test11i" Subscript
(fn _=>String.extract("", 0, SOME 3))
val test12a = check' "test12a"
(fn _=>
s2 = String.substring(s2, 0, size s2) andalso
"" = String.substring(s2, size s2, 0) andalso
"" = String.substring(s1, 0, 0))
val test12b = checkexn' "test12b" Subscript
(fn _=>String.substring(s2, ~1, 0))
val test12c = checkexn' "test12c" Subscript
(fn _=>String.substring(s2, 11, 0))
val test12d = checkexn' "test12d" Subscript
(fn _=>String.substring(s2, 0, 11))
val test12e = checkexn' "test12e" Subscript
(fn _=>String.substring(s2, 10, 1))
val test12f = check' "test12f"
(fn _ =>
"ABCDE" = String.substring(s2, 0, 5) andalso
"FGHI" = String.substring(s2, 6, 4))
val test13a = check' "test13a"
(fn _=>
(String.translate (fn _ => "") s2 = "" andalso
String.translate (fn x => String.str x) "" = "" andalso
String.translate (fn x => String.str x) s2 = s2))
val test13b = check' "test13b"
(fn _ =>
(String.translate
(fn c => if c = #"\t" then "XYZ " else String.str c)
s2
= "ABCDEXYZ FGHI"))
val test14 = check' "test14"
(fn _=>
(String.tokens Char.isSpace "" = []
andalso String.tokens Char.isSpace "   \t \n" = []
andalso String.tokens (fn c => c = #",") ",asd,,def,fgh"
= ["asd","def","fgh"]))
val test15 =
check' "test15"
(fn _=>
(String.fields Char.isSpace "" = [""]
andalso String.fields Char.isSpace "   \t \n" = ["","","","","","",""]
andalso String.fields (fn c => c = #",") ",asd,,def,fgh"
= ["","asd","","def","fgh"]))
val test16a = check' "test16a"
(fn _ =>
EQUAL = String.compare(s1,s1) andalso EQUAL = String.compare(s2,s2) andalso
LESS = String.compare("A", "B") andalso
GREATER = String.compare("B", "A") andalso
LESS = String.compare("ABCD", "ABCDE") andalso
GREATER = String.compare("ABCDE", "ABCD"))
val test16b = check' "test16b"
(fn _=>
EQUAL = String.compare(s1,s1) andalso EQUAL = String.compare(s2,s2) andalso
LESS = String.compare("A", "a") andalso
GREATER = String.compare("b", "B") andalso
LESS = String.compare("abcd", "abcde") andalso
GREATER = String.compare("abcde", "abcd"))
val test17 = check' "test17"
(fn _=>
String.^("abc", "def") = "abcdef" andalso
String.^("abc\000def", "ghi") = "abc\000defghi" andalso
String.^("", "abc") = "abc" andalso
String.^("", "\000") = "\000")
val test18b = check' "test18b"
(fn _=>
String.concat [] = "")
val test19 = check' "test19"
(fn _=>
String.< (s1, s2) andalso
not (String.< (s2, s1)) andalso
not (String.< (s1, s1)) andalso
not (String.< (s2, s2)) andalso
String.<= (s1, s2) andalso
not (String.<= (s2, s1)) andalso
String.<= (s1, s1) andalso
String.<= (s2, s2) andalso
not (String.> (s1, s2)) andalso
String.> (s2, s1) andalso
not (String.> (s1, s1)) andalso
not (String.> (s2, s2)) andalso
not (String.>= (s1, s2)) andalso
String.>= (s2, s1) andalso
String.>= (s1, s1) andalso
String.>= (s2, s2))
val test20 = check' "test20"
(fn _=>
List.length (String.explode "abcdef\000\001\0031\0032") = (String.size "abcdef\000\001\0031\0032"))
val test20a = check' "test20a"
(fn _=>
String.explode "abc" = [#"a", #"b", #"c"])
val test21 = check' "test21"
(fn _=>
String.collate Char.compare ("abc", "ABC") = GREATER andalso
String.collate Char.compare ("abc", "abc") = EQUAL andalso
String.collate Char.compare ("ABC", "abc") = LESS)
val charList =
[(#"\n", "\\n"),
(#"\t", "\\t"),
(#"\000", "\\^@"),
(#"\001", "\\^A"),
(#"\026", "\\^Z"),
(#"\031", "\\^_"),
(#"\032", " "),
(#"\126", "~"),
(#"\\", "\\\\"),
(#"\"", "\\\""),
(#"A", "A"),
(#"\127", "\\127"),
(#"\128", "\\128"),
(#"\255", "\\255")]
val test22 = check' "test22"
(fn _=>
String.toString (String.implode (map #1 charList)) =
(String.concat (map #2 charList)))
val charList2 = CharVector.tabulate (256, Char.chr)
fun chk (s:string, f, g) =
let
val sz = size s
fun scan (acc, i) =
if i < sz then
let
val c = String.str (String.sub (s, i))
in
case g c of
NONE => scan (c::acc, i+1)
| SOME s =>
case f s of
NONE => scan (c::acc, i+1)
| SOME t =>
if c = t then
scan (acc, i+1)
else
scan (c::acc, i+1)
end
else
rev acc
in
scan ([], 0)
end
val test23 = check' "test23"
(fn _=>
chk (charList2, String.fromString, fn c => SOME (String.toString c)) = [])
val argResList =
[("A", #"A"),
("z", #"z"),
("@", #"@"),
("~", #"~"),
("\\n", #"\n"),
("\\t", #"\t"),
("\\\\", #"\\"),
("\\\"", #"\""),
("\\^@", #"\000"),
("\\^A", #"\001"),
("\\^Z", #"\026"),
("\\^_", #"\031"),
("\\000", #"\000"),
("\\097", #"a"),
("\\255", #"\255"),
("\\   \t\n\n \\A", #"A"),
("\\   \t\n\n \\z", #"z"),
("\\   \t\n\n \\@", #"@"),
("\\   \t\n\n \\~", #"~"),
("\\   \t\n\n \\\\n", #"\n"),
("\\   \t\n\n \\\\t", #"\t"),
("\\   \t\n\n \\\\\\", #"\\"),
("\\   \t\n\n \\\\\"", #"\""),
("\\   \t\n\n \\\\^@", #"\000"),
("\\   \t\n\n \\\\^A", #"\001"),
("\\   \t\n\n \\\\^Z", #"\026"),
("\\   \t\n\n \\\\^_", #"\031"),
("\\   \t\n\n \\\\000", #"\000"),
("\\   \t\n\n \\\\097", #"a"),
("\\   \t\n\n \\\\255", #"\255")]
val test24 = check' "test24"
(fn _=>
let val (arg, res) = (String.concat (map #1 argResList),
String.implode (map #2 argResList))
in
List.all (fn (x,y)=> String.fromString x = SOME (String.str y))
argResList andalso
String.fromString arg = SOME res
end)
val test25 = check' "test25"
(fn _=>
List.all (fn arg => String.fromString arg = NONE)
["\\",
"\\N",
"\\T",
"\\1",
"\\11",
"\\256",
"\\999",
"\\-65",
"\\~65",
"\\?",
"\\^`",
"\\^a",
"\\^z",
"\\   a",
"\\   a\\B",
"\\   \\"])
val test25a = check' "test25a"
(fn _=>
String.fromString "abc\nA" = SOME "abc" andalso
String.fromString "abc\tA" = SOME "abc" andalso
String.fromString "\tABC" = NONE)
val test26 = check' "test26"
(fn _=>
String.fromCString "\000\001\031" = NONE andalso
String.fromCString "\\n\\t\\\"\\\\\\a\\b\\v\\f\\r\\?\\'\\x" = SOME "\n\t\"\\\a\b\v\f\r?'" andalso
String.fromCString "\\xff\\x00\\xff\\010\\100" = SOME "\255\000\255\008@" andalso
String.fromCString "?" = NONE andalso
String.fromCString "\127" = NONE andalso
String.fromCString "abcABCxyzXYZ012890" = SOME "abcABCxyzXYZ012890")
val test27 = check' "test27"
(fn _=>chk (charList2, String.fromCString, fn c=>(SOME (String.toCString c))) = [])
val argResList =
[("\n", "\\n"),
("\t", "\\t"),
("\v", "\\v"),
("\b", "\\b"),
("\r", "\\r"),
("\f", "\\f"),
("\a", "\\a"),
("\\", "\\\\"),
("?", "\\?"),
("'", "\\'"),
("\"", "\\\"")]
fun chk (s, t) =
let
val szs = size s
val szt = size t
fun scan (acc, i) =
if i < szs then
let val c = String.sub(s, i)
in
if c <> String.sub(t, i) then
scan (c::acc, i+1)
else
scan (acc, i+1)
end
else
rev acc
in
if szs <> szt then
raise General.Fail "chk"
else
scan ([], 0)
end
val test28 = check' "test28"
(fn _=>let
val (arg, res) = (String.concat (map #1 argResList),
String.concat (map #2 argResList))
in
chk (String.toCString arg, res) = []
end)
val argResList =
[("\\n", "\n"),
("\\t", "\t"),
("\\v", "\v"),
("\\b", "\b"),
("\\r", "\r"),
("\\f", "\f"),
("\\a", "\a"),
("\\\\", "\\"),
("\\?", "?"),
("\\'", "'"),
("\\\"", "\""),
("\\1", "\001"),
("\\11", "\009"),
("\\111", "\073"),
("\\1007", "\0647"),
("\\100A", "\064A"),
("\\0", "\000"),
("\\377", "\255"),
("\\18", "\0018"),
("\\178", "\0158"),
("\\1C", "\001C"),
("\\17C", "\015C"),
("\\x0", "\000"),
("\\xff", "\255"),
("\\xFF", "\255"),
("\\x1", "\001"),
("\\x11", "\017"),
("\\xag", "\010g"),
("\\xAAg", "\170g"),
("\\x0000000a", "\010"),
("\\x0000000a2", "\162"),
("\\x0000000ag", "\010g"),
("\\x0000000A", "\010"),
("\\x0000000A2", "\162"),
("\\x0000000Ag", "\010g"),
("\\x00000000000000000000000000000000000000000000000000000000000000011+",
"\017+")]
val test29 = check' "test29"
(fn _=>
let val (arg, res) = (String.concat (map #1 argResList),
String.concat (map #2 argResList))
fun chk [] = []
| chk ((x,y)::rest) =
case String.fromCString x of
NONE => x::chk rest
| SOME x' =>
if x' = y then chk rest
else
x::chk rest
in
chk argResList = [] andalso
String.fromCString arg = SOME res andalso
String.toCString (valOf (String.fromCString arg)) = String.toCString res andalso
String.fromCString "abc\n0123" = SOME "abc" andalso
String.fromCString "\000abc\n0123" = NONE andalso
String.fromCString "abc'123" = SOME "abc'123" andalso
String.fromCString "abc\\'123" = SOME "abc'123"
end)
val test30 = check' "test30"
(fn _=>
List.all
(fn arg=>String.fromCString arg = NONE)
["",
"\n",
"\t",
"\"",
"?",
"\\",
"\\X",
"\\=",
"\\400",
"\\777",
"\\8",
"\\9",
"\\c",
"\\d",
"\\x",
"\\x100",
"\\xG"])
val add1 = fn c => chr (1 + ord c)
val addi = fn (i, c) => chr (i + ord c)
val s = "ABCDabcd"
val test31 = check' "test31"
(fn _ => (String.map add1 s)="BCDEbcde")
val test32 = check' "test32"
(fn _ => (String.map add1 "")="")
val test33 = check' "test33"
(fn _ => (String.mapi addi (s, 0, NONE))="ACEGegik")
val test34 = check' "test34"
(fn _ => (String.mapi addi (s, 1, NONE))="CEGegik")
val test35 = check' "test35"
(fn _ => (String.mapi addi (s, 7, NONE))="k")
val test36 = checkexn' "test36" Subscript
(fn _ => (String.mapi addi (s, ~1, NONE)))
val test37a = check' "test37a"
(fn _ => (String.mapi addi (s, 8, NONE)) = "")
val test37b = checkexn' "test37b" Subscript
(fn _ => (String.mapi addi (s, 9, NONE)))
val test38 = check' "test38"
(fn _ => (String.mapi addi (s, 0, SOME 2))="AC")
val test39 = check' "test39"
(fn _ => (String.mapi addi (s, 6, SOME 2))="ik")
val test40 = checkexn' "test40" Subscript
(fn _ => (String.mapi addi (s, 7, SOME 2)))
val test41 = check' "test41"
(fn _ => (String.mapi addi (s, 2, SOME 0))="")
val test42 = checkexn' "test42" Subscript
(fn _ => (String.mapi addi (s, 2, SOME (~1))))
in
val it = ()
end
;
