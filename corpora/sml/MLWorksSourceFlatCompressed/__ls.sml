require "$.system.__os";
require "$.basis.__word";
require "$.foreign._mlworks_c_pointer";
require "$.foreign.__mlworks_c_interface";
require "$.foreign.__mlworks_c_resource";
require "__dirent";
structure Ls : sig end =
struct
exception OutOfMemory
exception NoSuchDirectory of string
structure C = MLWorksCInterface
val withNonNullResource = MLWorksCResource.withNonNullResource
fun withCString str action =
withNonNullResource (C.CharPtr.free, OutOfMemory, str, action)
fun withDirectory directoryName action =
withCString (C.CharPtr.fromString directoryName)
(fn cDirectoryName =>
withNonNullResource (Dirent.closedir, NoSuchDirectory directoryName,
Dirent.opendir cDirectoryName, action))
fun applyPair (f, g) x = (f x, g x)
fun dirApp (f, dir) =
let
val entry = Dirent.readdir dir
in
if entry = C.null then
()
else
(f entry; dirApp (f, dir))
end
val wordFromUshort = Word.fromLargeWord o C.Ushort.toLargeWord
val direntName : Dirent.struct'dirent -> string
= C.CharPtr.copySubString
o applyPair (Dirent.struct'dirent'd_name,
wordFromUshort o Dirent.struct'dirent'd_namlen)
structure direntPtr = MLWorksCPointer
(type value = Dirent.struct'dirent
val size = Dirent.struct'dirent'size'
val addr = Dirent.struct'dirent'addr'
structure C = C);
val printName = print o direntName o direntPtr.!
fun longListing directoryName = ()
fun shortListing directoryName =
withDirectory directoryName
(fn dir =>
dirApp (fn entry => (printName entry; print "\n"), dir))
fun usage () =
(print "[ directory ]\n";
OS.Process.exit 1)
fun parseArgs ([directoryName]) = shortListing directoryName
| parseArgs ([]) = shortListing "."
| parseArgs _ = usage ()
fun main () = parseArgs (MLWorks.arguments ())
handle
OutOfMemory => print "Out of Memory\n"
| NoSuchDirectory d =>
print ("No such directory: " ^ d ^ "\n")
val _ = MLWorks.Deliver.deliver ("simplels", main, MLWorks.Deliver.CONSOLE);
end
;
