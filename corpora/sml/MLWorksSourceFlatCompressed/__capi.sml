require "../utils/__lists";
require "../basis/__word32";
require "../main/__version";
require "__menus";
require "__windows_gui";
require "__labelstrings";
require "__capitypes";
require "_capi";
structure Capi_ =
Capi (structure Lists = Lists_
structure Menus = Menus_
structure WindowsGui = WindowsGui
structure LabelStrings = LabelStrings_
structure CapiTypes = CapiTypes_
structure Version = Version_
structure Word32 = Word32
)
;
