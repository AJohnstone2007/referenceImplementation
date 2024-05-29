package uk.ac.rhul.cs.csle.art;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import uk.ac.rhul.cs.csle.art.cfg.grammar.Grammar;
import uk.ac.rhul.cs.csle.art.cfg.grammar.GrammarElement;
import uk.ac.rhul.cs.csle.art.cfg.grammar.GrammarKind;
import uk.ac.rhul.cs.csle.art.cfg.grammar.GrammarNode;
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
import uk.ac.rhul.cs.csle.art.term.ITermsLowLevelAPI;
import uk.ac.rhul.cs.csle.art.util.Relation;
import uk.ac.rhul.cs.csle.art.util.Util;

public class AJDebug {
  /* AJ debug material below this line */
  Grammar grammarV5; // regression V5 grammar
  ARTGrammar grammarV3; // regression V3 grammar

  ARTScriptInterpreter regressionScriptInterpreter;

  public AJDebug(String[] args) {
    try {
      System.out.println("ajDebug " + args[1]);
      Path arg1AsPath = Paths.get(args[1]);
      if (args[1].endsWith(".art"))
        processFile(arg1AsPath);
      else if (Files.isDirectory(arg1AsPath)) {
        for (Path filePath : Files.list(arg1AsPath).collect(Collectors.toList()))
          if (filePath.toString().endsWith(".art")) processFile(filePath);
      } else
        Util.fatal("ajDebug: argument must be a filename ending with .art or a directory");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void processFile(Path filePath) throws IOException {
    System.out.println("AJDebug on file " + filePath);
    boolean good = v5v3RegressionFirstAndFollowSets(Files.readString(filePath));
    System.out.println("File " + filePath + " " + (good ? " Good " : "Bad"));

    System.out.println("gen1:\n" + grammarV5.gen1.toString());
    PrintStream gen1Out = new PrintStream(new File("gen1.dot"));
    gen1Out.println(grammarV5.gen1.toDotString());
    gen1Out.close();

    Relation<GrammarElement, GrammarElement> gen = new Relation<>(grammarV5.gen1);
    gen.transitiveClosure();

    System.out.println("gen:\n" + gen.toString());
    PrintStream reachableOut = new PrintStream(new File("gen.dot"));
    reachableOut.println(gen.toDotString());
    reachableOut.close();
  }

  private boolean v5v3RegressionFirstAndFollowSets(String scriptString) {
    regressionScriptInterpreter = new ARTScriptInterpreter(new ITermsLowLevelAPI());

    // System.out.print("v5v3RegressionFirstAndFollowSets");

    regressionScriptInterpreter.interpret(scriptString);

    ARTV3 artV3 = new ARTV3(scriptString);

    grammarV3 = artV3.artManager.addGrammar("Parser grammar", artV3.artManager.getDefaultMainModule(), false, artV3.artManager.artDirectives);

    // System.out.print("\n*** V3 grammar\n" + grammarV3.toString());
    grammarV5 = regressionScriptInterpreter.currentGrammar;
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

      if (!v5v3ElementSetSame(v5Nonterminal.first, new TreeSet<>(v3Nonterminal.getFirst()), artV3.artManager.getDefaultMainModule(), v5Nonterminal)) {
        System.out.println("First for " + v5Nonterminal + " differ:\nV5 " + v5Nonterminal.first + "\nV3 " + new TreeSet<>(v3Nonterminal.getFirst()) + "\n");
        good = false;
      }

      if (!v5v3ElementSetSame(v5Nonterminal.follow, new TreeSet<>(v3Nonterminal.getFollow()), artV3.artManager.getDefaultMainModule(), null)) {
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

  private boolean v5v3RegressionCheckFirstAndFollowInstanceSetsRec(GrammarNode v5, ARTV3 artV3) {
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

  private boolean v5v3RegressionCheckFirstAndFollowInstanceSets(Grammar grammarV5, ARTV3 artV3) {
    boolean good = true;
    for (GrammarElement e : grammarV5.elements.keySet())
      if (e.kind == GrammarKind.N) good &= v5v3RegressionCheckFirstAndFollowInstanceSetsRec(grammarV5.rules.get(e).alt, artV3);

    return good;
  }

  Map<String, Set<ARTGrammarElement>> v3InstanceFirsts = new HashMap<>();
  Map<String, Set<ARTGrammarElement>> v3InstanceFollows = new HashMap<>();
  Set<String> checked = new HashSet<>();

  void v5v3RegressionGatherV3FirstAndFollowInstanceSetsRec(ARTGrammarInstance v3) {
    if (v3 == null) return;
    // System.out.println(
    // "v5v3RegressionGatherV3FirstAndFollowInstanceSetsRec at [" + v3.getKey() + "] " + v3.toGrammarString() + " first=" + v3.first + " follow=" +
    // v3.follow);
    if (v3 instanceof ARTGrammarInstanceSlot) {
      v3InstanceFirsts.put(v3.toGrammarString(".").replaceAll("\\s", ""), v3.first); // (v3.getSibling() == null ? v3.first : v3.getSibling().first));
      v3InstanceFollows.put(v3.toGrammarString(".").replaceAll("\\s", ""), (v3.getSibling() == null ? v3.follow : v3.getSibling().follow));
    }
    v5v3RegressionGatherV3FirstAndFollowInstanceSetsRec(v3.getChild());
    v5v3RegressionGatherV3FirstAndFollowInstanceSetsRec(v3.getSibling());
  }

  private boolean v5v3ElementSetSame(Set<GrammarElement> v5, Set<ARTGrammarElement> v3, ARTV3Module artv3Module, GrammarElement v5IgnoreElement) {
    boolean ret = true;

    for (GrammarElement ve5 : v5) {
      if (ve5.equals(v5IgnoreElement)) continue;
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

  GrammarElement v3Element2v5Element(ARTGrammarElement elem) {
    if (elem instanceof ARTGrammarElementTerminalBuiltin) return new GrammarElement(GrammarKind.B, ((ARTGrammarElementTerminal) elem).getId());
    if (elem instanceof ARTGrammarElementTerminalCharacter) return new GrammarElement(GrammarKind.C, ((ARTGrammarElementTerminal) elem).getId());
    if (elem instanceof ARTGrammarElementEoS) return new GrammarElement(GrammarKind.EOS, "$");
    if (elem instanceof ARTGrammarElementEpsilon) return new GrammarElement(GrammarKind.EPS, "#");
    if (elem instanceof ARTGrammarElementNonterminal) return new GrammarElement(GrammarKind.N, elem.toString());
    if (elem instanceof ARTGrammarElementTerminalCaseSensitive) return new GrammarElement(GrammarKind.T, ((ARTGrammarElementTerminal) elem).getId());
    if (elem instanceof ARTGrammarElementTerminalCaseInsensitive) return new GrammarElement(GrammarKind.TI, ((ARTGrammarElementTerminal) elem).getId());

    return null;
  }

  ARTGrammarElement v5Element2v3Element(GrammarElement elem, ARTV3Module artV3Module) {
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
