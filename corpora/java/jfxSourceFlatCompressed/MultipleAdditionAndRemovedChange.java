package com.sun.javafx.scene.control;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import java.util.Collections;
import java.util.List;
public class MultipleAdditionAndRemovedChange<E> extends ListChangeListener.Change<E> {
private static final int[] EMPTY_PERM = new int[0];
private boolean invalid = true;
private final List<E> addedElements;
private final List<E> removedElements;
private boolean iteratingThroughAdded = true;
private boolean returnedRemovedElements = false;
private int addedIndex = 0;
private int from;
private int to;
public MultipleAdditionAndRemovedChange(List<E> addedElements, List<E> removedElements, ObservableList<E> list) {
super(list);
this.addedElements = addedElements;
this.removedElements = removedElements;
}
@Override public boolean next() {
if (invalid) {
invalid = false;
}
if (addedIndex < addedElements.size()) {
from = getList().indexOf(addedElements.get(addedIndex));
to = from + 1;
addedIndex++;
for (int i = 0; (i + addedIndex) < addedElements.size(); i++) {
E nextElement = addedElements.get(i + addedIndex);
if (nextElement != getList().get(from + i)) {
to = from + 1 + i;
addedIndex = addedIndex + i;
break;
}
}
return true;
} else if (!returnedRemovedElements) {
returnedRemovedElements = true;
iteratingThroughAdded = false;
from = 0;
to = 0;
return !removedElements.isEmpty();
}
return false;
}
@Override public void reset() {
invalid = true;
from = 0;
to = 0;
addedIndex = 0;
iteratingThroughAdded = true;
returnedRemovedElements = false;
}
@Override public int getFrom() {
checkState();
return from;
}
@Override public int getTo() {
checkState();
return to;
}
@Override public List<E> getRemoved() {
return iteratingThroughAdded ? Collections.<E>emptyList() : removedElements;
}
@Override protected int[] getPermutation() {
return EMPTY_PERM;
}
@Override public boolean wasAdded() {
return iteratingThroughAdded && !addedElements.isEmpty();
}
@Override public boolean wasRemoved() {
return !iteratingThroughAdded && !removedElements.isEmpty();
}
private void checkState() {
if (invalid) {
throw new IllegalStateException("Invalid Change state: next() must be called before inspecting the Change.");
}
}
}
