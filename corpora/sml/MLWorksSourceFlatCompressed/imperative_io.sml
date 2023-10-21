local
open General
open TextIO
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
val amy = mkOutstream (StreamIO.mkOutstream(w,IO.LINE_BUF));
val the_answer = "One Two Three\n1Four Five Six\nSeven Eight Nine."
val _ = output(amy,"One Two Three\n");
val _ = output1(amy,#"1");
val _ = output(amy,"Four Five Six\n");
val _ = output(amy,"Seven Eight Nine.");
val test1 = the_answer <> (!comm_medium);
val test2 = the_answer = (flushOut amy; !comm_medium);
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
avail = fn()=> NONE,
getPos = SOME(fn ()=> (!pos)),
setPos = SOME(fn x => pos:=x),
endPos = SOME(fn ()=> CharVector.length (!comm_medium)),
verifyPos = NONE,
close = fn () => (),
ioDesc = NONE};
val arthur = mkInstream (StreamIO.mkInstream(r,""));
fun reportOK true = print"test succeeded.\n"
| reportOK false = print"test failed.\n"
val x = reportOK (the_answer=(input arthur)^
(case input1 arthur
of NONE => ""
| (SOME c) => Char.toString c)^
(inputAll arthur));
val x = reportOK (endOfStream arthur);
val s_1 = "Oh the grand old Duke of York,\n";
val s_2 = "He had a leg of Pork,\n";
val s_3 = "He marched it up to the Microwave,\n";
val s_4 = "And he ate it with a fork.\n";
val s_5 = "\n\nFile created by test_suite/basis/imperative_io.sml";
val x = openOut "123";
val _ = output(x,s_1);
val _ = output(x,s_2);
val _ = output(x,s_3);
val _ = output(x,s_4);
val _ = output(x,s_5);
val _ = closeOut x;
val y = openIn "123";
val s = inputAll y;
val _ = closeIn y;
val _ = reportOK (s=s_1^s_2^s_3^s_4^s_5);
val s_6 = "Some enchanted evening,...";
val x = openAppend "123";
val _ = output(x,s_6);
val _ = closeOut x;
val y = openIn "123";
val s'_1 = inputLine y;
val s'_2 = inputAll y;
val _ = closeIn y;
val _ = reportOK (s'_1=s_1);
val _ = reportOK ((s'_1^s'_2)=(s_1^s_2^s_3^s_4^s_5^s_6));
val _ = OS.FileSys.remove "123";
val comm_2 = ref "BBBBB";
val pos2 = ref 0;
val _ = comm_medium:= "AAAAA";
val _ = pos:=0;
val r2 = TextPrimIO.RD{
name = "Arthur's twin",
chunkSize = 5,
readVec =
SOME (fn (x:int) =>
let val y = if x+(!pos2)>CharVector.length(!comm_2)
then CharVector.length(!comm_2)-(!pos2)
else x
val r = CharVector.extract(!comm_2,!pos2,SOME y)
in
(pos2:=(!pos2)+y; r)
end),
readArr = NONE,
readVecNB = NONE,
readArrNB=NONE,
block=SOME(fn ()=>()),
canInput= SOME(fn ()=>
not(CharVector.length (!comm_2)=(!pos2))),
avail = fn()=>NONE,
getPos = SOME(fn ()=> (!pos2)),
setPos = SOME(fn x => pos2:=x),
endPos = SOME(fn ()=> CharVector.length (!comm_2)),
verifyPos = NONE,
close = fn () => (),
ioDesc = NONE};
val arthur = mkInstream (StreamIO.mkInstream (r,""));
val ruhtra = StreamIO.mkInstream (r2,"");
fun interleave one other =
if (endOfStream one) andalso (StreamIO.endOfStream other) then ""
else (case (input1 one)
of SOME c => Char.toString c
| NONE => "")
^(let val temp = getInstream one
val _ = setInstream (one, other)
in interleave one temp
end);
val x = reportOK ("ABABABABAB"=(interleave arthur ruhtra));
end
;
