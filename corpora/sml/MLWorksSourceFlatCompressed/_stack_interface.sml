require "^.basis.__text_io";
require "^.rts.gen.tags";
require "^.utils.crash";
require "^.main.stack_interface";
functor StackInterface (structure Tags : TAGS
structure Crash : CRASH
) : STACK_INTERFACE =
struct
type frame = MLWorks.Internal.Value.Frame.frame
type ml_value = MLWorks.Internal.Value.ml_value
structure Bits = MLWorks.Internal.Bits
val do_debug = false
fun debug f =
if do_debug
then TextIO.output(TextIO.stdErr,"  # " ^ f () ^ "\n")
else ()
val is_ml_frame = MLWorks.Internal.Value.Frame.is_ml_frame
val cast = MLWorks.Internal.Value.cast
val sp_offset = 14
val closure_offset = 9
val arg_offset = 8
fun frame_arg frame = MLWorks.Internal.Value.Frame.sub (frame,arg_offset)
fun set_frame_return_value (frame,value) = MLWorks.Internal.Value.Frame.update (frame,arg_offset,value)
fun next_frame frame : frame =
cast (MLWorks.Internal.Value.Frame.sub (frame,sp_offset))
fun get_basic_frames (bottom,base_frame) =
let
fun scan (bottom,acc) =
let
val (another,next,offset) = MLWorks.Internal.Value.Frame.frame_next bottom
in
if another andalso next <> base_frame
then
scan (next,(next,offset,offset <> 0)::acc)
else
(if next <> base_frame then debug (fn _ => "No base frame") else ();
acc)
end
val bottom = next_frame bottom
val acc = [(bottom,0,is_ml_frame bottom)]
in
case scan (bottom,acc) of
(_::rest) => rest
| rest => rest
end
fun variable_debug_frame frame = frame
fun frame_code frame =
let
open MLWorks.Internal.Value
val closure = Frame.sub (frame, closure_offset)
val primary = primary closure
val offset =
if primary = Tags.PAIRPTR then 0
else if primary = Tags.POINTER then 1
else Crash.impossible "bad primary for frame_name"
in
sub (closure, offset)
end
fun frame_name frame = MLWorks.Internal.Value.code_name (frame_code frame)
fun frame_closure frame = MLWorks.Internal.Value.Frame.sub (frame,closure_offset)
fun is_stack_extension_frame frame =
let
val closure = frame_closure frame
in
cast closure = Bits.rshift (Tags.STACK_EXTENSION,2)
end
end
;
