require "../utils/__crash";
require "../utils/__lists";
require "../utils/__set";
require "../basics/__ident";
require "../basics/__symbol";
require "../lambda/__environtypes";
require "../lambda/__lambdatypes";
require "../lambda/__environ";
require "__pervasives";
require "_primitives";
structure Primitives_ =
Primitives(structure Crash = Crash_
structure Lists = Lists_
structure Set = Set_
structure Symbol = Symbol_
structure Ident = Ident_
structure EnvironTypes = EnvironTypes_
structure LambdaTypes = LambdaTypes_
structure Environ = Environ_
structure Pervasives = Pervasives_);
