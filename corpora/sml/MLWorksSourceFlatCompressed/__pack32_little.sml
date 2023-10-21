require "pack_word";
require "_pack_words_little";
require "__pre_word32";
structure Pack32Little : PACK_WORD =
PackWordsLittle(
structure Word = PreWord32
);
