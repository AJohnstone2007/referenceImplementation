package javafx.collections;
public interface ObservableFloatArray extends ObservableArray<ObservableFloatArray> {
public void copyTo(int srcIndex, float[] dest, int destIndex, int length);
public void copyTo(int srcIndex, ObservableFloatArray dest, int destIndex, int length);
public float get(int index);
public void addAll(float... elements);
public void addAll(ObservableFloatArray src);
public void addAll(float[] src, int srcIndex, int length);
public void addAll(ObservableFloatArray src, int srcIndex, int length);
public void setAll(float... elements);
public void setAll(float[] src, int srcIndex, int length);
public void setAll(ObservableFloatArray src);
public void setAll(ObservableFloatArray src, int srcIndex, int length);
public void set(int destIndex, float[] src, int srcIndex, int length);
public void set(int destIndex, ObservableFloatArray src, int srcIndex, int length);
public void set(int index, float value);
public float[] toArray(float[] dest);
public float[] toArray(int srcIndex, float[] dest, int length);
}
