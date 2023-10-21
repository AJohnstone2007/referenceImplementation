require "../utils/__crash";
require "../utils/__lists";
require "../utils/__inthashtable";
require "../utils/__hashtable";
require "../utils/__bignum";
require "../utils/__mlworks_timer";
require "../main/__pervasives";
require "../basics/__scons";
require "../main/__options";
require "__lambdaflow";
require "__lambdaprint";
require "__transsimple";
require "__simpleutils";
require "_simplelambda";
structure LambdaOptimiser_ =
SimpleLambda (structure Crash = Crash_
structure Lists = Lists_
structure IntHashTable = IntHashTable_
structure HashTable = HashTable_
structure Bignum = BigNum_
structure Timer = Timer_
structure Scons = Scons_
structure Options = Options_
structure Pervasives = Pervasives_
structure TransSimple = TransSimple_
structure SimpleUtils = SimpleUtils_
structure LambdaFlow = LambdaFlow_
structure LambdaPrint = LambdaPrint_
)
;
