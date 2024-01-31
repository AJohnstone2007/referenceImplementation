import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import uk.ac.rhul.cs.csle.art.term.Value;
import uk.ac.rhul.cs.csle.art.term.ValueException;
import uk.ac.rhul.cs.csle.art.term.ValueUserPluginInterface;
import uk.ac.rhul.cs.csle.art.term.__string;

// Rename this class to ARTPlugin if you want to develop plugins within the Eclipse workspace; but change it back again before distribution
public class ValueUserPlugin_FX implements ValueUserPluginInterface {

  @Override
  public boolean useFX() {
    return true;
  }

  @Override
  public String name() {
    return "Adrian's example JavaFX plugin";
  }

  @Override
  public Value plugin(Value... args) throws ValueException {
    Group root = new Group();
    root.getChildren().add(new Label(args[0].toString()));

    Stage stage = new Stage();
    stage.setScene(new Scene(root, 300, 250));
    stage.show();

    return new __string("Return value from JavaFX example plugin");
  }
}