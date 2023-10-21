signature LIB_ML =
sig
val registerExternalValue : string * 'a -> unit
val deleteExternalValue : string -> unit
val externalValues : unit -> string list
val clearExternalValues : unit -> unit
end
;
