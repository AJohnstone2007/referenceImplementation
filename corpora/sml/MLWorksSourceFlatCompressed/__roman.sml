require "roman";
require "$.basis.__string";
structure Roman : ROMAN =
struct
datatype numeral = I of int | X of int | C of int | M of int
exception Numeral
fun digit (0, one, five, ten) = []
| digit (1, one, five, ten) = [one]
| digit (2, one, five, ten) = [one, one]
| digit (3, one, five, ten) = [one, one, one]
| digit (4, one, five, ten) = [one, five]
| digit (5, one, five, ten) = [five]
| digit (6, one, five, ten) = [five, one]
| digit (7, one, five, ten) = [five, one, one]
| digit (8, one, five, ten) = [five, one, one, one]
| digit (9, one, five, ten) = [one, ten]
| digit _ = []
fun numerals (I d) = digit (d, #"I", #"V", #"X")
| numerals (X d) = digit (d, #"X", #"L", #"C")
| numerals (C d) = digit (d, #"C", #"D", #"M")
| numerals (M 0) = []
| numerals (M n) = #"M" :: numerals (M (n - 1))
fun value #"I" = 1
| value #"V" = 5
| value #"X" = 10
| value #"L" = 50
| value #"C" = 100
| value #"D" = 500
| value #"M" = 1000
| value _ = raise Numeral
fun intToRoman n =
let
val ones = I (n mod 10)
val tens = X ((n div 10) mod 10)
val hundreds = C ((n div 100) mod 10)
val thousands = M (n div 1000)
in
String.implode (numerals thousands @
numerals hundreds @
numerals tens @
numerals ones)
end
fun romanToInt s =
let
fun addNumerals (~1, previous) = 0
| addNumerals (i, previous) =
let
val num = value (String.Char.toUpper (String.sub (s, i)))
in
(if num >= previous then
num + addNumerals (i - 1, num)
else
~num + addNumerals (i - 1, num))
end
in
SOME (addNumerals ((String.size s) - 1, 0))
handle Numeral => NONE
end
end
;
