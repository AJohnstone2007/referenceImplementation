require "../system/__time";
require "date";
require "__string";
require "__pre_string_cvt";
require "__pre_basis";
require "__int";
structure Date : DATE =
struct
val env = MLWorks.Internal.Runtime.environment
datatype weekday = Mon | Tue | Wed | Thu | Fri | Sat | Sun
datatype month = Jan | Feb | Mar | Apr | May | Jun
| Jul | Aug | Sep | Oct | Nov | Dec
datatype date = DATE of
{
year : int,
month : month,
day : int,
hour : int,
minute : int,
second : int,
wday : weekday,
yday : int,
offset : Time.time option,
isDst : bool option
}
exception Date
val dateRef : exn ref = env "Date.Date"
val _ = dateRef := Date
fun year (DATE{year, ...}) = year
fun month (DATE{month, ...}) = month
fun day (DATE{day, ...}) = day
fun hour (DATE{hour, ...}) = hour
fun minute (DATE{minute, ...}) = minute
fun second (DATE{second, ...}) = second
fun weekDay (DATE{wday, ...}) = wday
fun yearDay (DATE{yday, ...}) = yday
fun isDst (DATE{isDst, ...}) = isDst
fun offset (DATE{offset, ...}) = offset
local
fun leapYear year =
(year mod 4 = 0 andalso year mod 100 <> 0) orelse
year mod 400 = 0
fun dayOfWeek 0 = Mon
| dayOfWeek 1 = Tue
| dayOfWeek 2 = Wed
| dayOfWeek 3 = Thu
| dayOfWeek 4 = Fri
| dayOfWeek 5 = Sat
| dayOfWeek 6 = Sun
| dayOfWeek _ = raise Fail "dayOfWeek"
fun makeOffset NONE = (NONE, 0)
| makeOffset (SOME offset) =
let
val secs = Time.toSeconds offset
val offsetSecs = secs mod (60 * 60 * 24)
val excessSecs = secs - offsetSecs
val offset' = Time.fromSeconds offsetSecs
in
(SOME offset', excessSecs)
end
fun dayToYear (day, year) =
if year <= 0 then
raise Date
else
let
val priorYears = year - 1
val absYear =
365 * priorYears +
priorYears div 4 -
priorYears div 100 +
priorYears div 400
val absDay = Int.toLarge absYear + day
val weekDay = dayOfWeek (Int.fromLarge((absDay-1) mod 7))
val oneYear = 365
val fourYears = 4 * oneYear + 1
val oneHundredYears = 25 * fourYears - 1
val fourHundredYears = 4 * oneHundredYears + 1
val d0 = absDay - 1
val n400 = d0 div fourHundredYears
val d1 = d0 mod fourHundredYears
val n100 = d1 div oneHundredYears
val d2 = d1 mod oneHundredYears
val n4 = d2 div fourYears
val d3 = d2 mod fourYears
val n1 = d3 div oneYear
val day' = d3 mod oneYear + 1
val year' = n400 * 400 + n100 * 100 + n4 * 4 + n1
in
(weekDay, Int.fromLarge day', Int.fromLarge(year' + 1))
end
val months =
[(Jan,31),
(Feb,28),
(Mar,31),
(Apr,30),
(May,31),
(Jun,30),
(Jul,31),
(Aug,31),
(Sep,30),
(Oct,31),
(Nov,30),
(Dec,31)]
val leapMonths =
[(Jan,31),
(Feb,29),
(Mar,31),
(Apr,30),
(May,31),
(Jun,30),
(Jul,31),
(Aug,31),
(Sep,30),
(Oct,31),
(Nov,30),
(Dec,31)]
fun dayToMonth' (day, []) =
raise Fail ("dayToMonth excess="^(Int.toString day))
| dayToMonth' (day, (month,length) :: l) =
if day <= length then
(day, month)
else
dayToMonth' (day - length, l)
fun dayToMonth (day, year) =
if leapYear year then
dayToMonth' (day, leapMonths)
else
dayToMonth' (day, months)
fun mkYearDay' (month, day, []) = raise Fail "yearDay'"
| mkYearDay' (month, day, (month', length) :: l) =
if month = month' then
day
else
mkYearDay' (month, day + length, l)
fun mkYearDay (year, month, day) =
if leapYear year then
mkYearDay' (month, day, leapMonths)
else
mkYearDay' (month, day, months)
in
fun date {year, month, day, hour, minute, second, offset} =
let
val (offset', excessSecs) = makeOffset offset
val yday = mkYearDay (year, month, day)
val second' = Int.toLarge second + excessSecs
val (second'', adjMinute) = (Int.fromLarge(second' mod 60),
Int.toLarge minute + second' div 60)
val (minute', adjHour) = (Int.fromLarge(adjMinute mod 60),
Int.toLarge hour + adjMinute div 60)
val (hour', adjDay) = (Int.fromLarge(adjHour mod 24), Int.toLarge yday + adjHour div 24)
val (wday', yday', year') = dayToYear (adjDay, year)
val (day', month') = dayToMonth (yday', year')
in
DATE
{year = year', month = month', day = day',
hour = hour' , minute = minute', second = second'',
wday = wday', yday = yday',
offset = offset', isDst = NONE}
end
end
val localOffset : unit -> Time.time = env "Date.localOffset"
val fromTimeLocal : Time.time -> date = env "Date.fromTimeLocal"
val fromTimeUniv : Time.time -> date = env "Date.fromTimeUniv"
val toTime : date -> Time.time = env "Date.toTime"
val toString : date -> string = env "Date.toString"
local
fun ignore_space((s, i, size)) =
if i >= size then
i
else
let
val ch = String.sub(s, i)
in
if PreBasis.isSpace ch then ignore_space(s, i+1, size) else i
end
val ignore_space = fn (s, i) => ignore_space(s, i, size s)
fun check_space(s, i) =
if i >= size s orelse String.sub(s, i) <> #" " then raise Date else ()
fun three_char_string(s, i) = String.substring(s, i, 3)
fun day_of_string "Mon" = Mon
| day_of_string "Tue" = Tue
| day_of_string "Wed" = Wed
| day_of_string "Thu" = Thu
| day_of_string "Fri" = Fri
| day_of_string "Sat" = Sat
| day_of_string "Sun" = Sun
| day_of_string _ = raise Date
fun mon_of_string "Jan" = Jan
| mon_of_string "Feb" = Feb
| mon_of_string "Mar" = Mar
| mon_of_string "Apr" = Apr
| mon_of_string "May" = May
| mon_of_string "Jun" = Jun
| mon_of_string "Jul" = Jul
| mon_of_string "Aug" = Aug
| mon_of_string "Sep" = Sep
| mon_of_string "Oct" = Oct
| mon_of_string "Nov" = Nov
| mon_of_string "Dec" = Dec
| mon_of_string _ = raise Date
fun digit_to_int #"0" = 0
| digit_to_int #"1" = 1
| digit_to_int #"2" = 2
| digit_to_int #"3" = 3
| digit_to_int #"4" = 4
| digit_to_int #"5" = 5
| digit_to_int #"6" = 6
| digit_to_int #"7" = 7
| digit_to_int #"8" = 8
| digit_to_int #"9" = 9
| digit_to_int _ = raise Date
fun read_mon_day(s, i) =
let
val ch = String.sub(s, i)
val ch' = String.sub(s, i+1)
val d = digit_to_int ch'
in
if ch = #" " then d else d + 10*digit_to_int ch
end
fun read_two_digits(s, i) =
if i+1 >= size s then
raise Date
else
10*digit_to_int(String.sub(s, i)) + digit_to_int(String.sub(s, i+1))
fun check_colon(s, i) =
if i >= size s orelse String.sub(s, i) <> #":" then raise Date else ()
fun check_size(s, i) = if i + 24 <= size s then () else raise Date
in
fun fromString s =
let
val i = ignore_space(s, 0)
val _ = check_size(s, i)
val wday = three_char_string(s, i)
val _ = check_space(s, i+3)
val mon = three_char_string(s, i+4)
val month = mon_of_string mon
val _ = check_space(s, i+7)
val mon_day = read_mon_day(s, i+8)
val _ = check_space(s, i+10)
val hour = read_two_digits(s, i+11)
val _ = check_colon(s, i+13)
val min = read_two_digits(s, i+14)
val _ = check_colon(s, i+16)
val sec = read_two_digits(s, i+17)
val _ = check_space(s, i+19)
val hi_year = read_two_digits(s, i+20)
val lo_year = read_two_digits(s, i+22)
val year = if hi_year <19 then
raise Date
else
hi_year*100+lo_year
in
SOME
(date{year = year, month = month, day = mon_day,
hour = hour, minute = min, second = sec,
offset = NONE})
end
handle Date => NONE
end
fun scan getc stream =
let
val orig_stream = stream
val stream = PreStringCvt.skipWS getc stream
in
case PreStringCvt.getNChar 24 getc stream of
NONE => NONE
| SOME(charlist, stream) =>
let val string = implode charlist
in
case fromString string of
SOME date => SOME (date, stream)
| NONE => NONE
end handle Date => NONE
end
val uncurry_fmt = env "Date.fmt": string * date -> string
fun fmt string date = uncurry_fmt(string, date)
local
fun number_order(n : int, n') =
if n < n' then LESS
else
if n > n' then GREATER
else EQUAL
fun month_number Jan = 0
| month_number Feb = 1
| month_number Mar = 2
| month_number Apr = 3
| month_number May = 4
| month_number Jun = 5
| month_number Jul = 6
| month_number Aug = 7
| month_number Sep = 8
| month_number Oct = 9
| month_number Nov = 10
| month_number Dec = 11
fun month_order(m, m') = number_order(month_number m, month_number m')
in
fun compare
(DATE{year,
month,
day,
hour,
minute,
second,
...},
DATE{year=year',
month=month',
day=day',
hour=hour',
minute=minute',
second=second',
...}) =
if year < year' then
LESS
else
if year > year' then
GREATER
else
let
val order = month_order(month, month')
in
if order = EQUAL then
if day < day' then
LESS
else
if day > day' then
GREATER
else
if hour < hour' then
LESS
else
if hour > hour' then
GREATER
else
if minute < minute' then
LESS
else
if minute > minute' then
GREATER
else
number_order(second, second')
else
order
end
end
end
;
