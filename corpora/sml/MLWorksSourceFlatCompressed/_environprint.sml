require "^.basis.__text_io";
require "pretty";
require "environ";
require "environprint";
require "../basics/identprint";
functor EnvironPrint(
structure Pretty : PRETTY
structure IdentPrint : IDENTPRINT
structure Environ: ENVIRON
sharing IdentPrint.Ident = Environ.EnvironTypes.LambdaTypes.Ident
) : ENVIRONPRINT =
struct
structure P = Pretty
structure IP = IdentPrint
structure EnvironTypes = Environ.EnvironTypes
structure LambdaTypes = EnvironTypes.LambdaTypes
structure NewMap = EnvironTypes.NewMap
structure Options = IdentPrint.Options
fun decodecomp x =
P.blk(0,
case x of
EnvironTypes.LAMB (lvar,_) =>
[P.str "LAMB ",
P.str (LambdaTypes.printLVar lvar)]
| EnvironTypes.FIELD {size,index} =>
[P.str "FIELD ",
P.str (LambdaTypes.printField {size=size,index=index,selecttype=LambdaTypes.TUPLE})]
| EnvironTypes.PRIM prim =>
[P.str "PRIM ",
P.str (LambdaTypes.printPrim prim)]
| EnvironTypes.EXTERNAL => [P.str"EXTERNAL"])
fun envir_block l = P.blk(1,
[P.str "{"] @
(P.lst("", [P.str ",", P.brk 1], "") l) @
[P.str " }"]);
fun decodeenv options (x as EnvironTypes.ENV(valids, strids)) =
let
fun decode_valid_list l =
let
fun tof (valid,comp) = P.blk(2,
[P.str " ",
P.str (IP.printValId options valid),
P.str " --> ",
decodecomp comp])
in
envir_block (map tof l)
end
fun decode_strid_list l =
let
fun tof (strid, (env, comp, _)) =
P.blk(0,
[P.str " ",
P.str (IP.printStrId strid),
P.str " --> ",
decodecomp comp,
P.blk(0,[P.str " ",
decodeenv options env])])
in
P.blk(1, [P.str "{"] @
(P.lst("", [P.brk 2], "") (map tof l)) @
[P.str " }"])
end;
in
P.blk(0,
[P.str " VE: ",
P.blk(0, [decode_valid_list (NewMap.to_list_ordered valids)]),
P.nl,
P.str " SE: ",
P.blk(0, [decode_strid_list (NewMap.to_list_ordered strids)])])
end;
fun stringenv options env = P.string_of_T (decodeenv options env);
fun printenv options env stream =
P.print_T (fn x => TextIO.output(stream, x)) (decodeenv options env)
fun decodetopenv options (EnvironTypes.TOP_ENV(env, EnvironTypes.FUN_ENV fun_env)) =
let
fun decode_functor_list l =
let
fun tof (funid, (c, e, _)) =
P.blk(2, [P.str (IP.printFunId funid),
P.str " --> ",
P.blk(0,
[decodecomp c,
P.nl,
P.str "ResEnv: ",
P.blk(0, [decodeenv options e])])])
in
envir_block (map tof l)
end;
in
P.blk(0,
[P.str " Ftr: ",
P.blk(0, [decode_functor_list (NewMap.to_list_ordered fun_env)]),
P.nl,
P.str " Top:",
P.blk(0, [decodeenv options env])])
end;
fun stringtopenv options env = P.string_of_T (decodetopenv options env);
fun printtopenv options env stream =
P.print_T (fn x => TextIO.output(stream, x)) (decodetopenv options env)
end
;
