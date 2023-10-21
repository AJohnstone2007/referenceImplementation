package com.sun.webkit.text;
import java.text.BreakIterator;
public class TextBreakIteratorShim {
public static final int CHARACTER_ITERATOR = TextBreakIterator.CHARACTER_ITERATOR;
public static final int WORD_ITERATOR = TextBreakIterator.WORD_ITERATOR;
public static final int LINE_ITERATOR = TextBreakIterator.LINE_ITERATOR;
public static final int SENTENCE_ITERATOR = TextBreakIterator.SENTENCE_ITERATOR;
public static final int TEXT_BREAK_FIRST = TextBreakIterator.TEXT_BREAK_FIRST;
public static final int TEXT_BREAK_LAST = TextBreakIterator.TEXT_BREAK_LAST;
public static final int TEXT_BREAK_NEXT = TextBreakIterator.TEXT_BREAK_NEXT;
public static final int TEXT_BREAK_PREVIOUS = TextBreakIterator.TEXT_BREAK_PRECEDING;
public static final int TEXT_BREAK_CURRENT = TextBreakIterator.TEXT_BREAK_CURRENT;
public static final int TEXT_BREAK_PRECEDING = TextBreakIterator.TEXT_BREAK_PRECEDING;
public static final int TEXT_BREAK_FOLLOWING = TextBreakIterator.TEXT_BREAK_FOLLOWING;
public static final int IS_TEXT_BREAK = TextBreakIterator.IS_TEXT_BREAK;
public static final int IS_WORD_TEXT_BREAK = TextBreakIterator.IS_WORD_TEXT_BREAK;
public static BreakIterator getIterator(
int type,
String localeName,
String text,
boolean create) {
return TextBreakIterator.getIterator(type, localeName, text, create);
}
public static int invokeMethod(BreakIterator iterator, int method, int pos) {
return TextBreakIterator.invokeMethod(iterator, method, pos);
}
}
