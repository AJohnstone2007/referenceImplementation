require "random";
require "$.system.__time";
require "$.basis.__date";
require "$.foreign.__interface";
structure Random : RANDOM =
struct
structure Store = Interface.Store
structure CStructure = Interface.C.Structure
structure CSignature = Interface.C.Signature
structure CFunction = Interface.C.Function
structure CType = Interface.C.Type
structure CValue = Interface.C.Value
val random_struct =
CStructure.loadObjectFile ("foreign/samples/random.so", CStructure.IMMEDIATE_LOAD);
val random_store =
Store.store {alloc = Store.ALIGNED_4,
overflow = Store.BREAK,
size = 60,
status = Store.RDWR_STATUS};
val void_object =
CValue.object {ctype = CType.VOID_TYPE,
store = random_store};
val long_object1 =
CValue.object {ctype = CType.LONG_TYPE,
store = random_store};
val long_object2 =
CValue.object {ctype = CType.LONG_TYPE,
store = random_store};
val long_object3 =
CValue.object {ctype = CType.LONG_TYPE,
store = random_store};
val random_sig = CSignature.newSignature ();
val _ = CSignature.defEntry (random_sig,
CSignature.FUN_DECL
{name = "random_num",
source = [CType.LONG_TYPE,
CType.LONG_TYPE],
target = CType.LONG_TYPE });
val _ = CSignature.defEntry (random_sig,
CSignature.FUN_DECL
{name = "set_seed",
source = [CType.LONG_TYPE],
target = CType.VOID_TYPE });
val def_random =
CFunction.defineForeignFun (random_struct, random_sig);
val randomFF = def_random "random_num";
val set_seedFF = def_random "set_seed";
fun setSeed seed =
(CValue.setLong (long_object1, seed);
CFunction.call set_seedFF ([long_object1], void_object))
fun random (a, b) =
(CValue.setLong (long_object1, a);
CValue.setLong (long_object2, b);
CFunction.call randomFF ([long_object1, long_object2], long_object3);
CValue.getLong (long_object3))
fun randomize () =
let
val theTime = Time.now ()
val roundedTime = Date.toTime (Date.fromTimeLocal theTime)
val fracTime = Time.- (theTime, roundedTime)
val seed = Time.toMicroseconds fracTime
in
setSeed seed
end
end
;
