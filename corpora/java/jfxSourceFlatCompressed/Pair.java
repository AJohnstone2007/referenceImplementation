package javafx.util;
import java.io.Serializable;
import javafx.beans.NamedArg;
public class Pair<K,V> implements Serializable{
private K key;
public K getKey() { return key; }
private V value;
public V getValue() { return value; }
public Pair(@NamedArg("key") K key, @NamedArg("value") V value) {
this.key = key;
this.value = value;
}
@Override
public String toString() {
return key + "=" + value;
}
@Override
public int hashCode() {
int hash = 7;
hash = 31 * hash + (key != null ? key.hashCode() : 0);
hash = 31 * hash + (value != null ? value.hashCode() : 0);
return hash;
}
@Override
public boolean equals(Object o) {
if (this == o) return true;
if (o instanceof Pair) {
Pair pair = (Pair) o;
if (key != null ? !key.equals(pair.key) : pair.key != null) return false;
if (value != null ? !value.equals(pair.value) : pair.value != null) return false;
return true;
}
return false;
}
}
