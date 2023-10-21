(
Shell.Project.newProject (OS.Path.fromUnixPath "/tmp");
let
val path = OS.Path.concat [OS.FileSys.getDir(), "static_modules"]
val files = map (fn s => OS.Path.concat [path, s])
["separate1_c.sml",
"separate1_b.sml",
"separate1_a.sml"]
in
Shell.Project.setFiles (files)
end;
Shell.Project.setTargetDetails "separate1_c.sml";
Shell.Project.setTargets ["separate1_c.sml"];
Shell.Project.forceCompileAll();
Shell.Project.loadAll()
);
