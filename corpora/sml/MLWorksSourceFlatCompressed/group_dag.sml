require "__ordered_set";
require "module_dec";
require "module_name";
require "import_export";
require "../basics/module_id";
signature GROUP_DAG = sig
structure ModuleName: MODULE_NAME
structure ModuleDec : MODULE_DEC
structure ImportExport: IMPORT_EXPORT
structure ModuleId: MODULE_ID;
sharing ModuleDec = ImportExport.ModuleDec
and ModuleName = ImportExport.ModuleName = ModuleDec.ModuleName
type sml_source = ModuleId.ModuleId
exception MultipleDefinitions of string * string * string
and Cycle of sml_source * (sml_source * ModuleName.t) list
and IllegalToplevelOpen of string
and GroupDagInternalError
datatype 'ext_info dag =
DAG of {
seq_no: int,
marked: bool ref,
smlsource: sml_source,
symmap: ModuleName.t -> ImportExport.env,
intern: 'ext_info dag OrderedSet.set,
extern: 'ext_info
}
val analyze:
{
union_dag: 'info dag OrderedSet.set * 'info dag OrderedSet.set
-> 'info dag OrderedSet.set,
smlsources: (sml_source * ModuleDec.Dec * bool) list,
enone: 'info,
eglob: ModuleName.t -> ImportExport.env * 'info,
ecombine: 'info * 'info -> 'info,
seq_no: int ref
}
->
(ModuleName.set * 'info dag) list
end
;
