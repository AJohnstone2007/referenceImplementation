require "queens";
require "$.basis.__word";
require "$.basis.__int";
structure Queens : QUEENS =
struct
fun getBit (w, n) =
let
val place = Word.fromInt n
val mask = Word.<< (0w1, place)
val b = Word.andb (w, mask)
in
Word.>> (b, place)
end
fun setBit (w, n) =
let
val mask = Word.<< (0w1, Word.fromInt n)
in
Word.orb (w, mask)
end
fun makeRow (column, queenPos, size) =
let
val letter = if column = queenPos then "Q " else "o "
in
if column < size then letter ^ makeRow (column + 1, queenPos, size)
else "\n"
end
fun makeBoard (size, []) = "\n"
| makeBoard (size, h::t) = makeRow (0, h, size) ^ makeBoard (size, t)
fun placeQueen (size, row, vert, ldiag, rdiag, queens) =
let
fun trySquare column =
if column < size then
if getBit (vert, column) = 0w0 andalso
getBit (ldiag, column) = 0w0 andalso
getBit (rdiag, column) = 0w0
then placeQueen (size, row - 1, setBit (vert, column),
Word.<< (setBit (ldiag, column), 0w1),
Word.>> (setBit (rdiag, column), 0w1),
column::queens)
+ trySquare (column + 1)
else trySquare (column + 1)
else 0
in
if row = 0 then (print (makeBoard (size, queens)); 1)
else trySquare 0
end
fun nQueens n =
if n > Word.wordSize then
print "Board size too large.\n"
else
(print ("Number of solutions: " ^
Int.toString (placeQueen (n, n, 0w0, 0w0, 0w0, [])) ^ "\n"))
handle Overflow => print ("Too many solutions!")
fun eightQueens () = nQueens 8
end
;
