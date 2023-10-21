require "sml90";
require "__text_io";
require "__string";
require "__io";
structure SML90: SML90 =
struct
abstype instream = IN_S of TextIO.instream
and outstream = OUT_S of TextIO.outstream
with
exception Io of string
val std_in = IN_S TextIO.stdIn
and open_in =
fn s => IN_S (TextIO.openIn s) handle _ => raise Io ("Cannot open "^s)
and input =
fn (IN_S is, n) => TextIO.inputN (is, n)
handle e => raise Io ("input: "^(exnMessage e))
and lookahead =
fn (IN_S is) => case (TextIO.lookahead is) of
SOME e => String.str e
| NONE => ""
handle e => raise Io ("lookahead: "^(exnMessage e))
and close_in =
fn (IN_S is) => TextIO.closeIn is
handle e => raise Io ("close_in: "^(exnMessage e))
and end_of_stream =
fn (IN_S is) => TextIO.endOfStream is
handle e => raise Io ("end_of_stream: "^(exnMessage e))
and std_out = OUT_S TextIO.stdOut
and open_out =
fn s => OUT_S (TextIO.openOut s)
handle _ => raise Io ("Cannot open "^s)
and output =
fn (OUT_S os, s) => TextIO.output (os, s)
handle IO.Io {cause = IO.ClosedStream, ...} =>
raise Io "Output stream is closed"
| e => raise Io ("output: "^(exnMessage e))
and close_out =
fn (OUT_S os) => TextIO.closeOut os
handle e => raise Io ("close_out: "^(exnMessage e))
end
exception Ord = MLWorks.String.Ord
exception Abs = Overflow
exception Quot = Overflow
exception Prod = Overflow
exception Neg = Overflow
exception Sum = Overflow
exception Diff = Overflow
exception Floor = Overflow
exception Sqrt
exception Exp
exception Ln
exception Mod = Div
exception Interrupt = MLWorks.Interrupt
val env = MLWorks.Internal.Runtime.environment
val sqrt: real -> real = env "real square root"
val sin: real -> real = env "real sin"
val cos: real -> real = env "real cos"
val arctan: real -> real = env "real arctan"
val exp: real -> real = env "real exp"
val ln: real -> real = env "real ln"
val chr = MLWorks.String.chr
val ord = MLWorks.String.ord
val explode = MLWorks.String.explode
val implode = MLWorks.String.implode
end
;
