signature FILE_SYS =
sig
exception BadHomeName of string
val expand_path: string -> string
val getdir: unit -> string
end
;
