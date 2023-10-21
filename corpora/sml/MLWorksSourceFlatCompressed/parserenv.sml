require "../utils/map";
require "../basics/ident";
signature PARSERENV =
sig
structure Map : MAP
structure Ident : IDENT
datatype Fixity = LEFT of int | RIGHT of int | NONFIX
datatype
pFE = FE of (Ident.Symbol.Symbol,Fixity) Map.map
and
pVE = VE of (Ident.Symbol.Symbol,Ident.ValId) Map.map
and
pTE = TE of (Ident.TyCon,pVE) Map.map
and
pSE = SE of (Ident.StrId,pE) Map.map
and
pE = E of (pFE * pVE * pTE * pSE)
datatype
pF = F of (Ident.FunId,pE) Map.map
and
pG = G of (Ident.SigId,pE * Ident.TyCon list) Map.map
datatype pB = B of (pF * pG * pE)
val empty_pFE : pFE
val empty_pVE : pVE
val empty_pTE : pTE
val empty_pSE : pSE
val empty_pE : pE
val builtins_pE: pE
val augment_pE : pE * pE -> pE
val unique_augment_pE : ((Ident.ValId -> unit) *
(Ident.StrId -> unit)) * pE * pE -> pE
val empty_pF : pF
val empty_pG : pG
val empty_pB : pB
val augment_pF : pF * pF -> pF
val augment_pG : pG * pG -> pG
val augment_pB : pB * pB -> pB
val remove_str : pB * Ident.StrId -> pB
val lookupFixity : Ident.Symbol.Symbol * pE -> Fixity
exception Lookup
exception LookupStrId of Ident.Symbol.Symbol
val lookupValId :
(Ident.Symbol.Symbol list * Ident.Symbol.Symbol) * pE ->
Ident.ValId option
val tryLookupValId :
(Ident.Symbol.Symbol list * Ident.Symbol.Symbol) * pE ->
Ident.ValId option
val lookupTycon : Ident.LongTyCon * pE -> pVE option
val lookupStrId : (Ident.Symbol.Symbol list * Ident.Symbol.Symbol) * pE
-> pE
val lookupFunId : (Ident.FunId * pB) -> pE
val lookupSigId : (Ident.SigId * pB) -> pE * Ident.TyCon list
val make_pFE : Ident.Symbol.Symbol list * Fixity -> pFE
val addFixity : (Ident.Symbol.Symbol * Fixity) * pFE -> pFE
val addValId : (Ident.Symbol.Symbol * Ident.ValId * Ident.ValId ->
Ident.ValId) * Ident.ValId * pVE -> pVE
val addTyCon : (Ident.TyCon * pVE * pVE -> pVE) *
Ident.TyCon * pVE * pTE -> pTE
val addStrId : (Ident.StrId * pE * pE -> pE) * Ident.StrId * pE * pSE
-> pSE
val addFunId : (Ident.FunId * pE * pE -> pE) * Ident.FunId * pE * pF -> pF
val addSigId : (Ident.SigId * (pE * Ident.TyCon list) *
(pE * Ident.TyCon list) ->
(pE * Ident.TyCon list)) * Ident.SigId * pE *
Ident.TyCon list * pG -> pG
end;
