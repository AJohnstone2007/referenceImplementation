require "../winsys/__capi";
require "../utils/__lists";
require "../utils/__crash";
require "../utils/__mutex";
require "../main/__user_options";
require "../main/__preferences";
require "../debugger/__ml_debugger";
require "../debugger/__newtrace";
require "../interpreter/__tty_listener";
require "../interpreter/__shell";
require "../interpreter/__shell_utils";
require "__tooldata";
require "__error_browser";
require "__debugger_window";
require "__profile_tool";
require "__inspector_tool";
require "../winsys/__menus";
require "__gui_utils";
require "../interpreter/__entry";
require "../interpreter/__save_image";
require "_listener";
structure Listener_ = Listener (
structure Capi = Capi_
structure Lists = Lists_
structure Crash = Crash_
structure UserOptions = UserOptions_
structure Preferences = Preferences_
structure Ml_Debugger = Ml_Debugger_
structure Trace = Trace_
structure ProfileTool = ProfileTool_
structure InspectorTool = InspectorTool_
structure TTYListener = TTYListener_
structure Shell = Shell_
structure ShellUtils = ShellUtils_
structure ToolData = ToolData_
structure DebuggerWindow = DebuggerWindow_
structure GuiUtils = GuiUtils_
structure Menus = Menus_
structure Entry = Entry_
structure ErrorBrowser = ErrorBrowser_
structure Mutex = Mutex
structure SaveImage = SaveImage_
);
