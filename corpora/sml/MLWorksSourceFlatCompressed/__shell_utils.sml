require "../utils/__lists";
require "../rts/gen/__tags";
require "__incremental";
require "__user_context";
require "__shell_types";
require "../system/__os";
require "../system/__getenv";
require "../basics/__module_id";
require "../parser/__parser";
require "../system/__editor";
require "../system/__link_support";
require "../system/__object_output";
require "../typechecker/__types";
require "../typechecker/__basis";
require "../typechecker/__completion";
require "../debugger/__value_printer";
require "../debugger/__newtrace";
require "../main/__toplevel";
require "../main/__project";
require "../main/__proj_file";
require "../main/__mlworks_io";
require "../main/__user_options";
require "../main/__preferences";
require "../main/__encapsulate";
require "../utils/_diagnostic";
require "../utils/__text";
require "_shell_utils";
structure ShellUtils_ = ShellUtils (
structure Lists = Lists_
structure Tags = Tags_
structure Incremental = Incremental_
structure UserContext = UserContext_
structure ShellTypes = ShellTypes_
structure Getenv = Getenv_
structure OS = OS
structure OSPath = OS.Path
structure LinkSupport = LinkSupport_
structure Encapsulate = Encapsulate_
structure Object_Output = Object_Output_
structure ModuleId = ModuleId_
structure Editor = Editor_
structure Types = Types_
structure Basis = Basis_
structure Completion = Completion_
structure ValuePrinter = ValuePrinter_
structure Trace = Trace_
structure TopLevel = TopLevel_
structure Project = Project_
structure ProjFile = ProjFile_
structure Parser = Parser_
structure Io = MLWorksIo_
structure UserOptions = UserOptions_
structure Preferences = Preferences_
structure Diagnostic =
Diagnostic (structure Text = Text_)
)
;
