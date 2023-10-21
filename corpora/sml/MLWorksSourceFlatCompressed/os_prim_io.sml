signature OS_PRIM_IO =
sig
type file_desc
type bin_reader
type text_reader
type bin_writer
type text_writer
val openRd : string -> bin_reader
val openWr : string -> bin_writer
val openApp: string -> bin_writer
val openString: string -> text_reader
val stdIn : bin_reader
val stdOut: bin_writer
val stdErr: bin_writer
val translateIn : bin_reader-> text_reader
val translateOut : bin_writer-> text_writer
end
;
