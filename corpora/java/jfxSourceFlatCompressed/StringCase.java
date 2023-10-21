package com.sun.webkit.text;
import java.util.Locale;
final class StringCase {
private static String toLowerCase(String src) {
return src.toLowerCase(Locale.ROOT);
}
private static String toUpperCase(String src) {
return src.toUpperCase(Locale.ROOT);
}
private static String foldCase(String src) {
return src.toUpperCase(Locale.ROOT).toLowerCase(Locale.ROOT);
}
}
