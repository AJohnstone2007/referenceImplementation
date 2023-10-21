require "../utils/__lists";
require "../interpreter/__incremental";
require "../typechecker/__basis";
require "../typechecker/__types";
require "../interpreter/__inspector_values";
require "../main/__user_options";
require "../interpreter/__shell_types";
require "../debugger/__value_printer";
require "_inspector";
structure Inspector_ = Inspector (
structure Lists = Lists_
structure Incremental = Incremental_
structure Basis = Basis_
structure Types = Types_
structure InspectorValues = InspectorValues_
structure UserOptions = UserOptions_
structure ShellTypes = ShellTypes_
structure ValuePrinter = ValuePrinter_
)
;
