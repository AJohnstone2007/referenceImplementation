package com.sun.glass.ui.monocle;
public class IntSetShim extends IntSet {
@Override
public void addInt(int value) {
super.addInt(value);
}
@Override
public int size() {
return super.size();
}
@Override
public void removeInt(int value) {
super.removeInt(value);
}
@Override
public int get(int index) {
return super.get(index);
}
}
