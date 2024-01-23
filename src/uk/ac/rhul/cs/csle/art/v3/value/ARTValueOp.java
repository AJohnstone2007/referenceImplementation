package uk.ac.rhul.cs.csle.art.v3.value;

public enum ARTValueOp {
  castToBoolean, castToCharacter, castToInteger32, castToIntegerArbitrary, castToReal64, castToRealArbitrary, castToString,

  castToArray, castToTuple, castToRecord, castToList, castToSet,

  castToMap, castToMapOrdered, castToMapOrderedHierarchy,

  castToTerm, castToMetavariable,

  isBoolean, isCharacter, isString,

  isInteger, isInteger32, isIntegerArbitrary, isRational, isReal, isReal64, isRealArbitrary, isComplex, isQuantity, isNumber,

  isArray, isTuple, isRecord, isList, isSet,

  isMap, isMapOrdered, isMapOrderedHierarchy,

  isTerm, isTermVariable,

  gt, lt, ge, le, eq, ne,

  not, and, or, xor, cnd,

  lsh, rsh, ash, rol, ror,

  neg, add, sub, mul, div, mod, exp,

  cardinality, contains, insert, insertKey, delete, deleteKey, update, updateOrdered, value, valueKey, key, finalKey,

  union, intersection, difference, cat,

  parameters, body,

  output, input, close,

  range
}
