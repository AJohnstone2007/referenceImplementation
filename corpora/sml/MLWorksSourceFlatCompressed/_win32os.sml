require "win32os";
require "^.basis.__word8_vector";
functor Win32OS () : WIN32OS =
struct
type file_desc = MLWorks.Internal.IO.file_desc
datatype seek_direction = FROM_BEGIN | FROM_CURRENT | FROM_END
datatype open_method = READ | READ_WRITE | WRITE
datatype open_action = CREATE_ALWAYS | OPEN_ALWAYS | OPEN_EXISTING
val env = MLWorks.Internal.Runtime.environment
val open_ : string * open_method * open_action -> file_desc = env "system os win32 open"
val close : file_desc -> unit = MLWorks.Internal.IO.close
val write : file_desc * Word8Vector.vector * int * int -> int =
MLWorks.Internal.Value.cast MLWorks.Internal.IO.write
val read : file_desc * int -> Word8Vector.vector =
MLWorks.Internal.Value.cast MLWorks.Internal.IO.read
val seek : file_desc * int * seek_direction -> int =
MLWorks.Internal.Value.cast MLWorks.Internal.IO.seek
val size : file_desc -> int = env "system os win32 size"
end
;
