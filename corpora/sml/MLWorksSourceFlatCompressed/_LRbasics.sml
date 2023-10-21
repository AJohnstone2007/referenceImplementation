require "LRbasics";
require "../utils/crash";
require "../utils/lists";
require "../basis/text_io";
require "../basis/string";
functor LRbasics (val table_dir : string
structure Crash : CRASH
structure Lists : LISTS
structure TextIO : TEXT_IO
structure String : STRING where type string = string where type char = char
) : LRBASICS =
struct
fun find_OS_arg s =
let
fun assoc [] = NONE
| assoc [_] = NONE
| assoc (a::b::rest) =
if a = s then SOME b else assoc (b::rest)
in
assoc (MLWorks.arguments ())
end
val table_dir =
case find_OS_arg "-parser-tables" of
NONE => table_dir
| SOME dir => dir
val _ = print("Loading parsing tables from: " ^ table_dir ^ "\n")
val actionfile = table_dir ^ "actions.data"
val gotofile = table_dir ^ "gotos.data"
val ifinfixinput = 0
and ifvarstack = 1
and ifstarstack = 2
and ifstarinput = 3
and ifleftassoc = 4
val ifinfixinputarity = 0
and ifvarstackarity = 1
and ifstarstackarity = 1
and ifstarinputarity = 0
and ifleftassocarity = 2
datatype GSymbol =
EOF
| ABSCOLON
| ABSDATBIND
| ABSTRACTION
| ABSTYPE
| AND
| ANDALSO
| APPEXP
| ARROW
| AS
| ATEXP
| ATPAT
| ATPAT1
| ATPATLIST
| BIN_ATPAT
| BRA
| CASE
| CHAR
| COLON
| COMMA
| CONBIND
| CONBIND1
| CONDEF
| CONDESC
| CONDESC1
| DARROW
| DATAHEADER
| DATATYPE
| DATBIND
| DATBIND1
| DATDESC
| DATDESC1
| DATREPL
| DATREPLDESC
| DEC
| DEC0
| DEC1
| DECSEP
| DO
| ELLIPSIS
| ELSE
| END
| EQTYPE
| EQUAL
| EXBIND
| EXBIND1
| EXCEPTION
| EXCONDEF
| EXDESC
| EXDESC1
| EXP
| EXPLIST
| EXPLIST2
| EXPROW
| EXPSEQ
| EXPSEQ2
| FN
| FUN
| FUNBIND
| FUNBIND1
| FUNCTOR
| FUNDEC1
| FUNDEC1PLUS
| FUNID
| FUNIDBIND
| FVAL
| FVALBIND
| FVALLIST
| HANDLE
| HASH
| IF
| IN
| INCLUDE
| INFEXP
| INFIX
| INFIXR
| INFVAR
| INTEGER
| KET
| LAB
| LBRACE
| LET
| LOCAL
| LONGID
| LONGIDLIST
| LONGSTRID
| LONGSTRIDEQLIST
| LONGSTRIDLIST
| LONGTYCON
| LONGTYCONEQLIST
| LONGTYPBIND
| LONGTYPBIND1
| LONGVAR
| LPAR
| MAGICOPEN
| MATCH
| MRULE
| NONFIX
| OF
| OP
| OPCONDEF
| OPEN
| OPEXCONDEF
| OPLONGVAR
| OPTOFTYPE
| OPTTYPE
| OPVARDEF
| ORELSE
| PAT
| PATLIST2
| PATROW
| PATROW1
| PATVAR
| PROGRAM
| RAISE
| RBRACE
| REAL
| REC
| REQUIRE
| RPAR
| SCON
| SEMICOLON
| SHAREQ
| SHAREQ1
| SHARING
| SIG
| SIGBIND
| SIGBINDER
| SIGDEC1
| SIGDEC1PLUS
| SIGEXP
| SIGID
| SIGIDLIST
| SIGNATURE
| SIG_START
| SPEC
| SPEC1
| STAR
| START
| STARTFUNBIND1
| STARTFUNBIND2
| START_LET
| START_LOCAL
| STRBIND
| STRBIND1
| STRDEC
| STRDEC0
| STRDEC1
| STRDEC1PLUS
| STRDEC1PLUS0
| STRDESC
| STRDESC1
| STREXP
| STRID
| STRING
| STRUCT
| STRUCTURE
| STRUCT_START
| SYM
| SYMID
| SYMLIST
| THEN
| TOPDEC
| TOPDEC1
| TY
| TYCON
| TYLIST
| TYPBIND
| TYPBIND1
| TYPDESC
| TYPDESC1
| TYPE
| TYROW
| TYSEQ
| TYTUPLE
| TYVAR
| TYVARLIST
| TYVARSEQ
| TYVARSEQ1
| UNDERBAR
| VAL
| VALBIND
| VALBIND1
| VALDESC
| VAR
| VARDEF
| VBAR
| WHERE
| WHILE
| WITH
| WITHTYPE
| WORD
datatype Action =
Accept
| Shift
| Reduce of int * GSymbol * int
| Funcall of int * int * Action * Action
| Resolve of Action list
| NoAction
val action_array = MLWorks.Internal.Array.arrayoflist [
Reduce(0,DECSEP,307),
Shift,
Reduce(1,START_LET,16),
Reduce(1,SCON,173),
Reduce(1,STRDEC1PLUS,213),
Reduce(1,OPLONGVAR,178),
Reduce(1,LONGVAR,177),
Reduce(1,SCON,170),
Reduce(1,SCON,172),
Reduce(1,INFEXP,29),
Funcall(ifinfixinput,ifinfixinputarity,Reduce(1,INFEXP,29),Shift),
Reduce(0,EXPLIST,19),
Reduce(1,PROGRAM,306),
Reduce(1,TOPDEC,299),
Reduce(1,TOPDEC,297),
Reduce(1,START_LOCAL,71),
Reduce(1,STRDEC1,217),
Reduce(1,ATEXP,1),
Reduce(1,ATEXP,2),
Reduce(1,TOPDEC,300),
Reduce(0,TYVARSEQ,96),
Reduce(1,APPEXP,27),
Reduce(1,SIGDEC1PLUS,231),
Accept,
Reduce(1,SCON,171),
Reduce(1,ATEXP,3),
Reduce(1,TOPDEC,298),
Reduce(1,EXP,33),
Reduce(1,SCON,174),
Reduce(1,FUNDEC1PLUS,285),
Reduce(1,STRDEC1PLUS,214),
Reduce(1,TOPDEC1,302),
Reduce(1,LAB,175),
Reduce(2,ATEXP,7),
Reduce(1,LAB,176),
Reduce(1,EXPLIST,20),
Reduce(1,EXPLIST,21),
Reduce(3,EXPLIST2,17),
Reduce(1,DEC,47),
Reduce(2,DECSEP,46),
Reduce(1,SYM,182),
Reduce(2,DEC1,68),
Reduce(1,SYM,183),
Reduce(1,SYMLIST,168),
Reduce(3,DEC1,69),
Reduce(2,SYMLIST,169),
Reduce(1,TYVARSEQ1,98),
Reduce(1,DATBIND,103),
Reduce(1,ABSDATBIND,62),
Reduce(1,TYVARSEQ,97),
Reduce(1,TYPBIND,90),
Reduce(1,TYVARLIST,100),
Reduce(3,TYVARSEQ1,99),
Reduce(3,TYVARLIST,101),
Reduce(1,TYCON,199),
Reduce(1,TY,146),
Reduce(1,LONGTYCON,180),
Reduce(1,TY,152),
Reduce(1,TY,151),
Reduce(4,TYPBIND1,92),
Reduce(2,TY,149),
Reduce(1,STAR,200),
Funcall(ifstarstack,ifstarstackarity,Reduce(1,STAR,200),Reduce(1,LONGTYCON,180)),
Reduce(2,TY,150),
Reduce(3,TYTUPLE,159),
Funcall(ifstarinput,ifstarinputarity,Reduce(3,TYTUPLE,159),Shift),
Reduce(3,TY,153),
Reduce(3,TYTUPLE,158),
Funcall(ifstarinput,ifstarinputarity,Reduce(3,TYTUPLE,158),Shift),
Reduce(1,TYLIST,156),
Reduce(3,TY,154),
Reduce(3,TYLIST,157),
Reduce(5,TYSEQ,155),
Reduce(2,TY,148),
Reduce(3,TYROW,160),
Reduce(3,TY,147),
Reduce(5,TYROW,161),
Reduce(3,TYPBIND,91),
Reduce(7,DEC1,61),
Reduce(5,DEC1,60),
Reduce(3,DATBIND,104),
Reduce(1,CONDEF,193),
Reduce(0,OPTOFTYPE,114),
Reduce(1,OPCONDEF,194),
Reduce(1,CONBIND,107),
Reduce(2,DATBIND1,105),
Reduce(2,OPCONDEF,195),
Reduce(3,CONBIND,108),
Reduce(2,CONBIND1,109),
Reduce(2,OPTOFTYPE,115),
Reduce(3,DATAHEADER,106),
Reduce(2,DEC1,57),
Reduce(2,DEC1,58),
Reduce(4,DEC1,59),
Reduce(3,DATREPL,102),
Reduce(1,VARDEF,189),
Funcall(ifvarstack,ifvarstackarity,Reduce(1,VARDEF,189),Reduce(1,LONGVAR,177)),
Reduce(2,DEC1,53),
Reduce(1,PAT,140),
Reduce(1,VALBIND,72),
Reduce(1,ATPAT,116),
Reduce(1,OPVARDEF,190),
Reduce(1,ATPAT,117),
Reduce(0,OPTTYPE,88),
Funcall(ifinfixinput,ifinfixinputarity,Reduce(1,ATPAT,118),Shift),
Resolve[Reduce(1,ATPAT,118),Shift],
Reduce(1,ATPAT,118),
Funcall(ifinfixinput,ifinfixinputarity,Reduce(1,ATPAT,119),Shift),
Reduce(1,ATPAT,119),
Reduce(2,PAT,141),
Reduce(2,ATPAT,126),
Reduce(2,OPLONGVAR,179),
Reduce(2,OPVARDEF,191),
Reduce(3,ATPAT,129),
Reduce(3,PATLIST2,134),
Reduce(2,PAT,142),
Reduce(2,OPTTYPE,89),
Reduce(2,ATPAT,124),
Reduce(3,ATPAT,125),
Reduce(1,VAR,188),
Reduce(3,ATPAT,128),
Reduce(3,PAT,144),
Reduce(3,PAT,143),
Funcall(ifleftassoc,ifleftassocarity,Reduce(3,PAT,143),Shift),
Reduce(1,SYMID,184),
Reduce(1,PATROW,135),
Reduce(2,ATPAT,123),
Reduce(3,ATPAT,122),
Reduce(2,PATROW1,138),
Reduce(4,PATROW1,139),
Reduce(3,PATROW1,137),
Reduce(3,ATPAT,120),
Reduce(3,PATROW,136),
Reduce(5,ATPAT,121),
Reduce(3,PATLIST2,133),
Reduce(4,PAT,145),
Reduce(3,ATPAT,127),
Reduce(2,VALBIND,75),
Reduce(3,VALBIND1,76),
Reduce(2,ATEXP,4),
Reduce(2,EXP,38),
Reduce(2,APPEXP,28),
Reduce(2,ATEXP,10),
Reduce(1,EXPSEQ,23),
Reduce(3,ATEXP,11),
Reduce(3,EXP,35),
Reduce(3,EXP,34),
Reduce(1,MATCH,44),
Reduce(3,EXP,37),
Reduce(3,MATCH,43),
Reduce(3,MRULE,45),
Reduce(3,EXP,36),
Reduce(4,EXP,41),
Reduce(1,INFVAR,31),
Reduce(1,INFVAR,32),
Reduce(3,INFEXP,30),
Funcall(ifleftassoc,ifleftassocarity,Reduce(3,INFEXP,30),Shift),
Reduce(2,ATEXP,6),
Reduce(3,EXPROW,25),
Reduce(2,EXP,42),
Reduce(3,ATEXP,5),
Reduce(5,EXPROW,26),
Reduce(4,EXP,40),
Reduce(3,ATEXP,15),
Reduce(3,EXPLIST2,18),
Reduce(3,ATEXP,13),
Reduce(3,EXPSEQ2,24),
Reduce(3,EXPSEQ,22),
Reduce(6,EXP,39),
Reduce(3,VALBIND,73),
Reduce(4,VALBIND,74),
Reduce(3,DEC1,52),
Reduce(1,ATPAT1,132),
Reduce(1,FVALLIST,79),
Reduce(2,DEC1,55),
Reduce(1,FVALBIND,77),
Funcall(ifinfixinput,ifinfixinputarity,Reduce(1,ATPAT1,131),Shift),
Reduce(1,ATPATLIST,162),
Reduce(2,ATPATLIST,163),
Reduce(5,FVAL,87),
Reduce(4,FVAL,86),
Reduce(5,FVAL,84),
Reduce(5,FVAL,85),
Reduce(7,FVAL,83),
Reduce(6,FVAL,82),
Reduce(5,FVAL,81),
Reduce(3,FVALLIST,80),
Funcall(ifinfixinput,ifinfixinputarity,Shift,Reduce(1,PAT,140)),
Reduce(1,PATVAR,192),
Reduce(5,BIN_ATPAT,130),
Reduce(3,FVALBIND,78),
Reduce(3,DEC1,54),
Reduce(1,LONGIDLIST,165),
Reduce(2,DEC1,65),
Reduce(2,LONGIDLIST,164),
Reduce(1,EXBIND,110),
Reduce(1,EXCONDEF,196),
Reduce(1,OPEXCONDEF,197),
Reduce(2,DEC1,63),
Reduce(3,EXBIND,111),
Reduce(2,OPEXCONDEF,198),
Reduce(2,EXBIND1,112),
Reduce(3,EXBIND1,113),
Reduce(2,DEC1,66),
Reduce(3,DEC1,67),
Reduce(3,DEC,49),
Reduce(3,DEC0,51),
Reduce(2,DEC1,70),
Reduce(5,DEC1,64),
Reduce(2,DEC1,56),
Reduce(3,DEC,48),
Reduce(3,DEC0,50),
Reduce(5,ATEXP,14),
Reduce(3,ATEXP,12),
Reduce(3,ATEXP,8),
Reduce(6,ATEXP,9),
Reduce(2,STRDEC1PLUS0,216),
Reduce(2,STRDEC1,219),
Reduce(1,STRBIND,223),
Reduce(1,STRID,187),
Reduce(1,SIGBINDER,222),
Reduce(1,SIGBINDER,221),
Reduce(1,STREXP,202),
Reduce(1,STRUCT_START,207),
Reduce(1,FUNID,185),
Reduce(3,STRBIND1,226),
Reduce(1,STRDEC,208),
Reduce(2,STRDEC1,218),
Reduce(3,STRBIND,224),
Reduce(3,STRDEC,210),
Reduce(3,STRDEC0,212),
Reduce(3,STRDEC,209),
Reduce(3,STRDEC0,211),
Reduce(5,STRDEC1,220),
Reduce(5,STREXP,205),
Reduce(1,SIG_START,230),
Reduce(1,SIGID,186),
Reduce(1,SIGEXP,228),
Reduce(0,SPEC,237),
Reduce(3,STREXP,206),
Reduce(4,SIGEXP,229),
Reduce(4,LONGTYPBIND1,95),
Reduce(1,SPEC1,240),
Reduce(2,SPEC,238),
Reduce(3,SIGEXP,227),
Reduce(2,SPEC1,254),
Reduce(3,SPEC1,255),
Reduce(2,SPEC1,250),
Reduce(1,SIGIDLIST,256),
Reduce(2,SPEC1,251),
Reduce(2,SIGIDLIST,257),
Reduce(1,STRDESC,274),
Reduce(2,SPEC1,247),
Reduce(3,STRDESC,275),
Reduce(3,STRDESC1,276),
Reduce(1,DATDESC,265),
Reduce(2,SPEC1,244),
Reduce(2,SPEC1,245),
Reduce(3,DATDESC,266),
Reduce(2,DATDESC1,267),
Reduce(1,CONDESC,268),
Reduce(2,CONDESC1,270),
Reduce(3,CONDESC,269),
Reduce(3,DATREPLDESC,264),
Reduce(2,SPEC1,241),
Reduce(3,VALDESC,258),
Reduce(5,VALDESC,259),
Reduce(2,SPEC1,243),
Reduce(1,TYPDESC,260),
Reduce(3,TYPDESC,261),
Reduce(2,TYPDESC1,262),
Reduce(4,TYPDESC1,263),
Reduce(2,SPEC1,249),
Reduce(1,LONGSTRID,181),
Reduce(1,SHAREQ1,279),
Reduce(3,SPEC,239),
Reduce(1,SHAREQ,277),
Reduce(3,LONGSTRIDEQLIST,281),
Reduce(3,SHAREQ,278),
Reduce(3,LONGSTRIDEQLIST,282),
Reduce(2,SHAREQ1,280),
Reduce(3,LONGTYCONEQLIST,283),
Reduce(3,LONGTYCONEQLIST,284),
Reduce(1,EXDESC,271),
Reduce(2,SPEC1,246),
Reduce(3,EXDESC,272),
Reduce(2,EXDESC1,273),
Reduce(2,SPEC1,252),
Reduce(3,SPEC1,253),
Reduce(5,SPEC1,248),
Reduce(2,SPEC1,242),
Reduce(4,STREXP,203),
Reduce(3,STREXP,201),
Reduce(4,STREXP,204),
Reduce(5,STRBIND1,225),
Reduce(2,SIGDEC1PLUS,232),
Reduce(2,SIGDEC1,233),
Reduce(5,SIGBIND,235),
Reduce(4,SIGBIND,236),
Reduce(3,SIGBIND,234),
Reduce(2,TOPDEC,301),
Reduce(2,FUNDEC1,287),
Reduce(1,FUNBIND,288),
Reduce(1,FUNIDBIND,296),
Reduce(3,FUNBIND1,291),
Reduce(5,FUNBIND1,290),
Reduce(3,FUNBIND1,293),
Reduce(5,FUNBIND1,292),
Reduce(4,STARTFUNBIND2,295),
Reduce(6,STARTFUNBIND1,294),
Reduce(3,FUNBIND,289),
Reduce(2,PROGRAM,305),
Reduce(3,PROGRAM,303),
Reduce(2,FUNDEC1PLUS,286),
Reduce(2,PROGRAM,304),
Reduce(2,STRDEC1PLUS0,215)
]
val symbol_array = MLWorks.Internal.Array.arrayoflist[
ABSCOLON,
ABSDATBIND,
ABSTRACTION,
ABSTYPE,
AND,
ANDALSO,
APPEXP,
ARROW,
AS,
ATEXP,
ATPAT,
ATPAT1,
ATPATLIST,
BIN_ATPAT,
BRA,
CASE,
CHAR,
COLON,
COMMA,
CONBIND,
CONBIND1,
CONDEF,
CONDESC,
CONDESC1,
DARROW,
DATAHEADER,
DATATYPE,
DATBIND,
DATBIND1,
DATDESC,
DATDESC1,
DATREPL,
DATREPLDESC,
DEC,
DEC0,
DEC1,
DECSEP,
DO,
ELLIPSIS,
ELSE,
END,
EQTYPE,
EQUAL,
EXBIND,
EXBIND1,
EXCEPTION,
EXCONDEF,
EXDESC,
EXDESC1,
EXP,
EXPLIST,
EXPLIST2,
EXPROW,
EXPSEQ,
EXPSEQ2,
FN,
FUN,
FUNBIND,
FUNBIND1,
FUNCTOR,
FUNDEC1,
FUNDEC1PLUS,
FUNID,
FUNIDBIND,
FVAL,
FVALBIND,
FVALLIST,
HANDLE,
HASH,
IF,
IN,
INCLUDE,
INFEXP,
INFIX,
INFIXR,
INFVAR,
INTEGER,
KET,
LAB,
LBRACE,
LET,
LOCAL,
LONGID,
LONGIDLIST,
LONGSTRID,
LONGSTRIDEQLIST,
LONGSTRIDLIST,
LONGTYCON,
LONGTYCONEQLIST,
LONGTYPBIND,
LONGTYPBIND1,
LONGVAR,
LPAR,
MAGICOPEN,
MATCH,
MRULE,
NONFIX,
OF,
OP,
OPCONDEF,
OPEN,
OPEXCONDEF,
OPLONGVAR,
OPTOFTYPE,
OPTTYPE,
OPVARDEF,
ORELSE,
PAT,
PATLIST2,
PATROW,
PATROW1,
PATVAR,
PROGRAM,
RAISE,
RBRACE,
REAL,
REC,
REQUIRE,
RPAR,
SCON,
SEMICOLON,
SHAREQ,
SHAREQ1,
SHARING,
SIG,
SIGBIND,
SIGBINDER,
SIGDEC1,
SIGDEC1PLUS,
SIGEXP,
SIGID,
SIGIDLIST,
SIGNATURE,
SIG_START,
SPEC,
SPEC1,
STAR,
START,
STARTFUNBIND1,
STARTFUNBIND2,
START_LET,
START_LOCAL,
STRBIND,
STRBIND1,
STRDEC,
STRDEC0,
STRDEC1,
STRDEC1PLUS,
STRDEC1PLUS0,
STRDESC,
STRDESC1,
STREXP,
STRID,
STRING,
STRUCT,
STRUCTURE,
STRUCT_START,
SYM,
SYMID,
SYMLIST,
THEN,
TOPDEC,
TOPDEC1,
TY,
TYCON,
TYLIST,
TYPBIND,
TYPBIND1,
TYPDESC,
TYPDESC1,
TYPE,
TYROW,
TYSEQ,
TYTUPLE,
TYVAR,
TYVARLIST,
TYVARSEQ,
TYVARSEQ1,
UNDERBAR,
VAL,
VALBIND,
VALBIND1,
VALDESC,
VAR,
VARDEF,
VBAR,
WHERE,
WHILE,
WITH,
WITHTYPE,
WORD
]
fun token_string t =
case t of
EOF => "EOF"
| ABSCOLON => ":>"
| ABSTYPE => "abstype"
| AND => "and"
| ANDALSO => "andalso"
| ARROW => "->"
| AS => "as"
| BRA => "["
| CASE => "case"
| CHAR => "CHAR"
| COLON => ":"
| COMMA => ","
| DARROW => "=>"
| DATATYPE => "datatype"
| DO => "do"
| ELLIPSIS => "..."
| ELSE => "else"
| END => "end"
| EQTYPE => "eqtype"
| EQUAL => "="
| EXCEPTION => "exception"
| FN => "fn"
| FUN => "fun"
| FUNCTOR => "functor"
| HANDLE => "handle"
| HASH => "#"
| IF => "if"
| IN => "in"
| INCLUDE => "include"
| INFIX => "infix"
| INFIXR => "infixr"
| INTEGER => "INTEGER"
| KET => "]"
| LBRACE => "{"
| LET => "let"
| LOCAL => "local"
| LONGID => "ID"
| LPAR => "("
| NONFIX => "nonfix"
| OF => "of"
| OP => "op"
| OPEN => "open"
| ORELSE => "orelse"
| RAISE => "raise"
| RBRACE => "}"
| REAL => "REAL"
| REC => "rec"
| REQUIRE => "require"
| RPAR => ")"
| SEMICOLON => ";"
| SHARING => "sharing"
| SIG => "sig"
| SIGNATURE => "signature"
| STRING => "STRING"
| STRUCT => "struct"
| STRUCTURE => "structure"
| THEN => "then"
| TYPE => "type"
| TYVAR => "TYVAR"
| UNDERBAR => "_"
| VAL => "val"
| VBAR => "|"
| WHERE => "where"
| WHILE => "while"
| WITH => "with"
| WORD => "WORD"
| WITHTYPE => "withtype"
| MAGICOPEN => "#("
| ABSTRACTION => "abstraction"
| _ => "*Non-terminal*"
local
fun digit_char (char:int):bool =
ord #"0" <= char andalso char <= ord #"9"
fun digit_val char = char - ord #"0"
fun parse_integers chars =
let
val string = String.implode chars
val len = size string
fun reverse l =
let
fun rev_aux([],l) = l
| rev_aux(a::b,l) = rev_aux(b,a::l)
in
rev_aux(l,[])
end
fun parse_integers1 (index,acc) =
if index >= len
then
reverse acc
else
let val char = MLWorks.String.ordof (string, index)
in
if char = ord #"-"
then
parse_integers2 (index+1,acc,~1,0)
else
if digit_char char
then
parse_integers2 (index+1,acc,1,(digit_val char))
else
parse_integers1 (index+1,acc)
end
and parse_integers2 (index,acc,neg,cum) =
if index >= (size string)
then
reverse (neg * cum :: acc)
else
let val char = MLWorks.String.ordof (string, index)
in
if digit_char char
then
parse_integers2 (index+1,acc,neg,(10 * cum) + (digit_val char))
else
parse_integers1 (index+1,neg * cum :: acc)
end
in
parse_integers1 (0,[])
end
fun parse_table_row row =
let
val integers = parse_integers row
exception Do_one
fun do_one [] = []
| do_one (n :: m :: l) = (n,m)::do_one l
| do_one _ = raise Do_one
in
do_one integers
end
fun with_open_in_file (file,f) =
let
val stream = TextIO.openIn file
val result = (f stream handle e => (TextIO.closeIn stream; raise e))
in
(TextIO.closeIn stream;
result)
end
exception Eof
fun read_line stream : char list =
let
fun extend_line line =
if TextIO.endOfStream stream then
case line of
[] => raise Eof
| _ => rev line
else
let
val char = valOf(TextIO.input1 stream)
in
if char = #"\n" then rev(#"\n" :: line)
else
extend_line(char :: line)
end
in
extend_line []
end
fun parse_table_file file =
let
exception Result of (int * int) list list
fun do_it (result,stream) =
let val line = read_line stream handle Eof => raise Result(result)
in
do_it (parse_table_row line::result,stream)
end
in
with_open_in_file (file,
fn stream =>
do_it ([],stream)
handle Result(l) => rev l)
end
val int_to_gsym : int -> GSymbol = MLWorks.Internal.Value.cast
val gsym_to_int : GSymbol -> int = MLWorks.Internal.Value.cast
val make_vector = MLWorks.Internal.Array.arrayoflist
val vector_sub = MLWorks.Internal.Value.unsafe_array_sub
val vector_length = MLWorks.Internal.Array.length
fun convert_array (array,default) =
let
fun arrayify_alist (alist,default) =
let
val len = (MLWorks.Internal.Array.length symbol_array) + 1
val a = MLWorks.Internal.Array.array (len,default)
val _ =
map
(fn (key,value) => MLWorks.Internal.Array.update(a,key,value))
alist
in
a
end
in
MLWorks.Internal.ExtendedArray.map
(fn alist => arrayify_alist (alist,default))
array
end
fun can_merge (array1,array2) =
let fun check 0 = true
| check n =
let
val n' = n-1
val v1 = MLWorks.Internal.Array.sub(array1,n')
val v2 = MLWorks.Internal.Array.sub(array2,n')
in
((v1 = ~1) orelse (v2 = ~1) orelse (v1 = v2))
andalso
check n'
end
in
check(MLWorks.Internal.Array.length array1)
end
fun merge (array1,array2) =
let fun merge_it 0 = array1
| merge_it n =
let
val n' = n-1
in
if (MLWorks.Internal.Array.sub(array1,n') = ~1) andalso
not (MLWorks.Internal.Array.sub(array2,n') = ~1)
then
MLWorks.Internal.Array.update(array1,n',MLWorks.Internal.Array.sub(array2,n'))
else ();
merge_it n'
end
in
merge_it (MLWorks.Internal.Array.length array1)
end
fun row_to_assoc row =
let
val len = MLWorks.Internal.Array.length row
fun row_to_assoc_aux (n,acc) =
if n = len
then rev acc
else
let
val entry = MLWorks.Internal.Array.sub (row,n)
in
if entry = ~1
then row_to_assoc_aux (n+1,acc)
else row_to_assoc_aux (n+1,(n,entry)::acc)
end
in
row_to_assoc_aux (0,[])
end
fun convert_action a =
if a = ~1
then NoAction
else MLWorks.Internal.Array.sub (action_array,a)
fun convert_symbol s =
if (s = ~1)
then EOF
else MLWorks.Internal.Array.sub (symbol_array,s)
fun make_vector_mapping alist =
let
val sorted = Lists.msort (fn ((a:int,_),(a':int,_)) => a < a') alist
in
(make_vector (map #1 sorted),
make_vector (map #2 sorted))
end
fun lookup (i:int,(indices,values),default) =
let
fun lookup_aux (n,m,i,indices,values) =
if n = m
then default
else
let
val t = (n + m) div 2
val x = vector_sub (indices,t)
in
if x < i then lookup_aux (t+1,m,i,indices,values)
else if x = i then vector_sub (values,t)
else lookup_aux (n,t,i,indices,values)
end
in
lookup_aux (0,vector_length indices,i,indices,values)
end
fun compress_goto_table gotos =
let
fun find_one (array,[]) = NONE
| find_one (array,a::l) =
if can_merge(array,a)
then SOME (a,Lists.length l)
else find_one (array,l)
fun compress_aux (n,arrays,acc) =
if n = MLWorks.Internal.Array.length gotos
then (rev arrays,rev acc)
else
let
val this_one = MLWorks.Internal.Array.sub(gotos,n)
in
case find_one(this_one,arrays) of
NONE => compress_aux (n+1,this_one::arrays,Lists.length arrays::acc)
| SOME (old_one,i) =>
(ignore(merge (old_one,this_one));
compress_aux(n+1,arrays,i::acc))
end
val (arrays,indices) = compress_aux (0,[],[])
val mergedmaps =
MLWorks.Internal.Array.arrayoflist
(map
(fn a => make_vector_mapping (row_to_assoc a))
arrays)
val result =
make_vector
(map
(fn n => MLWorks.Internal.Array.sub (mergedmaps,n))
indices)
in
result
end
val action_table =
let
val actionlist = parse_table_file actionfile
val resolved_actionlist =
map (map (fn (i,a) => (gsym_to_int (convert_symbol i),convert_action a))) actionlist
in
make_vector (map make_vector_mapping resolved_actionlist)
end
val goto_table =
let
val gotolist = parse_table_file gotofile
val resolved_gotolist =
map (map (fn (i,a) => (gsym_to_int (convert_symbol i),a))) gotolist
in
compress_goto_table (convert_array (MLWorks.Internal.Array.arrayoflist resolved_gotolist,~1))
end
in
exception NoNextState
fun get_next_state (gsym,state) =
let
val gotos = vector_sub (goto_table,state)
val next = lookup (gsym_to_int gsym,gotos,~1)
in
if next = ~1
then raise NoNextState
else next
end
fun vector_to_list vector =
let
fun aux (0,acc) = acc
| aux (n,acc) =
aux (n-1,vector_sub (vector,n-1)::acc)
val len = vector_length vector
in
aux (len,[])
end
exception GetPossibleSymbols
fun get_possible_symbols state =
let
val (indices,_) = vector_sub(action_table,state)
in
map int_to_gsym (vector_to_list indices)
end
fun get_action (gsym,state) =
let
val actions = vector_sub (action_table,state)
in
lookup (gsym_to_int gsym,actions,NoAction)
end
fun get_all_actions state =
let
val (_,actions) = vector_sub (action_table,state)
in
vector_to_list actions
end
end
end
;
