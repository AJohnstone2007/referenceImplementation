require "../basis/__text_io";
require "../basics/token";
require "../main/info";
signature LEXER =
sig
structure Token : TOKEN
structure Info : INFO
type TokenStream
type Options
val mkTokenStream : (int -> string) * string -> TokenStream
val mkLineTokenStream :
(int -> string) * string * int * bool -> TokenStream
val mkFileTokenStream : TextIO.instream * string -> TokenStream
val getToken : Info.options ->
(Options * Token.LexerState * TokenStream) ->
Token.Token
val ungetToken : (Token.Token * Info.Location.T) * TokenStream -> unit
val associated_filename : TokenStream -> string
val locate : TokenStream -> Info.Location.T
val eof : TokenStream -> bool
val clear_eof : TokenStream -> unit
val is_interactive : TokenStream -> bool
val flush_to_nl : TokenStream -> unit
end;
