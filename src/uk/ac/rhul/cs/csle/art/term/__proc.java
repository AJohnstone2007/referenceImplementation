package uk.ac.rhul.cs.csle.art.term;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class __proc extends Value {
  private final LinkedHashMap<__quote, Value> parameters;
  private final int bodyTerm;

  public int getBodyTerm() {
    return bodyTerm;
  }

  public __proc(LinkedHashMap<__quote, Value> parameters, int bodyTerm) {
    this.parameters = parameters;
    this.bodyTerm = bodyTerm;
  }

  @Override
  public Object javaValue() {
    return bodyTerm;
  }

  public Object javaValue1() {
    return parameters;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("__proc(");
    boolean notFirst = false;
    for (Value p : parameters.keySet()) {
      if (notFirst)
        sb.append(", ");
      else
        notFirst = true;
      sb.append(p);
      sb.append(":");
      sb.append(parameters.get(p));
    }
    sb.append(") {");
    sb.append(iTerms.toString(bodyTerm));
    sb.append("}");
    return sb.toString();
  }

  public __mapChain buildEnvironment(__mapChain env, Value unnamedArguments, Value namedArguments, Value namedValues) {
    System.out.println("** buildEnvironment with unnamed arguments " + unnamedArguments.toString() + " and named arguments " + namedArguments.toString()
        + " with values " + namedValues.toString());

    // Type check the arguments. Note that we allow multiples instances of the same names argument
    if (!(env instanceof __mapChain)) throw new ValueException("env argument supplied to __procedureV3 buildEnvironment() must be a __mapChain");
    if (!(unnamedArguments instanceof __list)) throw new ValueException("unnamed arguments supplied to __procedureV3 buildEnvironment() must be a __list");
    if (!(namedArguments instanceof __list)) throw new ValueException("named arguments supplied to __procedureV3 buildEnvironment() must be a __list");
    if (!(namedValues instanceof __list)) throw new ValueException("named values supplied to __procedureV3 buildEnvironment() must be a __list");

    // Local names for the argument payloads
    LinkedList<Value> unnamedArgs = ((__list) unnamedArguments).javaValue();
    LinkedList<Value> namedArgs = ((__list) namedArguments).javaValue();
    LinkedList<Value> namedVals = ((__list) namedValues).javaValue();

    if (namedArgs.size() != namedVals.size())
      throw new ValueException("named argument list and named value list supplied to __procedureV3 buildEnvironment() must have the same cardinality ");

    if (unnamedArgs.size() > parameters.keySet().size()) throw new ValueException("too many unnamed arguments");

    __mapChain ret = new __mapChain(env);

    // Iterate over the whole paramaters keyset. Whilst there are unnamed arguments, load their values otherwise load default value
    int unnamedIndex = 0;
    for (Value v : parameters.keySet()) {
      if (unnamedIndex < unnamedArgs.size()) {
        ret.__put(v, unnamedArgs.get(unnamedIndex));
        System.out.println("Set unnamed argument " + v + " to supplied value " + ret.__get(v));
      } else {
        ret.__put(v, parameters.get(v));
        System.out.println("Set unnamed argument " + v + " to default value " + ret.__get(v));
      }
      unnamedIndex++;
    }

    // Now iterate over then named argumemts, adding their values
    for (int i = 0; i < namedArgs.size(); i++) {
      ret.__put(namedArgs.get(i), namedVals.get(i));
      System.out.println("Set named argument " + namedArgs.get(i) + " to " + ret.__get(namedArgs.get(i)));
    }

    System.out.println("Environment for proc is " + env.toString());
    return ret;
  }
}
