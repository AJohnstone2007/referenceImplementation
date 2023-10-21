require "../winsys/__capi";
require "../winsys/__menus";
require "../utils/__lists";
require "../main/__user_options";
require "../debugger/__newtrace";
require "__tooldata";
require "__gui_utils";
require "_break_trace";
structure BreakTrace_ =
BreakTrace (
structure Capi = Capi_
structure Lists = Lists_
structure UserOptions = UserOptions_
structure Trace = Trace_
structure Menus = Menus_
structure GuiUtils = GuiUtils_
structure ToolData = ToolData_
);
