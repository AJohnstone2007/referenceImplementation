require "__regexp";
require "__inbuffer";
require "_lexrules";
require "../utils/__crash";
require "../utils/__lists";
require "../basics/__token";
require "../main/__options";
require "../main/__info";
structure LexRules_ =
MLRules (structure Crash = Crash_
structure Lists = Lists_
structure Token = Token_
structure RegExp = RegExp_
structure InBuffer = InBuffer_
structure Options = Options_
structure Info = Info_);
