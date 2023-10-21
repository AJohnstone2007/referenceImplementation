require "pack_word";
require "_pack_words_big";
require "__pre_word32";
structure Pack32Big : PACK_WORD =
PackWordsBig(
structure Word = PreWord32
);
