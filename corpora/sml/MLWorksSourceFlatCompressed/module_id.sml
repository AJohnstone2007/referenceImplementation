signature MODULE_ID =
sig
eqtype Symbol
type Location
type Path
type ModuleId
val lt: ModuleId * ModuleId -> bool
val eq: ModuleId * ModuleId -> bool
val is_pervasive: ModuleId -> bool
val string: ModuleId -> string
val module_unit_to_string : ModuleId * string -> string
val from_string: string * Location -> ModuleId
val perv_from_string: string * Location -> ModuleId
val from_mo_string: string * Location -> ModuleId
val from_string': string -> ModuleId option
val from_host: string * Location -> ModuleId
exception NoParent
val path: ModuleId -> Path
val path_string: Path -> string
val empty_path: Path
val add_path: Path * ModuleId -> ModuleId
val parent: Path -> Path
val from_require_string: string * Location -> ModuleId
val perv_from_require_string: string * Location -> ModuleId
end;
