require "../utils/crash";
require "../basis/__word32";
require "__os";
require "../main/link_support";
functor UnixLinkSupport (
structure Crash : CRASH
) : LINK_SUPPORT =
struct
datatype target_type = DLL | EXE
datatype linker_type = GNU | LOCAL
fun link
{objects,
libs,
target,
target_path,
dll_or_exe,
base,
make_map,
linker
} =
Crash.unimplemented"Unix linker support: link"
fun archive{archive : string, files : string list} =
Crash.unimplemented"Unix linker support: archive"
fun make_stamp _ = Crash.unimplemented"Unix linker support: make_stamp"
fun gcc _ = Crash.unimplemented"Unix linker support: gcc"
end
;
