require "../winsys/__capi";
require "../winsys/__menus";
require "../utils/__lists";
require "../interpreter/__shell_utils";
require "__gui_utils";
require "__tooldata";
require "_error_browser";
structure ErrorBrowser_ = ErrorBrowser (
structure Capi = Capi_
structure Lists = Lists_
structure ShellUtils = ShellUtils_
structure GuiUtils = GuiUtils_
structure Menus = Menus_
structure ToolData = ToolData_
);
