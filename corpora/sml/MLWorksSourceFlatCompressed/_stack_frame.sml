require "stack_frame";
functor StackFrame () : STACK_FRAME =
struct
val hide_c_frames = ref true
val hide_setup_frames = ref true
val hide_anonymous_frames = ref true
val hide_handler_frames = ref true
val hide_delivered_frames = ref true
val hide_duplicate_frames = ref true
end
;
