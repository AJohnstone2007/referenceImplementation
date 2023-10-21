Shell.Options.set(Shell.Options.Language.oldDefinition, true);
signature s = sig type T; val true: T end;
structure ss:s = struct datatype T = true end;
local open ss
in
val bad = if 1=2 then 3 else 4
end
;
