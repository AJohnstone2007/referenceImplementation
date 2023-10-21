require "../basis/__int";
require "__text";
require "__crash";
require "__lists";
require "_smallintset";
structure SmallIntSet_ =
SmallIntSet (structure Text = Text_
structure Crash = Crash_
structure Lists = Lists_
val width = 16
val lg_width = 4
fun int_to_text int =
Text.from_string (Int.toString int))
;
