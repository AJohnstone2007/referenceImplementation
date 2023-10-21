signature REGEXP =
sig
datatype RegExp =
DOT of RegExp * RegExp
| STAR of RegExp
| BAR of RegExp * RegExp
| NODE of string
| CLASS of string
| EPSILON
val plusRE : RegExp -> RegExp
val sequenceRE : RegExp list -> RegExp
val printable : RegExp
val letter : RegExp
val digit : RegExp
val hexDigit : RegExp
val any : RegExp
val negClass : string -> RegExp
end
;
