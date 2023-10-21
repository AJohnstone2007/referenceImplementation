require "../system/__os";
require "../utils/__crash";
require "../utils/__text";
require "../utils/__print";
require "../utils/__lists";
require "../utils/_diagnostic";
require "../utils/__mlworks_timer";
require "../basics/__module_id";
require "../parser/__parser";
require "../typechecker/__mod_rules";
require "../typechecker/__basis";
require "../typechecker/__stamp";
require "../lambda/__environ";
require "../lambda/__lambdaprint";
require "../lambda/__environprint";
require "../lambda/__lambda";
require "../lambda/__lambdaoptimiser";
require "../lambda/__lambdamodule";
require "../lambda/__topdecprint";
require "../mir/__mirtypes";
require "../mir/__mir_cg";
require "../mir/__mirprint";
require "../mir/__miroptimiser";
require "../machine/__mach_cg";
require "../machine/__machprint";
require "../machine/__object_output";
require "../debugger/__debugger_types";
require "__primitives";
require "__pervasives";
require "__encapsulate";
require "__mlworks_io";
require "__project";
require "_toplevel";
structure TopLevel_ = TopLevel(
structure OS = OS
structure Crash = Crash_
structure Print = Print_
structure Lists = Lists_
structure ModuleId = ModuleId_
structure Diagnostic = Diagnostic(structure Text = Text_)
structure Timer = Timer_
structure Parser = Parser_
structure Mod_Rules = Module_rules_
structure Basis = Basis_
structure Stamp = Stamp_
structure Environ = Environ_
structure LambdaPrint = LambdaPrint_
structure EnvironPrint = EnvironPrint_
structure Lambda = Lambda_
structure LambdaOptimiser = LambdaOptimiser_
structure LambdaModule = LambdaModule_
structure MirTypes = MirTypes_
structure Mir_Cg = Mir_Cg_
structure MirPrint = MirPrint_
structure MirOptimiser = MirOptimiser_
structure Mach_Cg = Mach_Cg_
structure MachPrint = MachPrint_
structure Object_Output = Object_Output_
structure TopdecPrint = TopdecPrint_
structure Primitives = Primitives_
structure Pervasives = Pervasives_
structure Encapsulate = Encapsulate_
structure Io = MLWorksIo_
structure Project = Project_
structure Debugger_Types = Debugger_Types_
)
;
