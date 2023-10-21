require "__sys_word";
require "bit_flags";
structure BitFlags : BIT_FLAGS =
struct
type flags = SysWord.word;
val toWord = fn x => x
val fromWord = fn x => x
fun flags(acc, []) = acc
| flags(acc, x :: xs) = flags(SysWord.orb(acc, x), xs)
val flags = fn x => flags(0w0, x)
fun allSet(total_flags, test_flags) =
let
val combined = SysWord.orb(total_flags, test_flags)
in
combined = total_flags
end
fun anySet(total_flags, test_flags) =
let
val intersected = SysWord.andb(total_flags, test_flags)
in
intersected <> 0w0
end
end
;
