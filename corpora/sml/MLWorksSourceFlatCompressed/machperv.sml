require "../main/pervasives";
signature MACHPERV =
sig
structure Pervasives : PERVASIVES
type CompilerOptions
val is_inline : CompilerOptions * Pervasives.pervasive -> bool
val is_fun : Pervasives.pervasive -> bool
val implicit_references : Pervasives.pervasive -> Pervasives.pervasive list
end
;
