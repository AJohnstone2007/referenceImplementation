require "$.foreign.__interface";
local
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
val newSignature = Signature.newSignature
val defEntry = Signature.defEntry
val FUN_DECL = Signature.FUN_DECL
val defineForeignFun = Function.defineForeignFun
val call = Function.call
type c_type = Type.c_type
val VOID_TYPE = Type.VOID_TYPE
val STRING_TYPE = Type.STRING_TYPE
val INT_TYPE = Type.INT_TYPE
val CHAR_TYPE = Type.CHAR_TYPE
val ptrType = Type.ptrType
val object = Value.object
val setString = Value.setString
val getString = Value.getString
val setInt = Value.setInt
val getInt = Value.getInt
val setPtrType = Value.setPtrType
val setPtrAddrOf = Value.setPtrAddrOf
val castPtrType = Value.castPtrType
end
val hello_struct = loadObjectFile("foreign\\samples\\hello.dll",IMMEDIATE_LOAD);
val hello_store =
store{ alloc = ALIGNED_4,
overflow = BREAK,
size = 60,
status = RDWR_STATUS };
val void_object =
object { ctype = VOID_TYPE,
store = hello_store };
val str_object =
object { ctype = STRING_TYPE{ length = 30 },
store = hello_store };
val int_object1 =
object { ctype = INT_TYPE,
store = hello_store };
val int_object2 =
object { ctype = INT_TYPE,
store = hello_store };
val ptr_object =
object { ctype = ptrType(VOID_TYPE),
store = hello_store };
setString(str_object, "What is 65 - 42? ---- Ans is ");
setInt(int_object1, 23);
getString(str_object);
getInt(int_object1);
val hello_sig = newSignature();
defEntry(hello_sig,
FUN_DECL { name = "hello",
source = [ptrType(CHAR_TYPE), INT_TYPE] : c_type list,
target = INT_TYPE }
);
val def_hello = defineForeignFun( hello_struct, hello_sig );
val hello = def_hello "hello";;
setPtrType { ptr = ptr_object, data = str_object };
setPtrAddrOf { ptr = ptr_object, data = str_object };
castPtrType { ptr = ptr_object, ctype = CHAR_TYPE };
call hello ( [ptr_object,int_object1], int_object2 );
getInt(int_object2);
