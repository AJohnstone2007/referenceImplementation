package ensemble;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.Node;
public interface Page {
public ReadOnlyStringProperty titleProperty();
public String getTitle();
public String getUrl();
public Node getNode();
}
