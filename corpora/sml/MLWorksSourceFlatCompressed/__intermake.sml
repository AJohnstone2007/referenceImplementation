require "../main/__compiler";
require "../lexer/__lexer";
require "../main/__mlworks_io";
require "../main/__encapsulate";
require "../typechecker/__basis";
require "../typechecker/__stamp";
require "../utils/__lists";
require "../utils/__text";
require "../utils/_diagnostic";
require "../utils/__crash";
require "../basics/__module_id";
require "../main/__project";
require "__interload";
require "_intermake";
structure InterMake_ =
InterMake (structure Compiler = Compiler_
structure Encapsulate = Encapsulate_
structure Basis = Basis_
structure Stamp = Stamp_
structure Lexer = Lexer_
structure MLWorksIo = MLWorksIo_
structure Lists = Lists_
structure Crash = Crash_
structure ModuleId = ModuleId_
structure Project = Project_
structure Diagnostic =
Diagnostic (structure Text = Text_)
structure InterLoad = InterLoad_
);
