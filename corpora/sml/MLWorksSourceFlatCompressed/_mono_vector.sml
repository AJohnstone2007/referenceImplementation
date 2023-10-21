require "mono_vector";
require "__vector";
functor MonoVector (type elem) : MONO_VECTOR =
struct
open Vector
type elem = elem
type vector = elem vector
end
;
