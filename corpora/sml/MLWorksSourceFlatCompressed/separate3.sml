(
Shell.Project.newProject (OS.Path.fromUnixPath "/tmp");
let
val path = OS.Path.concat [OS.FileSys.getDir(), "static_modules"]
val files = map (fn s => OS.Path.concat [path, s])
["separate3_b.sml",
"separate3_a.sml"]
in
Shell.Project.setFiles (files)
end;
Shell.Project.setTargetDetails "separate3_b.sml";
Shell.Project.setTargets ["separate3_b.sml"];
Shell.Project.forceCompileAll();
Shell.Project.loadAll()
);
