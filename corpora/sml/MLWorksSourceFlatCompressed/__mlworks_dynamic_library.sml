require "$.basis.__sys_word";
require "mlworks_dynamic_library";
structure MLWorksDynamicLibrary : MLWORKS_DYNAMIC_LIBRARY =
struct
datatype library = FILE of SysWord.word
val env = MLWorks.Internal.Runtime.environment
type syserror = MLWorks.Internal.Error.syserror
exception SysErr = MLWorks.Internal.Error.SysErr
val openLibrary : string * string -> library = env "C.File.openFile"
fun bind s = env s
val closeLibrary : library * string option -> unit = env "C.File.closeFile"
end
;
