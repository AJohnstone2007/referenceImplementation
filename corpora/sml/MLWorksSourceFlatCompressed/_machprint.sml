require "^.basis.__string_cvt";
require "^.basis.__text_io";
require "../utils/lists";
require "sparc_assembly";
require "../main/machprint";
functor MachPrint(
structure Lists : LISTS
structure Sparc_Assembly : SPARC_ASSEMBLY
) : MACHPRINT =
struct
structure Sparc_Assembly = Sparc_Assembly
type Opcode = Sparc_Assembly.opcode
val opcol = 35
val labcol = 5
fun pad columns x = StringCvt.padRight #" " columns x
fun align n = if n mod 2 = 0 then n else n+1
fun print_code (stream,labmap) (n,((tag, code),name)) =
(TextIO.output(stream, ("[Sparc_Assembly Code]" ^ " for " ^ name ^ "\n"));
Lists.reducel
(fn (n,(x,y)) =>
let
val (lab,ass) = Sparc_Assembly.labprint (x,n,labmap)
val line =
if size y = 0
then concat [pad labcol lab,ass,"\n"]
else
concat [pad labcol lab,pad opcol ass,"; ",y,"\n"]
in
TextIO.output(stream,line);
n+1
end)
(align (n+2),code))
fun print_mach_code code_list_list stream =
let
val labmap =
Sparc_Assembly.make_labmap
(map
(fn code_list =>
map
(fn ((tag,code),name) => (map (fn (x,y) => x) code))
code_list)
code_list_list)
in
ignore(
Lists.reducel
(fn (n,code_list) => Lists.reducel (print_code (stream,labmap)) (align n,code_list))
(0,code_list_list));
()
end
end
;
