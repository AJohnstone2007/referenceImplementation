signature GETENV =
sig
val env_path_to_list: string -> string list
val get_source_path: unit -> string option
val get_startup_dir: unit -> string option
val get_pervasive_dir: unit -> string option
val get_doc_dir: unit -> string option
val get_version_setting: unit -> string option
val get_startup_filename: unit -> string option
val get_preferences_filename: unit -> string option
exception BadHomeName of string
val expand_home_dir: string -> string
val get_object_path: unit -> string option
end;
