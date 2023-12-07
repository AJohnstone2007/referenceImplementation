package uk.ac.rhul.cs.csle.art.term;

public interface PluginInterface {
  String name();

  Value plugin(Value... args) throws ValueException;
}
