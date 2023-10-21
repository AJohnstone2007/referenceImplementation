require "../winsys/__capi";
require "../winsys/__menus";
require "../main/__user_options";
require "../main/__preferences";
require "../utils/__lists";
require "../utils/__lisp";
require "../interpreter/__inspector_values";
require "../interpreter/__shell_utils";
require "__gui_utils";
require "__graph_widget";
require "__tooldata";
require "_inspector_tool";
structure InspectorTool_ = InspectorTool (
structure Capi = Capi_
structure GraphWidget = GraphWidget_
structure ShellUtils = ShellUtils_
structure Lists = Lists_
structure LispUtils = LispUtils_
structure InspectorValues = InspectorValues_
structure UserOptions = UserOptions_
structure Preferences = Preferences_
structure GuiUtils = GuiUtils_
structure ToolData = ToolData_
structure Menus = Menus_
)
;
