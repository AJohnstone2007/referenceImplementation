require "../basis/list";
require "../typechecker/types";
require "../typechecker/scheme";
require "../main/user_options";
require "../rts/gen/tags";
require "../interpreter/incremental";
require "../interpreter/shell_types";
require "../main/stack_interface";
require "debugger_utilities";
require "value_printer";
require "newtrace";
functor Trace (
structure List : LIST
structure Types : TYPES
structure Scheme : SCHEME
structure UserOptions : USER_OPTIONS
structure Tags : TAGS
structure Incremental : INCREMENTAL
structure ShellTypes : SHELL_TYPES
structure ValuePrinter : VALUE_PRINTER
structure StackInterface : STACK_INTERFACE
structure DebuggerUtilities : DEBUGGER_UTILITIES
sharing ValuePrinter.Options = UserOptions.Options
sharing Types.Datatypes = Scheme.Datatypes
sharing type Incremental.Context = ShellTypes.Context
sharing type UserOptions.Options.options = ShellTypes.Options.options
sharing type Types.Options.options = UserOptions.Options.options
sharing type UserOptions.user_tool_options = ShellTypes.user_options
sharing type ValuePrinter.Type = Types.Datatypes.Type =
DebuggerUtilities.Debugger_Types.Type
sharing type ValuePrinter.DebugInformation =
Incremental.InterMake.Compiler.DebugInformation =
DebuggerUtilities.Debugger_Types.information
) : TRACE =
struct
structure Options = UserOptions.Options
structure DebuggerTypes = DebuggerUtilities.Debugger_Types
structure Datatypes = Types.Datatypes
structure Value = MLWorks.Internal.Value
structure Trace = MLWorks.Internal.Trace;
structure Frame = Value.Frame
structure Debugger = MLWorks.Internal.Debugger
structure NewMap = Datatypes.NewMap
type Type = Datatypes.Type
type Context = Incremental.Context
type UserOptions = UserOptions.user_tool_options
fun print_entry (name,arg) = print (name ^ " " ^ arg ^ "\n")
val do_debug = false
fun debug s = if do_debug then print (s ^ "\n") else ()
fun fdebug f = if do_debug then print (f() ^ "\n") else ()
exception Value of string
local
val env = MLWorks.Internal.Runtime.environment
in
val apply_all : (Value.T -> unit) -> unit = env "trace ml apply all"
val trace_code_intercept : Value.T * (Frame.frame -> unit) -> unit = env "trace code intercept"
val trace_code_replace : Value.T * (Frame.frame -> unit) -> unit = env "trace code replace"
val trace_set_code_loader_function : (Value.T -> unit) -> unit = env "trace set code loader function"
val trace_unset_code_loader_function : (unit -> unit) -> unit = env "trace unset code loader function"
end
fun get_name_and_location code_name =
let
fun aux1(#"[" ::l,acc) = (acc,l)
| aux1(c::l,acc) = aux1(l,c::acc)
| aux1([],acc) = (acc,[])
fun aux2([#"]" ],acc) = acc
| aux2(#"]" ::l,acc) = acc
| aux2(c::l,acc) = aux2(l,c::acc)
| aux2([],acc) = acc
val (namechars,rest) = aux1(explode code_name,[])
val locchars = aux2 (rest,[])
in
(implode (rev namechars),implode (rev locchars))
end
fun strip_location s =
let
val len = size s
fun strip n =
if n = len then s
else
if MLWorks.String.ordof (s,n) = ord #"["
then substring (s,0,n)
else strip (n+1)
in
strip 0
end
fun get_code_string value =
if
Value.primary value = Tags.POINTER andalso
#1 (Value.header value) = Tags.BACKPTR
then
Value.code_name value
else
raise Value "code_name: not a code item"
fun get_code_name f =
let
val code_name = get_code_string f
val (name,loc) = get_name_and_location code_name
in
name
end
fun funarg (Datatypes.FUNTYPE (t,_)) = t
| funarg _ = Datatypes.NULLTYPE
fun funres (Datatypes.FUNTYPE (_,t)) = t
| funres _ = Datatypes.NULLTYPE
exception TypeReconstructionFailed of string
fun assoc (a,[]) = NONE
| assoc (a,(a',b)::rest) =
if a = a' then SOME b
else assoc (a,rest)
fun infer_types (ty,[]) = ty
| infer_types (ty,recipe::rest) =
infer_types (DebuggerUtilities.apply_recipe (recipe,ty),rest)
fun gather_recipes (frame,acc,debug_info) =
let
val (another,next,offset) = Frame.frame_next frame
in
if offset <> 0
then
let
val frame_name = StackInterface.frame_name next
in
case DebuggerTypes.lookup_debug_info (debug_info,frame_name) of
SOME (DebuggerTypes.FUNINFO {ty,annotations,...}) =>
let
val argty = funarg ty
val recipe =
case assoc (offset,annotations) of
SOME recipe => recipe
| _ => DebuggerTypes.NOP
in
if DebuggerUtilities.is_type_polymorphic argty
then
if another then gather_recipes (next,recipe::acc,debug_info)
else raise TypeReconstructionFailed "No more frames"
else
(argty,recipe::acc)
end
| _ => gather_recipes (next,acc,debug_info)
end
else
gather_recipes (next,acc,debug_info)
end
fun reconstruct_type (frame,ty,debug_info) =
if DebuggerUtilities.is_type_polymorphic ty
then
(infer_types (gather_recipes (frame,[],debug_info)))
handle DebuggerUtilities.ApplyRecipe s =>
(debug ("Recipe Application failed: " ^ s);
ty)
| TypeReconstructionFailed s =>
(debug ("Type Reconstruction Failed: " ^ s);
ty)
else ty
fun get_fun_types (f,debug_info) =
let
val name = get_code_string f
in
case DebuggerTypes.lookup_debug_info (debug_info,name) of
SOME (DebuggerTypes.FUNINFO {ty = Datatypes.FUNTYPE (dom,ran),...}) =>
(dom,ran)
| _ => (Datatypes.NULLTYPE,Datatypes.NULLTYPE)
end
local
open Datatypes
in
fun tyvars_of (ty,acc) =
case ty of
METATYVAR (ref (_,NULLTYPE,_),_,_) => ty :: acc
| METATYVAR (ref (_,ty',_),_,_) => tyvars_of (ty',acc)
| META_OVERLOADED (ref ty',_,_,_) => tyvars_of (ty',acc)
| TYVAR _ => ty :: acc
| METARECTYPE (ref (_,_,ty',_,_)) => tyvars_of (ty',acc)
| RECTYPE labtys =>
NewMap.fold (fn (acc, _, ty) => tyvars_of (ty,acc)) (acc,labtys)
| FUNTYPE (ty1,ty2) => tyvars_of (ty2,tyvars_of (ty1,acc))
| CONSTYPE (tylist,_) =>
List.foldl tyvars_of acc tylist
| DEBRUIJN _ => acc
| NULLTYPE => acc
end
fun generalize (ty1,ty2) =
let
val ty = Datatypes.FUNTYPE (ty1,ty2)
val tyvars = tyvars_of (ty,[])
in
case Scheme.make_scheme (tyvars,(ty,NONE)) of
Datatypes.SCHEME (i,(Datatypes.FUNTYPE (ty1',ty2'),_)) => (ty1',ty2')
| _ => (ty1,ty2)
end
fun instantiate_type (ty,gen_ty,spec_ty) =
if not (DebuggerUtilities.is_type_polymorphic ty)
then ty
else
let
val (ty,gen_ty) = generalize (ty,gen_ty)
val Options.OPTIONS{compat_options=Options.COMPATOPTIONS
{old_definition,...},...} =
ShellTypes.get_current_options (!ShellTypes.shell_data_ref)
in
Scheme.apply_instantiation
(ty, Scheme.generalises_map old_definition (gen_ty,spec_ty))
handle Mismatch =>
(debug ("Instantiation failed");
ty)
end
fun get_frame_info frame =
let
val shell_data = !ShellTypes.shell_data_ref
val user_options = ShellTypes.get_user_options shell_data
val options = ShellTypes.get_current_options shell_data
val context = ShellTypes.get_current_context shell_data
val name = StackInterface.frame_name frame
val debug_info = Incremental.InterMake.current_debug_information ()
val funinfo = DebuggerTypes.lookup_debug_info (debug_info,name)
val (arg_string,ranty) =
case funinfo of
SOME (DebuggerTypes.FUNINFO {ty,...}) =>
let
val arg = StackInterface.frame_arg frame
val arg_type = funarg ty
val reconstructed_arg_type = reconstruct_type (frame,arg_type,debug_info)
val final_type = Types.combine_types (reconstructed_arg_type,arg_type)
handle Types.CombineTypes =>
(fdebug (fn _ =>
concat ["Combine types fails for: ",
name, "\n arg_type: ",
Types.debug_print_type options arg_type,
"\n reconstructed arg type: ",
Types.debug_print_type options
reconstructed_arg_type]);
arg_type)
val ranty = instantiate_type (funres ty,arg_type,final_type)
val print_options = UserOptions.new_print_options user_options
in
(ValuePrinter.stringify_value false (print_options,arg,final_type,debug_info),
ranty)
end
| _ => ("<Unknown arg>",Datatypes.NULLTYPE)
in
(strip_location name,arg_string,ranty)
end
structure Test :
sig
val trace : Value.T * (('a -> 'b) * 'a * Frame.frame -> 'b) -> unit
val replace : Value.T * ('c -> 'd) -> unit
end =
struct
fun trace (cv, new) =
let
fun untraced_once f' a =
(trace_code_intercept (cv, fn _ => trace (cv, new));
f' a)
val replacement =
(fn frame =>
StackInterface.set_frame_return_value
(frame,
Value.cast (new (untraced_once (Value.cast (StackInterface.frame_closure frame)),
Value.cast (StackInterface.frame_arg frame),
frame))))
in
trace_code_replace (cv, replacement)
end
fun replace (cv, new) =
let
val replacement =
(fn frame =>
StackInterface.set_frame_return_value
(frame,
Value.cast (new (Value.cast (StackInterface.frame_arg frame)))))
in
trace_code_replace (cv, replacement)
end
end
val indent_level = ref 0
fun outspaces 0 = ()
| outspaces n = (print"  ";outspaces(n-1))
val always = ((Value.cast (fn _ => true)) : Value.T,
Datatypes.FUNTYPE (Datatypes.DEBRUIJN (0,false,false,
NONE),
Types.bool_type))
val never = ((Value.cast (fn _ => false)) : Value.T,
Datatypes.FUNTYPE (Datatypes.DEBRUIJN (0,false,false,NONE),
Types.bool_type))
exception Error
fun generalises (ty1,ty2) =
let val Options.OPTIONS{compat_options=
Options.COMPATOPTIONS{old_definition,...},...}
= ShellTypes.get_current_options (!ShellTypes.shell_data_ref)
in
Scheme.generalises old_definition (ty1,ty2)
handle Scheme.Mismatch => false
end
fun type_eq (ty1,ty2) = Types.type_eq (ty1,ty2,true,true)
fun trace_full_internal entry_only ((test_val,test_ty), (break_val,break_ty)) fun_val =
let
fun message_fn s = print(s ^ "\n")
val shell_data = !ShellTypes.shell_data_ref
val user_options = ShellTypes.get_user_options shell_data
val options = ShellTypes.get_current_options shell_data
fun do_error s = (message_fn s;raise Error)
in
if not (Types.isFunType test_ty)
then do_error "Test function doesn't have function type"
else ();
if not (Types.isFunType break_ty)
then do_error "Break function doesn't have function type"
else ();
let
fun string_value (value,ty) =
let
val print_options = UserOptions.new_print_options user_options
val debug_info = Incremental.InterMake.current_debug_information ()
in
ValuePrinter.stringify_value false (print_options,value,ty,debug_info)
end
val debug_info = Incremental.InterMake.current_debug_information ()
val (fun_dom,_) = get_fun_types (fun_val,debug_info)
val (test_dom,test_ran) = Types.argres test_ty
val (break_dom,break_ran) = Types.argres break_ty
val fun_name = strip_location (get_code_name fun_val)
val _ =
(if generalises (test_dom,fun_dom)
then ()
else
do_error
(concat [fun_name ^ ": Test function domain not compatible with traced function domain:",
"\n  test domain: ", Types.debug_print_type options test_dom,
"\n  function domain: ", Types.debug_print_type
options fun_dom, "\n"]);
if type_eq (test_ran,Types.bool_type)
then ()
else
do_error
(concat [fun_name ^ ": Test function range not boolean",
"\n  test range: ", Types.debug_print_type options
test_ran, "\n"]);
if generalises (break_dom,fun_dom)
then ()
else
do_error
(concat [fun_name ^ ": Break function domain not compatible with traced function domain:",
"\n  break domain: ", Types.debug_print_type
options break_dom,
"\n  function domain: ", Types.debug_print_type
options fun_dom, "\n"]);
if type_eq (break_ran,Types.bool_type)
then ()
else
do_error
(concat [fun_name ^ ": Break function range not boolean:",
"\n  break range: ", Types.debug_print_type options break_ran,"\n"]))
in
Test.trace
((Value.cast fun_val),
fn (f,a,frame) =>
let
val do_output = ((Value.cast test_val) a) : bool
val do_break = ((Value.cast break_val) a) : bool
in
if do_output
then
if entry_only then
let
val (name,arg,_) = get_frame_info frame
val _ = print_entry (name,arg)
val _ =
if do_break
then
Debugger.break ("Function entry: " ^ name ^ " " ^ arg)
else ()
in
f a
end
else
let
val (name,arg,res_ty) = get_frame_info frame
val level = !indent_level
val _ = indent_level := (!indent_level) + 1
val _ = outspaces level
val _ = print_entry (name,arg)
val _ =
if do_break
then
Debugger.break ("Function entry: " ^ name ^ " " ^ arg)
else ()
val result = f a handle exn => (indent_level := level;raise exn)
in
indent_level := level;
outspaces level;
print (name ^ " returns " ^ string_value (result,res_ty) ^ "\n");
result
end
else
(if do_break
then
let
val (name,arg,_) = get_frame_info frame
in
Debugger.break ("Function entry: " ^ name ^ " " ^ arg)
end
else ();
f a)
end)
handle Trace.Trace s =>
message_fn ("Cannot trace " ^ fun_name ^ ": " ^ s)
end
end handle Error => ()
val traced_functions : (string * ((Value.T * Type) * (Value.T * Type))) list ref = ref []
val broken_functions : (string * ((Value.T * Type) * (Value.T * Type))) list ref = ref []
fun remove' (s,[],acc) = rev acc
| remove' (s,(a,b)::c,acc) =
if s = a then (rev acc) @ c
else remove' (s,c,(a,b)::acc)
fun remove l s = l := remove' (s,!l,[])
val remove_breakpoint = remove broken_functions
val remove_traced = remove traced_functions
fun trim (s,n) =
if size s > n then substring (s,0,n) else s
fun trace_function frame =
let
val (name,arg_string,_) = get_frame_info frame
in
print_entry (name, arg_string)
end
fun break_function frame =
let
val (name,arg_string,_) = get_frame_info frame
in
Debugger.break ("Entering: " ^ name ^ " " ^ arg_string)
end
fun assoc (name,[]) = NONE
| assoc (name,(s,b) :: rest) =
if size name >= size s andalso substring (name,0,size s) = s
then SOME b
else assoc (name,rest)
fun traceit code =
let
val name = Value.code_name code
in
case assoc (name,!traced_functions) of
SOME (f,g) =>
trace_full_internal false (f,g) code
| _ => ()
end
fun frame_message (frame, message) =
let val (name, arg_string, _) = get_frame_info frame in
print( message);
print( name);
print( " ");
print( arg_string);
print( "\n")
end
datatype Stepping = OFF | LOGICALLY_ON | ON
val stepping_status = ref OFF
val step_count = ref 0
val intercepted_function = ref NONE
val breakpoint_table =
ref(NewMap.empty' ((op<):string*string->bool)) : (string, {hits: int, max: int, name: string}) NewMap.map ref
fun is_a_break s = NewMap.tryApply'(!breakpoint_table, s)
fun update_break(arg as {name, ...}) =
breakpoint_table := NewMap.define'(!breakpoint_table, (name, arg))
fun add_break{hits, max, name} =
breakpoint_table :=
NewMap.define'(!breakpoint_table,
(name, {hits=hits, max=max, name=name}))
fun remove_break name =
breakpoint_table := NewMap.undefine(!breakpoint_table, name)
fun install_breakpoint (name, code) =
case assoc (name,!broken_functions) of
SOME _ =>
trace_code_replace (code, breakpoint_replacement)
| NONE => ()
and breakpoint_replacement frame =
let
val (name, arg_string, _) = get_frame_info frame
val f = Value.cast (StackInterface.frame_closure frame)
val a = Value.cast (StackInterface.frame_arg frame)
val c = StackInterface.frame_code frame
in
MLWorks.Internal.Trace.restore f;
intercepted_function := SOME name;
(Debugger.break ("Entering: " ^ name ^ " " ^ arg_string)
handle exn =>
(intercepted_function := NONE;
install_breakpoint (name, c);
raise exn));
intercepted_function := NONE;
(StackInterface.set_frame_return_value (frame, Value.cast (f a))
handle exn => (install_breakpoint (name, c); raise exn));
install_breakpoint (name, c)
end
and breakit code =
let
val name = Value.code_name code
in
install_breakpoint (name, code)
end
and restore_intercepts () =
let
fun restore code = (traceit code; breakit code)
in
Trace.restore_all ();
step_count := 0;
stepping_status := OFF;
trace_set_code_loader_function restore;
apply_all restore
end
fun restore_intercepts_if_not_stepping () =
case !stepping_status of
OFF => restore_intercepts ()
| _ => ()
fun reinstall function frame =
let
val code = StackInterface.frame_code frame
in
trace_code_replace (code, function)
end
fun step_always_replacement frame =
let
val (name, arg_string, _) = get_frame_info frame
val f = Value.cast (StackInterface.frame_closure frame)
val a = Value.cast (StackInterface.frame_arg frame)
val c = StackInterface.frame_code frame
in
restore_intercepts ();
MLWorks.Internal.Trace.restore f;
(if !step_count > 0 then
step_count := !step_count - 1
else
(intercepted_function := SOME name;
stepping_status := LOGICALLY_ON;
Debugger.break ("Entering: " ^ name ^ " " ^ arg_string);
intercepted_function := NONE));
StackInterface.set_frame_return_value (frame, Value.cast (f a))
end
fun step_once_replacement frame =
let
val (name, arg_string, _) = get_frame_info frame
val f = Value.cast (StackInterface.frame_closure frame)
val a = Value.cast (StackInterface.frame_arg frame)
val c = StackInterface.frame_code frame
in
restore_intercepts ();
MLWorks.Internal.Trace.restore f;
intercepted_function := SOME name;
stepping_status := LOGICALLY_ON;
Debugger.break ("Entering: " ^ name ^ " " ^ arg_string);
intercepted_function := NONE;
StackInterface.set_frame_return_value (frame, Value.cast (f a))
end
fun replace_with step code =
let
val full_code_name = Value.code_name code
val function_name = strip_location full_code_name
in
if function_name = "<Setup>" then
()
else
case !intercepted_function of
NONE =>
trace_code_replace (code, step)
| SOME name =>
if name = function_name
then trace_code_intercept (code, reinstall step)
else trace_code_replace (code, step)
end
val make_code_step_always = replace_with step_always_replacement
val make_code_step_once = replace_with step_once_replacement
fun set_stepping b =
if b then
case !stepping_status of
ON => ()
| _ =>
(apply_all make_code_step_always;
trace_set_code_loader_function make_code_step_always;
stepping_status := ON)
else
case !stepping_status of
OFF => ()
| LOGICALLY_ON => stepping_status := OFF
| ON => restore_intercepts ()
fun make_all_code_step_once () =
(apply_all make_code_step_once;
stepping_status := ON)
fun stepping () =
case !stepping_status of
OFF => false
| _ => true
fun set_step_count n =
if n > 0
then step_count := n-1
else step_count := 0
fun step_through f a =
let
val _ = set_stepping true
val result = f a handle exn => (set_stepping false; raise exn)
in
set_stepping false;
result
end
fun tracealways code = trace_code_intercept (code,trace_function)
fun update (name,data,list,acc) =
case list of
[] => (name,data)::acc
| ((a,d)::rest) =>
if a = name then rev acc @ ((a,data)::rest)
else update (name,data,rest,(a,d)::acc)
fun trace name =
let
fun restore code = (traceit code; breakit code)
in
traced_functions := update (name,(always,never),!traced_functions,[]);
remove_breakpoint name;
if stepping () then
()
else
(trace_set_code_loader_function restore;
apply_all traceit)
end
fun trace_full (name,f,g) =
let
fun restore code = (traceit code; breakit code)
in
traced_functions := update (name,(f,g),!traced_functions,[]);
remove_breakpoint name;
if stepping () then
()
else
(trace_set_code_loader_function restore;
apply_all traceit)
end
fun break name =
let
fun restore code = (traceit code; breakit code)
in
broken_functions := update (name,(always,never),!broken_functions,[]);
remove_traced name;
trace_set_code_loader_function restore;
apply_all breakit
end
val break = fn(arg as {hits, max, name}) =>
(break name;
add_break arg)
local
fun addTrace (name, tfl) = (name, (always,never))::tfl
in
fun trace_list names =
if stepping () then
let
val tfl = List.foldl addTrace [] names
in
traced_functions := tfl
end
else
(traced_functions := [];
restore_intercepts ();
List.app trace names)
end
local
val init = ([], NewMap.empty' ((op<):string*string->bool))
fun addBreakpoint (bp as {hits, max, name}, (bpl, bpt)) =
((name, (always,never))::bpl, NewMap.define (bpt, name, bp))
in
fun break_list name_hits_list =
if stepping () then
let
val (bpl, bpt) = List.foldl addBreakpoint init name_hits_list
in
broken_functions := bpl;
breakpoint_table := bpt
end
else
(broken_functions := [];
breakpoint_table := NewMap.empty' ((op<):string*string->bool);
restore_intercepts ();
List.app break name_hits_list)
end
fun untrace name =
(remove_traced name;
restore_intercepts_if_not_stepping ())
fun unbreak name =
(remove_breakpoint name;
remove_break name;
restore_intercepts_if_not_stepping ())
fun untrace_all () =
(traced_functions := [];
restore_intercepts_if_not_stepping ())
fun unbreak_all () =
(broken_functions := [];
breakpoint_table :=
NewMap.empty' ((op<):string*string->bool);
restore_intercepts_if_not_stepping ())
fun breakpoints () =
NewMap.range_ordered(!breakpoint_table)
fun traces () = map #1 (!traced_functions)
fun set_trace_all b =
if b
then apply_all tracealways
else restore_intercepts ()
fun next frame =
let
val step_status_before_next_called = !stepping_status
fun replacement frame =
let
val (name, arg_string, _) = get_frame_info frame
val f = Value.cast (StackInterface.frame_closure frame)
val a = Value.cast (StackInterface.frame_arg frame)
val c = StackInterface.frame_code frame
val _ = MLWorks.Internal.Trace.restore f
in
StackInterface.set_frame_return_value (frame, Value.cast (f a));
case step_status_before_next_called of
ON => set_stepping true
| _ => make_all_code_step_once ()
end
in
set_stepping false;
trace_code_replace ((StackInterface.frame_code frame), replacement)
handle Trace.Trace s =>
(print( "Cannot step over ");
print( strip_location (StackInterface.frame_name frame));
print( ": ");
print( s);
print( "\n"))
end
fun select field =
if field < 0 then
raise Value "select: negative field"
else
fn value =>
let
val primary = Value.primary value
in
if primary = Tags.PAIRPTR
then
if field >= 2
then
raise Value "select: field >= 2 in pair"
else
Value.sub (value, field)
else if primary = Tags.POINTER
then
let
val (secondary, length) = Value.header value
in
if (secondary = Tags.INTEGER0 andalso field=0)
orelse (secondary = Tags.INTEGER1 andalso field=0)
then
Value.sub (value, 1)
else
if secondary = Tags.RECORD then
if field >= length then
raise Value "select: field >= length in record"
else
Value.sub (value, field+1)
else
raise Value "select: invalid secondary"
end
else
raise Value "select: invalid primary"
end
fun get_function_string f =
let
val f : Value.T = Value.cast f
val value = select 0 f
in
if
Value.primary value = Tags.POINTER andalso
#1 (Value.header value) = Tags.BACKPTR
then
Value.code_name value
else
raise Value "code_name: not a code item"
end
fun get_function_name f =
let
val code_name = get_function_string f
val (name,loc) = get_name_and_location code_name
in
name
end
end
;
