signature MLWORKS_DYNAMIC_LIBRARY =
sig
type library
type syserror
exception SysErr of (string * syserror option)
val openLibrary : string * string -> library
val bind : string -> 'a
val closeLibrary : library * string option -> unit
end
;
