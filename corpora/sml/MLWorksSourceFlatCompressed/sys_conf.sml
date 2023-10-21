require "__string";
require "basic_util";
signature SYS_CONF =
sig
val getSmlTkPath : unit -> string
val getProtPath : unit -> string
val getTclPath : unit -> string
val getFontPath : unit -> string
val getDisplay : unit -> string
end;
structure SysConf : SYS_CONF =
struct
open BasicUtil
val pathPrefix = ""
val smlTkPath = "/sml_tk_mlw/"
val tclPath = ""
val protPath = ""
val srcEnvVar = "SMLTK_ROOT"
val tclVar = "SMLTK_TCL"
val protVar = "SMLTK_LOGFILE"
fun getSmlTkPath () = "/u/johnh/temp/src/sml_tk_mlw/"
fun getProtPath () = "/u/johnh/temp/smltk_mlw.log"
fun getTclPath () = "/u/johnh/temp/tk4.2/unix/wish"
fun getFontPath () = (getSmlTkPath ())^"testfont"
fun getDisplay () =
let val dpy = getOpt(FileUtil.getEnv "DISPLAY", "NO-DISPLAY")
val host= getOpt(FileUtil.getEnv "HOSTNAME", "NO-HOST")
in (if String.sub(dpy, 0)= #":" then host^dpy
else dpy)
handle Subscript=> host
end
end
;
