require "$.basis.__char";
require "$.basis.__string";
require "$.basis.__int";
require "$.basis.__text_io";
require "$.basis.__io";
require "$.basis.__list";
require "$.system.__os";
require "$.basis.__word";
require "$.foreign._mlworks_c_pointer";
require "$.foreign.__mlworks_c_interface";
require "$.foreign.__mlworks_c_resource";
require "__open_flags";
require "__ndbm";
structure Phones =
struct
exception OutOfMemory
exception CannotOpenDbmFile
structure C = MLWorksCInterface
fun openError databaseName =
(print "Cannot open ";
print databaseName;
print " as a phones database\n";
OS.Process.exit OS.Process.failure)
fun internalError databaseName =
(print "internal error when reading ";
print databaseName;
print "\n";
OS.Process.exit OS.Process.failure)
structure datumPtr = MLWorksCPointer
(type value = Ndbm.datum;
val size = Ndbm.datum'size';
val addr = Ndbm.datum'addr')
fun makeDatum data =
let
val datum' = datumPtr.make ()
val datum = datumPtr.! datum'
val dataPtr = C.CharPtr.fromString data
val size' = (C.Uint.fromInt (size data))
val _ = C.PtrPtr.:= (Ndbm.datum'dptr'addr datum, dataPtr)
val _ = C.UintPtr.:= (Ndbm.datum'dsize'addr datum, size')
in
datum
end
datatype access_type = READ | WRITE
fun accessTypeToFlags (m, flags) =
case m of
READ =>
if flags = OpenFlags.O_WRONLY
then OpenFlags.O_RDWR + OpenFlags.O_CREAT
else OpenFlags.O_RDONLY
| WRITE =>
if flags = OpenFlags.O_RDONLY
then OpenFlags.O_RDWR + OpenFlags.O_CREAT
else OpenFlags.O_WRONLY
fun foldDb f z (db: Ndbm.DBM C.ptr) =
let
fun aux (v as (z, keyDatum)) =
if Ndbm.datum'dptr keyDatum = C.null then
z
else
aux (f v, Ndbm.dbm_nextkey db)
in
aux (z, Ndbm.dbm_firstkey db)
end
val withResource = MLWorksCResource.withResource
val withNonNullResource = MLWorksCResource.withNonNullResource
fun withDatum datum f =
withResource (datumPtr.free o Ndbm.datum'addr', datum, f)
fun withCString str action =
withNonNullResource (C.CharPtr.free, OutOfMemory, str, action)
fun withNdbm db action =
withNonNullResource (Ndbm.dbm_close, CannotOpenDbmFile, db, action)
local
fun fWrapper (f, data) datum' =
let
val datum = datumPtr.! datum'
val dataPtr = C.CharPtr.fromString data
val size' = C.Uint.fromInt (size data)
val _ = C.PtrPtr.:= (Ndbm.datum'dptr'addr datum, dataPtr)
val _ = C.UintPtr.:= (Ndbm.datum'dsize'addr datum, size')
in
f datum
end
in
fun withNewDatum data f =
withNonNullResource (datumPtr.free, OutOfMemory, datumPtr.make (), fWrapper (f, data))
end
fun withDb (fileName, accessTypes, action: Ndbm.DBM C.ptr -> 'a) =
let
val flags = C.Int.fromInt (List.foldl accessTypeToFlags 0 accessTypes)
val mode = C.Int.fromInt 0x1b0
in
withCString (C.CharPtr.fromString fileName)
(fn name =>
withNdbm (Ndbm.dbm_open (name, flags, mode)) action)
end
fun withReadOnlyDb (databaseName: string) action =
withDb (databaseName, [READ], action)
handle CannotOpenDbmFile => openError databaseName
fun withWriteOnlyDb (databaseName: string) action =
withDb (databaseName, [WRITE], action)
handle CannotOpenDbmFile => openError databaseName
fun addPerson (db, name: string, phoneNumber: string) =
withNewDatum name
(fn nameDatum =>
withNewDatum phoneNumber
(fn phoneDatum =>
let
val result = Ndbm.dbm_store (db, nameDatum, phoneDatum, Ndbm.DBM_INSERT)
val result' = C.Int.toInt result
in
if result' = 1 then
(print "Sorry, cannot add ";
print name;
print " since it is already in the database\n")
else
()
end))
fun foldOverLinesInInputFile
(inputFileName: string)
(fail: string * string -> unit)
(succ: int * string -> unit) =
let
val stream = TextIO.openIn inputFileName
fun loop lineNumber =
if TextIO.endOfStream stream then
TextIO.closeIn stream
else
(succ (lineNumber, TextIO.inputLine stream);
loop (lineNumber+1))
in
loop 0
end
handle IO.Io {name, function, ...} => fail (name, function)
fun createDb (inputFileName, databaseName) =
withWriteOnlyDb databaseName
(fn db =>
(foldOverLinesInInputFile inputFileName
(fn (name, function) =>
(print "Could not ";
print function;
print " from ";
print name;
print "\n"))
(fn (lineNumber, line) =>
(case String.fields Char.isSpace line of
[name, phoneNumber, ""] =>
(addPerson (db, name, phoneNumber); ())
| [] => ()
| _ =>
(print "Malformed input on line ";
print (Int.toString lineNumber);
print " ";
print line;
print "\n")))))
val wordFromUint = Word.fromLargeWord o C.Uint.toLargeWord
fun datumToString (datum: Ndbm.datum): string =
let
val data = Ndbm.datum'dptr datum
val size = wordFromUint (Ndbm.datum'dsize datum)
in
C.CharPtr.copySubString (data, size)
end
fun getNumber (db, nameDatum) =
withDatum (Ndbm.dbm_fetch (db, nameDatum))
(fn phoneDatum =>
if Ndbm.datum'dptr phoneDatum = C.null then
NONE
else
SOME (datumToString phoneDatum))
fun dumpDb databaseName =
withReadOnlyDb databaseName
(fn db =>
let
fun dumpItem (z, nameDatum) =
let
val name = datumToString nameDatum
in
case (getNumber (db, nameDatum)) of
NONE => internalError databaseName
| SOME number =>
(print name;
print " ";
print number;
print "\n";
datumPtr.free (Ndbm.datum'addr' nameDatum))
end
in
foldDb dumpItem () db
end)
fun lookupInDb (personName, databaseName) =
withReadOnlyDb databaseName
(fn db =>
withNewDatum personName
(fn personDatum =>
case getNumber (db, personDatum) of
NONE =>
(print "Sorry, no number available for ";
print personName;
print "\n")
| SOME number =>
(print personName;
print " ";
print number;
print "\n")))
fun removeFromDb (personName, databaseName) =
withWriteOnlyDb databaseName
(fn db =>
withNewDatum personName
(fn person =>
let
val result = Ndbm.dbm_delete (db, person)
val result' = C.Int.toInt result
in
if result' <> 0 then
(print "Sorry, cannot remove ";
print personName;
print " from ";
print databaseName;
print " since ";
print personName;
print " is not in the database\n")
else
()
end))
fun addToDb (personName, phoneNumber, databaseName) =
withWriteOnlyDb databaseName
(fn db => addPerson (db, personName, phoneNumber))
fun usage () =
(print "[ -c input | -d | -r person | -a person phone | -l person ] database\n";
OS.Process.exit OS.Process.failure)
fun parseArgs ("-c"::inputFileName::[databaseName]) =
createDb (inputFileName, databaseName)
| parseArgs ("-d"::[databaseName]) =
dumpDb databaseName
| parseArgs ("-l"::personName::[databaseName]) =
lookupInDb (personName, databaseName)
| parseArgs ("-r"::personName::[databaseName]) =
removeFromDb (personName, databaseName)
| parseArgs ("-a"::personName::phoneNumber::[databaseName]) =
addToDb (personName, phoneNumber, databaseName)
| parseArgs _ = usage ()
fun main () =
(parseArgs (MLWorks.arguments ()))
handle OutOfMemory => print "out of memory\n";
val _ = MLWorks.Deliver.deliver ("phones", main, MLWorks.Deliver.CONSOLE);
end
;
