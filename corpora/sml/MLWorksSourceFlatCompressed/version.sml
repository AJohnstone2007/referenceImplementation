signature VERSION =
sig
datatype kind = MILESTONE of int
| ALPHA of int
| BETA of int
| FULL of int
datatype edition = ENTERPRISE | PERSONAL | PROFESSIONAL
val edition: unit -> edition
val versionString : unit -> string
val get_status : unit -> kind
end
;
