require "../utils/__text";
require "../utils/_diagnostic";
require "../parser/__parser";
require "../lambda/__lambda";
require "../lambda/__lambdaoptimiser";
require "../lambda/__lambdamodule";
require "../lambda/__lambdaprint";
require "../lambda/__topdecprint";
require "../mir/__mir_cg";
require "../mir/__miroptimiser";
require "../mir/__mirprint";
require "../machine/__mach_cg";
require "../machine/__machprint";
require "../utils/__lists";
require "../utils/__crash";
require "../lambda/__environ";
require "../lambda/__environprint";
require "../typechecker/__mod_rules";
require "../typechecker/__basis";
require "../typechecker/__stamp";
require "../main/__primitives";
require "../main/__pervasives";
require "../main/__mlworks_io";
require "_compiler";
structure Compiler_ =
Compiler (structure Parser = Parser_
structure Lambda = Lambda_
structure LambdaOptimiser = LambdaOptimiser_
structure LambdaModule = LambdaModule_
structure LambdaPrint = LambdaPrint_
structure Environ = Environ_
structure EnvironPrint = EnvironPrint_
structure Mir_Cg = Mir_Cg_
structure MirOptimiser = MirOptimiser_
structure MirPrint = MirPrint_
structure Mach_Cg = Mach_Cg_
structure MachPrint = MachPrint_
structure Mod_Rules = Module_rules_
structure Basis = Basis_
structure Stamp = Stamp_
structure Primitives = Primitives_
structure Pervasives = Pervasives_
structure Io = MLWorksIo_
structure Lists = Lists_
structure Crash = Crash_
structure Diagnostic = Diagnostic (structure Text = Text_)
structure TopdecPrint = TopdecPrint_);
