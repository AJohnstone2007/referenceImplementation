require "../utils/__crash";
require "../lambda/__lambdatypes";
require "../main/__pervasives";
require "../debugger/__debugger_utilities";
require "_auglambda";
structure AugLambda_ = AugLambda(
structure Crash = Crash_
structure LambdaTypes = LambdaTypes_
structure Pervasives = Pervasives_
structure DebuggerUtilities = DebuggerUtilities_
)
;
