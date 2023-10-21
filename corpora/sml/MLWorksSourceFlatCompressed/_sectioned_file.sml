require "^.basis.__int";
require "^.basis.__list";
require "^.basis.__char";
require "^.basis.__string";
require "^.basis.__substring";
require "^.basis.os";
require "^.basis.text_io";
require "sectioned_file";
functor SectionedFile (
structure TextIO: TEXT_IO
structure OS: OS
) : SECTIONED_FILE =
struct
type item = string
type name = string
type path = name list
type file_stamp = string
datatype section =
Section of {name : name,
size : int ref,
subsections : subsection list,
items : item list }
withtype subsection = section
type descendent = section * path
exception InvalidPath
exception InvalidSectionedFile of string;
fun eq_section_name name (Section{name=name', ...}) = name = name'
fun cvt_int (section_name, token_name, token) =
case Int.fromString token of
NONE => raise InvalidSectionedFile
( "Incorrect format for " ^ token_name
^ " in section " ^ section_name )
| SOME int => int;
fun parse_section_header header =
let val tokens = String.tokens Char.isSpace header
in case tokens of
[] => raise InvalidSectionedFile "Missing section header"
| [name] =>
raise InvalidSectionedFile
("Missing section size for section " ^ name)
| [name, size] => (name, cvt_int(name, "size", size), 0)
| [name, size, subsections] =>
(name, cvt_int(name, "size", size),
cvt_int(name, "subsection count", subsections))
| name :: _ =>
raise InvalidSectionedFile
("Too many tokens in section header for " ^ name)
end;
fun readSectionedFile filename =
let val instream = TextIO.openIn filename
fun get_line() =
let val line = TextIO.inputLine instream
in if line = ""
then raise (InvalidSectionedFile "Premature EOF")
else line end
fun read_section () =
let val header = get_line()
val (name, size, subcount) = parse_section_header header
val (subs, subsize) = read_sections subcount
val items = read_items (size - subsize)
in (Section{name = name, size = ref 0,
subsections = subs, items = items},
size + 1 )
end
and read_sections 0 = ([], 0)
| read_sections i =
let val (section, size) = read_section ()
val (rest, rest_size) = read_sections (i - 1)
in (section :: rest, size + rest_size)
end
and read_items 0 = []
| read_items n =
let val line = Substring.all (get_line())
val (_, line) = Substring.splitl Char.isSpace line
val (line, _) = Substring.splitr Char.isSpace line
val item =
getOpt(String.fromString(Substring.string line),"")
in item :: (read_items (n - 1)) end
val stamp = getOpt(String.fromString(TextIO.inputLine instream),"")
val (section, _) = read_section()
in (TextIO.closeIn instream; (stamp, section))
handle exn => (TextIO.closeIn instream; raise exn)
end
fun update_sizes (Section{name, size, subsections, items}) =
( app update_sizes subsections;
size :=
1 + foldl (fn (Section{size=ref s, ...}, sofar) => sofar + s)
(List.length items) subsections );
fun writeSectionedFile(filename, stamp, section) =
let val _ =
OS.FileSys.remove filename handle _ => ()
val outstream = TextIO.openOut filename
fun indent 0 = ()
| indent level =
(TextIO.output(outstream, "  "); indent(level - 1))
fun write_section level
(Section {name,size=ref sz,subsections,items}) =
( indent level;
TextIO.output(outstream,
name ^ " " ^ (Int.toString (sz-1))
^ " " ^ (Int.toString(length(subsections))) ^ "\n");
app (write_section (level + 1)) subsections;
app (write_item level) items )
and write_item level string =
( indent (level + 1);
TextIO.output (outstream, (String.toString string) ^ "\n") )
in update_sizes section;
TextIO.output (outstream, (String.toString stamp) ^ "\n");
write_section 0 section;
TextIO.closeOut outstream
end;
fun createSection (name: name, subsections : subsection list, items : item list) =
Section{name = name, size = ref 0, subsections = subsections, items = items}
fun getName (Section{name, ...}) = name;
fun getItems (Section{items, ...}) = items;
fun getSubsections (Section{subsections, ...}) = subsections;
fun getDescendent (section, []) = section
| getDescendent (Section{subsections, ...}, h::t) =
case List.find (eq_section_name h) subsections of
NONE => raise InvalidPath
| SOME subsection => getDescendent (subsection, t);
fun addSubsection (section as Section{name, size, subsections, items})
(subsection as Section{name = subname, ...}) =
let val subsections' =
List.filter (not o (eq_section_name subname)) subsections
in Section{name = name, size = ref 0,
subsections = subsection :: subsections', items = items}
end
fun removeSubsection (section as Section{name = n, size, subsections, items})
name =
let val (removed, subsections') =
List.partition (eq_section_name name) subsections
in if null removed
then (section, false)
else (Section{name = n, size = ref 0, subsections = subsections',
items = items},
true)
end
fun addItem (Section{name, size, subsections, items}) item =
Section{name = name, size = ref 0, subsections = subsections,
items = item :: items}
fun filterItems (section as Section{name, size, subsections, items})
filter =
let val (items', removed) = List.partition filter items
in if null removed
then section
else Section{name = name, size = ref 0,
subsections = subsections, items = items'} end
fun replaceDescendent descendent replacement =
let fun replace (section, []) =
if (getName section = getName replacement)
then replacement
else raise InvalidPath
| replace (section as Section{name, size, subsections, items},
h::t) =
case List.partition (eq_section_name h) subsections of
([subsection], subsections') =>
Section{name = name, size = ref 0, items = items,
subsections =
(replace(subsection, t)) :: subsections'}
| _ => raise InvalidPath
in replace descendent end
end
;
