require "../winsys/__capi";
require "../winsys/__menus";
require "../utils/__lists";
require "../utils/__crash";
require "../main/__user_options";
require "../main/__preferences";
require "../interpreter/__shell_utils";
require "../interpreter/__save_image";
require "../interpreter/__entry";
require "__inspector_tool";
require "__gui_utils";
require "__graph_widget";
require "__tooldata";
require "_browser_tool";
structure BrowserTool_ =
BrowserTool (
structure Capi = Capi_
structure GraphWidget = GraphWidget_
structure Lists = Lists_
structure Crash = Crash_
structure UserOptions = UserOptions_
structure Preferences = Preferences_
structure Menus = Menus_
structure ToolData = ToolData_
structure InspectorTool = InspectorTool_
structure GuiUtils = GuiUtils_
structure ShellUtils = ShellUtils_
structure Entry = Entry_
structure SaveImage = SaveImage_
)
;
