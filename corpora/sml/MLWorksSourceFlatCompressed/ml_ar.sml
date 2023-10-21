require "../utils/__lists";
exception err of string
fun do_file filename =
let
val names = MLWorks.Internal.Images.table filename
handle MLWorks.Internal.Images.Table string => raise err string
in
Lists_.iterate
(fn str => output(std_out, str ^ "\n"))
names
end
fun obey[filename] =
(do_file filename handle err string =>
output(MLWorks.IO.std_err, "ml_ar: " ^ string ^ "\n"))
| obey args =
output(MLWorks.IO.std_err, "ml_ar: bad args: required ml_ar <library name>\n")
fun obey1["-save", filename] =
(MLWorks.save(filename, fn () => obey(MLWorks.arguments()));
())
| obey1 arg = (output(MLWorks.IO.std_err, "Bad initial args\n");
Lists_.iterate (fn str => output(MLWorks.IO.std_err, str ^ "\n")) arg)
val _ = obey1(MLWorks.arguments());
