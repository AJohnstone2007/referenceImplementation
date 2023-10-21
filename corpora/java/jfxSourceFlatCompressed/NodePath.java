package com.sun.javafx.sg.prism;
import java.util.ArrayList;
import java.util.List;
public class NodePath {
private List<NGNode> path = new ArrayList<>();
private int position;
public NGNode last() {
return path.isEmpty() ? null : path.get(path.size() - 1);
}
public NGNode getCurrentNode() {
return path.get(position);
}
public boolean hasNext() {
return position < path.size() -1 && !isEmpty();
}
public void next() {
if (!hasNext()) {
throw new IllegalStateException();
}
position++;
}
public void reset() {
position = path.isEmpty() ? -1 : 0;
}
public void clear() {
position = -1;
path.clear();
}
public void add(NGNode n) {
path.add(0, n);
if (position == -1) position = 0;
}
public int size() {
return path.size();
}
public boolean isEmpty() {
return path.isEmpty();
}
@Override public String toString() {
return path.toString();
}
}
