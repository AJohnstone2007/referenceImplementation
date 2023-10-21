require "../utils/__lists";
require "../utils/__intbtree";
require "../utils/__print";
require "../utils/__crash";
require "../utils/_hashset";
require "../utils/__hashtable";
require "../basics/__identprint";
require "__datatypes";
require "__types";
require "__strnames";
require "__valenv";
require "__tyenv";
require "__strenv";
require "__environment";
require "__basistypes";
require "../typechecker/__namehash";
require "_assemblies";
structure Assemblies_ = Assemblies(
structure StrnameSet = HashSet(
structure Crash = Crash_
structure Lists = Lists_
type element = Datatypes_.Strname
val eq = Strnames_.strname_eq
val hash = NameHash_.strname_hash
val size = 512
)
structure TyfunSet = HashSet(
structure Crash = Crash_
structure Lists = Lists_
type element = Datatypes_.Tyfun
val eq = Types_.tyfun_eq
val hash = NameHash_.tyfun_hash
val size = 512
)
structure Lists = Lists_
structure IntMap = IntBTree_
structure Print = Print_
structure Crash = Crash_
structure IdentPrint = IdentPrint_
structure Conenv = Valenv_
structure Types = Types_
structure Strnames = Strnames_
structure Tyenv = Tyenv_
structure Strenv = Strenv_
structure Env = Environment_
structure Basistypes = BasisTypes_
structure NameHash = NameHash_
structure HashTable = HashTable_
);
