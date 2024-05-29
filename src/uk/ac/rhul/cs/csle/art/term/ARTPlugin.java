package uk.ac.rhul.cs.csle.art.term;

public class ARTPlugin implements ARTPluginInterface {

  @Override
  public String name() {
    return "Default ARTPlugin";
  }

  @Override
  public Value plugin(Value... args) throws ValueException {
    System.out.println(name() + " called with " + args.length + " argument" + (args.length == 1 ? "" : "s"));
    for (Value a : args) {
      if (a.javaValue().getClass().isArray()) {
        Value[] v = (Value[]) a.javaValue();

        System.out.println("__array of length " + v.length + " with contents");
        for (int i = 0; i < v.length; i++)
          System.out.println("  " + i + ":" + v[i] + " which has underlying Java " + v[i].getClass() + " and value: " + v[i].javaValue());
      } else
        System.out.println(a + " which has underlying Java " + a.javaValue().getClass() + " and value: " + a.javaValue());
    }
    return new __string("Default");
  }

}
