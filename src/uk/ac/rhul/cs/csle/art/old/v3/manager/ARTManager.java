package uk.ac.rhul.cs.csle.art.old.v3.manager;

import java.util.HashMap;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.old.core.OLDDirectives;
import uk.ac.rhul.cs.csle.art.old.util.graph.ARTTree;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTTextHandlerConsole;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.old.v3.manager.module.ARTV3Module;
import uk.ac.rhul.cs.csle.art.old.v3.manager.parser.ARTV3Lexer;
import uk.ac.rhul.cs.csle.art.old.v3.manager.parser.ARTV3Parser;
import uk.ac.rhul.cs.csle.art.old.v3.manager.parser.ARTV3Parser.ARTAT_ART_text;

/**
 * ARTManager - central manager object for a running ART system
 * <P>
 * Maintains a set of ARTModule, a set of ARTGrammar, a text handler, an ART specification parser and a default module
 *
 * @author Adrian Johnstone {@literal <a.johnstone@rhul.ac.uk>}
 * @version 3.1
 *
 */

public class ARTManager {
  // private ARTText text;
  public final OLDDirectives artDirectives = new OLDDirectives();

  private final ARTV3Parser parser;
  private final HashMap<String, ARTV3Module> modules = new HashMap<String, ARTV3Module>();
  private final HashMap<String, ARTGrammar> grammars = new HashMap<String, ARTGrammar>();
  private ARTV3Module defaultMainModule = null;
  public String defaultName = "";

  public ARTManager() {
    this(new ARTText(new ARTTextHandlerConsole()));
  }

  public ARTManager(ARTText text) {
    parser = new ARTV3Parser(new ARTV3Lexer()); // This version uses classical GLL
  }

  public ARTV3Parser getParser() {
    return parser;
  }

  public HashMap<String, ARTV3Module> getModules() {
    return modules;
  }

  public HashMap<String, ARTGrammar> getGrammars() {
    return grammars;
  }

  public ARTV3Module getDefaultMainModule() {
    return defaultMainModule;
  }

  public ARTV3Module setDefaultMainModule(String id) {
    return defaultMainModule = findModule(id);
  }

  public ARTV3Module setDefaultMainModule(ARTV3Module defaultMainModule) {
    return this.defaultMainModule = defaultMainModule;
  }

  public void clearDefaultMainModule() {
    defaultMainModule = null;
  }

  public ARTV3Module findModule(String id) {
    ARTV3Module ret;

    if ((ret = modules.get(id)) == null) modules.put(id, ret = new ARTV3Module(this, id));
    return ret;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (String m : modules.keySet())
      sb.append("* Module " + modules.get(m).toString());

    if (grammars.isEmpty())
      sb.append("* No grammars\n");
    else
      for (String m : grammars.keySet())
        sb.append("* Grammar " + grammars.get(m).toString());

    return sb.toString();
  }

  public ARTTree parseARTSpecification(String artSpecification) {
    ARTAT_ART_text textAttributes = new ARTAT_ART_text();
    textAttributes.artManager = this;
    parser.artDirectives.set("sppfOrderedLongest", true);
    parser.artDirectives.set("sppfSelectOne", true);
    parser.artParse(artSpecification, textAttributes);

    if (!parser.artIsInLanguage) throw new ARTUncheckedException("Syntax error in ART specification");
    if (parser.computeIsAmbiguous(null)) throw new ARTUncheckedException("Internal specification error in artParser.art: ARTParser returns ambiguous SPPF");

    // parser.artRDT.printDot("rdt.dot");
    return parser.artRDT;
  }

  public ARTGrammar addGrammar(String id, ARTV3Module module, boolean augment, OLDDirectives artDirectives) {
    ARTGrammar ret = new ARTGrammar(this, id, module, augment);
    // System.out.println("ART manager added grammar: ");
    // ret.prettyPrint();

    grammars.put(id, ret);
    return ret;
  }

  public void printMemory(String label) {
    Runtime instance = Runtime.getRuntime();

    System.out.println(label + " memory " + (instance.totalMemory() - instance.freeMemory()) + " ("
        + (instance.totalMemory() - instance.freeMemory()) / (1024 * 1024) + "Mbytes)");
  }

  public void forceGC(String label) {
    Runtime instance = Runtime.getRuntime();

    System.out.println(label + " - before forcing garbage collection" + " memory " + (instance.totalMemory() - instance.freeMemory()) + " ("
        + (instance.totalMemory() - instance.freeMemory()) / (1024 * 1024) + "Mbytes)");

    instance.gc();
    instance.gc();

    System.out.println(label + " - after forcing garbage collection" + " memory " + (instance.totalMemory() - instance.freeMemory()) + " ("
        + (instance.totalMemory() - instance.freeMemory()) / (1024 * 1024) + "Mbytes)");
  }
}
