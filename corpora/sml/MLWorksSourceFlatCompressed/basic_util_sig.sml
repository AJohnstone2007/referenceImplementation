require "__text_io";
infix 1 /=
signature BASIC_UTIL =
sig
val fst : 'a * 'b -> 'a
val snd : 'a * 'b -> 'b
val pair : ('c-> 'a)*('c-> 'b)-> 'c-> 'a* 'b
val /= : ''a * ''a -> bool
val eq : ''a-> ''a -> bool
val inc : int ref -> int
val curry : ('a * 'b-> 'c)-> 'a-> 'b-> 'c
val uncurry : ('a-> 'b-> 'c)-> 'a * 'b-> 'c
val twist : ('a * 'b-> 'c)-> 'b * 'a-> 'c
val K0 : 'a -> unit
structure ListUtil :
sig
val getx : ('a -> bool) -> 'a list -> exn -> 'a
val updateVal : ('a -> bool) -> 'a -> 'a list -> 'a list
val dropWhile : ('a -> bool) -> 'a list -> 'a list
val break : ('a -> bool) -> 'a list -> 'a list * 'a list
val sort : ('a * 'a -> bool) -> 'a list -> 'a list
val prefix : ''a list -> ''a list -> bool
end
structure StringUtil :
sig
val words : string -> string list
val concatWith : string -> string list-> string
val breakAtDot : string -> string* string
val toInt : string -> int
val adaptString : string -> string
val all : (char-> bool)-> string-> bool
val isDot : char -> bool
val isComma : char -> bool
val isLinefeed : char -> bool
val isOpenParen : char -> bool
val isCloseParen: char -> bool
end
structure FileUtil :
sig
val execute: string * string list -> TextIO.instream*
TextIO.outstream
val exec : string * string list -> bool
val cd : string -> unit
val ls : string -> string list
val pwd : unit -> string
val isDir : string -> bool
val getEnv : string -> string option
val isDirRdAndWr : string -> bool
val isFileRd : string -> bool
val isFileRdAndEx : string -> bool
val openFile : ( string -> exn) -> string -> TextIO.instream
end
end
;
