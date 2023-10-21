require "../main/code_module";
require "../rts/gen/objectfile";
require "../utils/lists";
require "inter_envtypes";
require "interload";
functor InterLoad (
structure Code_Module : CODE_MODULE
structure Inter_EnvTypes : INTER_ENVTYPES
structure Lists : LISTS
structure ObjectFile: OBJECTFILE
) : INTERLOAD =
struct
structure Inter_EnvTypes = Inter_EnvTypes
structure Ident = Inter_EnvTypes.EnvironTypes.LambdaTypes.Ident
structure Symbol = Ident.Symbol
structure NewMap = Inter_EnvTypes.EnvironTypes.NewMap
type Module = Code_Module.Module
val cast = MLWorks.Internal.Value.cast
fun load debugger
(Inter_EnvTypes.INTER_ENV (values, structures, functors),
module_map)
(Code_Module.MODULE module_element_list) =
let
val value_map = NewMap.apply values
val structure_map = NewMap.apply structures
val functor_map = NewMap.apply functors
fun element (result, []) = result
| element (result, Code_Module.REAL (i, string) :: rest) =
element ((i, cast (MLWorks.Internal.Value.string_to_real string))::result, rest)
| element (result, Code_Module.STRING (i, string) :: rest) =
element ((i, cast string)::result, rest)
| element (result, Code_Module.MLVALUE (i, value) :: rest) =
element ((i, cast value)::result, rest)
| element (result, Code_Module.VAR (i, name) :: rest) =
element ((i, value_map (Ident.VAR (Symbol.find_symbol name)))::result, rest)
| element (result, Code_Module.EXN (i, name) :: rest) =
element ((i, value_map (Ident.EXCON (Symbol.find_symbol name)))::result, rest)
| element (result, Code_Module.STRUCT (i, name) :: rest) =
element ((i, structure_map (Ident.STRID (Symbol.find_symbol name)))::result, rest)
| element (result, Code_Module.FUNCT (i, name) :: rest) =
element ((i, functor_map (Ident.FUNID (Symbol.find_symbol name)))::result, rest)
| element (result, Code_Module.WORDSET (Code_Module.WORD_SET list) :: rest) =
element (MLWorks.Internal.Runtime.Loader.load_wordset (
ObjectFile.OBJECT_FILE_VERSION,
list
) @ result,
rest
)
| element (result, Code_Module.EXTERNAL (i, name) :: rest) =
element ((i, module_map name)::result, rest)
fun order_fn((i:int, _), (j, _)) = i < j
val elements = element([], module_element_list)
val closure =
MLWorks.Internal.Value.list_to_tuple
(map #2
(if Lists.check_order order_fn elements then
elements
else
Lists.msort order_fn elements))
in
cast ((debugger (cast closure)) (cast closure)) : MLWorks.Internal.Value.T
end
end
;
