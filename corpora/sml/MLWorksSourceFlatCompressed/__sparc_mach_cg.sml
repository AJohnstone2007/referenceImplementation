require "../rts/gen/__tags";
require "../utils/__text";
require "../utils/__print";
require "../utils/__mlworks_timer" ;
require "../utils/__lists";
require "../utils/__crash";
require "../main/__info";
require "../main/__options";
require "../utils/__sexpr";
require "../main/__reals";
require "../main/__code_module";
require "../basics/__ident";
require "../mir/__mirtables";
require "../mir/__mirregisters";
require "__machspec";
require "__sparc_schedule";
require "../rts/gen/__implicit";
require "../utils/_diagnostic";
require "_mach_cg";
structure Sparc_Mach_Cg_ = Mach_Cg(
structure Tags = Tags_
structure Print = Print_
structure Timer = Timer_
structure Lists = Lists_
structure Crash = Crash_
structure Info = Info_
structure Options = Options_
structure Sexpr = Sexpr_
structure Reals = Reals_
structure Ident = Ident_
structure MirTables = MirTables_
structure MirRegisters = MirRegisters_
structure MachSpec = MachSpec_
structure Code_Module = Code_Module_
structure Sparc_Schedule = Sparc_Schedule_
structure Implicit_Vector = ImplicitVector_
structure Diagnostic = Diagnostic(structure Text = Text_ )
)
;
