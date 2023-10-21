signature OBJECT_OUTPUT =
sig
datatype OUTPUT_TYPE = ASM | BINARY
type Opcode
type Module
type ModuleId
type Project
val output_object_code :
(OUTPUT_TYPE * ModuleId * string * Project) ->
Module ->
unit
end
;
