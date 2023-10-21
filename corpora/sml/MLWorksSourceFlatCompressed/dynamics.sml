signature DYNAMICS =
sig
type object
type constraint
type agent
type word
type context
val resetUnknownsCounter: unit -> unit
val nullCntxt: unit -> context
val index: context * int -> (int ref * bool ref * object ref)
val setx: context * int * (int ref * bool ref * object ref) -> unit
val valOf: context * int -> object ref
val suspensionNumber: context * int -> int ref
val traceStatus: context * int -> bool ref
val buildCntxt: object ref list * int * context * bool ref -> context
val makePtr: object * context -> object
val stripPtr: object -> object
val instantiate: object * context -> object
val instList: object list * context -> object list
val instRefList: object ref list * context -> object list
val instPARAMS: object * object list * object ref list -> object
val instPList: object list * object list * object ref list -> object list
val instantiateConstraint: constraint * context
* object list * object ref list -> constraint
val instantiateAgent: agent * context
* object list * object ref list -> agent
val setVal: object ref * object * bool ref * context -> unit
datatype condition = varsEq of bool * object list * object ref list
| waitFixed of object list * constraint
| allButOneFixed of object list * constraint
| oneOf of object list * bool ref * constraint
| rangeNarrowed of object * constraint
val conditionToWords: condition * context
* object list * object ref list -> word list
val negate: condition -> condition
datatype answer = yes | no | maybe of condition
val isYes: answer -> bool
end;
