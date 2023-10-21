require "../utils/__print";
require "../utils/__lists";
require "../utils/__crash";
require "../basics/__identprint";
require "../typechecker/__tyenv";
require "../typechecker/__environment";
require "../typechecker/__type_exp";
require "../typechecker/__control_unify";
require "../typechecker/__types";
require "../typechecker/__scheme";
require "../typechecker/__valenv";
require "../typechecker/__completion";
require "../typechecker/__basis";
require "../typechecker/__context_print";
require "../debugger/__runtime_env";
require "../typechecker/_patterns";
structure Patterns_ = Patterns(
structure Print = Print_
structure Lists = Lists_
structure Crash = Crash_
structure IdentPrint = IdentPrint_
structure Tyenv = Tyenv_
structure Env = Environment_
structure Type_exp = Type_exp_
structure Control_Unify = Control_Unify_
structure Types = Types_
structure Scheme = Scheme_
structure Valenv = Valenv_
structure Basis = Basis_
structure Completion = Completion_
structure Context_Print = Context_Print_
structure RuntimeEnv = RuntimeEnv_
);
