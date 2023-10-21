require "../utils/__crash";
require "../utils/__lists";
require "../utils/__text";
require "../utils/__btree";
require "../basics/__ident";
require "../utils/_diagnostic";
require "_pervasives";
structure Pervasives_ = Pervasives(
structure Lists = Lists_
structure Map = BTree_
structure Crash = Crash_
structure Ident = Ident_
structure Diagnostic = Diagnostic ( structure Text = Text_ )
)
;
