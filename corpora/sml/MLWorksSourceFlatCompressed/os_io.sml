local
fun hd [] = raise Match
| hd(x :: _) = x;
val _ = Shell.Options.set(Shell.Options.ValuePrinter.maximumStrDepth, 0);
val path = hd(Shell.Path.sourcePath());
val _ = Shell.Project.openProject(OS.Path.joinDirFile{dir=path, file="basis.mlp"});
val config_files = #files(Shell.Project.showConfigurationDetails"I386/NT")
val files = Shell.Project.showFiles() @ config_files
val _ = Shell.Project.closeProject()
val files =
map
(fn file =>
OS.Path.joinDirFile{dir=path, file=file})
files
val _ = Shell.Project.newProject(OS.Path.fromUnixPath "/tmp");
val _ = Shell.Project.setFiles files;
val _ = Shell.Project.setTargetDetails "__win32.sml";
val _ = Shell.Project.setTargetDetails "__os_io.sml";
val _ = Shell.Project.setTargets ["__win32.sml", "__os_io.sml"];
val _ = Shell.Project.forceCompileAll();
in
val _ = Shell.Project.loadAll();
end;
val kind_a = OSIO_.kind (Win32_.IODESC 0) = OSIO_.Kind.file;
val kind_b = OSIO_.kind (Win32_.IODESC 1) = OSIO_.Kind.file;
val kind_c = OSIO_.kind (Win32_.IODESC 2) = OSIO_.Kind.file;
val kind_e = (ignore(OSIO_.kind (Win32_.IODESC 42)); false) handle OS.SysErr _ => true;
local
fun makePD n =
case OSIO_.pollDesc (Win32_.IODESC n) of
NONE => raise OS.SysErr ("foo", NONE)
| SOME pd => (OSIO_.pollIn o OSIO_.pollOut) pd
in
val poll_a = (ignore(OSIO_.poll ([makePD 0], SOME Time.zeroTime)); false) handle OS.SysErr _ => true;
end
;
