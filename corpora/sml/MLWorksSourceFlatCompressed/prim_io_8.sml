local
open General;
open BinPrimIO;
infix ==;
fun a == b =
let
val len = Word8Vector.length a
fun scan i = if i=len then true
else (Word8Vector.sub(a,i) = Word8Vector.sub(b,i)
andalso scan (i+1))
in
len = Word8Vector.length b
andalso scan 0
end
in
val number = ref (Word8.fromInt 0);
val result = ref (Word8Vector.fromList [])
fun nextno () = (number:=(Word8.fromInt(1+Word8.toInt(!number)));
!number);
fun reset () = number:=Word8.fromInt 0;
fun makelist x = if x<=0 then []
else nextno()::(makelist(x-1));
val w=WR{ name = "Doris",
chunkSize = 5,
writeVec = NONE,
writeArr = NONE,
writeVecNB = NONE,
writeArrNB= SOME (fn {buf=b,i=p,sz=s} => SOME (
result:=Word8Array.extract(b,p,s);
case s of
NONE => Word8Array.length b -p
| SOME(si) => si)),
block=SOME(fn ()=>()),
canOutput= SOME(fn ()=>true),
getPos = SOME(fn ()=> 0),
setPos = SOME(fn x => ()),
endPos = SOME(fn ()=> ~1),
verifyPos = NONE,
close = fn () => (),
ioDesc = NONE};
val w'= augmentWriter w;
val f = (fn WR({writeVec=SOME(f),...}) => f
| WR({writeVec=NONE,...}) => raise Div) w';
val v = (reset(); Word8Vector.fromList (makelist 10));
val x = f{buf = v, i=0, sz=NONE};
fun compare x = if v == !result then print"Vector write succeeded.\n"
else print"Vector write failed.\n"
val test1 = compare 1;
val f = (fn WR({writeVecNB=SOME(f),...}) => f
| WR({writeVecNB=NONE,...}) => raise Div) w';
val v = (reset(); Word8Vector.fromList (makelist 10));
val x = f{buf = v, i=0, sz=NONE};
fun compare x = if v == !result then print"VectorNB write succeeded.\n"
else print"VectorNB write failed.\n"
val test2 = compare 1;
val f = (fn WR({writeArr=SOME(f),...}) => f
| WR({writeArr=NONE,...}) => raise Div) w';
val a = (reset(); Word8Array.fromList (makelist 10));
val x = f{buf = a, i=0, sz=NONE};
fun compare x = if x = 10 then print"Array write succeeded.\n"
else if Word8.toInt(Word8Vector.sub(!result,x-1))<>x
then print"Array write failed.\n"
else compare (x+1);
val test3 = compare 1;
end;
