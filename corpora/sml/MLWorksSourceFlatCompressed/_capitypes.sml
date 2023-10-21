require "capitypes";
require "windows_gui";
functor CapiTypes (structure WindowsGui : WINDOWS_GUI) : CAPITYPES =
struct
type Hwnd = WindowsGui.hwnd
datatype Widget =
NONE
| REAL of Hwnd * Widget
| FAKE of Widget * Widget list
fun get_real NONE = WindowsGui.nullWindow
| get_real (REAL (hwnd,_)) = hwnd
| get_real (FAKE (parent,_)) = get_real parent
end
;
