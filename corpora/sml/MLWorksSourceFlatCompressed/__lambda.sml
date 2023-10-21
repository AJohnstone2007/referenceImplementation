require "../utils/_diagnostic";
require "../utils/__text";
require "../utils/__lists";
require "../utils/__intbtree";
require "../utils/__crash";
require "../utils/__print";
require "../utils/__inthashtable";
require "../basics/__absynprint";
require "../basics/__identprint";
require "../typechecker/__types";
require "../typechecker/__basis";
require "../match/__type_utils";
require "../main/__primitives";
require "../main/__pervasives";
require "../machine/__machspec";
require "../rts/gen/__implicit";
require "../debugger/__debugger_types";
require "../match/__match";
require "__environ";
require "__lambdaprint";
require "__lambdaoptimiser";
require "../main/__info";
require "../typechecker/__typerep_utils";
require "_lambda";
structure Lambda_ = Lambda (
structure Diagnostic = Diagnostic(structure Text = Text_)
structure Lists = Lists_
structure IntNewMap = IntBTree_
structure Crash = Crash_
structure Print = Print_
structure IntHashTable = IntHashTable_
structure AbsynPrint = AbsynPrint_
structure IdentPrint = IdentPrint_
structure Types = Types_
structure Basis = Basis_
structure TypeUtils = TypeUtils_
structure Primitives = Primitives_
structure Pervasives = Pervasives_
structure MachSpec = MachSpec_
structure ImplicitVector = ImplicitVector_
structure Debugger_Types = Debugger_Types_
structure Match = Match_
structure Environ = Environ_
structure LambdaPrint = LambdaPrint_
structure LambdaOptimiser = LambdaOptimiser_
structure Info = Info_
structure TyperepUtils = TyperepUtils_
)
;
