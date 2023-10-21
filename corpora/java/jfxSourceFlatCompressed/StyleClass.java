package javafx.css;
public final class StyleClass {
public StyleClass(String styleClassName, int index) {
this.styleClassName = styleClassName;
this.index = index;
}
public String getStyleClassName() {
return styleClassName;
}
@Override public String toString() {
return styleClassName;
}
public int getIndex() {
return index;
}
private final String styleClassName;
private final int index;
}
