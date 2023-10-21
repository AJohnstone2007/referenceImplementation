require "symbol";
require "location";
signature IDENT =
sig
structure Symbol : SYMBOL
structure Location : LOCATION
datatype ValId =
VAR of Symbol.Symbol
| CON of Symbol.Symbol
| EXCON of Symbol.Symbol
| TYCON' of Symbol.Symbol
val valid_eq : ValId * ValId -> bool
val valid_order : ValId * ValId -> bool
val valid_lt : ValId * ValId -> bool
datatype TyVar = TYVAR of Symbol.Symbol * bool * bool
val tyvar_eq : TyVar * TyVar -> bool
val tyvar_order : TyVar * TyVar -> bool
val tyvar_lt : TyVar * TyVar -> bool
val int_literal_tyvar : TyVar
val real_literal_tyvar : TyVar
val word_literal_tyvar : TyVar
val real_tyvar : TyVar
val wordint_tyvar : TyVar
val realint_tyvar : TyVar
val num_tyvar : TyVar
val numtext_tyvar : TyVar
datatype TyCon = TYCON of Symbol.Symbol
datatype Lab = LAB of Symbol.Symbol
datatype StrId = STRID of Symbol.Symbol
val tycon_eq : TyCon * TyCon -> bool
val tycon_order : TyCon * TyCon -> bool
val tycon_lt : TyCon * TyCon -> bool
val lab_eq : Lab * Lab -> bool
val lab_order : Lab * Lab -> bool
val lab_lt : Lab * Lab -> bool
val strid_eq : StrId * StrId -> bool
val strid_order : StrId * StrId -> bool
val strid_lt : StrId * StrId -> bool
datatype SigId = SIGID of Symbol.Symbol
datatype FunId = FUNID of Symbol.Symbol
val sigid_eq : SigId * SigId -> bool
val sigid_order : SigId * SigId -> bool
val sigid_lt : SigId * SigId -> bool
val funid_eq : FunId * FunId -> bool
val funid_order : FunId * FunId -> bool
val funid_lt : FunId * FunId -> bool
datatype Path = NOPATH | PATH of Symbol.Symbol * Path
val followPath : (StrId * 'a -> 'a) -> Path * 'a -> 'a
val followPath' :
((StrId * 'a -> 'b) * (StrId * 'b -> 'b)) -> Path * 'a -> 'b
val mkPath : Symbol.Symbol list -> Path
val isemptyPath : Path -> bool
datatype LongValId = LONGVALID of Path * ValId
datatype LongTyCon = LONGTYCON of Path * TyCon
datatype LongStrId = LONGSTRID of Path * StrId
datatype SCon = INT of (string * Location.T) | REAL of (string * Location.T) | STRING of string | CHAR of string | WORD of (string * Location.T)
val ref_valid : ValId
val eq_valid : ValId
datatype Identifier =
VALUE of ValId |
TYPE of TyCon |
STRUCTURE of StrId |
SIGNATURE of SigId |
FUNCTOR of FunId
val dummy_identifier: Identifier
val compare_identifiers : Identifier * Identifier -> bool
end
;
