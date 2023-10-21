require "../basis/__word32";
signature LINK_SUPPORT =
sig
datatype target_type = DLL | EXE
datatype linker_type = GNU | LOCAL
val link :
{objects : string list,
libs : string list,
target : string,
target_path : string,
dll_or_exe : target_type,
base : Word32.word,
make_map : bool,
linker : linker_type
} -> unit
val archive : {archive : string, files : string list} -> unit
val make_stamp : string -> string
val gcc : string -> unit
end
;
