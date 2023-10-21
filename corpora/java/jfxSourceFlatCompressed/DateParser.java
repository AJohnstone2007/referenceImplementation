package com.sun.webkit.network;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
final class DateParser {
private static final PlatformLogger logger =
PlatformLogger.getLogger(DateParser.class.getName());
private static final Pattern DELIMITER_PATTERN = Pattern.compile(
"[\\x09\\x20-\\x2F\\x3B-\\x40\\x5B-\\x60\\x7B-\\x7E]+");
private static final Pattern TIME_PATTERN = Pattern.compile(
"(\\d{1,2}):(\\d{1,2}):(\\d{1,2})(?:[^\\d].*)*");
private static final Pattern DAY_OF_MONTH_PATTERN = Pattern.compile(
"(\\d{1,2})(?:[^\\d].*)*");
private static final Pattern YEAR_PATTERN = Pattern.compile(
"(\\d{2,4})(?:[^\\d].*)*");
private static final Map<String, Integer> MONTH_MAP = Map.ofEntries(
Map.entry("jan", 0),
Map.entry("feb", 1),
Map.entry("mar", 2),
Map.entry("apr", 3),
Map.entry("may", 4),
Map.entry("jun", 5),
Map.entry("jul", 6),
Map.entry("aug", 7),
Map.entry("sep", 8),
Map.entry("oct", 9),
Map.entry("nov", 10),
Map.entry("dec", 11));
private DateParser() {
throw new AssertionError();
}
static long parse(String date) throws ParseException {
logger.finest("date: [{0}]", date);
Time time = null;
Integer dayOfMonth = null;
Integer month = null;
Integer year = null;
String[] tokens = DELIMITER_PATTERN.split(date, 0);
for (String token : tokens) {
if (token.length() == 0) {
continue;
}
Time timeTmp;
if (time == null && (timeTmp = parseTime(token)) != null) {
time = timeTmp;
continue;
}
Integer dayOfMonthTmp;
if (dayOfMonth == null
&& (dayOfMonthTmp = parseDayOfMonth(token)) != null)
{
dayOfMonth = dayOfMonthTmp;
continue;
}
Integer monthTmp;
if (month == null && (monthTmp = parseMonth(token)) != null) {
month = monthTmp;
continue;
}
Integer yearTmp;
if (year == null && (yearTmp = parseYear(token)) != null) {
year = yearTmp;
continue;
}
}
if (year != null) {
if (year >= 70 && year <= 99) {
year += 1900;
} else if (year >= 0 && year <= 69) {
year += 2000;
}
}
if (time == null || dayOfMonth == null || month == null || year == null
|| dayOfMonth < 1 || dayOfMonth > 31
|| year < 1601
|| time.hour > 23
|| time.minute > 59
|| time.second > 59)
{
throw new ParseException("Error parsing date", 0);
}
Calendar calendar = Calendar.getInstance(
TimeZone.getTimeZone("UTC"), Locale.US);
calendar.setLenient(false);
calendar.clear();
calendar.set(year, month, dayOfMonth,
time.hour, time.minute, time.second);
try {
long result = calendar.getTimeInMillis();
if (logger.isLoggable(Level.FINEST)) {
logger.finest("result: [{0}]", new Date(result).toString());
}
return result;
} catch (Exception ex) {
ParseException pe = new ParseException("Error parsing date", 0);
pe.initCause(ex);
throw pe;
}
}
private static Time parseTime(String token) {
Matcher matcher = TIME_PATTERN.matcher(token);
if (matcher.matches()) {
return new Time(
Integer.parseInt(matcher.group(1)),
Integer.parseInt(matcher.group(2)),
Integer.parseInt(matcher.group(3)));
} else {
return null;
}
}
private static final class Time {
private final int hour;
private final int minute;
private final int second;
private Time(int hour, int minute, int second) {
this.hour = hour;
this.minute = minute;
this.second = second;
}
}
private static Integer parseDayOfMonth(String token) {
Matcher matcher = DAY_OF_MONTH_PATTERN.matcher(token);
if (matcher.matches()) {
return Integer.parseInt(matcher.group(1));
} else {
return null;
}
}
private static Integer parseMonth(String token) {
if (token.length() >= 3) {
return MONTH_MAP.get(token.substring(0, 3).toLowerCase());
} else {
return null;
}
}
private static Integer parseYear(String token) {
Matcher matcher = YEAR_PATTERN.matcher(token);
if (matcher.matches()) {
return Integer.parseInt(matcher.group(1));
} else {
return null;
}
}
}
