require "^.lib.base";
require "sigs";
require "yacc_grm";
require "__hdr";
require "$.basis.__char";
require "$.basis.__string";
require "$.basis.__vector";
functor LexMLYACC(include sig
structure Tokens : Mlyacc_TOKENS
structure Hdr : HEADER
end where type Hdr.prec = Header.prec) : ARG_LEXER=
struct
structure UserDeclarations =
struct
structure Tokens = Tokens
type svalue = Tokens.svalue
type pos = int
type ('a,'b) token = ('a,'b) Tokens.token
type lexresult = (svalue,pos) token
type lexarg = Hdr.inputSource
type arg = lexarg
open Tokens
val error = Hdr.error
val lineno = Hdr.lineno
val text = Hdr.text
val pcount = ref 0
val commentLevel = ref 0
val actionstart = ref 0
val eof = fn i => (if (!pcount)>0 then
error i (!actionstart)
" eof encountered in action beginning here !"
else (); EOF(!lineno,!lineno))
val Add = fn s => (text := s::(!text))
val lookup =
let val dict = [("%prec",PREC_TAG),("%term",TERM),
("%nonterm",NONTERM), ("%eop",PERCENT_EOP),("%start",START),
("%prefer",PREFER),("%subst",SUBST),("%change",CHANGE),
("%keyword",KEYWORD),("%name",NAME),
("%verbose",VERBOSE), ("%nodefault",NODEFAULT),
("%value",VALUE), ("%noshift",NOSHIFT),
("%header",PERCENT_HEADER),("%pure",PERCENT_PURE),
("%arg",PERCENT_ARG),
("%pos",PERCENT_POS)]
in fn (s,left,right) =>
let fun f ((a,d)::b) = if a=s then d(left,right) else f b
| f nil = UNKNOWN(s,left,right)
in f dict
end
end
fun inc (ri as ref i) = (ri := i+1)
fun dec (ri as ref i) = (ri := i-1)
end
exception LexError
structure Internal =
struct
datatype yyfinstate = N of int
type statedata = {fin : yyfinstate list, trans: string}
val tab = let
val s0 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000"
val s1 =
"\015\015\015\015\015\015\015\015\015\015\021\015\015\015\015\015\
\\015\015\015\015\015\015\015\015\015\015\015\015\015\015\015\015\
\\015\015\015\015\015\019\015\015\017\015\015\015\015\015\015\015\
\\015\015\015\015\015\015\015\015\015\015\015\015\015\015\015\015\
\\015\015\015\015\015\015\015\015\015\015\015\015\015\015\015\015\
\\015\015\015\015\015\015\015\015\015\015\015\015\015\015\015\015\
\\015\015\015\015\015\015\015\015\015\015\015\015\015\015\015\015\
\\015\015\015\015\015\015\015\015\015\015\015\015\015\015\015\015\
\\015"
val s3 =
"\022\022\022\022\022\022\022\022\022\065\067\022\022\022\022\022\
\\022\022\022\022\022\022\022\022\022\022\022\022\022\022\022\022\
\\065\022\022\022\022\045\022\043\041\022\040\022\039\037\022\022\
\\035\035\035\035\035\035\035\035\035\035\034\022\022\022\022\022\
\\022\026\026\026\026\026\026\026\026\026\026\026\026\026\026\026\
\\026\026\026\026\026\026\026\026\026\026\026\022\022\022\022\022\
\\022\026\026\026\026\026\031\026\026\026\026\026\026\026\026\029\
\\026\026\026\026\026\026\026\026\026\026\026\025\024\023\022\022\
\\022"
val s5 =
"\068\068\068\068\068\068\068\068\068\068\021\068\068\068\068\068\
\\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\
\\068\068\072\068\068\068\068\068\070\069\068\068\068\068\068\068\
\\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\
\\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\
\\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\
\\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\
\\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\
\\068"
val s7 =
"\073\073\073\073\073\073\073\073\073\075\021\073\073\073\073\073\
\\073\073\073\073\073\073\073\073\073\073\073\073\073\073\073\073\
\\075\073\073\073\073\073\073\073\073\073\073\073\073\073\073\073\
\\073\073\073\073\073\073\073\073\073\073\073\073\073\073\073\073\
\\073\073\073\073\073\073\073\073\073\073\073\073\073\073\073\073\
\\073\073\073\073\073\073\073\073\073\073\073\073\074\073\073\073\
\\073\073\073\073\073\073\073\073\073\073\073\073\073\073\073\073\
\\073\073\073\073\073\073\073\073\073\073\073\073\073\073\073\073\
\\073"
val s9 =
"\077\077\077\077\077\077\077\077\077\077\021\077\077\077\077\077\
\\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\
\\077\077\077\077\077\077\077\077\081\080\078\077\077\077\077\077\
\\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\
\\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\
\\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\
\\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\
\\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\
\\077"
val s11 =
"\083\083\083\083\083\083\083\083\083\083\088\083\083\083\083\083\
\\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\
\\083\083\087\083\083\083\083\083\083\083\083\083\083\083\083\083\
\\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\
\\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\
\\083\083\083\083\083\083\083\083\083\083\083\083\084\083\083\083\
\\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\
\\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\
\\083"
val s13 =
"\089\089\089\089\089\089\089\089\089\089\021\089\089\089\089\089\
\\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\
\\089\089\089\089\089\089\089\089\093\092\090\089\089\089\089\089\
\\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\
\\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\
\\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\
\\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\
\\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\
\\089"
val s15 =
"\016\016\016\016\016\016\016\016\016\016\000\016\016\016\016\016\
\\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\
\\016\016\016\016\016\000\016\016\016\016\016\016\016\016\016\016\
\\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\
\\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\
\\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\
\\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\
\\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\
\\016"
val s17 =
"\016\016\016\016\016\016\016\016\016\016\000\016\016\016\016\016\
\\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\
\\016\016\016\016\016\000\016\016\016\016\018\016\016\016\016\016\
\\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\
\\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\
\\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\
\\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\
\\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\
\\016"
val s19 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\020\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000"
val s26 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\027\000\000\000\000\000\000\028\000\
\\027\027\027\027\027\027\027\027\027\027\000\000\000\000\000\000\
\\000\027\027\027\027\027\027\027\027\027\027\027\027\027\027\027\
\\027\027\027\027\027\027\027\027\027\027\027\000\000\000\000\027\
\\000\027\027\027\027\027\027\027\027\027\027\027\027\027\027\027\
\\027\027\027\027\027\027\027\027\027\027\027\000\000\000\000\000\
\\000"
val s29 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\027\000\000\000\000\000\000\028\000\
\\027\027\027\027\027\027\027\027\027\027\000\000\000\000\000\000\
\\000\027\027\027\027\027\027\027\027\027\027\027\027\027\027\027\
\\027\027\027\027\027\027\027\027\027\027\027\000\000\000\000\027\
\\000\027\027\027\027\027\030\027\027\027\027\027\027\027\027\027\
\\027\027\027\027\027\027\027\027\027\027\027\000\000\000\000\000\
\\000"
val s31 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\027\000\000\000\000\000\000\028\000\
\\027\027\027\027\027\027\027\027\027\027\000\000\000\000\000\000\
\\000\027\027\027\027\027\027\027\027\027\027\027\027\027\027\027\
\\027\027\027\027\027\027\027\027\027\027\027\000\000\000\000\027\
\\000\027\027\027\027\027\027\027\027\027\027\027\027\027\027\032\
\\027\027\027\027\027\027\027\027\027\027\027\000\000\000\000\000\
\\000"
val s32 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\027\000\000\000\000\000\000\028\000\
\\027\027\027\027\027\027\027\027\027\027\000\000\000\000\000\000\
\\000\027\027\027\027\027\027\027\027\027\027\027\027\027\027\027\
\\027\027\027\027\027\027\027\027\027\027\027\000\000\000\000\027\
\\000\027\027\027\027\027\027\027\027\027\027\027\027\027\027\027\
\\027\027\033\027\027\027\027\027\027\027\027\000\000\000\000\000\
\\000"
val s35 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\036\036\036\036\036\036\036\036\036\036\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000"
val s37 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\038\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000"
val s41 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\042\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000"
val s43 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\044\000\000\000\000\000\000\000\000\
\\044\044\044\044\044\044\044\044\044\044\000\000\000\000\000\000\
\\000\044\044\044\044\044\044\044\044\044\044\044\044\044\044\044\
\\044\044\044\044\044\044\044\044\044\044\044\000\000\000\000\044\
\\000\044\044\044\044\044\044\044\044\044\044\044\044\044\044\044\
\\044\044\044\044\044\044\044\044\044\044\044\000\000\000\000\000\
\\000"
val s45 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\064\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\046\
\\000\046\046\046\046\046\046\046\046\046\046\046\060\046\052\046\
\\046\046\047\046\046\046\046\046\046\046\046\000\000\000\000\000\
\\000"
val s46 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\046\
\\000\046\046\046\046\046\046\046\046\046\046\046\046\046\046\046\
\\046\046\046\046\046\046\046\046\046\046\046\000\000\000\000\000\
\\000"
val s47 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\046\
\\000\046\046\046\046\046\046\046\046\048\046\046\046\046\046\046\
\\046\046\046\046\046\046\046\046\046\046\046\000\000\000\000\000\
\\000"
val s48 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\046\
\\000\046\046\046\046\046\046\049\046\046\046\046\046\046\046\046\
\\046\046\046\046\046\046\046\046\046\046\046\000\000\000\000\000\
\\000"
val s49 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\046\
\\000\046\046\046\046\046\046\046\050\046\046\046\046\046\046\046\
\\046\046\046\046\046\046\046\046\046\046\046\000\000\000\000\000\
\\000"
val s50 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\046\
\\000\046\046\046\046\046\046\046\046\046\046\046\046\046\046\046\
\\046\046\046\046\051\046\046\046\046\046\046\000\000\000\000\000\
\\000"
val s52 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\046\
\\000\046\046\046\046\046\046\046\046\046\046\046\046\046\046\053\
\\046\046\046\046\046\046\046\046\046\046\046\000\000\000\000\000\
\\000"
val s53 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\046\
\\000\046\046\046\046\046\046\046\046\046\046\046\046\046\054\046\
\\046\046\046\046\046\046\046\046\046\046\046\000\000\000\000\000\
\\000"
val s54 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\046\
\\000\055\046\046\046\046\046\046\046\046\046\046\046\046\046\046\
\\046\046\046\046\046\046\046\046\046\046\046\000\000\000\000\000\
\\000"
val s55 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\046\
\\000\046\046\046\046\046\046\046\046\046\046\046\046\046\046\046\
\\046\046\046\056\046\046\046\046\046\046\046\000\000\000\000\000\
\\000"
val s56 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\046\
\\000\046\046\046\046\046\046\046\046\046\046\046\046\046\046\046\
\\046\046\046\057\046\046\046\046\046\046\046\000\000\000\000\000\
\\000"
val s57 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\046\
\\000\046\046\046\046\046\046\046\046\046\046\046\046\046\046\058\
\\046\046\046\046\046\046\046\046\046\046\046\000\000\000\000\000\
\\000"
val s58 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\046\
\\000\046\046\059\046\046\046\046\046\046\046\046\046\046\046\046\
\\046\046\046\046\046\046\046\046\046\046\046\000\000\000\000\000\
\\000"
val s60 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\046\
\\000\046\046\046\046\061\046\046\046\046\046\046\046\046\046\046\
\\046\046\046\046\046\046\046\046\046\046\046\000\000\000\000\000\
\\000"
val s61 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\046\
\\000\046\046\046\046\046\062\046\046\046\046\046\046\046\046\046\
\\046\046\046\046\046\046\046\046\046\046\046\000\000\000\000\000\
\\000"
val s62 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\046\
\\000\046\046\046\046\046\046\046\046\046\046\046\046\046\046\046\
\\046\046\046\046\063\046\046\046\046\046\046\000\000\000\000\000\
\\000"
val s65 =
"\000\000\000\000\000\000\000\000\000\066\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\066\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000"
val s68 =
"\068\068\068\068\068\068\068\068\068\068\000\068\068\068\068\068\
\\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\
\\068\068\000\068\068\068\068\068\000\000\068\068\068\068\068\068\
\\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\
\\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\
\\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\
\\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\
\\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\068\
\\068"
val s70 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\071\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000"
val s75 =
"\000\000\000\000\000\000\000\000\000\076\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\076\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000"
val s77 =
"\077\077\077\077\077\077\077\077\077\077\000\077\077\077\077\077\
\\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\
\\077\077\077\077\077\077\077\077\000\000\000\077\077\077\077\077\
\\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\
\\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\
\\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\
\\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\
\\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\077\
\\077"
val s78 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\079\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000"
val s81 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\082\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000"
val s83 =
"\083\083\083\083\083\083\083\083\083\083\000\083\083\083\083\083\
\\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\
\\083\083\000\083\083\083\083\083\083\083\083\083\083\083\083\083\
\\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\
\\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\
\\083\083\083\083\083\083\083\083\083\083\083\083\000\083\083\083\
\\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\
\\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\083\
\\083"
val s84 =
"\000\000\000\000\000\000\000\000\000\086\086\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\086\000\085\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000"
val s89 =
"\089\089\089\089\089\089\089\089\089\089\000\089\089\089\089\089\
\\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\
\\089\089\089\089\089\089\089\089\000\000\000\089\089\089\089\089\
\\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\
\\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\
\\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\
\\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\
\\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\089\
\\089"
val s90 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\091\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000"
val s93 =
"\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\094\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\
\\000"
in Vector.fromList
[{fin = [], trans = s0},
{fin = [], trans = s1},
{fin = [], trans = s1},
{fin = [], trans = s3},
{fin = [], trans = s3},
{fin = [], trans = s5},
{fin = [], trans = s5},
{fin = [], trans = s7},
{fin = [], trans = s7},
{fin = [], trans = s9},
{fin = [], trans = s9},
{fin = [], trans = s11},
{fin = [], trans = s11},
{fin = [], trans = s13},
{fin = [], trans = s13},
{fin = [(N 11),(N 18)], trans = s15},
{fin = [(N 11)], trans = s15},
{fin = [(N 11),(N 18)], trans = s17},
{fin = [(N 2),(N 11)], trans = s15},
{fin = [(N 18)], trans = s19},
{fin = [(N 14)], trans = s0},
{fin = [(N 16)], trans = s0},
{fin = [(N 94)], trans = s0},
{fin = [(N 36),(N 94)], trans = s0},
{fin = [(N 87),(N 94)], trans = s0},
{fin = [(N 34),(N 94)], trans = s0},
{fin = [(N 90),(N 94)], trans = s26},
{fin = [(N 90)], trans = s26},
{fin = [(N 77)], trans = s0},
{fin = [(N 90),(N 94)], trans = s29},
{fin = [(N 28),(N 90)], trans = s26},
{fin = [(N 90),(N 94)], trans = s31},
{fin = [(N 90)], trans = s32},
{fin = [(N 32),(N 90)], trans = s26},
{fin = [(N 85),(N 94)], trans = s0},
{fin = [(N 80),(N 94)], trans = s35},
{fin = [(N 80)], trans = s35},
{fin = [(N 94)], trans = s37},
{fin = [(N 43)], trans = s0},
{fin = [(N 38),(N 94)], trans = s0},
{fin = [(N 40),(N 94)], trans = s0},
{fin = [(N 92),(N 94)], trans = s41},
{fin = [(N 5)], trans = s0},
{fin = [(N 73),(N 94)], trans = s43},
{fin = [(N 73)], trans = s43},
{fin = [(N 94)], trans = s45},
{fin = [(N 70)], trans = s46},
{fin = [(N 70)], trans = s47},
{fin = [(N 70)], trans = s48},
{fin = [(N 70)], trans = s49},
{fin = [(N 70)], trans = s50},
{fin = [(N 56),(N 70)], trans = s46},
{fin = [(N 70)], trans = s52},
{fin = [(N 70)], trans = s53},
{fin = [(N 70)], trans = s54},
{fin = [(N 70)], trans = s55},
{fin = [(N 70)], trans = s56},
{fin = [(N 70)], trans = s57},
{fin = [(N 70)], trans = s58},
{fin = [(N 66),(N 70)], trans = s46},
{fin = [(N 70)], trans = s60},
{fin = [(N 70)], trans = s61},
{fin = [(N 70)], trans = s62},
{fin = [(N 49),(N 70)], trans = s46},
{fin = [(N 83)], trans = s0},
{fin = [(N 25),(N 94)], trans = s65},
{fin = [(N 25)], trans = s65},
{fin = [(N 20)], trans = s0},
{fin = [(N 103)], trans = s68},
{fin = [(N 98)], trans = s0},
{fin = [(N 96)], trans = s70},
{fin = [(N 8)], trans = s0},
{fin = [(N 100)], trans = s0},
{fin = [(N 147)], trans = s0},
{fin = [(N 145),(N 147)], trans = s0},
{fin = [(N 143),(N 147)], trans = s75},
{fin = [(N 143)], trans = s75},
{fin = [(N 114)], trans = s77},
{fin = [(N 105)], trans = s78},
{fin = [(N 108)], trans = s0},
{fin = [(N 105)], trans = s0},
{fin = [(N 105)], trans = s81},
{fin = [(N 111)], trans = s0},
{fin = [(N 134)], trans = s83},
{fin = [(N 129)], trans = s84},
{fin = [(N 137)], trans = s0},
{fin = [(N 140)], trans = s0},
{fin = [(N 127)], trans = s0},
{fin = [(N 131)], trans = s0},
{fin = [(N 125)], trans = s89},
{fin = [(N 116)], trans = s90},
{fin = [(N 119)], trans = s0},
{fin = [(N 116)], trans = s0},
{fin = [(N 116)], trans = s93},
{fin = [(N 122)], trans = s0}]
end
structure StartStates =
struct
datatype yystartstate = STARTSTATE of int
val A = STARTSTATE 3;
val CODE = STARTSTATE 5;
val COMMENT = STARTSTATE 9;
val EMPTYCOMMENT = STARTSTATE 13;
val F = STARTSTATE 7;
val INITIAL = STARTSTATE 1;
val STRING = STARTSTATE 11;
end
type result = UserDeclarations.lexresult
exception LexerError
end
fun makeLexer yyinput =
let
val yyb = ref "\n"
val yybl = ref 1
val yybufpos = ref 1
val yygone = ref 1
val yydone = ref false
val yybegin = ref 1
val YYBEGIN = fn (Internal.StartStates.STARTSTATE x) =>
yybegin := x
fun lex (yyarg as (inputSource)) =
let fun continue() : Internal.result =
let fun scan (s,AcceptingLeaves : Internal.yyfinstate list list,l,i0) =
let fun action (i,nil) = raise LexError
| action (i,nil::l) = action (i-1,l)
| action (i,(node::acts)::l) =
case node of
Internal.N yyk =>
(let val yytext = substring(!yyb,i0,i-i0)
val yypos = i0+ !yygone
open UserDeclarations Internal.StartStates
in (yybufpos := i; case yyk of
100 => (Add yytext; YYBEGIN STRING; continue())
| 103 => (Add yytext; continue())
| 105 => (Add yytext; continue())
| 108 => (Add yytext; dec commentLevel;
if !commentLevel=0
then BOGUS_VALUE(!lineno,!lineno)
else continue()
)
| 11 => (Add yytext; continue())
| 111 => (Add yytext; inc commentLevel; continue())
| 114 => (Add yytext; continue())
| 116 => (continue())
| 119 => (dec commentLevel;
if !commentLevel=0 then YYBEGIN A else ();
continue ())
| 122 => (inc commentLevel; continue())
| 125 => (continue())
| 127 => (Add yytext; YYBEGIN CODE; continue())
| 129 => (Add yytext; continue())
| 131 => (Add yytext; error inputSource (!lineno) "unclosed string";
inc lineno; YYBEGIN CODE; continue())
| 134 => (Add yytext; continue())
| 137 => (Add yytext; continue())
| 14 => (YYBEGIN A; HEADER (concat (rev (!text)),!lineno,!lineno))
| 140 => (Add yytext;
if substring(yytext,1,1)="\n" then inc lineno else ();
YYBEGIN F; continue())
| 143 => (Add yytext; continue())
| 145 => (Add yytext; YYBEGIN STRING; continue())
| 147 => (Add yytext; error inputSource (!lineno) "unclosed string";
YYBEGIN CODE; continue())
| 16 => (Add yytext; inc lineno; continue())
| 18 => (Add yytext; continue())
| 2 => (Add yytext; YYBEGIN COMMENT; commentLevel := 1;
continue() before YYBEGIN INITIAL)
| 20 => (inc lineno; continue ())
| 25 => (continue())
| 28 => (OF(!lineno,!lineno))
| 32 => (FOR(!lineno,!lineno))
| 34 => (LBRACE(!lineno,!lineno))
| 36 => (RBRACE(!lineno,!lineno))
| 38 => (COMMA(!lineno,!lineno))
| 40 => (ASTERISK(!lineno,!lineno))
| 43 => (ARROW(!lineno,!lineno))
| 49 => (PREC(Hdr.LEFT,!lineno,!lineno))
| 5 => (YYBEGIN EMPTYCOMMENT; commentLevel := 1; continue())
| 56 => (PREC(Hdr.RIGHT,!lineno,!lineno))
| 66 => (PREC(Hdr.NONASSOC,!lineno,!lineno))
| 70 => (lookup(yytext,!lineno,!lineno))
| 73 => (TYVAR(yytext,!lineno,!lineno))
| 77 => (IDDOT(yytext,!lineno,!lineno))
| 8 => (Add yytext; YYBEGIN COMMENT; commentLevel := 1;
continue() before YYBEGIN CODE)
| 80 => (INT (yytext,!lineno,!lineno))
| 83 => (DELIMITER(!lineno,!lineno))
| 85 => (COLON(!lineno,!lineno))
| 87 => (BAR(!lineno,!lineno))
| 90 => (ID ((yytext,!lineno),!lineno,!lineno))
| 92 => (pcount := 1; actionstart := (!lineno);
text := nil; YYBEGIN CODE; continue() before YYBEGIN A)
| 94 => (UNKNOWN(yytext,!lineno,!lineno))
| 96 => (inc pcount; Add yytext; continue())
| 98 => (dec pcount;
if !pcount = 0 then
PROG (concat (rev (!text)),!lineno,!lineno)
else (Add yytext; continue()))
| _ => raise Internal.LexerError
) end )
val {fin,trans} = Vector.sub(Internal.tab, s)
val NewAcceptingLeaves = fin::AcceptingLeaves
in if l = !yybl then
if trans = #trans(Vector.sub(Internal.tab,0))
then action(l,NewAcceptingLeaves
) else let val newchars= if !yydone then "" else yyinput 1024
in if (size newchars)=0
then (yydone := true;
if (l=i0) then UserDeclarations.eof yyarg
else action(l,NewAcceptingLeaves))
else (if i0=l then yyb := newchars
else yyb := substring(!yyb,i0,l-i0)^newchars;
yygone := !yygone+i0;
yybl := size (!yyb);
scan (s,AcceptingLeaves,l-i0,0))
end
else let val NewChar = Char.ord(String.sub(!yyb,l))
val NewState = if NewChar<128 then Char.ord(String.sub(trans,NewChar)) else Char.ord(String.sub(trans,128))
in if NewState=0 then action(l,NewAcceptingLeaves)
else scan(NewState,NewAcceptingLeaves,l+1,i0)
end
end
in scan(!yybegin ,nil,!yybufpos,!yybufpos)
end
in continue end
in lex
end
end
;
