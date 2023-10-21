require "_text_io";
require "__text_prim_io";
require "^.system.__os_prim_io";
structure TextIO = TextIO(structure TextPrimIO = TextPrimIO
structure OSPrimIO = OSPrimIO);
