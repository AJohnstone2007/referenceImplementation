signature CODE =
sig
datatype sorts = variable | wildcard | atomic | tree | number
datatype object = VOID
| UNKNOWN of int
| POINTER of string * int * bool ref * int ref * object ref
| VARIABLE of string * int
| PARAMETER of string * int
| WILDCARD
| ATOMIC of string
| HERBRAND of object * object list * object
| INFINITY | MINFINITY
| NUMBER of real
| RANGE of object * bool * bool * object
| PLUS of object * object
| MINUS of object * object
| TIMES of object * object
| DIVIDES of object * object
datatype constraint = known of object
| fixed of object
| neg of constraint
| consistent of constraint
| eq of object * object
| greater of bool * object * object
| less of bool * object * object
| tt | ff
| none
datatype agent = success
| failure
| guard of constraint * agent
| select of constraint * agent
| tell of constraint
| call of string * object list
| par of agent list
| alt of agent list
datatype clause =
clause of string * object list * int * constraint * agent
val parAgents: agent * agent -> agent
val altAgents: agent * agent -> agent
exception NotFound of string
val store: clause -> unit
val retrieve: string -> clause list
val wipe: string -> unit
val retrieveAll: unit -> clause list list
val wipeAll: unit -> unit
datatype word = combinator of string
| separator of string
| terminator of string
| openParen of string
| closeParen of string
| characters of string
val agentToWords: agent -> word list
val constraintToWords: constraint -> word list
val objectToWords: object -> word list
val objectListToWords: object list -> word list
val objectRefListToWords: object ref list -> word list
val isUnknown: object -> bool
val isNumber: object -> bool
val isConstant: object -> bool
val isRange: object -> bool
val sameObjects: object * object -> bool
end;
