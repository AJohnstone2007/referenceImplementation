require "_bin_io";
require "__bin_prim_io";
require "^.system.__os_prim_io";
structure BinIO = BinIO(structure BinPrimIO = BinPrimIO
structure OSPrimIO = OSPrimIO);
