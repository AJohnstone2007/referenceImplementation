signature INTERNAL_TIMER =
sig
val xtime : string * bool * (unit -> 'a) -> 'a
val time_it : string * (unit -> 'a) -> 'a
end;
