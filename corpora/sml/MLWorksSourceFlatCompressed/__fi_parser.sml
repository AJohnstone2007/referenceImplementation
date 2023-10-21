require "fi_abs_syntax";
require "__fi_abs_syntax";
require "fi_int_abs_syn";
require "_fi_int_abs_syn";
require "lex_parse_interface";
require "__lex_parse_interface";
require "_fi_parser";
require "fi_grm";
require "_fi_grm";
require "fi_lex";
require "$.lib.base";
require "$.lib.join";
require "$.lib.parser2";
local
structure FIIntAbsSyn = FIIntAbsSyn (structure FIAbsSyntax=FIAbsSyntax)
structure FILrVals = FILrValsFun(structure FIIntAbsSyn = FIIntAbsSyn
structure Token = LrParser.Token
structure LexParseInterface = LexParseInterface)
structure FILex = FILexFun(structure Tokens = FILrVals.Tokens
structure LexParseInterface = LexParseInterface)
structure Joined = Join(structure LrParser = LrParser
structure ParserData = FILrVals.ParserData
structure Lex = FILex)
in
structure FIParser = FIParser(structure FIIntAbsSyn = FIIntAbsSyn
structure LexParseInterface = LexParseInterface
structure Joined = Joined
structure Tokens = FILrVals.Tokens)
end
;
