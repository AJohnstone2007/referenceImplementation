signature SAVE_IMAGE =
sig
type ShellData
val showBanner: unit -> bool
val saveImage: bool * (string -> unit) -> string * bool -> unit
val add_with_fn: (((bool * (string -> unit)) -> (string * bool) -> unit) -> (bool * (string -> unit)) -> (string * bool) -> unit) -> unit
val startGUI: bool -> ShellData -> unit
end;
