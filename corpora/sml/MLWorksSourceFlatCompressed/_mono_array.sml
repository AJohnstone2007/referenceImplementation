require "mono_array";
require "__array";
require "_mono_vector";
functor MonoArray(type elem) : MONO_ARRAY =
struct
open Array
structure Vector = MonoVector (type elem = elem)
type vector = Vector.vector
type elem = elem
type array = elem array
end
;
