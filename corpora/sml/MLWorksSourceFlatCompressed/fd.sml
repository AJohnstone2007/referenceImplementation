local
structure s = Shell.Project;
in
val _ = s.openProject"../src/mlworks.mlp";
val _ = s.setConfiguration"I386/NT";
val {binariesLoc, libraryPath, objectsLoc} = s.showLocations();
val _ = s.setLocations
{binariesLoc=binariesLoc, libraryPath=libraryPath, objectsLoc="objects"};
val _ = s.setMode"Debug";
val _ = s.setTargets["__batch.sml"];
val _ = s.forceCompileAll();
val _ = s.loadAll();
end
;
