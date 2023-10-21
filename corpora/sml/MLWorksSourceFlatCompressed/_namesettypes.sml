require "../utils/hashset";
require "namesettypes";
functor NamesetTypes (structure TynameSet : HASHSET
structure StrnameSet : HASHSET) : NAMESETTYPES =
struct
structure TynameSet = TynameSet
structure StrnameSet = StrnameSet
datatype Nameset = NAMESET of (TynameSet.HashSet * StrnameSet.HashSet)
end
;
