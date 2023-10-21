require "pack_word";
require "_pack_words_big";
require "__word16";
structure Pack16Big : PACK_WORD =
PackWordsBig(
structure Word = Word16
);
