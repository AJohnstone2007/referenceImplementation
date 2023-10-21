require "$.basis.__int";
require "$.foreign.__interface";
local
structure Aliens = Interface.Aliens
structure Store = Interface.Store
structure Structure = Interface.C.Structure
structure Signature = Interface.C.Signature
structure Function = Interface.C.Function
structure Type = Interface.C.Type
structure Value = Interface.C.Value
in
val ALIGNED_4 = Store.ALIGNED_4
val BREAK = Store.BREAK
val RDWR_STATUS = Store.RDWR_STATUS
val store = Store.store
val loadObjectFile = Structure.loadObjectFile
val IMMEDIATE_LOAD = Structure.IMMEDIATE_LOAD
val refreshAliens = Aliens.refreshAliens
val newSignature = Signature.newSignature
val defEntry = Signature.defEntry
val FUN_DECL = Signature.FUN_DECL
val defineForeignFun = Function.defineForeignFun
val call = Function.call
type c_type = Type.c_type
val INT_TYPE = Type.INT_TYPE
val object = Value.object
val setInt = Value.setInt
val getInt = Value.getInt
end
val test_struct = loadObjectFile("foreign/samples/sum.dll",IMMEDIATE_LOAD);
val test_store =
store{ alloc = ALIGNED_4,
overflow = BREAK,
size = 60,
status = RDWR_STATUS };
val int_object1 =
object { ctype = INT_TYPE,
store = test_store };
val int_object2 =
object { ctype = INT_TYPE,
store = test_store };
val int_object3 =
object { ctype = INT_TYPE,
store = test_store };
val test_sig = newSignature ();
defEntry(test_sig,
FUN_DECL { name = "sum",
source = [INT_TYPE, INT_TYPE] : c_type list,
target = INT_TYPE }
);
val def_test = defineForeignFun( test_struct, test_sig );
val sum = def_test "sum";;
fun testWrapper (i, j) =
(
setInt (int_object1, i);
setInt (int_object2, j);
call sum ( [int_object1, int_object2], int_object3 );
getInt(int_object3)
);
fun topLevel () =
(
refreshAliens ();
print (Int.toString (testWrapper (23, 19)));
()
);
MLWorks.Deliver.deliver ("deliverTest", topLevel, true);
