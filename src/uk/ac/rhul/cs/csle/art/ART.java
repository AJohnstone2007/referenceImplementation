package uk.ac.rhul.cs.csle.art;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import uk.ac.rhul.cs.csle.art.cfg.grammar.Grammar;
import uk.ac.rhul.cs.csle.art.cfg.grammar.GrammarElement;
import uk.ac.rhul.cs.csle.art.cfg.grammar.GrammarKind;
import uk.ac.rhul.cs.csle.art.cfg.grammar.GrammarNode;
import uk.ac.rhul.cs.csle.art.old.core.ARTV4;
import uk.ac.rhul.cs.csle.art.old.core.ARTV5Transition;
import uk.ac.rhul.cs.csle.art.old.v3.ARTV3;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementEoS;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementEpsilon;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminalBuiltin;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminalCaseInsensitive;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminalCaseSensitive;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminalCharacter;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstance;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceSlot;
import uk.ac.rhul.cs.csle.art.old.v3.manager.module.ARTV3Module;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ART {
  static final Pattern filenamePattern = Pattern.compile("[a-zA-Z0-9/\\\\]+\\.[a-zA-Z0-9]+"); // NB this is a limited idea of what a filename looks like

  public static void main(String[] args) {
    if (args.length == 0) Util.fatal("ART " + ARTVersion.version() + ": no arguments supplied");

    ARTScriptInterpreter artScriptInterpreter = new ARTScriptInterpreter();

    switch (args[0]) { // Test for initial special mode argument
    case "ide":
      ARTIDE.interpretUnderIDE(artScriptInterpreter, buildScriptString(args, 1));
      break;
    case "v3":
      new ARTV3(buildScriptString(args, 1));
      break;
    case "v4":
      new ARTV4(buildScriptString(args, 1));
      break;
    case "V5Transition":
      new ARTV5Transition(buildScriptString(args, 1));
      break;
    case "ajDebug":
      ajDebugCode(artScriptInterpreter, args);
      break;
    default:
      if (artScriptInterpreter.iTerms.plugin.useFX()) // does the user code need JavaFX?
        ARTFXWrapper.interpretUnderFX(artScriptInterpreter, buildScriptString(args, 0));
      else
        artScriptInterpreter.interpret(buildScriptString(args, 0));
      break;
    }
  }

  static String buildScriptString(String[] args, int startArg) {
    StringBuilder sb = new StringBuilder();

    for (int argp = startArg; argp < args.length; argp++) {
      sb.append(" ");
      if (filenamePattern.matcher(args[argp]).matches()) { // Process args that look like filenames
        // System.out.println("Argument " + argp + " " + args[argp] + " looks like a file");
        if (args[argp].endsWith(".art")) {
          /* When we have include working, replace this insertion with an !include directive */
          try {
            sb.append(Files.readString(Paths.get((args[argp]))));
          } catch (IOException e) {
            Util.fatal("Unable to open script file " + args[argp]);
          }
          // sb.append("!include '" + args[i] + "'");
        } else
          sb.append("!try '" + args[argp] + "'");
      } else
        sb.append(args[argp]);
    }
    // System.out.println(" Script string: " + sb);
    return sb.toString();
  }

  // Adrian's debug comparison sand pit - undocumented
  private static void ajDebugCode(ARTScriptInterpreter artScriptInterpreter, String[] args) {
    try {
      Path inputDir = Paths.get(args[1]);
      if (Files.isDirectory(inputDir)) {
        List<Path> filePaths;
        filePaths = Files.list(inputDir).collect(Collectors.toList());
        for (Path filePath : filePaths)
          if (filePath.toString().endsWith(".art")) {
            // System.out.println("File " + filePath);
            boolean good = v5v3RegressionFirstAndFollowSets(Files.readString(filePath));
            System.out.println("File " + filePath + " " + (good ? " Good " : "Bad"));
          }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static Grammar grammarV5; // regression V5 grammar
  static ARTGrammar grammarV3; // regression V3 grammar

  private static boolean v5v3RegressionFirstAndFollowSets(String scriptString) {
    ARTScriptInterpreter artScriptInterpreter = new ARTScriptInterpreter();

    // System.out.print("v5v3RegressionFirstAndFollowSets");

    artScriptInterpreter.interpret(scriptString);

    ARTV3 artV3 = new ARTV3(scriptString);

    grammarV3 = artV3.artManager.addGrammar("Parser grammar", artV3.artManager.getDefaultMainModule(), false, artV3.artManager.artDirectives);

    // System.out.print("\n*** V3 grammar\n" + grammarV3.toString());
    grammarV5 = artScriptInterpreter.currentGrammar;
    grammarV5.normalise();
    grammarV5.show("grammar.dot");

    System.out.println("\n*** V5 grammar\n" + grammarV5.toStringBody(true));

    boolean good = true;

    // First check the nonterminals
    for (ARTGrammarElementNonterminal v3Nonterminal : grammarV3.getNonterminals()) {
      GrammarElement v5Nonterminal = grammarV5.elements.get(new GrammarElement(GrammarKind.N, v3Nonterminal.getId()));

      // System.out.println(
      // "V3 nonterminal " + v3Nonterminal + " first " + new TreeSet<>(v3Nonterminal.getFirst()) + " follow " + new TreeSet<>(v3Nonterminal.getFollow()));
      // System.out.println("V5 nonterminal " + v5Nonterminal + " first " + v5Nonterminal.first + " follow " + v5Nonterminal.follow + "\n");

      if (!v5v3ElementSetSame(v5Nonterminal.first, new TreeSet<>(v3Nonterminal.getFirst()), artV3.artManager.getDefaultMainModule())) {
        System.out.println("First for " + v5Nonterminal + " differ:\nV5 " + v5Nonterminal.first + "\nV3 " + new TreeSet<>(v3Nonterminal.getFirst()) + "\n");
        good = false;
      }

      if (!v5v3ElementSetSame(v5Nonterminal.follow, new TreeSet<>(v3Nonterminal.getFollow()), artV3.artManager.getDefaultMainModule())) {
        // Bug in V3? Spurious $ check
        Set<GrammarElement> v5prime = new TreeSet<>(v5Nonterminal.follow);
        v5prime.add(grammarV5.endOfStringElement);
        // if (!v5v3ElementSetSame(v5prime, new TreeSet<>(v3Nonterminal.getFollow()), artV3.artManager.getDefaultMainModule()))
        {

          System.out
              .println("Follow for " + v5Nonterminal + " differ:\nV5 " + v5Nonterminal.follow + "\nV3 " + new TreeSet<>(v3Nonterminal.getFollow()) + "\n");
          // System.out.println("v5:v3 cardinality " + v5Nonterminal.follow.size() + " : " + v3Nonterminal.getFollow().size() + "\n");
          good = false;
        }
      }
    }

    // Now work through instance sets
    v5v3RegressionGatherV3FirstAndFollowInstanceSetsRec((ARTGrammarInstance) grammarV3.getInstanceTree().getRoot());
    good &= v5v3RegressionCheckFirstAndFollowInstanceSets(grammarV5, artV3);
    return good;
  }

  private static boolean v5v3RegressionCheckFirstAndFollowInstanceSetsRec(GrammarNode v5, ARTV3 artV3) {
    if (v5 == null) return true;

    boolean good = true;
    String key = v5.toStringAsProduction().replaceAll("\\s", "");
    // System.out.println("V5 instance " + key + " first " + v5.instanceFirst + " follow " + v5.instanceFollow);
    Set<ARTGrammarElement> v3InstanceFirst = v3InstanceFirsts.get(key), v3InstanceFollow = v3InstanceFollows.get(key);

    if (v3InstanceFirsts.get(key) == null)
      // System.out.println(" v3 key is missing")
      ;
    else {
      // if (!v5v3ElementSetSame(v5.instanceFirst, v3InstanceFirst, artV3.artManager.getDefaultMainModule())) {
      // System.out.println("Instance first differ: V5 " + v5.instanceFirst + " V3 " + v3InstanceFirst);
      // good = false;
      // }
      // if (v5.elm.kind == GrammarKind.N && !v5v3ElementSetSame(v5.instanceFollow, v3InstanceFollow, artV3.artManager.getDefaultMainModule())) {
      // System.out.println("Instance follow differ: V5 " + v5.instanceFollow + " V3 " + v3InstanceFollow);
      // good = false;
      // }
    }
    // System.out.println();
    if (v5.elm.kind == GrammarKind.END) return good;

    good &= v5v3RegressionCheckFirstAndFollowInstanceSetsRec(v5.seq, artV3);
    good &= v5v3RegressionCheckFirstAndFollowInstanceSetsRec(v5.alt, artV3);

    return good;
  }

  private static boolean v5v3RegressionCheckFirstAndFollowInstanceSets(Grammar grammarV5, ARTV3 artV3) {
    boolean good = true;
    for (GrammarElement e : grammarV5.elements.keySet())
      if (e.kind == GrammarKind.N) good &= v5v3RegressionCheckFirstAndFollowInstanceSetsRec(grammarV5.rules.get(e).alt, artV3);

    return good;
  }

  static Map<String, Set<ARTGrammarElement>> v3InstanceFirsts = new HashMap<>(), v3InstanceFollows = new HashMap<>();
  static Set<String> checked = new HashSet<>();

  static void v5v3RegressionGatherV3FirstAndFollowInstanceSetsRec(ARTGrammarInstance v3) {
    if (v3 == null) return;
    // System.out.println(
    // "v5v3RegressionGatherV3FirstAndFollowInstanceSetsRec at [" + v3.getKey() + "] " + v3.toGrammarString() + " first=" + v3.first + " follow=" + v3.follow);
    if (v3 instanceof ARTGrammarInstanceSlot) {
      v3InstanceFirsts.put(v3.toGrammarString(".").replaceAll("\\s", ""), v3.first); // (v3.getSibling() == null ? v3.first : v3.getSibling().first));
      v3InstanceFollows.put(v3.toGrammarString(".").replaceAll("\\s", ""), (v3.getSibling() == null ? v3.follow : v3.getSibling().follow));
    }
    v5v3RegressionGatherV3FirstAndFollowInstanceSetsRec(v3.getChild());
    v5v3RegressionGatherV3FirstAndFollowInstanceSetsRec(v3.getSibling());
  }

  private static boolean v5v3ElementSetSame(Set<GrammarElement> v5, Set<ARTGrammarElement> v3, ARTV3Module artv3Module) {
    boolean ret = true;

    for (GrammarElement ve5 : v5) {
      ret &= v3.contains(v5Element2v3Element(ve5, artv3Module));
      // System.out.println("Checked v5 " + ve5 + " " + ret);
    }

    for (ARTGrammarElement ve3 : v3) {
      GrammarElement ve5 = v3Element2v5Element(ve3);

      ret &= v5.contains(ve5);
      // System.out.println("Checked v3 " + ve3 + " " + ret + " with v5 as " + ve5);
    }
    return ret;
  }

  static GrammarElement v3Element2v5Element(ARTGrammarElement elem) {
    if (elem instanceof ARTGrammarElementTerminalBuiltin) return new GrammarElement(GrammarKind.B, ((ARTGrammarElementTerminal) elem).getId());
    if (elem instanceof ARTGrammarElementTerminalCharacter) return new GrammarElement(GrammarKind.C, ((ARTGrammarElementTerminal) elem).getId());
    if (elem instanceof ARTGrammarElementEoS) return new GrammarElement(GrammarKind.EOS, "$");
    if (elem instanceof ARTGrammarElementEpsilon) return new GrammarElement(GrammarKind.EPS, "#");
    if (elem instanceof ARTGrammarElementNonterminal) return new GrammarElement(GrammarKind.N, elem.toString());
    if (elem instanceof ARTGrammarElementTerminalCaseSensitive) return new GrammarElement(GrammarKind.T, ((ARTGrammarElementTerminal) elem).getId());
    if (elem instanceof ARTGrammarElementTerminalCaseInsensitive) return new GrammarElement(GrammarKind.TI, ((ARTGrammarElementTerminal) elem).getId());

    return null;
  }

  static ARTGrammarElement v5Element2v3Element(GrammarElement elem, ARTV3Module artV3Module) {
    switch (elem.kind) {
    case ALT, DO, END, KLN, OPT, POS:
      return null; // These should not appear

    case B:
      return new ARTGrammarElementTerminalBuiltin(elem.str);
    case C:
      return new ARTGrammarElementTerminalCharacter(elem.str);
    case EOS:
      return new ARTGrammarElementEoS();
    case EPS:
      return new ARTGrammarElementEpsilon();
    case N:
      return new ARTGrammarElementNonterminal(artV3Module, elem.str);
    case T:
      return new ARTGrammarElementTerminalCaseSensitive(elem.str);
    case TI:
      return new ARTGrammarElementTerminalCaseInsensitive(elem.str);
    }
    return null; // To settle the Java control flow analyser - the above case list should be complete
  }
}
