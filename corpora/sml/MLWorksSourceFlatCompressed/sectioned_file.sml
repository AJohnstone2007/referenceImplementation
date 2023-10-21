signature SECTIONED_FILE =
sig
type item = string
type name = string
type path = name list
type file_stamp = string
type section
type descendent = section * path
type subsection = section
exception InvalidPath
exception InvalidSectionedFile of string
val readSectionedFile: string -> file_stamp * section
val writeSectionedFile: string * file_stamp * section -> unit
val createSection: name * subsection list * item list -> section
val getName: section -> name
val getItems: section -> item list
val getSubsections: section -> subsection list
val getDescendent: descendent -> subsection
val addSubsection: section -> subsection -> section
val removeSubsection: section -> name -> section * bool
val addItem: section -> item -> section
val filterItems: section -> (item -> bool) -> section
val replaceDescendent: descendent -> subsection -> section
end;
