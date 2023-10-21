local
open General;
open BinPrimIO;
in
val number = ref (Word8.fromInt 0);
fun nextno () = (number:=(Word8.fromInt(1+Word8.toInt(!number)));
!number);
fun makelist x = if x<=0 then []
else nextno()::(makelist(x-1));
fun makearray(b,p,s) = if s = 0 then ()
else (Word8Array.update(b,p,nextno());
makearray(b,p+1,s-1));
val r = RD{ name = "Bertrand",
chunkSize = 5,
readVec = NONE,
readArr = SOME (fn {buf=b, i=p, sz=s} =>
case s of
NONE =>
(makearray(b,p,Word8Array.length b-p);
Word8Array.length b -p)
| SOME(si) =>
if p+si>Word8Array.length b then
raise Fail "array too small"
else (makearray(b,p,si); si)),
readVecNB = NONE,
readArrNB=NONE,
block=NONE,
avail=fn()=>NONE,
canInput= SOME(fn ()=>true),
getPos = SOME(fn ()=> Word8.toInt(!number)),
setPos = SOME(fn x => (number:=Word8.fromInt x)),
endPos = SOME(fn ()=> raise Fail "No end of file."),
verifyPos = NONE,
close = fn () => (),
ioDesc = NONE};
val r'=augmentReader r;
val s = (fn (RD({setPos=SOME s,...})) => s | _ => raise Div) r';
val f = (fn RD({readVec=SOME(f),...}) => f
| RD({readVec=NONE,...}) => raise Div) r';
val y = (s 0; f 11);
fun compare x = if x = 11 then print"Vector read succeeded.\n"
else if Word8.toInt(Word8Vector.sub(y,x))<>(x+1)
then print"Vector read failed.\n"
else compare (x+1);
val test1=compare 1;
val f = (fn RD({readVecNB=SOME(f),...}) => f
| RD({readVecNB=NONE,...}) => raise Div) r';
val y = (fn SOME(y) => y | NONE => raise Div) (s 0; f 11);
fun compare x = if x = 11 then print"VectorNB read succeeded.\n"
else if Word8.toInt(Word8Vector.sub(y,x))<>(x+1)
then print"VectorNB read failed.\n"
else compare (x+1);
val test2=compare 1;
val f = (fn RD({readArrNB=SOME(f),...}) => f
| RD({readArrNB=NONE,...}) => raise Div) r';
val buffer = Word8Array.array(10,Word8.fromInt 0);
val x = (s 0;f{buf = buffer, i=1, sz=SOME(9)});
fun compare x = if x = 10 then print"ArrayNB read succeeded.\n"
else if Word8.toInt(Word8Array.sub(buffer,x))<>x
then print"ArrayNB read failed.\n"
else compare (x+1);
val test3=compare 1;
end;
