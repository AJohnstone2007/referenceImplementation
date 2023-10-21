require "^.basis.__char";
require "^.basis.__string";
require "^.basis.__int";
require "lists";
require "stringlib";
functor StringLib(
structure Lists : LISTS
) : STRING_LIB =
struct
fun substr_excl (str,left,right1) =
let val origin = Int.max(0, left)
val size = Int.max(0, right1-origin)
in
substring(str,origin,size)
end
fun substr_incl (str,left,right) = substr_excl(str,left,1+right)
fun rev_explode_char(str) =
let fun getord(0,_,l) = l
| getord(n,i,l) = getord(n-1,i+1,MLWorks.String.ordof(str,i)::l)
in
getord(size(str),0,[])
end
fun rev_explode_char s =
let fun scan (0, _, l) = l
| scan (n,i,l) = scan (n-1, i+1, chr (MLWorks.String.ordof (s,i)) ::l)
in
scan (size s, 0, [])
end
fun memstr(str) =
let val chars = rev_explode_char(str)
in
fn (c) => Lists.member(c,chars)
end
fun squash(P,str) =
implode(Lists.filter_outp P (explode str))
fun trim_left(P, str) =
let val len_str = size(str)
val len_str1 = len_str - 1
fun from_left(k,i) =
if (k<=0) then i else
if P(chr (MLWorks.String.ordof(str,i))) then from_left(k-1,i+1) else i
in
substr_excl(str,from_left(len_str1,0),len_str)
end
fun trim_right(P, str) =
let val len_str1 = size(str) - 1
fun from_right(i) =
if (i<=0) then i else
if P(chr (MLWorks.String.ordof(str,i))) then from_right(i-1) else i
in
substr_incl(str,0,from_right(len_str1))
end
local
fun find_first_char(str,sz,P) =
let fun fst_ch(k,i) =
if (k<0) then i else
if P(String.sub(str,i)) then i else fst_ch(k-1,i+1)
in
fst_ch(sz,0)
end
in
fun leading(P, str) =
let val len_str1 = size(str) - 1
val first_idx = find_first_char(str,len_str1,P)
in
substr_excl(str,0,first_idx)
end
fun leading_split(P,str) =
let val len_str = size(str)
val len_str1 = len_str - 1
val split_idx = find_first_char(str,len_str1,P)
val left_str = substr_excl(str,0,split_idx)
val right_str = substr_excl(str,split_idx,len_str)
in
( left_str, right_str )
end
end
local
fun find_last_char(str,sz,P) =
let fun lst_ch(k) =
if (k<0) then ~1 else
if P(String.sub(str,k)) then k+1 else lst_ch(k-1)
in
lst_ch(sz)
end
in
fun trailing(P,str) =
let val len_str = size(str)
val len_str1 = len_str - 1
val last_idx = find_last_char(str,len_str1,P)
in
substr_excl(str,last_idx,len_str)
end
fun trailing_split(P,str) =
let val len_str = size(str)
val len_str1 = len_str - 1
val split_idx = find_last_char(str,len_str1,P)
val left_str = substr_excl(str,0,split_idx)
val right_str = substr_excl(str,split_idx,len_str)
in
( left_str, right_str )
end
end
local
val a = #"a"
val A = #"A"
val z = #"z"
val Z = #"Z"
fun is_lc(i) = ( a <= i andalso i <= z )
fun is_uc(i) = ( A <= i andalso i <= Z )
val zero = #"0"
val one = #"1"
val nine = #"9"
fun is_dg(i) = ( one <= i andalso i <= nine ) orelse ( zero = i )
fun lc'(i) = chr (ord i - ord A + ord a)
fun uc'(i) = chr (ord i - ord a + ord A)
fun lc(i) = if is_uc(i) then lc'(i) else i
fun uc(i) = if is_lc(i) then uc'(i) else i
datatype cap_state = UC | LC
fun cap_char(UC,i) = uc(i)
| cap_char(LC,i) = lc(i)
in
val is_lower_case = Char.isLower
val is_upper_case = Char.isUpper
val is_digit = Char.isDigit
val is_letter = Char.isAlpha
val is_alpha = Char.isAlphaNum
fun to_lower(str) =
let fun doit(i::l,r) = doit(l,lc(i)::r)
| doit([],r) = r
in
implode (doit(explode str,[]))
end
val to_lower = implode o (map Char.toLower) o explode
fun to_upper(str) =
let fun doit(i::l,r) = doit(l,uc(i)::r)
| doit([],r) = r
in
implode (doit(rev_explode_char(str),[]))
end
val to_upper = implode o (map Char.toUpper) o explode
fun capitalise(P,str) =
let fun doit(st,0,_,r) = rev r
| doit(st,i,j,r) =
let val a = chr (MLWorks.String.ordof(str,j))
val letterp = is_letter(a)
val a' = if letterp then cap_char(st,a) else a
val st' = if letterp then LC else
if P(a) then UC else st
in
doit(st',i-1,j+1,a'::r)
end
in
implode (doit(UC,size(str),0,[]))
end
end
end
;
