require "../utils/__crash";
require "../utils/__lists";
require "__LRbasics";
require "__actionfunctions";
require "_LRparser";
structure LRparser_ = LRparser(structure LRbasics = LRbasics_
structure ActionFunctions = ActionFunctions_
structure Lists = Lists_
structure Crash = Crash_
);
