require "../utils/__lists";
require "../utils/__crash";
require "../system/__getenv";
require "__windows_gui";
require "__capitypes";
require "__labelstrings";
require "../rts/gen/__control_names";
require "../main/__version";
require "_menus";
structure Menus_ = Menus(structure Lists = Lists_
structure Crash = Crash_
structure WindowsGui = WindowsGui
structure LabelStrings = LabelStrings_
structure CapiTypes = CapiTypes_
structure ControlName = ControlName
structure Getenv = Getenv_
structure Version = Version_
);
