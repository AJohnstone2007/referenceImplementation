require "../utils/__lists";
require "../utils/__crash";
require "../main/__pervasives";
require "../basics/__scons";
require "__lambdatypes";
require "_simpleutils";
structure SimpleUtils_ = SimpleUtils ( structure Lists = Lists_
structure LambdaTypes = LambdaTypes_
structure Pervasives = Pervasives_
structure Scons = Scons_
structure Crash = Crash_
)
;
