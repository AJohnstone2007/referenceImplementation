package com.sun.javafx.css;
import java.util.Collection;
import java.util.Iterator;
public class BitSetShim {
public static boolean add(BitSet s, Object t) {
return s.add(t);
}
public static boolean addAll(BitSet s, Collection c) {
return s.addAll(c);
}
public static boolean contains(BitSet s, Object o) {
return s.contains(o);
}
public static boolean containsAll(BitSet s, Collection<?> c) {
return s.containsAll(c);
}
public static boolean equals(BitSet s, Object obj) {
return s.equals(obj);
}
public static long[] getBits(BitSet s) {
return s.getBits();
}
public static boolean isEmpty(BitSet s) {
return s.isEmpty();
}
public static Iterator iterator(BitSet s) {
return s.iterator();
}
public static boolean remove(BitSet s, Object o) {
return s.remove(o);
}
public static boolean retainAll(BitSet s, Collection<?> c) {
return s.retainAll(c);
}
public static int size(BitSet s) {
return s.size();
}
}
