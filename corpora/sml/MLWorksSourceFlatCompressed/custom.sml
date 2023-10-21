signature CUSTOM_EDITOR =
sig
val addCommand : string * string -> unit
val addConnectDialog : string * string * string list -> unit
val removeCommand : string -> string
val removeDialog : string -> (string * string list)
val commandNames : unit -> string list
val dialogNames : unit -> string list
val getCommandEntry : string -> string
val getDialogEntry : string -> (string * string list)
end;
