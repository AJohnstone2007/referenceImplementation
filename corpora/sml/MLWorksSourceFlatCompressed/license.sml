signature LICENSE =
sig
val license: (string -> bool option) -> bool option
val ttyComplain: string -> bool option
end
;
