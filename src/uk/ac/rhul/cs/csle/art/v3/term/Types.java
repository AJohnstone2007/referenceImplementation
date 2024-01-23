package uk.ac.rhul.cs.csle.art.v3.term;

public enum Types {

  __procedure,

  /* Exception processing signals */

  __signal, __signalBreak, __signalContinue, __signalYield, __signalReturn,

  /* Collection types */

  __list, __tuple, __array, __string,

  __map, __mapOrdered, __set,

  /* atomic types below this line, following the Scheme numeric tower ideas */

  __quantity,

  __number,

  __complex, __complexArbitrary, __complex32, __complex64, __complex80, __complex128,

  __real, __realArbitrary, __real32, __real64, __real80, __real128,

  __rational, __rationalArbitrary, __rational8, __rational16, __rational32, __rational64, __rational128,

  __int, __intArbitrary, __int8, __int16, __int32, __int64, __int128,

  __char,

  __bool,

  __null,

}
