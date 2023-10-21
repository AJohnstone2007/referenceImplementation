require "../winsys/__capi";
require "../winsys/__menus";
require "../utils/__lists";
require "../main/__user_options";
require "../main/__preferences";
require "__tooldata";
require "__inspector_tool";
require "__file_viewer";
require "__gui_utils";
require "../interpreter/__shell_utils";
require "../interpreter/__save_image";
require "_context";
structure ContextHistory_ = ContextHistory (
structure Capi = Capi_
structure Lists = Lists_
structure UserOptions = UserOptions_
structure Preferences = Preferences_
structure ToolData = ToolData_
structure InspectorTool = InspectorTool_
structure FileViewer = FileViewer_
structure GuiUtils = GuiUtils_
structure Menus = Menus_
structure SaveImage = SaveImage_
structure ShellUtils = ShellUtils_
);
