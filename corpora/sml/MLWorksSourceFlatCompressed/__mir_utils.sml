require "../utils/_diagnostic";
require "../utils/__sexpr";
require "../utils/__text";
require "../utils/__lists";
require "../utils/__crash";
require "../utils/__bignum";
require "../main/__pervasives";
require "../main/__library";
require "../typechecker/__types";
require "__mirregisters";
require "__mir_env";
require "_mir_utils";
structure Mir_Utils_ = Mir_Utils(
structure Diagnostic =
Diagnostic(structure Text = Text_)
structure Sexpr = Sexpr_
structure Lists = Lists_
structure Crash = Crash_
structure BigNum = BigNum_
structure BigNum32 = BigNum32_
structure Pervasives = Pervasives_
structure Library = Library_
structure Types = Types_
structure MirRegisters = MirRegisters_
structure Mir_Env = Mir_Env_
);
