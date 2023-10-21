structure CommonIO =
struct
local
val Log = ref false
val LogFile = ref ""
val LogStream = ref std_out
in
fun read i n = input(i,n)
fun write os s = (output(os,s) ; flush_out os)
fun printlength n = System.Control.Print.printLength := n
fun printdepth n = System.Control.Print.printDepth := n
fun interrupt () = false
fun setLogFile logfile =
if logfile = ""
then Log := false
else
if !LogFile = logfile
then Log := true
else
(if !LogFile = "" then ()
else close_out (!LogStream);
LogFile := logfile;
LogStream := open_out logfile;
Log := true)
fun isLogSet () = !Log
fun logFile () = !LogFile
fun noLog () = (Log := false; LogFile := "" ; LogStream := std_out)
fun closeLogFile () = close_out (!LogStream)
val read_terminal = read std_in
fun write_terminal s = (if (!Log) then write (!LogStream) s else () ; write std_out s)
val read_line = input_line
fun read_line_terminal () =
let val line = input_line std_in
in (if !Log then write (!LogStream) line else () ; line)
end
end
fun newline () = write_terminal "\n"
end ;
