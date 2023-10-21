package com.sun.webkit.network;
import java.text.ParseException;
public class DateParserShim {
public static long parse(String date) throws ParseException {
return DateParser.parse(date);
}
}
