package ensemble.playground;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public class PlaygroundProperty {
public final String fieldName;
public final String propertyName;
public final Map<String,String> properties;
public PlaygroundProperty(String fieldName, String propertyName,String...propertyParts) {
this.fieldName = fieldName;
this.propertyName = propertyName;
Map<String,String> p = new HashMap<>();
for (int i=0; i<propertyParts.length; i+=2) {
p.put(propertyParts[i], propertyParts[i+1]);
}
this.properties = Collections.unmodifiableMap(p);
}
@Override public String toString() {
return "PlaygroundProperty{" + "fieldName=" + fieldName + ", propertyName=" + propertyName + ", properties=" + properties + '}';
}
}
