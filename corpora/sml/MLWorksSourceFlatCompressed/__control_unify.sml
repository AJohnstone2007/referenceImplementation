require "../typechecker/_control_unify";
require "../utils/__crash";
require "../utils/__lists";
require "../basics/__identprint";
require "../typechecker/__types";
require "../typechecker/__unify";
require "../typechecker/__completion";
require "__basis";
require "../main/__info";
structure Control_Unify_ = Control_Unify(
structure Crash = Crash_
structure Lists = Lists_
structure IdentPrint = IdentPrint_
structure Types = Types_
structure Unify = Unify_
structure Completion = Completion_
structure Basis = Basis_
structure Info = Info_)
;
