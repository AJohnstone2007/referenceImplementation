require "__os";
require "^.basis.__substring";
require "^.basis.__char";
require "^.basis.__text_io";
require "version";
require "license";
functor License (
structure Version: VERSION
): LICENSE =
struct
fun ttyComplain st = SOME true
fun license complain = SOME true
end
;
