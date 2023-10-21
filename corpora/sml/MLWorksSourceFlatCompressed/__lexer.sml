require "../utils/__lists";
require "../utils/__btree";
require "../utils/__mlworks_timer";
require "../basics/__token";
require "__ndfa";
require "__lexrules";
require "_lexgen";
structure Lexer_ =
LexGen(structure Lists = Lists_
structure Map = BTree_
structure Timer = Timer_
structure Token = Token_
structure Ndfa = Ndfa_
structure LexRules = LexRules_
);
