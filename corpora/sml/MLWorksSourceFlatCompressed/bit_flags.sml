require "__sys_word";
signature BIT_FLAGS =
sig
eqtype flags
val toWord : flags -> SysWord.word
val fromWord : SysWord.word -> flags
val flags : flags list -> flags
val allSet : (flags * flags) -> bool
val anySet : (flags * flags) -> bool
end
;
