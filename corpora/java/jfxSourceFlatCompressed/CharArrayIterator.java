package com.sun.javafx.text;
import java.text.CharacterIterator;
class CharArrayIterator implements CharacterIterator {
private char[] chars;
private int pos;
private int begin;
public CharArrayIterator(char[] chars) {
reset(chars, 0);
}
public CharArrayIterator(char[] chars, int begin) {
reset(chars, begin);
}
public char first() {
pos = 0;
return current();
}
public char last() {
if (chars.length > 0) {
pos = chars.length-1;
}
else {
pos = 0;
}
return current();
}
public char current() {
if (pos >= 0 && pos < chars.length) {
return chars[pos];
}
else {
return DONE;
}
}
public char next() {
if (pos < chars.length-1) {
pos++;
return chars[pos];
}
else {
pos = chars.length;
return DONE;
}
}
public char previous() {
if (pos > 0) {
pos--;
return chars[pos];
}
else {
pos = 0;
return DONE;
}
}
public char setIndex(int position) {
position -= begin;
if (position < 0 || position > chars.length) {
throw new IllegalArgumentException("Invalid index");
}
pos = position;
return current();
}
public int getBeginIndex() {
return begin;
}
public int getEndIndex() {
return begin+chars.length;
}
public int getIndex() {
return begin+pos;
}
public Object clone() {
CharArrayIterator c = new CharArrayIterator(chars, begin);
c.pos = this.pos;
return c;
}
void reset(char[] chars) {
reset(chars, 0);
}
void reset(char[] chars, int begin) {
this.chars = chars;
this.begin = begin;
pos = 0;
}
}
