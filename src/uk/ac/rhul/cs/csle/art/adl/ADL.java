package uk.ac.rhul.cs.csle.art.adl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import uk.ac.rhul.cs.csle.art.ARTScriptInterpreter;
import uk.ac.rhul.cs.csle.art.cfg.ParserBase;
import uk.ac.rhul.cs.csle.art.cfg.gll.GLLBaseLine;
import uk.ac.rhul.cs.csle.art.cfg.grammar.Grammar;
import uk.ac.rhul.cs.csle.art.cfg.lexer.LexerLM;
import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.term.ITermsLowLevelAPI;
import uk.ac.rhul.cs.csle.art.term.Value;
import uk.ac.rhul.cs.csle.art.term.__char;
import uk.ac.rhul.cs.csle.art.term.__class;
import uk.ac.rhul.cs.csle.art.term.__int32;
import uk.ac.rhul.cs.csle.art.term.__list;
import uk.ac.rhul.cs.csle.art.term.__mapChain;
import uk.ac.rhul.cs.csle.art.term.__proc;
import uk.ac.rhul.cs.csle.art.term.__quote;
import uk.ac.rhul.cs.csle.art.term.__real64;
import uk.ac.rhul.cs.csle.art.term.__string;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ADL {
  private final ITerms iTerms;
  private final BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

  public static void main(String[] args) {
    if (args.length == 0) Util.fatal("ADL - usage adl <filename.adl>");
    new ADL(args[0]);
  }

  public ADL(String filename) {
    System.out.println("ADL V1.00");
    iTerms = new ITermsLowLevelAPI();
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("uk/ac/rhul/cs/csle/art/adl/adlSpecification.art");
    String ADLSpecification = "** null **";
    try {
      ADLSpecification = new String(inputStream.readAllBytes());
    } catch (IOException e) {
      Util.fatal("Unable to read ADL specification");
    }
    // System.out.println(ADLSpecification);
    ARTScriptInterpreter artScriptInterpreter = new ARTScriptInterpreter(iTerms);
    artScriptInterpreter.interpret(ADLSpecification);
    String inputString = "3+4";

    Grammar adlGrammar = artScriptInterpreter.getGrammar();
    LexerLM adlLexer = new LexerLM();
    ParserBase adlParser = new GLLBaseLine();

    adlParser.traceLevel = 0;
    adlParser.inputStringName = "";
    adlParser.inputString = inputString;
    adlParser.suppressEcho = true;
    adlGrammar.normalise();
    adlParser.grammar = adlGrammar;
    int adlDerivationTerm = 0;
    adlParser.accepted = false;

    adlLexer.lex(inputString, adlGrammar.lexicalKindsArray(), adlGrammar.lexicalStringsArray(), adlGrammar.whitespacesArray());
    // lexer.report();
    adlParser.input = adlLexer.tokens;
    adlParser.positions = adlLexer.positions;
    if (adlParser.input != null) adlParser.parse();
    if (adlParser.accepted) {
      adlParser.chooseLongestMatch();
      adlParser.selectFirst();
      adlDerivationTerm = adlParser.derivationAsTerm();
      __mapChain topLevelEnvironment = new __mapChain();
      Value topLevelReturn = interpret(adlDerivationTerm, topLevelEnvironment);

      System.out.println("ADL returns " + topLevelReturn + " with environment " + topLevelEnvironment);
    }
  }

  // String objectClassBody = ";";
  //
  __class objectClass = new __class(new LinkedList<>(), 0);

//@formatter:off
  public Value interpret(int term, __mapChain env) {
//    System.out.println("ADL interpret " + iTerms.toString(term));
    Value ret; // tenporary used in some switch cases
    int children[] = iTerms.getTermChildren(term);
    switch (iTerms.getTermSymbolString(term)) {
    case "apply":    ret = iTerms.valueEmpty;
                     for (int c : children) {
                       if (ret instanceof __proc) { // ret is result of previous child
                        System.out.println("Applying procedure");
                         __mapChain callEnv = new __mapChain(env);
                         Value argument = interpret(c, env);
                         Iterator<Value> parameterIterator = ((__proc) ret).getParameters().keySet().iterator();

                         if (argument instanceof __list) {
                           Iterator<Value> argumentIterator = ((__list) argument).javaValue().iterator();
                           while (argumentIterator.hasNext()) callEnv.__put(parameterIterator.next(), argumentIterator.next());
                         } else callEnv.__put(parameterIterator.next(), argument); // singleton case

                         System.out.println("apply " + ret + " with argument environment "  + callEnv.toStringLocal());

                         ret = interpret(((__proc) ret).getBodyTerm(), callEnv);
                       } else ret = interpret(c, env);
                     }
                     return ret; // Result of expression is the last thing computed
    case "system":   return systemCall(interpret(children[0], env), interpret(children[1], env));
    case "const":    return updateEnvironment(env, children[0],children[1],env, true);
    case "assign":   return updateEnvironment(env, children[0],children[1],env, false);
    case "match":    if (interpret(children[0], env).equals(iTerms.valueBoolTrue)) return interpret(children[1], env);
                     return interpret(children[2], env);
    case "matchr":   if (interpret(children[0], env).equals(iTerms.valueBoolTrue)) {
                       ret = interpret(children[1], env);
                       while (interpret(children[0], env).equals(iTerms.valueBoolTrue)) ret = interpret(children[1], env);
                       return ret;
                     }
                     return interpret(children[2], env);
    case "slice":    throw new ADLException("Slice not yet implemented");
    case "blist":    int gc = iTerms.getSubterm(term, 0,0); if (iTerms.hasSymbol(gc, "list")) children = iTerms.getTermChildren(gc);
    case "mtlist":   // blist flattens list as top argument and flows through to list; mtlist just flows through
    case "list":     ret = new __list(); for (int c : children) ret.__put(interpret(c, env)); return ret;
    case "adl":      return iTerms.valueEmpty; // Special case - empty program
    case "seq":      interpret(children[0], env); return interpret(children[1], env);
    case "body":    return iTerms.valueDone;
    case "skip":     return iTerms.valueDone;
    case "pair":     ret = new __list(); ret.__add(interpret(children[0], env)); ret.__add(interpret(children[1], env)); return ret;
    case "or":       return interpret(children[0],env).__or(interpret(children[1], env));
    case "xor":      return interpret(children[0],env).__xor(interpret(children[1], env));
    case "and":      return interpret(children[0],env).__and(interpret(children[1], env));
    case "eq":       return interpret(children[0],env).__eq(interpret(children[1], env));
    case "ne":       return interpret(children[0],env).__ne(interpret(children[1], env));
    case "ge":       return interpret(children[0],env).__ge(interpret(children[1], env));
    case "gt":       return interpret(children[0],env).__gt(interpret(children[1], env));
    case "le":       return interpret(children[0],env).__le(interpret(children[1], env));
    case "lt":       return interpret(children[0],env).__lt(interpret(children[1], env));
    case "cat":      return interpret(children[0],env).__cat(interpret(children[1], env));
    case "lsh":      return interpret(children[0],env).__lsh(interpret(children[1], env));
    case "rsh":      return interpret(children[0],env).__rsh(interpret(children[1], env));
    case "rol":      return interpret(children[0],env).__rol(interpret(children[1], env));
    case "ror":      return interpret(children[0],env).__ror(interpret(children[1], env));
    case "ash":      return interpret(children[0],env).__ash(interpret(children[1], env));
    case "add":      return interpret(children[0],env).__add(interpret(children[1], env));
    case "sub":      return interpret(children[0],env).__sub(interpret(children[1], env));
    case "mul":      return interpret(children[0],env).__mul(interpret(children[1], env));
    case "div":      return interpret(children[0],env).__div(interpret(children[1], env));
    case "mod":      return interpret(children[0],env).__div(interpret(children[1], env));
    case "exp":      return interpret(children[0],env).__exp(interpret(children[1], env));
    case "pos":      return interpret(children[0],env); // Add a pos routine to abstract class?
    case "neg":      return interpret(children[0],env).__neg();
    case "not":      return interpret(children[0],env).__not();
    case "true":     return iTerms.valueBoolTrue;
    case "false":    return iTerms.valueBoolFalse;
    case "__char":   return new __char(term);
    case "__int32":  return new __int32(term);
    case "__real64": return new __real64(term);
    case "__string": return new __string(term);
    case "use":      return env.__get(new __quote(children[0]));
    case "lambda":   LinkedHashMap<Value, Value> parameters = new LinkedHashMap<>();
                     if (children.length == 1) return new __proc(parameters, children[0]);
                     else {
                       for (int i: iTerms.getTermChildren(children[0])) parameters.put(new __quote(i), iTerms.valueEmpty);
                       return new __proc(parameters, children[1]);
                     }
    case "class":    LinkedList<Value> superClasses = new LinkedList<>();
                     if (children.length == 1) {
                       superClasses.add(objectClass);
                       return new __class(superClasses, children[0]);
                     } else {
                       for (int i: iTerms.getTermChildren(children[0])) {
                         superClasses.add(new __class(null,0));
                         return new __class(superClasses, children[1]);
                       }
                     }
   }
    throw new ADLException("in ADL term, unknown constructor '" + iTerms.getTermSymbolString(term) + "'");
  }

  private Value systemCall(Value opcode, Value argument) {
//     System.out.println("System call with opcode " + opcode + " and argument " + argument);
    if (!(opcode instanceof __int32)) throw new ADLException("Left operand of $$$ system operator must be an integer");
    int oc = ((__int32)opcode).javaValue;
    switch (oc){
    case 0: throw new ADLException("Plugin access not yet implemented"); // Plugin
    case 1: return print(System.out, argument);
    case 2: return print(System.err, argument);
    case 3: try { return new __string(keyboard.readLine());
                } catch (IOException e) { throw new ADLException("I/O error on keyboard read"); }
    default: throw new ADLException("Illegal system opcode " + oc);
    }
  }

  private Value print(PrintStream ps, Value argument) {
    LinkedList<Value> list = argument instanceof __list ? ((__list) argument).javaValue(): null;
    String nl = "\n";
    if (list == null) ps.print(argument.toValueString());
    else for (Value v:list)
      System.out.print(v.toValueString());
    return iTerms.valueEmpty;
  }

  private Value updateEnvironment(__mapChain env, int lhsTerm, int rhsTerm, __mapChain env2, boolean lock) {
    Value ret = iTerms.valueEmpty;
    if (iTerms.getTermArity(lhsTerm) == 1)
      env.__put(new __quote(iTerms.getSubterm(lhsTerm, 0)), ret = interpret(rhsTerm, env), lock);
    else throw new ADLException("lhsList with arity > 1 not yet implemented");
    return ret;
  }
}
