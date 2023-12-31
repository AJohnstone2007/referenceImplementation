import uk.ac.rhul.cs.csle.art.term.PluginInterface;
import uk.ac.rhul.cs.csle.art.term.Value;
import uk.ac.rhul.cs.csle.art.term.ValueException;
import uk.ac.rhul.cs.csle.art.term.__string;

// Rename this class to ARTPlugin if you want to develop plugins within the Eclipse workspace; but chnge it back again before distribution
public class XARTPlugin implements PluginInterface {

  @Override
  public String name() {
    return "Adrian's example plugin";
  }

  @Override
  public Value plugin(Value... args) throws ValueException {
    for (Value a : args) {
      if (a.javaValue().getClass().isArray()) {
        Value[] v = (Value[]) a.javaValue();

        System.out.println("__array of length " + v.length + " with contents");
        for (int i = 0; i < v.length; i++)
          System.out.println("  " + i + ":" + v[i] + " which has underlying Java " + v[i].getClass() + " and value " + v[i].javaValue());
      } else
        System.out.println(a + " which has underlying Java " + a.javaValue().getClass() + " and value " + a.javaValue());
    }
    return new __string("Return value from text example plugin");
  }
}