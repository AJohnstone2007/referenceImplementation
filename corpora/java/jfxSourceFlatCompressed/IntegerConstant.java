package com.sun.javafx.binding;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableIntegerValue;
public final class IntegerConstant implements ObservableIntegerValue {
private final int value;
private IntegerConstant(int value) {
this.value = value;
}
public static IntegerConstant valueOf(int value) {
return new IntegerConstant(value);
}
@Override
public int get() {
return value;
}
@Override
public Integer getValue() {
return value;
}
@Override
public void addListener(InvalidationListener observer) {
}
@Override
public void addListener(ChangeListener<? super Number> listener) {
}
@Override
public void removeListener(InvalidationListener observer) {
}
@Override
public void removeListener(ChangeListener<? super Number> listener) {
}
@Override
public int intValue() {
return value;
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
