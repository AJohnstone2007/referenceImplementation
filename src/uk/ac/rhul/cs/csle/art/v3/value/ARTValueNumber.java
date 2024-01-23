package uk.ac.rhul.cs.csle.art.v3.value;

public abstract class ARTValueNumber extends ARTValue {

  protected static int coercionPriorityInteger8 = 1;
  protected static int coercionPriorityCharacter = 2;
  protected static int coercionPriorityInteger16 = 3;
  protected static int coercionPriorityInteger32 = 4;
  protected static int coercionPriorityInteger64 = 5;
  protected static int coercionPriorityInteger128 = 6;
  protected static int coercionPriorityIntegerArbitrary = 9;

  protected static int coercionPriorityRational8 = 12;
  protected static int coercionPriorityRational16 = 13;
  protected static int coercionPriorityRational32 = 14;
  protected static int coercionPriorityRational64 = 15;
  protected static int coercionPriorityRational128 = 16;
  protected static int coercionPriorityRationalArbitrary = 19;

  protected static int coercionPriorityReal32 = 24;
  protected static int coercionPriorityReal64 = 25;
  protected static int coercionPriorityReal128 = 26;
  protected static int coercionPriorityRealArbitrary = 29;

  protected static int coercionPriorityComplex32 = 34;
  protected static int coercionPriorityComplex64 = 35;
  protected static int coercionPriorityComplex128 = 36;
  protected static int coercionPriorityComplexArbitrary = 39;

  protected static int coercionPriorityQuantity = 49;

  @Override
  protected int getCoercionPriority() { // Catch number classes with missing coercions
    error(" internal error - missing getCorcionPriority() implementation in class " + strip(this.getClass().toString()));
    return 0;
  }
}
