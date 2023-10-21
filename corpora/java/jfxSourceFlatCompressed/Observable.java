package javafx.beans;
public interface Observable {
void addListener(InvalidationListener listener);
void removeListener(InvalidationListener listener);
}
