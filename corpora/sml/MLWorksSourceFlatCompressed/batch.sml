require "../utils/mlworks_exit";
signature BATCH =
sig
structure Exit : MLWORKS_EXIT
val obey : string list -> Exit.status
end;
