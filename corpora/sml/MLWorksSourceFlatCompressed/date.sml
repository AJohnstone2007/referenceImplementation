local
val date_string_a = "Thu May  9 16:30:10 1996"
val date_string_b = "Thu May 09 16:30:10 1996"
val fmt_string = "%a %b %d %H:%M:%S %Y"
fun getc(s, i) =
if i >= size s then
NONE
else
SOME(Char.chr(MLWorks.String.ordof(s, i)), (s, i+1))
fun compare_dates
(msg_string,
date, date') =
if Date.compare(date, date') = EQUAL then
(print (msg_string ^ " succeeded.\n"); true)
else
let
val year = Date.year date and year' = Date.year date'
val month = Date.month date and month' = Date.month date'
val day = Date.day date and day' = Date.day date'
val hour = Date.hour date and hour' = Date.hour date'
val minute = Date.minute date and minute' = Date.minute date'
val second = Date.second date and second' = Date.second date'
val wday = Date.weekDay date and wday' = Date.weekDay date'
val yday = Date.yearDay date and yday' = Date.yearDay date'
in
(print (msg_string ^ " Error different dates:\n");
print ("  Dates are "^(Date.toString date)^
" and "^(Date.toString date')^"\n");
(if year<>year' then print"  years fail\n" else ());
(if month<>month' then print"  months fail\n" else ());
(if day<>day' then print"   days fail\n" else ());
(if hour<>hour' then print"  hours fail\n" else ());
(if minute<>minute' then print"  minutes fail\n" else ());
(if second<>second' then print"  seconds fail\n" else ());
(if wday<>wday' then print"  weekDays fail\n" else ());
(if yday<>yday' then print"  yearDays fail\n" else ());
false)
end
in
local
val date =
case Date.fromString date_string_a of
NONE => (print ("Date.fromString \""^date_string_a^"\" failed\n");
raise Match)
| SOME date => date
val date' =
let
val date_string' = MLWorks.String.substring(Date.toString date, 0, 24)
in
if date_string' = date_string_a then
let
val date' = Date.scan getc (date_string_a, 0)
in
case date' of
SOME(date', _) =>
(if compare_dates("date", date, date') then
let
val date_fmt = Date.fmt fmt_string date
in
if date_fmt = date_string_b then
print "Date fmt succeeded\n"
else
print("Date fmt returned '" ^ date_fmt ^
"', instead of '" ^ date_string_b ^ "'\n")
end
else
print "Date scan gave wrong answer\n";
date')
| NONE =>
(print "Date scan failed entirely\n";
date)
end
else
(print ("Date print failed with '" ^ date_string' ^ "'\n");
date)
end
in
end
local
val dateStart = Date.date {year= 1970, month= Date.Jan, day= 2,
hour= 0, minute= 0, second= 0,
offset = NONE}
val dateEnd = Date.date {year= 2030, month= Date.Dec, day= 31,
hour= 23, minute= 59, second= 59,
offset = NONE}
val dateStart' = Date.fromTimeLocal (Date.toTime dateStart)
val dateEnd' = Date.fromTimeLocal (Date.toTime dateEnd)
in
val _ = compare_dates("dateStart", dateStart, dateStart')
val _ = compare_dates("dateEnd", dateEnd, dateEnd')
end
local
val date1 =
Date.date{year=1900, month=Date.Jan, day=1,
hour=00, minute=00, second=00, offset=NONE}
val date2 =
Date.date{year=1899, month=Date.Dec, day=31,
hour=23, minute=59, second=60, offset=NONE}
in
val _ = print ((Date.toString date1)^"\n");
val _ = print ((Date.toString date2)^"\n");
val _ = compare_dates("Carrying over start of 1900", date1, date2)
end
local
fun isLeap year =
let
val date = Date.date{year = year, month = Date.Feb, day = 29,
hour = 12, minute = 0, second = 0, offset = NONE}
val month = Date.month date
in
month = Date.Feb
end
fun test year = if isLeap year then
print ((Int.toString year)^" is a leap year\n")
else
print ((Int.toString year)^" is not a leap year\n")
in
val _ = (test 1900; test 1984; test 1983; test 2000; test 1600)
end
val _ =
let
val date = Date.date{year=1997, month=Date.Sep, day = 23,
hour=12, minute=0, second=0, offset=NONE}
val day = Date.weekDay date
in
if day = Date.Tue then
print "Day of the week succeeded.\n"
else
print ("Day of the week failed.\n  "^(Date.toString date)^"\n")
end
val _ =
let
val date = Date.date{year=1500, month=Date.Sep, day = 23,
hour=12, minute=0, second=0,
offset=SOME (Time.zeroTime)}
val time = Date.toTime date
handle Date.Date =>
(print "Date.toTime raised exception Date ok.\n";
raise Fail "ok")
val date' = Date.fromTimeUniv time
in
if Date.compare(date, date') = EQUAL then
print "Date.toTime failed, incorrect time created\n"
else
print ("Date.toTime test failed, please change test file.\n")
end
handle Fail "ok" => ()
| _ => print "Date.ToTime raised wrong exception.\n"
val _ =
let
val now = Time.now ()
val dateLocal = Date.fromTimeLocal now
val dateUniv = Date.fromTimeUniv now
val offsetLocal = Date.offset dateLocal
val offsetUniv = Date.offset dateUniv
val timeLocal = Date.toTime dateLocal
val timeUniv = Date.toTime dateUniv
val reportedOffset = Date.localOffset ()
val measuredOffset =
Time.-(timeUniv, timeLocal)
handle Time.Time =>
Time.-(Time.+(timeUniv, Time.fromSeconds(24*60*60)), timeLocal)
in
(case offsetLocal of
NONE => print "Offset of timeLocal is correct (NONE).\n"
| SOME _ => print "Offset of timeLocal is incorrect (SOME _).\n");
(case offsetUniv of
NONE => print "offsetUniv is incorrect (NONE).\n"
| SOME t => if t=Time.zeroTime then
print "Offset of timeUniv is correct (SOME 0).\n"
else
print ("Offset of timeUniv is incorrect (SOME " ^
(Time.toString t) ^ ").\n") );
(if reportedOffset = measuredOffset then
print "localOffset matches my calculation ok.\n"
else
print ("Local offset doesn't match, measured = " ^
(Time.toString measuredOffset) ^
", reported = " ^ (Time.toString reportedOffset) ^ ".\n") )
end
end
val x = Date.fmt "" (Date.fromTimeLocal(Time.now())) = "";
