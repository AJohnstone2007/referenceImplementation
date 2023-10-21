require "../utils/map";
require "../basics/ident";
signature DATATYPES =
sig
structure NewMap : MAP
structure Ident : IDENT
eqtype Stamp
type 'a StampMap
datatype OverLoaded =
UNARY of Ident.ValId * Ident.TyVar |
BINARY of Ident.ValId * Ident.TyVar |
PREDICATE of Ident.ValId * Ident.TyVar
datatype Strname =
STRNAME of Stamp |
METASTRNAME of Strname ref |
NULLNAME of Stamp
datatype InstanceInfo =
ZERO
| ONE of int
| TWO of int * int
datatype Tyname =
TYNAME of (Stamp * string * int * bool ref
* Valenv ref * string option * bool ref
* Valenv ref * int) |
METATYNAME of (Tyfun ref * string * int * bool ref * Valenv ref
* bool ref)
and Type =
METATYVAR of ((int * Type * Instance) ref * bool * bool) |
META_OVERLOADED of
(Type ref * Ident.TyVar * Ident.ValId * Ident.Location.T) |
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
NULL_TYFUN of Stamp * Tyfun ref
and Typescheme =
SCHEME of (int * (Type * (Instance ref * Instance ref option ref) option))
| UNBOUND_SCHEME of Type * (Instance ref * Instance ref option ref) option
| OVERLOADED_SCHEME of OverLoaded
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
val empty_valenv : Valenv
datatype type_error_atom =
Err_String of string
| Err_Type of Type
| Err_Scheme of Type
| Err_Reset
end
;
