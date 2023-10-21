require "_stream_io";
require "__word8_vector";
require "__word8_array";
require "__word8";
require "__bin_prim_io";
structure BinStreamIO =
StreamIO(structure PrimIO = BinPrimIO
structure Vector = Word8Vector
structure Array = Word8Array
val someElem = 0w0: Word8.word)
;
