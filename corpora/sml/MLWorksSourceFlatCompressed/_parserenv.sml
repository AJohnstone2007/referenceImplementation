require "parserenv";
require "../utils/lists";
require "../utils/map";
require "../basics/ident";
functor ParserEnv
(structure Lists : LISTS
structure Map : MAP
structure Ident : IDENT
) : PARSERENV =
struct
structure Map = Map
structure Ident = Ident
structure Symbol = Ident.Symbol
datatype Fixity = LEFT of int | RIGHT of int | NONFIX
datatype
pFE = FE of (Symbol.Symbol,Fixity) Map.map
and
pVE = VE of (Symbol.Symbol,Ident.ValId) Map.map
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
val empty_pFE = FE(Map.empty (Symbol.symbol_lt,Symbol.eq_symbol))
val empty_pVE = VE(Map.empty (Symbol.symbol_lt,Symbol.eq_symbol))
val empty_pTE = TE(Map.empty (Ident.tycon_lt,Ident.tycon_eq))
val empty_pSE = SE(Map.empty (Ident.strid_lt,Ident.strid_eq))
val empty_pE = E(empty_pFE,empty_pVE,empty_pTE,empty_pSE)
local
val true_sym = Symbol.find_symbol "true"
val false_sym = Symbol.find_symbol "false"
val bool_sym = Symbol.find_symbol "bool"
val ref_sym = Symbol.find_symbol "ref"
val cons_sym = Symbol.find_symbol "::"
val nil_sym = Symbol.find_symbol "nil"
val list_sym = Symbol.find_symbol "list"
val bool_pVE = VE (Map.from_list (Symbol.symbol_lt,Symbol.eq_symbol)
[(true_sym,Ident.CON true_sym),
(false_sym, Ident.CON false_sym)])
val ref_pVE = VE (Map.from_list (Symbol.symbol_lt,Symbol.eq_symbol)
[(ref_sym,Ident.CON ref_sym)])
val list_pVE = VE (Map.from_list (Symbol.symbol_lt,Symbol.eq_symbol)
[(cons_sym,Ident.CON cons_sym),
(nil_sym,Ident.CON nil_sym)])
val builtins_pTE = TE(Map.from_list (Ident.tycon_lt,Ident.tycon_eq)
[(Ident.TYCON bool_sym,bool_pVE),
(Ident.TYCON ref_sym, ref_pVE),
(Ident.TYCON list_sym, list_pVE)])
in
val builtins_pE = E(empty_pFE,empty_pVE,builtins_pTE,empty_pSE)
end
fun augment_pE (E(FE fe,VE ve,TE te,SE se),
E(FE fe',VE ve',TE te',SE se')) =
E(FE (Map.union (fe,fe')), VE (Map.union (ve,ve')),
TE (Map.union (te,te')), SE (Map.union (se,se')))
fun unique_augment_pE ((valid_fn,strid_fn),E(FE fe,VE ve,TE te,SE se),
E(FE fe',VE ve',TE te',SE se')) =
E(FE (Map.union (fe,fe')),
VE (Map.merge (fn (x,a,b) => (ignore(valid_fn a);b))(ve,ve')),
TE (Map.merge (fn (x,a,b) => b) (te,te')),
SE (Map.merge (fn (x,a,b) => (ignore(strid_fn x);b))(se,se')))
val empty_pF = F(Map.empty (Ident.funid_lt,Ident.funid_eq))
val empty_pG = G(Map.empty (Ident.sigid_lt,Ident.sigid_eq))
val empty_pB = B (empty_pF,empty_pG,empty_pE)
fun augment_pF (F map,F map') =
F (Map.union (map,map'))
fun augment_pG (G map,G map') =
G (Map.union (map,map'))
fun augment_pB (B(F f,G g,pE),B(F f',G g',pE')) =
B (F (Map.union (f,f')), G (Map.union (g,g')), (augment_pE (pE,pE')))
fun remove_str (B(f, g, E(fE, vE, tE, SE sE)), strid) =
B(f, g, E(fE, vE, tE, SE(Map.undefine(sE, strid))))
fun lookupFixity (sym, E (FE map,_,_,_)) =
Map.apply_default'(map, NONFIX, sym)
exception Lookup = Map.Undefined
exception LookupStrId of Symbol.Symbol
fun lookupValId ((syms,sym),pe) =
let
fun follow ([],pe) = pe
| follow (sym ::strids,E(_,_,_,SE map)) =
case Map.tryApply'(map,Ident.STRID sym) of
SOME pe' => follow (strids,pe')
| _ => raise LookupStrId sym
val (E (_,VE map,_,_)) = follow (syms,pe)
in
Map.tryApply'(map, sym)
end
fun tryLookupValId ((syms,sym),pe) =
let
fun follow ([],pe) = SOME pe
| follow (sym ::strids,E(_,_,_,SE map)) =
case Map.tryApply'(map,Ident.STRID sym) of
SOME pe' => follow (strids,pe')
| _ => NONE
in
case follow (syms,pe) of
SOME (E (_,VE map,_,_)) => Map.tryApply'(map, sym)
| _ => NONE
end
fun lookupTycon (Ident.LONGTYCON(path,tycon),pe) =
let
fun follower (strId as Ident.STRID sym,E(_,_,_,SE map)) =
case Map.tryApply'(map, strId)
of SOME pe' => pe'
| _ => raise LookupStrId sym
val (E (_,_,TE map,_)) = Ident.followPath follower (path,pe)
in
Map.tryApply'(map, tycon)
end
fun lookupStrId ((syms,sym),pe) =
let
fun aux ([],pe) = pe
| aux ((s::ss),E (_,_,_,SE map)) =
(case Map.tryApply' (map,Ident.STRID s) of
SOME pe => aux (ss,pe)
| _ => raise LookupStrId s)
in
aux (syms@[sym],pe)
end
fun lookupFunId (funid, B(F map,_,_)) =
Map.apply'(map, funid)
fun lookupSigId (sigid,B(_,G map,_)) =
Map.apply'(map, sigid)
fun make_pFE (syms, fixity) =
let
fun f (res, sym) = Map.define(res, sym, fixity)
in
FE (Lists.reducel f (Map.empty (Symbol.symbol_lt,Symbol.eq_symbol), syms))
end
exception Seen
fun addFixity((symbol, fixity), FE map) =
FE (Map.define(map, symbol, fixity))
fun addValId (f, valid, VE map) =
let
val sym =
case valid of
Ident.VAR sym => sym
| Ident.CON sym => sym
| Ident.EXCON sym => sym
| Ident.TYCON' sym => sym
in
VE(Map.combine f (map, sym, valid))
end
fun addTyCon (f, tycon, ve, TE map) =
TE(Map.combine f (map, tycon, ve))
fun addStrId (f, strid, pe, SE map) =
SE(Map.combine f (map, strid, pe))
fun addFunId (f, funid, pe, F map) =
F(Map.combine f (map, funid, pe))
fun addSigId (f, sigid, pe, tycons, G map) =
G(Map.combine f (map, sigid, (pe,tycons)))
end;
