(
Shell.Project.newProject (OS.Path.fromUnixPath "/tmp");
let
val path = OS.Path.concat [OS.FileSys.getDir(), "static_modules"]
val files = map (fn s => OS.Path.concat [path, s])
["separate2_c.sml",
"separate2_b.sml",
"separate2_a.sml"]
in
Shell.Project.setFiles (files)
end;
Shell.Project.setTargetDetails "separate2_c.sml";
Shell.Project.setTargets ["separate2_c.sml"];
Shell.Project.forceCompileAll();
Shell.Project.loadAll()
);
