require "../main/__info";
require "../motif/__xm";
require "../system/__os";
require "_file_dialog";
structure FileDialog_ =
FileDialog
(structure Xm = Xm
structure Info = Info_
structure OS = OS)
;
