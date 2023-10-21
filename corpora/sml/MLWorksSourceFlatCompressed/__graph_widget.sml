require "../utils/__lists";
require "../winsys/__capi";
require "../winsys/__menus";
require "__gui_utils";
require "_graph_widget";
structure GraphWidget_ = GraphWidget (structure Lists = Lists_
structure Capi = Capi_
structure Menus = Menus_
structure GuiUtils = GuiUtils_
)
;
