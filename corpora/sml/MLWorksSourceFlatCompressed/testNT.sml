use "open";
val my_store =
store{ alloc = SUCC,
overflow = EXTEND,
size = 1000,
status = RDWR_STATUS };
val my_store_a4 =
store{ alloc = ALIGNED_4,
overflow = BREAK,
size = 80,
status = RDWR_STATUS };
val f_name = "tstNT.dll";
val my_struct = loadObjectFile(f_name,IMMEDIATE_LOAD);
filesLoaded();
fileInfo my_struct;
symbols(my_struct);
symbolInfo(my_struct,"hw");
val my_object =
object { ctype = VOID_TYPE,
store = my_store };
val my_object1 = dupObject(my_object);
val int_object1 =
object { ctype = INT_TYPE,
store = my_store };
setInt(int_object1, 3);
val int_object2 =
object { ctype = INT_TYPE,
store = my_store };
setInt(int_object2, 5);
getInt(int_object1);
getInt(int_object2);
val my_sig = newSignature();
defEntry(my_sig, FUN_DECL { name = "hw",
source = [] : c_type list,
target = VOID_TYPE });
defEntry(my_sig, VAR_DECL { name = "my_value",
ctype = INT_TYPE});
showEntries(my_sig);
val lookup_my_sig = lookupEntry my_sig;
val hw = defineForeignFun(my_struct,my_sig)("hw");
call(hw)([],my_object);
defEntry(my_sig, FUN_DECL { name = "hw1",
source = [] : c_type list,
target = INT_TYPE});
showEntries my_sig;
val hw1 = defineForeignFun(my_struct,my_sig)("hw1");
call(hw1)([],int_object1);
getInt(int_object1);
defEntry(my_sig, FUN_DECL { name = "hw2",
source = [INT_TYPE] : c_type list,
target = INT_TYPE });
showEntries(my_sig);
val hw2 = defineForeignFun(my_struct,my_sig)("hw2");
setInt(int_object1,4);
call(hw2)([int_object1],int_object2);
getInt(int_object2);
val str_object1 =
object { ctype = STRING_TYPE{ length = 64 },
store = my_store };
setString(str_object1,"Hello World");
getString(str_object1);
val str_object1_1 = dupObject(str_object1);
castObjectType(str_object1_1, ARRAY_TYPE{ ctype = CHAR_TYPE, length = 64 });
val char_object1 =
object { ctype = CHAR_TYPE,
store = my_store };
indexObject{array=str_object1_1,tgt=char_object1,index=0};
getChar(char_object1);
indexObject{array=str_object1_1,tgt=char_object1,index=1};
getChar(char_object1);
val str_object2 =
object { ctype = STRING_TYPE{ length = 64 },
store = my_store };
setString(str_object2,"");
defEntry(my_sig, FUN_DECL { name = "hw3",
source = [INT_TYPE] : c_type list,
target = STRING_TYPE{ length = 64 } });
showEntries(my_sig);
val hw3 = defineForeignFun(my_struct,my_sig)("hw3");
setInt(int_object1,24);
call(hw3)([int_object1],str_object2);
getString(str_object2);
