signature PROJ_FILE =
sig
type error_info
type location
exception InvalidProjectFile of string
val changed : unit -> bool
datatype target_type = IMAGE | OBJECT_FILE | EXECUTABLE | LIBRARY
type target_details = (string * target_type)
exception NoTargetDetailsFound of string
val getTargetDetails: string * target_details list -> target_details
type mode_details =
{name: string,
location: string ref,
generate_interruptable_code: bool ref,
generate_interceptable_code: bool ref,
generate_debug_info: bool ref,
generate_variable_debug_info: bool ref,
optimize_leaf_fns: bool ref,
optimize_tail_calls: bool ref,
optimize_self_tail_calls: bool ref,
mips_r4000: bool ref,
sparc_v7: bool ref}
exception NoModeDetailsFound of string
val getModeDetails: string * mode_details list -> mode_details
type config_details =
{name: string,
files: string list,
library: string list}
exception NoConfigDetailsFound of string
val getConfigDetails: string * config_details list -> config_details
val modifyConfigDetails:
config_details * config_details list -> config_details list
val getProjectName: unit -> string option
val getProjectDir: unit -> string
val getFiles: unit -> string list
val getSubprojects: unit -> string list
val getTargets: unit -> string list * string list * target_details list
val getModes: unit -> (string list * mode_details list *
string option)
val getConfigurations: unit -> (string list * config_details list *
string option)
val getLocations: unit -> string list * string * string
val getAboutInfo: unit -> string * string
val setFiles: string list -> unit
val setSubprojects: string list -> unit
exception InvalidTarget of string
val setTargets: string list * string list * target_details list -> unit
val setModes: string list * mode_details list -> unit
val setInitialModes: unit -> unit
val setConfigurations: string list * config_details list -> unit
val setLocations: string list * string * string -> unit
val setAboutInfo: string * string -> unit
val setCurrentConfiguration: (error_info * location) -> string option -> unit
val setCurrentMode: (error_info * location) -> string -> unit
val setCurrentTargets: (error_info * location) -> string list -> unit
val new_proj: string -> unit
val open_proj: string -> unit
val save_proj: string -> unit
val close_proj: unit -> unit
val peek_project: string ->
{name: string,
files: string list,
curTargets: string list,
disTargets: string list,
subprojects: string list,
libraryPath: string list,
objectsLoc: string,
binariesLoc: string,
currentConfig: string option,
configDetails: config_details list,
currentMode: string option,
modeDetails: mode_details list}
val getAllSubProjects: string -> string list
val getSubTargets: string -> string list
end
;
