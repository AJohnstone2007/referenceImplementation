package javafx.collections;
public interface ObservableIntegerArray extends ObservableArray<ObservableIntegerArray> {
public void copyTo(int srcIndex, int[] dest, int destIndex, int length);
public void copyTo(int srcIndex, ObservableIntegerArray dest, int destIndex, int length);
public int get(int index);
public void addAll(int... elements);
public void addAll(ObservableIntegerArray src);
public void addAll(int[] src, int srcIndex, int length);
public void addAll(ObservableIntegerArray src, int srcIndex, int length);
public void setAll(int... elements);
public void setAll(int[] src, int srcIndex, int length);
public void setAll(ObservableIntegerArray src);
public void setAll(ObservableIntegerArray src, int srcIndex, int length);
public void set(int destIndex, int[] src, int srcIndex, int length);
public void set(int destIndex, ObservableIntegerArray src, int srcIndex, int length);
public void set(int index, int value);
public int[] toArray(int[] dest);
public int[] toArray(int srcIndex, int[] dest, int length);
}
