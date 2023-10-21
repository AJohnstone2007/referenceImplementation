require "../interpreter/__shell_types";
require "../interpreter/__user_context";
require "../winsys/__capi";
require "../winsys/__menus";
require "../utils/__btree";
require "../utils/__lists";
require "../main/__user_options";
require "__gui_utils";
require "_tooldata";
structure ToolData_ =
ToolData
(structure ShellTypes = ShellTypes_
structure UserContext = UserContext_
structure GuiUtils = GuiUtils_
structure Capi = Capi_
structure Map = BTree_
structure UserOptions = UserOptions_
structure Menus = Menus_
structure Lists = Lists_);
