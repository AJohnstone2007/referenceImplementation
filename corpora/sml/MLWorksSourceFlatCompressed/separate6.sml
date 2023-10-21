(
Shell.Project.newProject (OS.Path.fromUnixPath "/tmp");
Shell.Project.setFiles
[OS.Path.concat [OS.FileSys.getDir(), "static_modules", "separate6_a.sml"]];
Shell.Project.setTargetDetails "separate6_a.sml";
Shell.Project.setTargets ["separate6_a.sml"];
Shell.Project.forceCompileAll();
Shell.Project.loadAll()
);
