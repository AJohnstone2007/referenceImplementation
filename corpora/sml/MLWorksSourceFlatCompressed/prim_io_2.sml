local
open General;
open BinPrimIO;
in
val number = ref (Word8.fromInt 0);
fun nextno () = (number:=(Word8.fromInt(1+Word8.toInt(!number)));
!number);
fun makelist x = if x<=0 then []
else nextno()::(makelist(x-1));
val r = RD{ name = "Charlie",
chunkSize = 5,
readVec = NONE,
readArr = NONE,
readVecNB = SOME (fn x => SOME(Word8Vector.fromList
(makelist x))),
readArrNB=NONE,
block=SOME(fn ()=>()),
canInput= SOME(fn ()=>true),
avail=fn()=>NONE,
getPos = SOME(fn ()=> Word8.toInt(!number)),
setPos = SOME(fn x => (number:=Word8.fromInt x)),
endPos = SOME(fn ()=> raise Fail "No end of file."),
verifyPos = NONE,
close = fn () => (),
ioDesc = NONE};
val r'= augmentReader r;
val s = (fn (RD({setPos=SOME s,...})) => s | _ => raise Div) r';
val f = (fn RD({readVec=SOME(f),...}) => f
| RD({readVec=NONE,...}) => raise Div) r';
val y = (s 0;f 10);
fun compare x = if x = 11 then print"Vector read succeeded.\n"
else if Word8.toInt(Word8Vector.sub(y,x-1))<>x
then print"Vector read failed.\n"
else compare (x+1);
val test1=compare 1;
val buffer = Word8Array.array(10,Word8.fromInt(0));
val f = (fn RD({readArr=SOME(f),...}) => f
| RD({readArr=NONE,...}) => raise Div) r';
val x = (s 0;f{buf = buffer, i=1, sz=SOME(9)});
fun compare x = if x = 10 then print"Array read succeeded.\n"
else if Word8.toInt(Word8Array.sub(buffer,x))<>x
then print"Array read failed.\n"
else compare (x+1);
val test2 = compare 1;
val buffer = Word8Array.array(10,Word8.fromInt(0));
val f = (fn RD({readArrNB=SOME(f),...}) =>f
| RD({readArrNB=NONE,...}) => raise Div) r';
val x = (s 0;f{buf = buffer, i=1, sz=SOME(9)});
fun compare x = if x = 10 then print"ArrayNB read succeeded.\n"
else if Word8.toInt(Word8Array.sub(buffer,x))<>x
then print"ArrayNB read failed.\n"
else compare (x+1);
val test3 = compare 1;
end;
