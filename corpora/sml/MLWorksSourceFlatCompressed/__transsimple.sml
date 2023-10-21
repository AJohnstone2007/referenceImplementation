require "../utils/__lists";
require "../utils/__crash";
require "__simpleutils";
require "../main/__pervasives";
require "_transsimple";
structure TransSimple_ = TransSimple ( structure Lists = Lists_
structure Crash = Crash_
structure SimpleUtils = SimpleUtils_
structure Pervasives = Pervasives_)
;
