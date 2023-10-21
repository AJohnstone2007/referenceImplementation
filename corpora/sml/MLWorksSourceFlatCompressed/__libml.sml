require "libml";
structure LibML_ : LIB_ML =
struct
val env = MLWorks.Internal.Runtime.environment;
val registerExternalValue : string * 'a -> unit =
fn x => env "add external ml value" x
val deleteExternalValue : string -> unit =
env "delete external ml value"
val externalValues : unit -> string list =
env "external ml value names"
val clearExternalValues : unit -> unit =
env "clear external ml values"
end;
