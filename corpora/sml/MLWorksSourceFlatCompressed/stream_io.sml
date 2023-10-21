local
open General;
open TextIO.StreamIO;
in
val comm_medium = ref "";
val pos = ref 0;
val w = TextPrimIO.WR{ name = "Amy",
chunkSize = 20,
writeVec = SOME (fn {buf=b,i=p,sz=s} => (
comm_medium:=(!comm_medium)^
CharVector.extract(b,p,s);
case s of
NONE => CharVector.length b -p
| SOME(si) => si)),
writeArr = NONE,
writeVecNB = NONE,
writeArrNB=NONE,
block=NONE,
canOutput= SOME(fn ()=>true),
getPos = SOME(fn ()=> 0),
setPos = SOME(fn x => ()),
endPos = SOME(fn ()=> ~1),
verifyPos = NONE,
close = fn () => (),
ioDesc = NONE};
val amy = mkOutstream(w,IO.LINE_BUF);
val the_answer = "One Two Three\n1Four Five Six\nSeven Eight Nine."
val _ = output(amy,"One Two Three\n");
val _ = output1(amy,#"1");
val _ = output(amy,"Four Five Six\n");
val _ = output(amy,"Seven Eight Nine.");
val test1 = the_answer <> (!comm_medium);
val text2 = the_answer = (flushOut amy; !comm_medium);
val r = TextPrimIO.RD{
name = "Arthur",
chunkSize = 5,
readVec =
SOME (fn (x:int) =>
let val y = if x+(!pos)>CharVector.length(!comm_medium)
then CharVector.length(!comm_medium)-(!pos)
else x
val r = CharVector.extract(!comm_medium,!pos,SOME y)
in
(pos:=(!pos)+y; r)
end),
readArr = NONE,
readVecNB = NONE,
readArrNB=NONE,
block=SOME(fn ()=>()),
canInput= SOME(fn ()=>
not(CharVector.length (!comm_medium)=(!pos))),
avail=fn()=>NONE,
getPos = SOME(fn ()=> (!pos)),
setPos = SOME(fn x => pos:=x),
endPos = SOME(fn ()=> raise Fail "No end of file."),
verifyPos = NONE,
close = fn () =>
comm_medium:=CharVector.extract(!comm_medium,0,SOME (!pos)),
ioDesc = NONE};
val arthur = mkInstream(r,"");
val test1 = input arthur;
val test2 as (s1,benjamin) = input arthur;
val test3 as (s2,colin) = input benjamin;
val test4 = input arthur;
val test5 as s3= inputAll colin;
val (_,dave) = input colin;
val test6 = input benjamin;
fun reportOK true = print"test succeeded.\n"
| reportOK false = print"test failed.\n"
val x = reportOK ((#1 test1)=(#1 test2));
val x = reportOK ((#1 test1)=(#1 test4));
val x = reportOK ((#1 test3)=(#1 test6));
val x = reportOK (the_answer=s1^s2^s3);
val x = reportOK (not (endOfStream benjamin));
val x = reportOK (let val (a,_) = input arthur
val (b,_) = input arthur
in a=b end);
val x = reportOK (closeIn arthur; closeIn arthur; true);
val _ = comm_medium:="Oh the grand old Duke of York...";
val _ = pos:=0;
val x = reportOK (let val (a,_) = input arthur
in canInput (arthur, (CharVector.length a))
= SOME (CharVector.length a)
end);
val x = reportOK (let val a = mkInstream (r,"")
in closeIn a;
CharVector.length (#1(input a))=0
end);
val _ = comm_medium:="Yes, we have no bananas.";
val _ = pos:=0;
val x = reportOK (let val art = mkInstream (r,"")
val (a,bart) = input art
val _ = closeIn art
val (b,_) = input art
in a=b andalso (endOfStream bart)
end);
end
;
