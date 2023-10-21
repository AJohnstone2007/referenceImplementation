require "../utils/__lists";
require "../utils/__print";
require "../utils/__crash";
require "../basics/__identprint";
require "../main/__info";
require "../typechecker/__types";
require "../typechecker/__scheme";
require "../typechecker/__valenv";
require "../typechecker/__tyenv";
require "../typechecker/__strenv";
require "../typechecker/__environment";
require "../typechecker/__strnames";
require "../typechecker/__core_rules";
require "../typechecker/__realise";
require "../typechecker/__sharetypes";
require "../typechecker/__share";
require "../typechecker/__basis";
require "../typechecker/__sigma";
require "../typechecker/__nameset";
require "../typechecker/__type_debugger";
require "../typechecker/_mod_rules";
structure Module_rules_ = Module_rules(
structure Lists = Lists_
structure Print = Print_
structure Crash = Crash_
structure IdentPrint = IdentPrint_
structure Types = Types_
structure Scheme = Scheme_
structure Valenv = Valenv_
structure Tyenv = Tyenv_
structure Strenv = Strenv_
structure Env = Environment_
structure Strnames = Strnames_
structure Core_rules = Core_rules_
structure Realise = Realise_
structure Sharetypes = Sharetypes_
structure Share = Share_
structure Basis = Basis_
structure Sigma = Sigma_
structure Nameset = Nameset_
structure Info = Info_
structure TypeDebugger = TypeDebugger_);
