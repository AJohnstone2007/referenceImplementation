require "pack_word";
require "_pack_words_little";
require "__word16";
structure Pack16Little : PACK_WORD =
PackWordsLittle(
structure Word = Word16
);
