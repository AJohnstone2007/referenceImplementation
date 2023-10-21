require "puzzle";
require "$.basis.__char_vector";
require "$.basis.__char_array";
structure Puzzle : PUZZLE =
struct
datatype direction = HORIZ | VERT
datatype gap = GAP of {dir: direction,
x: int,
y: int,
length: int}
datatype puzzle = PUZZLE of {grid : CharArray.array,
width : int,
height : int,
words : string list,
gaps : gap list}
datatype placement = PLACE of {updates : (char * int) list,
gap : int}
fun removeNth ([], _) = raise Empty
| removeNth (h::t, 1) = t
| removeNth (h::t, n) = h :: removeNth (t, n - 1)
fun getPos (PUZZLE {width = width, ...}, x, y) = y * width + x
fun getCell (PUZZLE {grid = grid, width = width, ...}, x, y) =
CharArray.sub (grid, y * width + x)
fun place (PUZZLE {grid = grid, ...},
PLACE {updates = updates, ...}) =
let
fun placeChars [] = ()
| placeChars ((chr, pos)::t) =
(CharArray.update (grid, pos, chr);
placeChars t)
in
placeChars updates
end
fun unplace (PUZZLE {grid = grid, ...},
PLACE {updates = updates, ...}) =
let
fun unplaceChars [] = ()
| unplaceChars ((chr, pos)::t) =
(CharArray.update (grid, pos, #"#");
unplaceChars t)
in
unplaceChars updates
end
fun matchGap' (puzzle as PUZZLE {grid = grid, ...},
GAP {dir = dir, x = xStart, y = yStart, length = length},
word) =
let
fun matchChar (i, places) =
if i >= length then
SOME places
else
let
val gridPos = case dir of
HORIZ => getPos (puzzle, xStart + i, yStart)
| VERT => getPos (puzzle, xStart, yStart + i)
val gridChar = case dir of
HORIZ => getCell (puzzle, xStart + i, yStart)
| VERT => getCell (puzzle, xStart, yStart + i)
val wordChar = CharVector.sub (word, i)
in
case gridChar of
#" " => NONE
| #"#" => matchChar (i + 1, (wordChar, gridPos) :: places)
| c =>
if wordChar = c then
matchChar (i + 1, places)
else
NONE
end
in
matchChar (0, [])
end
fun matchGap (puzzle, gap as GAP {length = length, ...}, word) =
if length <> (CharVector.length word) then
NONE
else
matchGap' (puzzle, gap, word)
fun fitWord (puzzle as PUZZLE {gaps = gaps, ...}, word) =
let
fun fitGap ([], _) = []
| fitGap (h::t, gapNum) =
case matchGap (puzzle, h, word) of
NONE => fitGap (t, gapNum + 1)
| SOME places => PLACE {updates = places,
gap = gapNum} :: fitGap (t, gapNum + 1)
in
fitGap (gaps, 1)
end
fun printGrid (PUZZLE {grid = grid,
width = width,
height = height,
...}) =
let
fun printLine h =
if h >= height then
()
else
(print (CharArray.extract (grid, h * width, SOME width) ^ "\n");
printLine (h + 1))
in
(print "\nSolution:\n\n";
printLine 0)
end
fun solvePuzzle (puzzle as PUZZLE {words = words, ...}) =
case words of
[] => printGrid puzzle
| (word :: rest) => placeWord (puzzle, fitWord (puzzle, word), rest)
and placeWord (puzzle as PUZZLE {grid = grid,
width = width,
height = height,
words = words,
gaps = gaps},
places,
otherWords) =
case places of
[] => ()
| ((headPlace as PLACE {gap = gap, ...}) :: tailPlace) =>
(place (puzzle, headPlace);
solvePuzzle (PUZZLE {grid = grid,
width = width,
height = height,
words = otherWords,
gaps = removeNth (gaps, gap)});
unplace (puzzle, headPlace);
placeWord (puzzle, tailPlace, otherWords))
fun findGaps (puzzle as PUZZLE {grid = grid,
width = width,
height = height,
words = words,
gaps = gaps}) =
let
fun consOption (NONE, l) = l
| consOption (SOME v, l) = v :: l
fun findHorizGaps (x, y, blanks) =
let
fun horizGap () =
if blanks > 1 then
SOME (GAP {dir = HORIZ,
x = x - blanks,
y = y,
length = blanks})
else
NONE
in
if y >= height then
[]
else
if x >= width then
consOption (horizGap (), findHorizGaps (0, y + 1, 0))
else
if getCell (puzzle, x, y) <> #" " then
findHorizGaps (x + 1, y, blanks + 1)
else
consOption (horizGap (), findHorizGaps (x + 1, y, 0))
end
fun findVertGaps (x, y, blanks) =
let
fun vertGap () =
if blanks > 1 then
SOME (GAP {dir = VERT,
x = x,
y = y - blanks,
length = blanks})
else
NONE
in
if x >= width then
[]
else
if y >= height then
consOption (vertGap (), findVertGaps (x + 1, 0, 0))
else
if getCell (puzzle, x, y) <> #" " then
findVertGaps (x, y + 1, blanks + 1)
else
consOption (vertGap (), findVertGaps (x, y + 1, 0))
end
in
PUZZLE {grid = grid,
width = width,
height = height,
words = words,
gaps = findHorizGaps (0, 0, 0) @ findVertGaps (0, 0, 0)}
end
local
fun sort (l, compare) =
let
fun ins (x, []) = [x]
| ins (x, y::ys) =
if compare (x, y) = GREATER then
y::ins (x, ys)
else
x::y::ys
fun insort [] = []
| insort (x::xs) = ins (x, insort xs)
in
insort l
end
fun compareStrings (s1, s2) =
if size s1 > size s2 then GREATER
else if size s1 = size s2 then EQUAL
else LESS
fun compareLists (l1, l2) =
if length l1 > length l2 then GREATER
else if length l1 = length l2 then EQUAL
else LESS
fun separate ([], _, acc) = acc
| separate (_, _, []) = raise Empty
| separate (h::t, length, ah::at) =
if size h = length then
separate (t, length, (h::ah)::at)
else
separate (t, size h, [h]::ah::at)
fun join [] = []
| join (h::t) = h @ join t
in
fun orderWords words =
let
val sortWords = sort (words, compareStrings)
val separateWords = separate (sortWords, ~1, [[]])
val orderedWords = sort (separateWords, compareLists)
in
join orderedWords
end
end
fun getWidth lineList =
let
val width = CharVector.length (hd lineList)
fun getWidth' ([], size) = size
| getWidth' (h::t, size) =
if CharVector.length h = size then
getWidth' (t, size)
else
raise Size
in
getWidth' (lineList, width)
end
fun makePuzzle (lineList, wordList) =
let
val width = getWidth lineList
val height = length lineList
val words = orderWords wordList
val gridVec = CharVector.concat lineList
val grid = CharArray.array (CharVector.length gridVec, #" ")
val _ = CharArray.copyVec {src = gridVec,
si = 0,
len = NONE,
dst = grid,
di = 0}
in
findGaps (PUZZLE {grid = grid,
width = width,
height = height,
words = words,
gaps = []})
end
fun solve (lineList, wordList) =
solvePuzzle (makePuzzle (lineList, wordList))
val smallPuzzle =
(["##### #",
"# # # #",
"# # ###",
"##### #",
"### ###",
"###   #"],
["APPLE", "AWE", "BUBBLE", "EAT", "LABEL", "OWL",
"PIT", "ROTATE", "TEE", "ZEALOT", "ZEBRA"])
val mediumPuzzle =
(["      #  # #### #  ",
"      #  #    # # #",
"#   # #  #    # # #",
"#   ###############",
"##### #  # #  # # #",
"#   #    # #  # # #",
"    #      #    # #",
"    #  #######  # #",
"  #####    #    # #",
"    #    ###### # #",
"    #  #   #    # #",
" ######### #      #",
"  # #  #  ######   ",
"  # #    #         ",
" ########### ####  ",
"  #      # #  #    ",
"  #      # ######  ",
"########   #  #    ",
"  #        #  #####",
"########## #       "],
["BAR", "BILL", "CHEF", "LIFT", "MAID", "FOYER", "GUARD", "GUEST",
"HOTEL", "LUNCH", "BARMAN", "DINNER", "PORTER", "STAIRS",
"TOWELS", "WAITER", "CARPARK", "WAITRESS", "BREAKFAST", "RECEPTION",
"SINGLEROOM", "RESTAURANT", "CHAMBERMAID", "SITTINGROOM",
"TELEPHONIST", "SERVICECHARGE", "EARLYMORNINGTEA"])
val largePuzzle =
(["  #########  ######## #  #",
"     #    #  #        #  #",
"   ####   #  #        #  #",
" #   #    ############## #",
" #   #    #  # #      #  #",
"###########  # ###### #  #",
" #   #    #  # #      #  #",
" #   #    #  # #   #  ####",
" # # # ####    ###### #   ",
" # # #         #   #  # # ",
" # # # #     ##### #  # # ",
" # ########    #  ########",
"   # # #    #  #   #    # ",
"   #   #    #   ######  # ",
"   #        #      #    # ",
"#################  #    # ",
"   #        #           # ",
"#########   #     ########",
"#         # #         # # ",
"#    #  ######        # # ",
"#    #    # #         # # ",
"#    #    # #   ####### # ",
"#    #      #         # # ",
"   ##########         # # ",
"     #         ######## # "],
["ANTS", "MICE", "RATS", "RUST", "SCAB", "SLUGS", "APHIDS", "CAPSID",
"EARWIG", "MILDEW", "SNAILS", "WEEVIL", "TERMITE", "BLACKFLY",
"CLUBROOT", "CUTWORMS", "GREENFLY", "LEAFSPOT", "WHITEFLY",
"WIREWORM", "WOODLICE", "APPLESCAB", "BLACKSPOT", "CARROTFLY",
"PEAWEEVIL", "TULIPFIRE", "FROGHOPPER", "GREENMOULD", "REDSPIDERS",
"FLEABEETLES", "CATERPILLARS", "CODLINGMOTHS", "LEATHERJACKET",
"CABBAGEROOTFLY", "GOOSEBERRYSAWFLY", "RASPBERRYCANESPOT"])
end
;
