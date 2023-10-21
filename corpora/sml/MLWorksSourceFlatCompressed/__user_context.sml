require "__incremental";
require "__interprint";
require "../main/__user_options";
require "../main/__preferences";
require "../utils/__btree";
require "../utils/__lists";
require "../utils/__crash";
require "_user_context";
structure UserContext_ = UserContext (
structure Incremental = Incremental_
structure InterPrint = InterPrint_
structure UserOptions = UserOptions_
structure Preferences = Preferences_
structure Map = BTree_
structure Lists = Lists_
structure Crash = Crash_
)
;
