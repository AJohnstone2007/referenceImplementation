require "../basis/__text_io";
require "lambdatypes";
require "../main/options";
signature LAMBDAPRINT =
sig
structure LambdaTypes : LAMBDATYPES
structure Options : OPTIONS
val output_lambda : Options.options ->
TextIO.outstream * LambdaTypes.LambdaExp ->
unit
val print_lambda : Options.options -> LambdaTypes.LambdaExp -> unit
val string_of_lambda : LambdaTypes.LambdaExp -> string
val print_var : LambdaTypes.LVar -> string
val print_exp : LambdaTypes.LambdaExp -> string
val pds : LambdaTypes.program -> string
val pde : LambdaTypes.LambdaExp -> string
end
;
