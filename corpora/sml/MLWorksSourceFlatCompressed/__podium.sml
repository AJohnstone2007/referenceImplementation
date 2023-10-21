require "../main/__user_options";
require "../main/__preferences";
require "../main/__version";
require "../main/__license";
require "../main/__proj_file";
require "../winsys/__capi";
require "../debugger/__ml_debugger";
require "^.gui.__debugger_window";
require "^.gui.__listener";
require "^.gui.__tooldata";
require "^.gui.__gui_utils";
require "^.gui.__browser_tool";
require "^.gui.__proj_workspace";
require "^.gui.__context";
require "^.gui.__break_trace";
require "^.gui.__sys_messages";
require "^.gui.__proj_properties";
require "^.gui.__path_tool";
require "../interpreter/__save_image";
require "../winsys/__menus";
require "_podium";
structure Podium_ = Podium (
structure Capi = Capi_
structure UserOptions = UserOptions_
structure Preferences = Preferences_
structure Version = Version_
structure License = License_
structure Debugger_Window = DebuggerWindow_
structure ToolData = ToolData_
structure Menus = Menus_
structure Listener = Listener_
structure BrowserTool = BrowserTool_
structure ProjectWorkspace = ProjectWorkspace_
structure ContextHistory = ContextHistory_
structure Ml_Debugger = Ml_Debugger_
structure GuiUtils = GuiUtils_
structure SaveImage = SaveImage_
structure BreakTrace = BreakTrace_
structure SysMessages = SysMessages_
structure ProjProperties = ProjProperties_
structure PathTool = PathTool_
structure ProjFile = ProjFile_
);
