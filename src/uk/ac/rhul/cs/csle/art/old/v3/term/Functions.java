package uk.ac.rhul.cs.csle.art.old.v3.term;

// Notes: no need for associativities or priorities as these will all be called from well bracketed terms
// A fixed coercian rule, following the Scheme numereic tower needs to be built into __cast and __isCastable

public enum Functions {
  __gt, __lt, __ge, __le, __eq, __ne, // relations

  __not, __and, __or, __xor, __cnd, // logic and bitwise

  __lsh, __rsh, __ash, __rol, __rolc, __ror, __rorc, // shift and rotate

  __neg, __add, __sub, __mul, __div, __mod, __exp, // arithmetic

  __cardinality, __contains, __union, __intersection, __difference, // sets

  __cat, __head, __tail, __range, __insert, // lists

  __insertKey, __delete, __deleteKey, __update, __updateOrdered, __value, __valueKey, __key, __finalKey, // maps

  __block, // code

  __is, __isCastable, __cast, // types

  __open, __input, __output, __flush, __close, // resources
}
