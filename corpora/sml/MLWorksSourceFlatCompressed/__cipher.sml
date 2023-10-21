require "cipher";
require "$.basis.__list_pair";
require "$.basis.__char";
require "$.basis.__string";
structure Cipher : CIPHER =
struct
fun textList s =
let
fun notAlpha c = not (Char.isAlpha c)
val wordList = String.tokens notAlpha s
val joinedStr = String.concat wordList
in
map Char.toUpper (String.explode joinedStr)
end
fun extendKey (message, key) =
key @ extendKey (String.extract (message, length key, NONE), key)
handle Subscript => key
fun encipherChar (msgChar, keyChar) =
let
val ordA = Char.ord #"A"
val msg = Char.ord msgChar - ordA
val key = Char.ord keyChar - ordA
in
Char.chr ((msg + key) mod 26 + ordA)
end
fun decipherChar (msgChar, keyChar) =
let
val ordA = Char.ord #"A"
val msg = Char.ord msgChar - ordA
val key = Char.ord keyChar - ordA
in
Char.chr ((msg - key) mod 26 + ordA)
end
fun encipher (message, key) =
String.implode (ListPair.map encipherChar
(textList message,
extendKey (message, textList (key))))
fun decipher (message, key) =
String.implode (ListPair.map decipherChar
(String.explode message,
extendKey (message, textList (key))))
end
;
