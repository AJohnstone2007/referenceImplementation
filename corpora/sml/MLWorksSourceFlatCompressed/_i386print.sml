require "../basis/__text_io";
require "../utils/lists";
require "i386_assembly";
require "../main/machprint";
require "^.basis.__string_cvt";
functor I386Print(
structure Lists : LISTS
structure I386_Assembly : I386_ASSEMBLY
) : MACHPRINT =
struct
structure I386_Assembly = I386_Assembly
structure I386_Opcodes = I386_Assembly.I386_Opcodes
type Opcode = I386_Assembly.opcode
val print_bytes = true
val opcol = 35
val labcol = 8
fun pad columns x =
StringCvt.padRight #" " columns x
fun print_nibble i =
if i >= 0 andalso i <= 9 then
chr (i+ ord #"0")
else
if i >= 10 andalso i <= 16 then
chr(i+ ord #"a" - 10)
else
raise Match
fun print_byte i =
let
val hi = (i div 16) mod 16
val lo = i mod 16
in
str (print_nibble hi) ^ str (print_nibble lo) ^ " "
end
val max_bytes = 12
fun space_pad 0 = []
| space_pad n =
if n < 0 then raise Match else "   " :: space_pad(n-1)
fun double_align n = ((n+7) div 8) * 8
fun print_code (stream,labmap) (n,((tag, code),name)) =
(TextIO.output(stream, ("[I386_Assembly Code]" ^ " for " ^ name ^
"\n"));
Lists.reducel
(fn (n,(x,y)) =>
let
val I386_Opcodes.OPCODE byte_list = I386_Assembly.assemble x
val (lab,ass) = I386_Assembly.labprint (x,n,labmap)
val bytes = length byte_list
val code = if print_bytes then map print_byte byte_list else []
val padding =
if print_bytes then
if bytes > max_bytes then
(print"Strange, very long opcode sequence";
[])
else
space_pad(max_bytes - bytes)
else []
val line =
if size y = 0 then
concat (padding @ code @ [pad labcol lab,ass,"\n"])
else
concat (padding @ code @ [pad labcol lab,pad opcol ass,"; ",y,"\n"])
in
TextIO.output(stream,line);
n+I386_Assembly.opcode_size x
end)
(double_align n + 8,code))
fun print_mach_code code_list_list stream =
let
val labmap =
I386_Assembly.make_labmap
(map
(fn code_list =>
map
(fn ((tag,code),name) => (map (fn (x,y) => x) code))
code_list)
code_list_list)
in
ignore(Lists.reducel
(fn (n,code_list) =>
Lists.reducel (print_code (stream,labmap)) (double_align n,code_list))
(0,code_list_list));
()
end
end
;
