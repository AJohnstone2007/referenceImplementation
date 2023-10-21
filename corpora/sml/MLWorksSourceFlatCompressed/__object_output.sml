require "__sparc_assembly";
require "../main/__code_module";
require "../main/__project";
require "../basics/__module_id";
structure Object_Output_ =
struct
datatype OUTPUT_TYPE = ASM | BINARY
type Opcode = Sparc_Assembly_.opcode
type Module = Code_Module_.Module
type ModuleId = ModuleId_.ModuleId
type Project = Project_.Project
fun output_object_code _ _ = ()
end
;
