require "file_find";
require "__match";
require "$.system.__os";
structure FileFind : FILE_FIND =
struct
fun isChildDir file =
OS.FileSys.isDir file
andalso not (OS.FileSys.isLink file)
andalso file <> "."
andalso file <> ".."
fun checkDir (filename, path) =
let
val oldPath = OS.FileSys.getDir ()
val dir = OS.FileSys.openDir path
val _ = OS.FileSys.chDir path
val fileList = checkFile (filename, dir)
val _ = OS.FileSys.chDir oldPath
val _ = OS.FileSys.closeDir dir
in
fileList
end
and checkFile (searchFile, dir) =
case (OS.FileSys.readDir dir) of
"" => []
| file =>
if isChildDir file then
checkDir (searchFile, file) @
checkFile (searchFile, dir)
else
if Match.match (file, searchFile) then
OS.FileSys.fullPath file ::
checkFile (searchFile, dir)
else
checkFile (searchFile, dir)
fun find (filename, path) =
let
fun fileFind () =
let
val userPath = OS.FileSys.getDir ()
fun display path = print ("\nFile: " ^ OS.Path.file path ^
"\nDir:  " ^ OS.Path.dir path ^ "\n")
in
app display (checkDir (filename, path))
handle OS.SysErr (message, error) =>
(print ("System error whilst searching directory " ^
OS.FileSys.getDir () ^ "\n" ^ message ^ "\n");
OS.FileSys.chDir userPath)
end
in
fileFind ()
handle OS.SysErr _ => print ("Directory error.\n")
end
end
;
