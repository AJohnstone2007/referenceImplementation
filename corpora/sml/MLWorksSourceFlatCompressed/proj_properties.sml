signature PROJ_PROPERTIES =
sig
type Widget
type config_details =
{name: string,
files: string list,
library: string list}
val need_saved: bool ref
val new_project: Widget -> bool
val save_project: Widget -> bool
val save_project_as: Widget -> bool
val open_project: Widget -> (unit -> unit) -> bool
val test_save: Widget * bool -> bool
val mk_files_dialog: Widget * (string list -> unit) -> (unit -> unit) * (unit -> unit)
val mk_subprojects_dialog:
Widget * (string list -> unit) * (bool -> unit) -> (unit -> unit) * (unit -> unit)
val mk_targets_dialog: Widget * (string list -> unit) -> (unit -> unit) * (unit -> unit)
val mk_modes_dialog: Widget * (string list -> unit) -> (unit -> unit) * (unit -> unit)
val mk_configs_dialog: Widget * (unit -> unit)
-> (unit -> unit) * (unit -> unit)
val mk_library_dialog: Widget * (string -> unit) -> (unit -> unit) * (unit -> unit)
val set_objects_dir: Widget * bool ref * (string -> unit) -> unit
val set_binaries_dir: Widget * bool ref * (string -> unit) -> unit
val setRelObjBin: bool * (string -> unit) * (bool ref) -> bool -> unit
val mk_about_dialog: Widget -> (unit -> unit) * (unit -> unit)
end;