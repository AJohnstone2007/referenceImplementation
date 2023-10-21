require "../winsys/__capi";
require "../winsys/__menus";
require "../utils/__lists";
require "../interpreter/__shell_utils";
require "__gui_utils";
require "_console";
structure Console_ = Console (
structure Capi = Capi_
structure Lists = Lists_
structure ShellUtils = ShellUtils_
structure GuiUtils = GuiUtils_
structure Menus = Menus_
);
