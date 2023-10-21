package com.sun.javafx.binding;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableFloatValue;
public final class FloatConstant implements ObservableFloatValue {
private final float value;
private FloatConstant(float value) {
this.value = value;
}
public static FloatConstant valueOf(float value) {
return new FloatConstant(value);
}
@Override
public float get() {
return value;
}
@Override
public Float getValue() {
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
return (int) value;
}
@Override
public long longValue() {
return (long) value;
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
