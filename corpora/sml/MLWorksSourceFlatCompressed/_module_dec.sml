require "module_dec";
require "module_name";
require "../basis/__list";
functor ModuleDec(structure ModuleName : MODULE_NAME): MODULE_DEC =
struct
structure ModuleName = ModuleName
datatype Dec =
StrDec of { name: ModuleName.t,
def: StrExp,
constraint: StrExp option
} list
| FctDec of { name: ModuleName.t,
params: (ModuleName.t option * StrExp) list,
body: StrExp,
constraint: StrExp option
} list
| LocalDec of Dec * Dec
| SeqDec of Dec list
| OpenDec of StrExp list
| DecRef of ModuleName.set
and StrExp =
VarStrExp of ModuleName.path
| BaseStrExp of Dec
| AppStrExp of ModuleName.t * StrExp
| LetStrExp of Dec * StrExp
| AugStrExp of StrExp * ModuleName.set
| ConStrExp of StrExp * StrExp
fun display pr dec =
let fun indent 0 = () | indent n = (pr " "; indent (n-1))
fun display_namespace name =
let val namespace = ModuleName.namespaceOf name
in if namespace = ModuleName.STRspace
then pr "(str)"
else if namespace = ModuleName.SIGspace
then pr "(sig)"
else if namespace = ModuleName.FCTspace
then pr "(fct)"
else pr "(?)"
end
fun display_nameOf name =
(pr (ModuleName.nameOf name); display_namespace name)
fun display_nameOfPath path =
(pr (ModuleName.nameOfPath path);
app display_namespace (ModuleName.mnListOfPath path))
fun display_dec ind (StrDec strdecs) =
let fun display_strdec (ind, initial_indent)
{name, def, constraint} =
(if initial_indent then indent ind else ();
display_nameOf name; pr "\n";
display_str (ind + 2) def;
case constraint of
NONE => ()
| SOME str => display_str (ind + 2) str)
in (indent ind;
pr "StrDec ";
case strdecs of
[strdec] => display_strdec (ind, false) strdec
| _ =>
(pr "\n";
List.app (display_strdec (ind + 2, true)) strdecs))
end
| display_dec ind (FctDec fundecs) =
(indent ind;
pr "FctDec\n";
List.app (fn {name, params, body, constraint} =>
(indent (ind + 2);
display_nameOf name; pr "\n";
List.app
(fn (SOME (sid), strexp) =>
(indent (ind + 6);
display_nameOf sid;
pr "\n";
display_str (ind + 6) strexp)
| (NONE, strexp) =>
display_str (ind + 6) strexp)
params;
display_str (ind + 4) body;
case constraint of
NONE => ()
| SOME str => display_str (ind + 4) str)
) fundecs)
| display_dec ind (LocalDec(dec1, dec2)) =
(indent ind;
pr "Local\n";
display_dec (ind + 2) dec1;
display_dec (ind + 2) dec2)
| display_dec ind (SeqDec decs) =
(indent ind;
pr "Seq\n";
List.app (display_dec (ind + 2)) decs)
| display_dec ind
(OpenDec [VarStrExp path]) =
(indent ind;
pr "Open ";
display_nameOfPath path;
pr "\n")
| display_dec ind (OpenDec strs) =
(indent ind;
pr "Open\n";
List.app (display_str (ind + 2)) strs)
| display_dec ind (DecRef set) =
(indent ind;
pr "DecRef ";
pr (ModuleName.setToString set);
pr "\n")
and display_str ind (VarStrExp path) =
(indent ind;
display_nameOfPath path;
pr "\n")
| display_str ind (BaseStrExp (dec as DecRef set)) =
(indent ind;
if ModuleName.isEmpty set
then
pr ".\n"
else
( pr "BaseStrExp\n";
display_dec (ind + 2) dec))
| display_str ind (BaseStrExp dec) =
(indent ind;
pr "BaseStrExp\n";
display_dec (ind + 2) dec)
| display_str ind (AppStrExp (id, strexp)) =
(indent ind;
pr "AppStrExp\n";
indent (ind + 2);
display_nameOf id; pr "\n";
display_str (ind + 2) strexp)
| display_str ind (LetStrExp (dec, strexp)) =
(indent ind;
pr "DecStrExp\n";
display_dec (ind + 2) dec;
display_str (ind + 2) strexp)
| display_str ind (AugStrExp (strexp, set)) =
(indent ind;
pr "AugStrExp\n";
display_str (ind + 2) strexp;
indent (ind + 2);
pr (ModuleName.setToString set);
pr "\n")
| display_str ind (ConStrExp (strexp1, strexp2)) =
(indent ind;
pr "ConStrExp\n";
display_str (ind + 2) strexp1;
display_str (ind + 2) strexp2)
in display_dec 0 dec
end
end
;
