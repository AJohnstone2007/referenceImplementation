require "../basics/__module_id";
require "../main/__preferences";
require "../main/__user_options";
require "../main/__project";
require "../main/__toplevel";
require "../main/__proj_file";
require "../system/__editor";
require "../utils/__btree";
require "../utils/__crash";
require "../basis/__list";
require "../interpreter/__incremental";
require "../interpreter/__shell_utils";
require "../interpreter/__save_image";
require "../debugger/__ml_debugger";
require "../winsys/__capi";
require "../winsys/__menus";
require "__graph_widget";
require "__gui_utils";
require "__tooldata";
require "__console";
require "__debugger_window";
require "__error_browser";
require "__proj_properties";
require "_proj_workspace";
structure ProjectWorkspace_ =
ProjectWorkspace (
structure ModuleId = ModuleId_
structure Preferences = Preferences_
structure UserOptions = UserOptions_
structure Editor = Editor_
structure TopLevel = TopLevel_
structure ProjFile = ProjFile_
structure Incremental = Incremental_
structure ShellUtils = ShellUtils_
structure Ml_Debugger = Ml_Debugger_
structure NewMap = BTree_
structure Crash = Crash_
structure List = List
structure Project = Project_
structure Capi = Capi_
structure Menus = Menus_
structure GraphWidget = GraphWidget_
structure GuiUtils = GuiUtils_
structure ToolData = ToolData_
structure Console = Console_
structure ErrorBrowser = ErrorBrowser_
structure DebuggerWindow = DebuggerWindow_
structure ProjProperties = ProjProperties_
structure SaveImage = SaveImage_
)
;
