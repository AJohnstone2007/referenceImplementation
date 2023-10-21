require "$.basis.__int";
require "$.motif.__xm";
structure Threads = MLWorks.Threads;
fun text_window {title, height, width} =
let
val appshell = Xm.initialize (title,
"appshellappclass", [])
val text_widget = Xm.Widget.createScrolledText
(appshell, "text_widget",
[(Xm.EDIT_MODE, Xm.EDIT_MODE_VALUE Xm.MULTI_LINE_EDIT),
(Xm.HEIGHT, Xm.INT height),
(Xm.WIDTH, Xm.INT width)])
val _ = Xm.Widget.manage text_widget
val _ = Xm.Widget.realize appshell
val pos = ref 0
fun output s =
let
val length = size s
val current = !pos
val new = current + length
in
(Xm.Text.insert (text_widget,
current,
s);
pos := new)
end
fun destroy () = Xm.Widget.destroy appshell
in
(output,destroy)
end
local
fun doit(0,f,a) = ()
| doit (n,f,a) = (ignore (f a);
doit (n-1,f,a))
fun id x = x
fun delay n = doit(n,id,())
fun delayed_dot (d,out) = (delay d;
out ".")
fun slow_line' (n,d,out) = (doit(n,delayed_dot,(d,out));
out "\n")
fun slow_lines (wname,n,m,d,out) =
(ignore (out("some dots : "^wname^"\n"));
doit (n,slow_line',(m,d,out));
out("done some dots for "^wname^"\n"))
in
fun slow_window(wname,h,w,n,m,d) =
let val (ptw,dtw) = text_window{title = wname, height = h, width = w}
in (slow_lines(wname:string,n,m,d,ptw);
dtw())
end
fun dots () = slow_window("dots",200,200,20,20,50000)
fun slow_line (n,out) = slow_line'(n,100000,out)
end
fun delay 0 = () | delay n = delay (n-1)
fun force t =
case Threads.result t of
Threads.Result r => r
| Threads.Exception e => raise e
| _ => (Threads.yield ();
force t)
val ms = Int.toString
fun tak_report (p,x,y,z) =
p ("tak("^(ms x)^", "^(ms y)^", "^(ms z)^")\n")
fun tak_result (p,n) =
p ("returning "^(ms n)^"\n")
fun tak (x,y,z) =
if x <= y
then z
else
if x+y+z < 34 then
let
val x' = tak(x-1,y,z)
val y' = tak(y-1,z,x)
val z' = tak(z-1,x,y)
in
tak(x',y',z')
end
else
let
val (ptw,dtw) = text_window{title =" tak",height = 300, width = 400}
fun tak' (x,y,z) =
if x <= y
then z
else
if x+y+z < 34 then
let
val x' = tak(x-1,y,z)
val y' = tak(y-1,z,x)
val z' = tak(z-1,x,y)
in
tak(x',y',z')
end
else
let
val x' = Threads.fork tak(x-1,y,z)
val _ = (ptw"forking ";
tak_report(ptw,x-1,y,z))
val _ = slow_line(20,ptw)
val y' = Threads.fork tak(y-1,z,x)
val _ = (ptw"forking ";
tak_report(ptw,y-1,z,x))
val _ = slow_line(20,ptw)
val z'' = tak'(z-1,x,y)
val x'' = force x'
val y'' = force y'
in
tak'(x'',y'',z'')
end
val _ = tak_report(ptw,x,y,z)
val _ = slow_line(20,ptw)
val ans = tak'(x,y,z)
val _ = tak_result(ptw,ans)
val _ = slow_line(20,ptw)
in
(dtw();
ans)
end
fun tak_test () = (Threads.Internal.Preemption.start();
tak (18,12,6))
fun par_tak () = (Threads.Internal.Preemption.start();
ignore (Threads.fork tak (18,12,6));
())
;
