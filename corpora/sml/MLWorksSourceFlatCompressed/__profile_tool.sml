require "../winsys/__capi";
require "../winsys/__menus";
require "../interpreter/__shell_utils";
require "../main/__preferences";
require "../utils/__lists";
require "../utils/__crash";
require "__bar_chart";
require "__tooldata";
require "__gui_utils";
require "_profile_tool";
structure ProfileTool_ =
ProfileTool(structure Capi = Capi_
structure Menus = Menus_
structure Preferences = Preferences_
structure ShellUtils = ShellUtils_
structure Lists = Lists_
structure Crash = Crash_
structure BarChart = BarChart_
structure ToolData = ToolData_
structure GuiUtils = GuiUtils_
);
