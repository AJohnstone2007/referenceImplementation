require "date_demo";
require "$.basis.__date";
require "$.system.__os";
require "$.system.__time";
structure DateDemo : DATE_DEMO =
struct
fun printDate dt =
(print (Date.toString dt);
print "\n")
fun fileDate file =
printDate (Date.fromTimeLocal (OS.FileSys.modTime file))
handle OS.SysErr (message, error) =>
print ("System error:\n" ^ message ^ "\n")
fun dateNow () =
printDate (Date.fromTimeLocal (Time.now ()))
end
;
