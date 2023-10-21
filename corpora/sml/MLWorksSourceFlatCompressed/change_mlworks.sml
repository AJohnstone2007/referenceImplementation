Shell.Options.set(Shell.Options.Language.oldDefinition, false);
structure MLWorks =
struct
open MLWorks
structure Deliver =
struct
open Deliver
datatype app_style = CONSOLE | WINDOWS
type deliverer = string * (unit -> unit) * app_style -> unit
val exitFn : (unit -> unit) ref = ref (fn () => ())
val add_delivery_hook : (deliverer -> deliverer) -> unit =
fn hook => ()
end
structure Internal =
struct
open Internal
structure Error =
struct
open Error
val errorMsg:syserror ->string = fn _ => ""
val errorName: syserror ->string = fn _ => ""
val syserror: string -> syserror option
= fn _ => NONE
end
structure Value =
struct
open Value
val arctan : real -> real = Runtime.environment"real arctan"
val sin : real -> real = Runtime.environment"real sin"
val cos : real -> real = Runtime.environment"real cos"
val exp : real -> real = Runtime.environment"real exp"
val sqrt : real -> real = Runtime.environment"real square root"
end
structure Runtime =
struct
open Runtime
val environment =
fn "Win32.fdToIOD" =>
Value.cast(fn _ => raise Error.SysErr("Unbound", NONE))
| s =>
environment s handle exn as Unbound _ =>
(case s of
"Windows.fileTimeToLocalFileTime" => Value.cast(fn x => x)
| "Win32.closeIOD" => Value.cast(fn x => ())
| _ => Value.cast (fn x => raise exn))
end
structure IO =
struct
exception Io of {name : string, function : string, cause : exn}
open IO
end
structure Types =
struct
open Types
datatype time = TIME of int * int * int
end
structure Exit =
struct
type key = int
type status = MLWorks.Internal.Types.word32
val success : status = 0w0
val failure : status = 0w1
val atExit : (unit -> unit) -> key = fn _ => 0
val removeAtExit : key -> unit = fn k => ()
exception dummy
val exit : status -> 'a = fn _ => raise dummy
val terminate : status -> 'a = fn _ => raise dummy
end
end
end
;
