package com.sun.javafx.binding;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableLongValue;
public final class LongConstant implements ObservableLongValue {
private final long value;
private LongConstant(long value) {
this.value = value;
}
public static LongConstant valueOf(long value) {
return new LongConstant(value);
}
@Override
public long get() {
return value;
}
@Override
public Long getValue() {
return value;
}
@Override
public void addListener(InvalidationListener observer) {
}
@Override
public void addListener(ChangeListener<? super Number> observer) {
}
@Override
public void removeListener(InvalidationListener observer) {
}
@Override
public void removeListener(ChangeListener<? super Number> observer) {
}
@Override
public int intValue() {
return (int) value;
}
@Override
public long longValue() {
return value;
}
@Override
public float floatValue() {
return value;
}
@Override
public double doubleValue() {
return value;
}
}
