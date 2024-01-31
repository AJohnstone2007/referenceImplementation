package uk.ac.rhul.cs.csle.art.term;

public interface ValueUserPluginInterface {
  boolean useFX();

  String name();

  Value plugin(Value... args) throws ValueException;
}
