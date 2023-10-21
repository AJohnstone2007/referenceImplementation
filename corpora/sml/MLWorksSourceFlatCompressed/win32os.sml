require "^.basis.__word8_vector";
signature WIN32OS =
sig
datatype seek_direction = FROM_BEGIN | FROM_CURRENT | FROM_END
datatype open_method = READ | READ_WRITE | WRITE
datatype open_action = CREATE_ALWAYS | OPEN_ALWAYS | OPEN_EXISTING
type file_desc
val open_ : string * open_method * open_action -> file_desc
val close : file_desc -> unit
val write : file_desc * Word8Vector.vector * int * int -> int
val read : file_desc * int -> Word8Vector.vector
val seek : file_desc * int * seek_direction -> int
val size : file_desc -> int
end
;
