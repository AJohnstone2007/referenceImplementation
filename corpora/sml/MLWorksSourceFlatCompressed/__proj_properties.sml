require "../basis/__list";
require "../system/__os";
require "../machine/__machspec";
require "../main/__toplevel";
require "../main/__info";
require "../main/__proj_file";
require "../main/__project";
require "../winsys/__capi";
require "../winsys/__menus";
require "../interpreter/__incremental";
require "../basics/__module_id";
require "_proj_properties";
structure ProjProperties_ =
ProjProperties (
structure List = List
structure MachSpec = MachSpec_
structure Info = Info_
structure TopLevel = TopLevel_
structure ProjFile = ProjFile_
structure Project = Project_
structure Capi = Capi_
structure Menus = Menus_
structure OS = OS
structure Incremental = Incremental_
structure ModuleId = ModuleId_
);
