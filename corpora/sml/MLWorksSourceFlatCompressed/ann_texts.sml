require "__int";
require "__substring";
require "basic_util";
require "basic_types";
require "ann_texts_sig";
structure AnnotatedText : ANNOTATED_TEXT =
struct
infix 6 ++
open BasicTypes BasicUtil
fun selText (AnnoText(_, t, _))= t
fun selAnno (AnnoText(_, _, a))= a
fun updAnno (AnnoText(x, t, _)) a = AnnoText(x, t, a)
fun addCols(c1, c2) = c1+c2- (Int.min(c1, 1))
fun add2Mark (c, r) (Mark(cm, rm)) = if (c<= 1) then Mark(c, r+rm)
else Mark(addCols(c, cm), rm)
| add2Mark (c, r) (MarkToEnd cm) = MarkToEnd(addCols(c, cm))
| add2Mark (c, r) MarkEnd = MarkEnd
fun pair f (x, y) = (f x, f y)
fun mapMark f (TATag(aid, marks, conf, binds)) =
TATag(aid, map (pair f) marks, conf, binds)
| mapMark f (TAWidget(aid, mark, wid, wids, co1, co2, binds)) =
TAWidget(aid, f mark, wid, wids, co1, co2, binds)
fun cat ((cols, rows), s, a) ((cols0, rows0), t, b) =
let val ann = a@(map (mapMark (add2Mark (cols, rows))) b)
in if cols0 <= 1
then AnnoText(SOME(cols, rows+rows0), s^t, ann)
else
AnnoText(SOME(addCols(cols, cols0), rows0), s^t, ann)
end
fun lenAT t =
let fun cnt (thischar, (line, char)) =
if (StringUtil.isLinefeed thischar)
then (line+1, 0)
else (line, char+1)
val (cols, rows) = Substring.foldl cnt (0, 0) (Substring.all t)
in (Int.max(cols, 1), rows)
end
fun ((AnnoText(len, s, a)) ++ (AnnoText(_ , "", b))) =
AnnoText(len, s, a)
| ((AnnoText(NONE, s, a)) ++ (AnnoText(NONE, t, []))) =
AnnoText(NONE, s^t, a)
| ((AnnoText(len1, s, a)) ++ (AnnoText(len2, t, b))) =
let fun get_len (SOME(c, r), s, a) = ((c, r), s, a)
| get_len (NONE, s, a) = (lenAT s, s, a)
in cat (get_len(len1, s, a)) (get_len(len2, t, b))
end
fun nl(AnnoText(NONE, s, a)) = AnnoText(NONE, s^"\n", a)
| nl(AnnoText(SOME(c, r), s, a)) = AnnoText(SOME(c+1, 0), s^"\n", a)
val mtAT = AnnoText(NONE, "", [])
fun mk str = AnnoText(NONE, str, [])
fun concatATWith str ls =
let val at = mk str
fun concatWith' [] = mtAT
| concatWith' [t] = t
| concatWith' (t::ts) = t++at++(concatWith' ts)
in concatWith' ls
end
end
;
