val fromReal_a = Time.fromReal 0.0 = Time.zeroTime;
val fromReal_b = (ignore(Time.fromReal ~1.0); false) handle Time => true;
val toReal_a = Real.==(Time.toReal Time.zeroTime, 0.0);
val fromSeconds_a = Time.fromSeconds 0 = Time.zeroTime;
val fromSeconds_b = (ignore(Time.fromSeconds ~1); false) handle Time => true;
val toSeconds_a = Time.toSeconds Time.zeroTime = 0;
val toSeconds_b = (ignore(Time.toSeconds (Time.fromReal 2147483648.0)); false) handle Overflow => true | Time.Time => true;
val fromMilliseconds_a = Time.fromMilliseconds 0 = Time.zeroTime;
val fromMilliseconds_b = (ignore(Time.fromMilliseconds ~1); false) handle Time => true;
val toMilliseconds_a = Time.toMilliseconds Time.zeroTime = 0;
val fromMicroseconds_a = Time.fromMicroseconds 0 = Time.zeroTime;
val fromMicroseconds_b = (ignore(Time.fromMicroseconds ~1); false) handle Time => true;
val toMicroseconds_a = Time.toMicroseconds Time.zeroTime = 0;
val plus_a = Time.fromSeconds 10 = Time.+ (Time.fromSeconds 5, Time.fromSeconds 5);
local
val now = Time.now ()
in
val plus_b = now = Time.+ (now, Time.zeroTime);
end
val sub_a = Time.zeroTime = Time.- (Time.fromSeconds 5, Time.fromSeconds 5);
val sub_b = Time.-(Time.fromReal 19.5, Time.fromReal 18.6) = Time.fromReal 0.9;
val sub_c = (ignore(Time.-(Time.fromSeconds 5, Time.fromSeconds 10)); false) handle Time => true;
val sub_d = (ignore(Time.-(Time.fromReal 19.5, Time.fromReal 19.6)); false) handle Time => true;
val leq_a = Time.<= (Time.zeroTime, Time.zeroTime);
val leq_b = Time.<= (Time.zeroTime, Time.fromSeconds 10);
val leq_c = Time.<= (Time.fromSeconds 9, Time.fromSeconds 10);
val leq_d = Time.<= (valOf (Time.fromString "844170614.164"), valOf (Time.fromString "844170614.589"));
val leq_e = Time.<= (valOf (Time.fromString "844170614.589"), valOf (Time.fromString "844170614.589"));
val geq_a = Time.>= (Time.zeroTime, Time.zeroTime);
val geq_b = Time.>= (Time.fromSeconds 10, Time.zeroTime);
val geq_c = Time.>= (Time.fromSeconds 10, Time.fromSeconds 9);
val geq_e = Time.>= (valOf (Time.fromString "844170614.164"), valOf (Time.fromString "844170614.164"));
val geq_d = Time.>= (valOf (Time.fromString "844170614.589"), valOf (Time.fromString "844170614.164"));
val lt_a = not (Time.< (Time.zeroTime, Time.zeroTime));
val lt_b = Time.< (Time.fromSeconds 9, Time.fromSeconds 10);
val lt_c = not (Time.< (Time.fromSeconds 10, Time.fromSeconds 10));
val lt_d = not (Time.< (Time.fromSeconds 10, Time.fromSeconds 9));
val lt_e = Time.< (valOf (Time.fromString "844170614.164"), valOf (Time.fromString "844170614.589"));
val gt_a = not (Time.> (Time.zeroTime, Time.zeroTime));
val gt_b = Time.> (Time.fromSeconds 10, Time.fromSeconds 9);
val gt_c = not (Time.> (Time.fromSeconds 10, Time.fromSeconds 10));
val gt_d = not (Time.> (Time.fromSeconds 9, Time.fromSeconds 10));
val gt_e = Time.> (valOf (Time.fromString "844170614.589"), valOf (Time.fromString "844170614.164"));
val toString_a = Time.toString (Time.fromReal 1.1234) = "1.123";
val toString_b = Time.toString (Time.fromReal 1.1235) = "1.123";
val toString_c = Time.toString (Time.fromReal 1.1236) = "1.124";
val toString_d = Time.toString (Time.fromReal 0.0) = "0.000";
val toString_e = Time.toString (Time.fromReal 1.1) = "1.100";
val fmt_a = Time.fmt 3 (Time.fromReal 1.1234) = "1.123";
val fmt_b = Time.fmt 3 (Time.fromReal 1.1235) = "1.123";
val fmt_c = Time.fmt 3 (Time.fromReal 1.1236) = "1.124";
val fmt_d = Time.fmt 3 (Time.fromReal 0.0) = "0.000";
val fmt_e = Time.fmt 3 (Time.fromReal 1.1) = "1.100";
val fmt_f = Time.fmt 0 (Time.fromReal 1.1234) = "1";
val fmt_g = Time.fmt 3 (Time.fromReal 1.8) = "1.800";
val fmt_h = Time.fmt 0 (Time.fromReal 1.8) = "2";
val fmt_h = Time.fmt ~1 (Time.fromReal 1.8) = "2";
local
fun invalid NONE = true
| invalid _ = false
fun eq (NONE, ans) = false
| eq (SOME t, ans) = (Time.toString t) = ans
in
val fromString_a = eq (Time.fromString "1", "1.000");
val fromString_b = eq (Time.fromString "1.1234", "1.123");
val fromString_c = invalid (Time.fromString "~1.1234");
val fromString_d = eq (Time.fromString "  1", "1.000");
val fromString_e = eq (Time.fromString "1.", "1.000");
val fromString_f = eq (Time.fromString " \n\t189.125125crap", "189.125");
val fromString_g = invalid (Time.fromString ".1234");
end
;
