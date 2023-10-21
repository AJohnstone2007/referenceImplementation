require "../main/__encapsulate";
require "../main/__compiler";
require "../main/__proj_file";
require "../utils/__btree.sml";
require "../utils/__crash.sml";
require "../utils/__lists";
require "../utils/_diagnostic";
require "../utils/__text";
require "../system/__os";
require "../make/__depend";
require "../basics/__module_id";
require "../system/__os";
require "__options";
require "__mlworks_io";
require "_project";
require "../dependency/__module_dec_io.sml";
require "../dependency/__import_export.sml";
structure Project_ =
Project (
structure Encapsulate = Encapsulate_;
structure Compiler = Compiler_;
structure ProjFile = ProjFile_;
structure NewMap = BTree_;
structure Crash = Crash_;
structure ModuleId = ModuleId_;
structure OS = OS;
structure Io = MLWorksIo_;
structure Depend = Depend_;
structure Options = Options_;
structure Lists = Lists_;
structure ModuleDecIO = ModuleDecIO;
structure ImportExport = ImportExport;
structure Diagnostic =
Diagnostic (structure Text = Text_)
);
