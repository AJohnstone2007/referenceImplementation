require "^.basics.__absyn";
require "^.main.__info";
require "^.basics.__identprint";
require "^.typechecker.__types";
require "__basis";
require "^.typechecker._type_exp";
structure Type_exp_ =
Type_exp(structure Absyn = Absyn_
structure IdentPrint = IdentPrint_
structure Types = Types_
structure Basis = Basis_
structure Info = Info_)
;
