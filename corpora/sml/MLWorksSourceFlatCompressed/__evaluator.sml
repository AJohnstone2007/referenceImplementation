require "../utils/__lists";
require "../main/__info";
require "../main/__preferences";
require "../main/__user_options";
require "../winsys/__capi";
require "../winsys/__menus";
require "__tooldata";
require "__gui_utils";
require "_evaluator";
structure Evaluator_ = Evaluator (
structure Info = Info_
structure Lists = Lists_
structure Preferences = Preferences_
structure UserOptions = UserOptions_
structure ToolData = ToolData_
structure GuiUtils = GuiUtils_
structure Menus = Menus_
structure Capi = Capi_
);
