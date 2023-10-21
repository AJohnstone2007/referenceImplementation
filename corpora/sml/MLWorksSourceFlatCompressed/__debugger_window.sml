require "../winsys/__capi";
require "../winsys/__menus";
require "../utils/__lists";
require "../debugger/__newtrace";
require "../debugger/__ml_debugger";
require "../main/__preferences";
require "../interpreter/__shell_utils";
require "../typechecker/__types";
require "__tooldata";
require "__inspector_tool";
require "__file_viewer";
require "../debugger/__stack_frame";
require "__gui_utils";
require "../main/__user_options";
require "_debugger_window";
structure DebuggerWindow_ =
DebuggerWindow
(structure Capi = Capi_
structure Lists = Lists_
structure UserOptions = UserOptions_
structure Types = Types_
structure Trace = Trace_
structure Ml_Debugger = Ml_Debugger_
structure Preferences = Preferences_
structure ToolData = ToolData_
structure InspectorTool = InspectorTool_
structure FileViewer = FileViewer_
structure GuiUtils = GuiUtils_
structure ShellUtils = ShellUtils_
structure Menus = Menus_
structure StackFrame = StackFrame_)
;
