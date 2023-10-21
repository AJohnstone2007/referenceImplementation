require "../utils/__btree";
require "../basics/__ident";
require "__stamp";
require "_datatypes";
structure Datatypes_ =
Datatypes (structure Stamp = Stamp_
structure Ident = Ident_
structure NewMap = BTree_
)
;
