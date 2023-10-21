require "^.basis.__text_io";
require "^.system.__getenv";
require "^.utils.__terminal";
require "^.system.__os";
require "../main/__info";
require "../utils/_diagnostic";
require "../utils/__text";
require "../utils/__lists";
require "__sectioned_file";
require "_proj_file";
structure ProjFile_ =
ProjFile (
structure OS = OS
structure TextIO = TextIO
structure SectionedFile = SectionedFile
structure Getenv = Getenv_
structure Terminal = Terminal
structure Info = Info_
structure Lists = Lists_
structure Diagnostic =
Diagnostic (structure Text = Text_)
)
;
