require "../utils/__crash";
require "../utils/__mlworks_timer";
require "../utils/__lists";
require "../utils/__intbtree";
require "../utils/__hashtable";
require "../utils/__inthashtable";
require "../basis/__bin_io";
require "../basis/__bin_prim_io";
require "../parser/__parserenv";
require "../typechecker/__basis";
require "../typechecker/__types";
require "../typechecker/__stamp";
require "../typechecker/__strnames";
require "../typechecker/__nameset";
require "../typechecker/__environment";
require "../lambda/__environtypes";
require "../main/__pervasives";
require "../main/__info";
require "../main/__code_module";
require "../rts/gen/__objectfile";
require "../debugger/__debugger_types";
require "__enc_sub";
require "_encapsulate";
structure Encapsulate_ = Encapsulate(
structure Timer = Timer_
structure Crash = Crash_
structure Lists = Lists_
structure BinIO = BinIO
structure PrimIO = BinPrimIO
structure IntMap = IntBTree_
structure ParserEnv = ParserEnv_
structure Basis = Basis_
structure Nameset = Nameset_
structure Types = Types_
structure Stamp = Stamp_
structure Strnames = Strnames_
structure Env = Environment_
structure EnvironTypes = EnvironTypes_
structure Pervasives = Pervasives_
structure Info = Info_
structure Code_Module = Code_Module_
structure ObjectFile = ObjectFile_
structure Debugger_Types = Debugger_Types_
structure Enc_Sub = Enc_Sub_
structure HashTable = HashTable_
structure IntHashTable = IntHashTable_
)
;
