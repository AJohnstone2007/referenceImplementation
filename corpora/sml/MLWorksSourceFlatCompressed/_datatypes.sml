require "../utils/map";
require "../basics/ident";
require "stamp";
require "datatypes";
functor Datatypes (structure Stamp : STAMP
structure Ident : IDENT
structure NewMap : MAP
) : DATATYPES =
struct
structure Ident = Ident
structure NewMap = NewMap
structure Stamp = Stamp
type Stamp= Stamp.Stamp
type 'a StampMap = 'a Stamp.Map.T
datatype OverLoaded =
UNARY of Ident.ValId * Ident.TyVar |
BINARY of Ident.ValId * Ident.TyVar |
PREDICATE of Ident.ValId * Ident.TyVar
datatype Strname =
STRNAME of Stamp.Stamp |
METASTRNAME of Strname ref |
NULLNAME of Stamp.Stamp
datatype InstanceInfo =
ZERO
| ONE of int
| TWO of int * int
datatype Tyname =
TYNAME of (Stamp.Stamp * string * int * bool ref
* Valenv ref * string option * bool ref
* Valenv ref * int) |
METATYNAME of (Tyfun ref * string * int * bool ref * Valenv ref * bool ref)
and Type =
METATYVAR of ((int * Type * Instance) ref * bool * bool) |
META_OVERLOADED of
(Type ref * Ident.TyVar * Ident.ValId * Ident.Location.T)|
TYVAR of ((int * Type * Instance) ref * Ident.TyVar) |
METARECTYPE of ((int * bool * Type * bool * bool) ref) |
RECTYPE of (Ident.Lab,Type) NewMap.map |
FUNTYPE of (Type * Type) |
CONSTYPE of ((Type list) * Tyname) |
DEBRUIJN of (int * bool * bool
* (int * Type * Instance) ref option) |
NULLTYPE
and Tyfun =
TYFUN of Type * int |
ETA_TYFUN of Tyname |
NULL_TYFUN of Stamp.Stamp * Tyfun ref
and Typescheme =
SCHEME of (int * (Type * (Instance ref * Instance ref option ref) option)) |
UNBOUND_SCHEME of Type * (Instance ref * Instance ref option ref) option |
OVERLOADED_SCHEME of OverLoaded
and Instance =
INSTANCE of (int * Type * Instance) ref list
| SIGNATURE_INSTANCE of InstanceInfo
| NO_INSTANCE
and Valenv = VE of int ref * ((Ident.ValId,Typescheme) NewMap.map)
and Tystr = TYSTR of (Tyfun * Valenv)
and Tyenv = TE of (Ident.TyCon,Tystr) NewMap.map
and Env = ENV of (Strenv * Tyenv * Valenv)
and Structure =
STR of (Strname * MLWorks.Internal.Value.ml_value option ref * Env) |
COPYSTR of ((Strname StampMap * Tyname StampMap) * Structure)
and Strenv = SE of (Ident.StrId,Structure) NewMap.map
datatype DebuggerStr =
DSTR of (Ident.StrId,DebuggerStr) NewMap.map * (Ident.TyCon,int) NewMap.map
* (Ident.ValId,int option) NewMap.map |
EMPTY_DSTR
val empty_valenv = VE (ref 0,NewMap.empty (Ident.valid_lt, Ident.valid_eq))
datatype type_error_atom =
Err_String of string
| Err_Type of Type
| Err_Scheme of Type
| Err_Reset
end
;
