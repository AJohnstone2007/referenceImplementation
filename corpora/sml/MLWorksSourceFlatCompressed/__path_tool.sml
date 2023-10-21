require "../utils/__lists";
require "../winsys/__capi";
require "../winsys/__menus";
require "../main/__mlworks_io";
require "../main/__info";
require "../main/__options";
require "../system/__os";
require "_path_tool";
structure PathTool_ =
PathTool
(structure Lists = Lists_
structure Capi = Capi_
structure Menus = Menus_
structure Io = MLWorksIo_
structure Info = Info_
structure Options = Options_
structure OS = OS)
;
