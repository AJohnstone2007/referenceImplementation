signature FILE_DIALOG =
sig
type Widget
val open_file_dialog : Widget * string * bool -> string list option
val open_dir_dialog : Widget -> string option
val set_dir_dialog : Widget -> string option
val save_as_dialog : Widget * string -> string option
end;
