package uk.ac.rhul.cs.csle.art.term;

public interface ARTPluginInterface {
  String name();

  Value plugin(Value... args) throws ValueException;
}
