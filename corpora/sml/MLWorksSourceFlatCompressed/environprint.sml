require "^.basis.__text_io";
require "environtypes";
require "../main/options";
signature ENVIRONPRINT = sig
structure EnvironTypes : ENVIRONTYPES
structure Options : OPTIONS
val stringenv: Options.print_options -> EnvironTypes.Env -> string
val stringtopenv: Options.print_options -> EnvironTypes.Top_Env -> string
val printenv: Options.print_options -> EnvironTypes.Env -> TextIO.outstream -> unit
val printtopenv: Options.print_options -> EnvironTypes.Top_Env -> TextIO.outstream -> unit
end
;
