require "version";
require "../basis/__int";
require "../system/__getenv";
structure Version_ : VERSION =
struct
datatype kind = MILESTONE of int
| ALPHA of int
| BETA of int
| FULL of int
datatype edition = ENTERPRISE | PERSONAL | PROFESSIONAL
fun edition () = PROFESSIONAL
fun current () = {major = 2,
minor = 1,
revision = 0,
status = FULL 0,
edition = edition()}
fun get_status () = #status(current())
fun statusString status =
case status of
BETA _ => "Beta "
| _ => ""
val copyright =
"Copyright (C) 1999 Harlequin Group plc." ^
"  All rights reserved.\n" ^
"MLWorks is a trademark of Harlequin Group plc.\n"
fun printEdition (edition) =
case edition of
ENTERPRISE => "Enterprise"
| PERSONAL => "Personal"
| PROFESSIONAL => "Professional"
fun printStatus (MILESTONE i) = "m" ^ Int.toString i
| printStatus (ALPHA i) = "a" ^ Int.toString i
| printStatus (BETA i) = "b" ^ Int.toString i
| printStatus (FULL i) =
case Getenv_.get_version_setting () of
SOME "full" => "c" ^ Int.toString i
| _ => ""
fun printVersion {major, minor, revision, status, edition} =
Int.toString major ^ "." ^
Int.toString minor ^
(if (revision=0) then "" else ("r" ^ Int.toString revision)) ^
printStatus status ^
" " ^ printEdition edition ^ " " ^
statusString status ^
"Edition"
fun versionString () =
"MLWorks " ^ (printVersion (current())) ^ "\n" ^ copyright
end
;
