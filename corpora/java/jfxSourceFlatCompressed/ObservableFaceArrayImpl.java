package com.sun.javafx.scene.shape;
import com.sun.javafx.collections.ObservableIntegerArrayImpl;
import javafx.scene.shape.ObservableFaceArray;
public class ObservableFaceArrayImpl extends ObservableIntegerArrayImpl implements ObservableFaceArray {
public ObservableFaceArrayImpl() {
}
public ObservableFaceArrayImpl(int... elements) {
super(elements);
}
public ObservableFaceArrayImpl(ObservableFaceArray src) {
super(src);
}
}
