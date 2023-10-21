require "__windows";
require "__os";
require "^.main.file_time";
require "^.basis.__string";
structure FileTime : FILE_TIME =
struct
fun modTime path =
let
val file_time = OS.FileSys.modTime path
val converted = Windows.fileTimeToLocalFileTime file_time
in
if file_time = converted then
file_time
else
let
val fullPath = OS.FileSys.fullPath path
val root =
if size fullPath <= 3 then
raise OS.SysErr("fullPath of '" ^ path ^ "' has returned '" ^ fullPath, NONE)
else
if String.sub(fullPath, 1) = #":" then
String.substring(fullPath, 0, 3)
else
let
val name = explode fullPath
in
case name of
#"\\" :: #"\\" :: name' =>
let
fun to_next_back([], acc) = raise OS.SysErr("malformed root in '" ^ fullPath, NONE)
| to_next_back(#"\\" :: name, acc) = (name, #"\\" :: acc)
| to_next_back(x :: name, acc) = to_next_back(name, x :: acc)
in
implode(#1(to_next_back(to_next_back(name, []))))
end
| _ => raise OS.SysErr("malformed root in '" ^ fullPath, NONE)
end
val {systemName, ...} = Windows.getVolumeInformation root
in
case systemName of
"NTFS" => file_time
| "FAT" => converted
| "VFAT" => converted
| "HPFS" => file_time
| "FAT32" => converted
| "Samba" => file_time
| _ =>
(print("Warning, unknown file system type '" ^ systemName ^ "' found during modTime\n");
file_time)
end
end
end
;
