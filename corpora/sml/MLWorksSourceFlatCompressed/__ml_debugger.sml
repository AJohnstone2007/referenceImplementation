require "../utils/__lists";
require "../utils/__crash";
require "../main/__encapsulate";
require "../main/__preferences";
require "../main/__project";
require "../basics/__module_id";
require "../basics/__location";
require "../typechecker/__types";
require "../interpreter/__incremental";
require "../interpreter/__shell_utils";
require "../rts/gen/__tags";
require "__value_printer";
require "__debugger_print";
require "../machine/__stack_interface";
require "__debugger_utilities";
require "__newtrace";
require "__stack_frame";
require "^.system.__os";
require "_ml_debugger";
structure Ml_Debugger_ =
Ml_Debugger(structure Lists = Lists_
structure Crash = Crash_
structure Path = OS.Path;
structure Encapsulate = Encapsulate_
structure Preferences = Preferences_
structure Project = Project_
structure ModuleId = ModuleId_
structure Location = Location_
structure Types = Types_
structure Incremental = Incremental_
structure ShellUtils = ShellUtils_
structure ValuePrinter = ValuePrinter_
structure StackInterface = StackInterface_
structure DebuggerUtilities = DebuggerUtilities_
structure Tags = Tags_
structure DebuggerPrint = DebuggerPrint_
structure Trace = Trace_
structure StackFrame = StackFrame_
)
;
