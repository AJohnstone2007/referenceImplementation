Shell.Options.set (Shell.Options.Compiler.generateTraceProfileCode, true);
Shell.Options.set(Shell.Options.Compiler.generateVariableDebugInfo,true);
functor F(type t val args:t list val compute:t list -> t) =
struct
fun f args =
let
val result = compute args
in
()
end
val args = args
end;
structure F1 = F(struct
type t = int
val args = [1,2]
fun compute args =
let
fun compute [] result = result
| compute (arg::args) result = compute args (result+arg)
in
compute args 0
end
end);
structure F2 = F(struct
type t = string
val args = ["1","2"]
fun compute args =
let
fun compute [] result = result
| compute (arg::args) result = compute args (result^arg)
in
compute args ""
end
end);
Shell.Trace.breakpoint "f";
Shell.Options.set(Shell.Options.Compiler.generateVariableDebugInfo,false);
functor F(type t
val args:t list
val compute:t list -> t) =
struct
fun f args =
let
val result = compute args
in
()
end
val args = args
end;
structure F1 = F(struct
type t = int
val args = [1,2]
fun compute args =
let
fun compute [] result = result
| compute (arg::args) result = compute args (result+arg)
in
compute args 0
end
end);
structure F2 = F(struct
type t = string
val args = ["1","2"]
fun compute args =
let
fun compute [] result = result
| compute (arg::args) result = compute args (result^arg)
in
compute args ""
end
end);
(F1.f F1.args,F2.f F2.args);
