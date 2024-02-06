package uk.ac.rhul.cs.csle.art.old.term;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;

public class __proc extends Value {
  private final __quote statements;
  private final LinkedHashMap<Value, Value> parameters;

  public __quote getStatements() {
    return statements;
  }

  public __proc(Value parameters, Value defaults, Value statements) {
    System.out.println("Constructing V3 style procedure with paramaters " + parameters + " and defaults " + defaults);
    if (!(parameters instanceof __list)) throw new ARTUncheckedException("parameters supplied to __proc constructor must be a __list");
    if (!(defaults instanceof __list)) throw new ARTUncheckedException("defaults supplied to __proc constructor must be a __list");
    if (!(statements instanceof __quote)) throw new ARTUncheckedException("statements supplied to __proc constructor must be a __quote");
    LinkedList<Value> pars = ((__list) parameters).javaValue();
    LinkedList<Value> defs = ((__list) defaults).javaValue();
    if (pars.size() != defs.size())
      throw new ARTUncheckedException("parameters and defaults supplied to __procedureV3 constructor must have the same cardinality ");
    this.parameters = new LinkedHashMap<>();
    for (int i = 0; i < pars.size(); i++)
      if (this.parameters.get(pars.get(i)) != null)
        throw new ARTUncheckedException("parameter " + pars.get(i) + " appears more than once");
      else
        this.parameters.put(pars.get(i), defs.get(i));
    this.statements = (__quote) statements;
    System.out.println("Created procedure: " + toString());
  }

  @Override
  public Object javaValue() {
    throw new ARTUncheckedException("__proc does not supply a value");
  }

  @Override
  public String toString() {
    return "__proc(" + parameters + ")";
  }

  public __mapChain buildEnvironment(__mapChain env, Value unnamedArguments, Value namedArguments, Value namedValues) {
    System.out.println("** buildEnvironment with unnamed arguments " + unnamedArguments.toString() + " and named arguments " + namedArguments.toString()
        + " with values " + namedValues.toString());

    // Type check the arguments. Note that we allow multiples instances of the same names argument
    if (!(env instanceof __mapChain)) throw new ARTUncheckedException("env argument supplied to __procedureV3 buildEnvironment() must be a __mapChain");
    if (!(unnamedArguments instanceof __list))
      throw new ARTUncheckedException("unnamed arguments supplied to __procedureV3 buildEnvironment() must be a __list");
    if (!(namedArguments instanceof __list)) throw new ARTUncheckedException("named arguments supplied to __procedureV3 buildEnvironment() must be a __list");
    if (!(namedValues instanceof __list)) throw new ARTUncheckedException("named values supplied to __procedureV3 buildEnvironment() must be a __list");

    // Local names for the argument payloads
    LinkedList<Value> unnamedArgs = ((__list) unnamedArguments).javaValue();
    LinkedList<Value> namedArgs = ((__list) namedArguments).javaValue();
    LinkedList<Value> namedVals = ((__list) namedValues).javaValue();

    if (namedArgs.size() != namedVals.size())
      throw new ARTUncheckedException("named argument list and named value list supplied to __procedureV3 buildEnvironment() must have the same cardinality ");

    if (unnamedArgs.size() > parameters.keySet().size()) throw new ARTUncheckedException("too many unnamed arguments");

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
