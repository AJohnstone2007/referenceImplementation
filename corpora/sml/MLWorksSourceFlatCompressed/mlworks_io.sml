signature MLWORKS_IO =
sig
type ModuleId
type Location
val pervasive_library_name : string
val builtin_library_name : string
val pervasive_library_id : ModuleId
val builtin_library_id : ModuleId
val get_source_path : unit -> string list
val set_source_path : string list -> unit
val set_source_path_without_expansion : string list -> unit
val set_source_path_from_env : Location * bool -> unit
val set_source_path_from_string : string * Location -> unit
exception NotSet of string
val get_pervasive_dir : unit -> string
val set_pervasive_dir : string * Location -> unit
val set_pervasive_dir_from_env : Location -> unit
val get_object_path : unit -> string
val set_object_path : string * Location -> unit
val set_object_path_from_env : Location -> unit
end
;
