require "../lambda/__environ";
require "__interload";
require "__intermake";
require "../typechecker/__basis";
require "../typechecker/__stamp";
require "../lexer/__lexer";
require "../parser/__parserenv";
require "../main/__mlworks_io";
require "../basics/__location";
require "../main/__project";
require "../main/__proj_file";
require "../main/__encapsulate";
require "../basics/__module_id";
require "^.basis.__list";
require "../utils/_diagnostic";
require "../utils/__text";
require "../utils/__crash";
require "_incremental";
local
fun parse_args [] = ()
| parse_args("-pervasive-dir" :: arg :: rest) =
(MLWorksIo_.set_pervasive_dir(arg, Location_.FILE"__incremental functor application");
parse_args rest)
| parse_args("-source-path" :: arg :: rest) =
(MLWorksIo_.set_source_path_from_string(arg, Location_.FILE"__incremental functor application");
parse_args rest)
| parse_args("-object-path" :: arg :: rest) =
(MLWorksIo_.set_object_path(arg, Location_.FILE"__incremental functor application");
parse_args rest)
| parse_args(_ :: rest) = parse_args rest
val args = MLWorks.arguments()
in
val _ = parse_args args
end
structure Incremental_ =
Incremental
(structure Environ = Environ_
structure InterLoad = InterLoad_
structure InterMake = InterMake_
structure Basis = Basis_
structure Stamp = Stamp_
structure Lexer = Lexer_
structure ParserEnv = ParserEnv_
structure Io = MLWorksIo_
structure ModuleId = ModuleId_
structure Project = Project_
structure ProjFile = ProjFile_
structure Encapsulate = Encapsulate_
structure List = List
structure Diagnostic =
Diagnostic (structure Text = Text_)
structure Crash = Crash_
val initial_name = "MLWorks"
);
