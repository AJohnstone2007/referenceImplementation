require "__symbol";
require "../main/__info";
require "../system/__os";
require "^.system.__os";
require "_module_id";
structure ModuleId_ =
ModuleId
(structure Symbol = Symbol_
structure Path = OS.Path;
structure Info = Info_);
