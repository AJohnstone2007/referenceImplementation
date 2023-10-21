require "__text_io";
require "basic_util";
require "sys_conf";
require "debug";
require "fonts";
require "basic_types";
signature SYS_INIT =
sig
val initSmlTk : unit -> unit
end;
structure SysInit : SYS_INIT =
struct
open BasicTypes BasicUtil.FileUtil
val oldDisplay = ref ""
fun warnme msg = Debug.error (msg^" (sml_tk may malfunction)")
fun checkUpdPaths () =
(updLogfilename(SysConf.getProtPath());
updWishPath(SysConf.getTclPath());
updSrcPath(SysConf.getSmlTkPath());
updFntPath(SysConf.getFontPath());
let val wish_ok = isFileRdAndEx(getWishPath())
val font_ok = isFileRdAndEx(getFntPath())
val src_ok = isDirRdAndWr(getSrcPath())
in
TextIO.output(TextIO.stdOut, "\nsml_tk parameter settings:\n\
		             \--------------------------\n");
TextIO.output(TextIO.stdOut, "wish       : "^(getWishPath())^
(if not wish_ok then
" *** WARNING: no executable found!\n"
else "\n"));
TextIO.output(TextIO.stdOut, "source dir : "^(getSrcPath())^
(if not src_ok then
" *** WARNING: not a r/w directory!\n"
else "\n"));
if not font_ok then
TextIO.output(TextIO.stdOut, "*** WARNING: no executable `testfont` found at "^(getFntPath())^"\n")
else ();
TextIO.output(TextIO.stdOut, "logfile    : "^(getLogfilename())^"\n");
if not (wish_ok andalso font_ok andalso src_ok) then
TextIO.output(TextIO.stdErr, "\n*** Warnings occured, sml_tk malfunction likely.\n\n")
else ()
end)
fun initSmlTk () =
(
checkUpdPaths();
updTclInit
" set tcl_prompt1 \"puts -nonewline {} \" \n \
	    \ set tcl_prompt2 \"puts -nonewline {} \" \n ";
let val nuDisplay= SysConf.getDisplay()
in if nuDisplay= (!oldDisplay) then ()
else (oldDisplay:= nuDisplay; Fonts.init())
end
)
end
;
