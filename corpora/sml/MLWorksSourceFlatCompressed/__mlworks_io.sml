require "../system/__os";
require "../main/__info";
require "../basics/__module_id";
require "../system/__getenv";
require "^.system.__os";
require "_mlworks_io";
structure MLWorksIo_ = MLWorksIo(
structure OS = OS
structure Path = OS.Path;
structure Info = Info_
structure ModuleId = ModuleId_
structure Getenv = Getenv_
val pervasive_library_name = "__pervasive_library"
val builtin_library_name = "__builtin_library"
val default_source_path = ["."]
);
