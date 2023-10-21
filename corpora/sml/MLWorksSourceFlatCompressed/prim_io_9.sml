let
val _ = Shell.Options.set(Shell.Options.ValuePrinter.maximumStrDepth, 0);
val dir = OS.FileSys.getDir()
fun setTargets l = (app Shell.Project.setTargetDetails l; Shell.Project.setTargets l)
val _ = Shell.Project.openProject (OS.Path.concat [dir, "..", "src", "basis.mlp"]);
val _ = Shell.Project.setConfiguration "I386/NT";
val {binariesLoc, libraryPath, objectsLoc} = Shell.Project.showLocations();
val _ = Shell.Project.setLocations
{binariesLoc=binariesLoc, libraryPath=libraryPath, objectsLoc="\\tmp\\objects"};
in
setTargets ["__io.sml", "__bin_prim_io.sml", "__os_prim_io.sml", "__text_io.sml", "__bin_io.sml",
"__text_stream_io.sml", "__bin_stream_io.sml", "__text_prim_io.sml", "__word8_vector.sml"];
Shell.Project.forceCompileAll();
Shell.Project.loadAll()
end;
local
val pos = ref 0;
val comm_medium = ref (Word8Vector.fromList [])
in
val w = BinPrimIO.WR{ name = "Amy",
chunkSize = 5,
writeVec = SOME (fn {buf=b,i=p,sz=s} => (
comm_medium:=Word8Vector.extract(b,p,s);
case s of
NONE => Word8Vector.length b -p
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
val r = BinPrimIO.RD{
name = "Arthur",
chunkSize = 5,
readVec =
SOME (fn (x:int) =>
let val y = if x+(!pos)>Word8Vector.length(!comm_medium)
then Word8Vector.length(!comm_medium)-(!pos)
else x
val r = Word8Vector.extract(!comm_medium,!pos,SOME y)
in
(pos:=(!pos)+y; r)
end),
readArr = NONE,
readVecNB = NONE,
readArrNB=NONE,
block=SOME(fn ()=>()),
canInput= SOME(fn ()=>true),
avail=fn()=>NONE,
getPos = SOME(fn ()=> 1),
setPos = SOME(fn x => ()),
endPos = SOME(fn ()=> raise Fail "No end of file."),
verifyPos = NONE,
close = fn () => (),
ioDesc = NONE};
val tr = OSPrimIO.translateIn(r);
val tw = OSPrimIO.translateOut(w);
val outfn = (fn TextPrimIO.WR({writeVec = NONE,...}) => raise Div
| TextPrimIO.WR({writeVec = SOME f,...}) => f) tw;
val infn = (fn TextPrimIO.RD({readVec = NONE,...}) => raise Div
| TextPrimIO.RD({readVec = SOME f,...}) => f) tr;
val teststring =
"Mary had a little lamb,\n a lobster and some prunes.\nYo!";
val test_in = (ignore(outfn {buf=teststring, i=0, sz=NONE});
infn 100);
val result = (teststring= test_in);
end
;
