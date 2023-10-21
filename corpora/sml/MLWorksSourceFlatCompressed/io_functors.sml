structure P =
PrimIO (
structure V = CharVector
structure A = CharArray
val someElem = #"p"
type pos = Position.int
val compare = Position.compare
);
structure S =
StreamIO (
structure PrimIO = P
structure Vector = CharVector
structure Array = CharArray
val someElem = #"p"
);
structure I =
ImperativeIO (
structure StreamIO = S
structure Vector = CharVector
structure Array = CharArray
);
