require "../winsys/__capi";
require "../winsys/__menus";
require "../utils/__lists";
require "../utils/__crash";
require "../main/__user_options";
require "../main/__preferences";
require "../machine/__machspec";
require "../interpreter/__user_context";
require "../interpreter/__shell_utils";
require "../interpreter/__entry";
require "../system/__getenv";
require "../typechecker/__types";
require "../editor/__custom";
require "_gui_utils";
structure GuiUtils_ =
GuiUtils (
structure Capi = Capi_
structure Lists = Lists_
structure Crash = Crash_
structure UserOptions = UserOptions_
structure Preferences = Preferences_
structure MachSpec = MachSpec_
structure Menus = Menus_
structure UserContext = UserContext_
structure ShellUtils = ShellUtils_
structure Entry = Entry_
structure Getenv = Getenv_
structure Types = Types_
structure CustomEditor = CustomEditor_
);
