let open Shell.Project in
newProject (OS.Path.fromUnixPath "/tmp");
setModeDetails("SubMode",
{location = "location",
generate_interruptable_code = false,
generate_interceptable_code = true,
generate_debug_info = false,
generate_variable_debug_info = true,
optimize_leaf_fns = false,
optimize_tail_calls = true,
optimize_self_tail_calls = false,
mips_r4000 = true,
sparc_v7 = false});
setMode "SubMode";
saveProjectAs (OS.Path.fromUnixPath "/tmp/subproject.mlp")
end;
let open Shell.Project in
newProject (OS.Path.fromUnixPath "/tmp");
setAboutInfo {description="Dummy project", version="V1"};
setConfigurationDetails ("Config", {library=["library"], files=["files"]});
setConfigurationDetails ("RConfig", {library=["rlibrary"], files=["rfiles"]});
setConfiguration "Config";
removeConfiguration "RConfig";
setLocations {binariesLoc = "binariesLoc", libraryPath = ["path1", "path2"],
objectsLoc = "objectsLoc"};
setModeDetails("Mode",
{location = "location",
generate_interruptable_code = true,
generate_interceptable_code = false,
generate_debug_info = true,
generate_variable_debug_info = false,
optimize_leaf_fns = true,
optimize_tail_calls = false,
optimize_self_tail_calls = true,
mips_r4000 = false,
sparc_v7 = true});
setModeDetails("RMode",
{location = "rlocation",
generate_interruptable_code = false,
generate_interceptable_code = true,
generate_debug_info = false,
generate_variable_debug_info = true,
optimize_leaf_fns = false,
optimize_tail_calls = true,
optimize_self_tail_calls = false,
mips_r4000 = true,
sparc_v7 = false});
setMode "Mode";
removeMode "RMode";
setFiles (map OS.Path.fromUnixPath ["file1.sml", "sub_dir/file2.sml", "sub_dir2/file3.sml",
"sub_dir/sub_sub_dir/file4.sml"]);
setSubprojects ["subproject.mlp"];
setTargetDetails "file2.sml"; setTargetDetails "file3.sml"; setTargetDetails "file4.sml";
setTargets ["file2.sml"];
saveProjectAs (OS.Path.fromUnixPath "test_project.mlp")
end;
Shell.Project.newProject (OS.Path.fromUnixPath "/tmp");
Shell.Project.openProject(OS.Path.fromUnixPath "/tmp/test_project.mlp");
Shell.Project.showAboutInfo();
Shell.Project.showAllConfigurations();
Shell.Project.showCurrentConfiguration();
Shell.Project.showConfigurationDetails "Config";
Shell.Project.showLocations();
Shell.Project.showAllModes();
Shell.Project.showCurrentMode();
Shell.Project.showModeDetails "Mode";
map OS.Path.toUnixPath (Shell.Project.showFiles());
Shell.Project.showSubprojects();
Shell.Project.showAllTargets();
Shell.Project.showCurrentTargets();
(Shell.Project.setMode "missing-mode"; "")
handle Shell.Project.ProjectError s => s;
(Shell.Project.setConfiguration "missing-configuration"; "")
handle Shell.Project.ProjectError s => s;
(Shell.Project.setTargets ["missing-target"]; "")
handle Shell.Project.ProjectError s => s;
(Shell.Project.removeMode "missing-mode"; "")
handle Shell.Project.ProjectError s => s;
(Shell.Project.removeConfiguration "missing-configuration"; "")
handle Shell.Project.ProjectError s => s;
(Shell.Project.removeTarget "missing-target"; "")
handle Shell.Project.ProjectError s => s;
(Shell.Project.setTargetDetails "invalid-target"; "")
handle Shell.Project.ProjectError s => s;
(Shell.Project.removeMode (Shell.Project.showCurrentMode()); "")
handle Shell.Project.ProjectError s => s;
Shell.Project.removeConfiguration (Shell.Project.showCurrentConfiguration());
app Shell.Project.removeTarget (Shell.Project.showCurrentTargets());
Shell.Project.showCurrentTargets();
OS.FileSys.remove (OS.Path.fromUnixPath "/tmp/subproject.mlp");
OS.FileSys.remove (OS.Path.fromUnixPath "/tmp/test_project.mlp");
