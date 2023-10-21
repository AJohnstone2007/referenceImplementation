signature PERM =
sig
val perm : 'a list -> 'a list list
val permString : string -> string list
val partition : 'a list -> 'a list list list
val partitionString : string -> string list list
end
;
