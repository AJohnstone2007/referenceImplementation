require "../utils/lists";
require "regexp";
functor RegExp (structure Lists : LISTS) : REGEXP =
struct
datatype RegExp =
DOT of RegExp * RegExp
| STAR of RegExp
| BAR of RegExp * RegExp
| NODE of string
| CLASS of string
| EPSILON
fun plusRE r = DOT(r,STAR(r))
fun sequenceRE [] = EPSILON
| sequenceRE (r::[]) = r
| sequenceRE (r::rs) = DOT(r,sequenceRE(rs))
local
fun upto(m, n) = if m > n then [] else chr m :: upto(m+1, n)
fun class(m, n) = CLASS(implode (upto(m, n)))
in
val printable = class(32, 126)
val letter = CLASS "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnbopqrstuvwxyz"
val digit = CLASS "0123456789"
val hexDigit = CLASS "0123456789abcdefABCDEF"
val any = class(0, 255)
fun negClass s = CLASS(implode (Lists.difference (upto(0, 255), explode s)))
end
end
;
