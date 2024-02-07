package uk.ac.rhul.cs.csle.art.old.v3;

import java.io.FileNotFoundException;

import uk.ac.rhul.cs.csle.art.old.cfg.rdFamily.ARTOSBRDGenerator;
import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.old.core.Directives;
import uk.ac.rhul.cs.csle.art.old.util.slotarray.ARTSlotArray;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTTextHandlerFile;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTTextHandlerString;
import uk.ac.rhul.cs.csle.art.old.v3.alg.ARTParserBase;
import uk.ac.rhul.cs.csle.art.old.v3.alg.cnp.generatedpool.ARTCNPGenerator;
import uk.ac.rhul.cs.csle.art.old.v3.alg.cnp.indexedapi.ARTCNPIndexedAPI;
import uk.ac.rhul.cs.csle.art.old.v3.alg.cnp.indexedpool.ARTCNPIndexedPool;
import uk.ac.rhul.cs.csle.art.old.v3.alg.cnp.linkedapi.ARTCNPLinkedAPI;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earley.indexedapi.ARTEarleyIndexedAPI;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earley.indexedpool.ARTEarleyIndexedPool;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earley.linkedapi.ARTEarleyLinkedAPI;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earley2007.linkedapi.ARTEarley2007LinkedAPI;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.indexedapi.ARTEarleyTableIndexedAPI;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.indexedpool.ARTEarleyTableIndexedPool;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.linkedapi.ARTEarleyTableLinkedAPI;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.support.ARTEarleyTableDataIndexed;
import uk.ac.rhul.cs.csle.art.old.v3.alg.gll.ARTGLLGenerator;
import uk.ac.rhul.cs.csle.art.old.v3.alg.lcnp.linkedapi.ARTLCNPLinkedAPI;
import uk.ac.rhul.cs.csle.art.old.v3.lex.ARTLexerV3;
import uk.ac.rhul.cs.csle.art.old.v3.manager.ARTManager;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;

public class ARTV3 {
  public ARTManager artManager;

  public ARTV3() {
    artManager = new ARTManager();
  }

  public ARTV3(String specification) {
    this();
    artManager.parseARTSpecification(specification);
    runFromDirectives();
  }

  private static void runInterpreter(ARTManager artManager, ARTParserBase interpreter) {
    // System.out.println("runInterpreter with option block " + artManager.artDirectives);
    if (artManager.artDirectives.inputs.size() == 0)
      throw new ARTUncheckedException("No input specified\n\n");
    else {
      String inputFilename = "???";
      if (!artManager.artDirectives.inputFilenames.isEmpty()) inputFilename = artManager.artDirectives.inputFilenames.get(0);
      interpreter.artParse(artManager.artDirectives.inputs.get(0));
      interpreter.artLog(inputFilename, false);
    }
  }

  public static void main(final String[] args) throws FileNotFoundException {
    try {
      ARTV3 artv3 = new ARTV3();
      String processedCommandLine = artv3.processCommandLine(args);
      artv3.artManager.parseARTSpecification(processedCommandLine);
      // System.out.println("*** Debug - module choosers%n" + artManager.getDefaultMainModule().getChoosers());
      artv3.runFromDirectives();
    } catch (ARTUncheckedException e) {
      ARTText.printFatal(e.getMessage());
    }
  }

  public void runFromDirectives() {
    switch (artManager.artDirectives.algorithmMode()) {
    case generateDepth:
      artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), false, artManager.artDirectives).generateStrings(false, false, 0);
      break;

    case generateBreadth:
      artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), false, artManager.artDirectives).generateStrings(true, false, 0);
      break;

    case generateRandom:
      artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), false, artManager.artDirectives).generateStrings(true, true, 0);
      break;

    case grammarWrite:
      ARTGrammar printGrammar = artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), false, artManager.artDirectives);

      System.out.println("Printing grammar\n" + printGrammar);
      printGrammar.prettyPrint("ARTCharacterGrammar.art", true, false, false, false, false);
      printGrammar.prettyPrint("ARTLexerGrammar.art", false, true, false, false, false);
      printGrammar.prettyPrint("ARTParserGrammar.art", false, false, true, false, false);
      printGrammar.prettyPrint("ARTPrettyGrammar.art", false, false, false, true, false);
      printGrammar.prettyPrint("ARTTokenGrammar.art", false, false, false, false, true);
      printGrammar.prettyPrintAllChoosers();

      break;

    case osbrdGenerator:
      ARTOSBRDGenerator artOSBRDGenerator = new ARTOSBRDGenerator(
          artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), false, artManager.artDirectives), artManager.artDirectives);
      ARTTextHandlerString stringHandler = new ARTTextHandlerString();
      ARTText text = new ARTText(stringHandler);
      artOSBRDGenerator.generateParser(text);
      ARTText.writeFile(artManager.artDirectives.s("outputDirectory"), artManager.artDirectives.s("parserName") + ".java", stringHandler.getText());
      break;

    case gllTWEGeneratorPool:
    case gllGeneratorPool:
    case mgllGeneratorPool:

      // Make a new grammar and generator using the default start nonterminal in the default module
      ARTGrammar artParserGrammar = artManager.addGrammar(artManager.defaultName, artManager.getDefaultMainModule(), false, artManager.artDirectives);

      ARTGLLGenerator artParserGenerator = new ARTGLLGenerator(artParserGrammar, artManager.artDirectives);

      // Generate parser and lexer as strings, and then write them out
      ARTTextHandlerString artStringHandler = new ARTTextHandlerString();
      ARTText artStringText = new ARTText(artStringHandler);

      artParserGenerator.generateParser(artStringText);
      ARTText.writeFile(artManager.artDirectives.s("outputDirectory"), artManager.artDirectives.s("parserName") + ".java",
          artStringHandler.getText()/* + slotArray.toJavaString() */);

      artStringHandler.clear();
      artParserGenerator.generateLexer(artStringText);
      ARTText.writeFile(artManager.artDirectives.s("outputDirectory"), artManager.artDirectives.s("lexerName") + ".java", artStringHandler.getText());
      break;

    case lexerData: {
      Directives ob = artManager.artDirectives;
      if (artManager.artDirectives.inputs.isEmpty()) throw new ARTUncheckedException("No input specified");
      String input = artManager.artDirectives.inputs.get(0);

      ARTGrammar grammar = artManager.addGrammar("Lexer data grammar", artManager.getDefaultMainModule(), true, ob);

      ARTLexerV3 lexerV3 = new ARTLexerV3(grammar);

      lexerV3.lexicaliseToLinkedTWESet(input);
      lexerV3.postProcess();
    }
      break;

    case lexDFA: {
      Directives ob = artManager.artDirectives;

      String input = ARTText.readFile("test.str");
      // System.out.println("Lexing " + input);
      ARTGrammar grammar = artManager.addGrammar("LexDFA grammar", artManager.getDefaultMainModule(), true, ob);

      ARTLexerV3 lexerV3 = new ARTLexerV3(grammar);

      lexerV3.buildDFA();
      lexerV3.lexicaliseUsingDFAToLinkedTWESetViaMap(input);
    }
      break;

    case earley2007LinkedAPI:
      runInterpreter(artManager,
          new ARTEarley2007LinkedAPI(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.artDirectives)));
      break;

    case earleyLinkedAPI:
      runInterpreter(artManager,
          new ARTEarleyLinkedAPI(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.artDirectives)));
      break;

    case earleyIndexedData:
      ARTText t = new ARTText(new ARTTextHandlerFile("ARTStaticSlotArray.h"));
      new ARTSlotArray(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.artDirectives)).toCString(t);
      t.close();
      break;

    case earleyIndexedAPI:
      runInterpreter(artManager,
          new ARTEarleyIndexedAPI(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.artDirectives)));
      break;

    case earleyIndexedPool:
      runInterpreter(artManager, new ARTEarleyIndexedPool(
          new ARTSlotArray(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.artDirectives))));
      break;

    case earleyTableLinkedAPI:
      runInterpreter(artManager,
          new ARTEarleyTableLinkedAPI(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.artDirectives)));
      break;

    case earleyTableIndexedData:
      ARTGrammar artGrammar = artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.artDirectives);
      ARTText t1 = new ARTText(new ARTTextHandlerFile("ARTStaticEarleyTable.h"));
      new ARTEarleyTableDataIndexed(artGrammar).toCString(t1);
      t1.close();
      break;

    case earleyTableIndexedAPI:
      runInterpreter(artManager,
          new ARTEarleyTableIndexedAPI(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.artDirectives)));
      break;

    case earleyTableIndexedPool:
      runInterpreter(artManager,
          new ARTEarleyTableIndexedPool(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.artDirectives)));
      break;

    case cnpGeneratorPool:

      // Make a new grammar and generator using the default start nonterminal in the default module
      ARTGrammar artCNPGrammar = artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), false, artManager.artDirectives);

      ARTCNPGenerator artCNPGenerator = new ARTCNPGenerator(artCNPGrammar);

      // Generate parser and lexer as strings, and then write them out
      artStringHandler = new ARTTextHandlerString();
      artStringText = new ARTText(artStringHandler);

      artCNPGenerator.generateParser(artStringText);
      ARTText.writeFile(artManager.artDirectives.s("outputDirectory"), artManager.artDirectives.s("parserName") + ".java",
          artStringHandler.getText()/* + slotArray.toJavaString() */);

      artStringHandler.clear();
      ARTText.writeFile(artManager.artDirectives.s("outputDirectory"), artManager.artDirectives.s("lexerName") + ".java",
          "class " + artManager.artDirectives.s("lexerName") + "{}\n");

      break;

    case cnpLinkedAPI:
      runInterpreter(artManager,
          new ARTCNPLinkedAPI(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.artDirectives)));
      break;

    case cnpIndexedAPI:
      runInterpreter(artManager,
          new ARTCNPIndexedAPI(new ARTSlotArray(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.artDirectives))));
      break;

    case cnpIndexedPool:
      runInterpreter(artManager,
          new ARTCNPIndexedPool(new ARTSlotArray(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.artDirectives))));
      break;

    case lcnpLinkedAPI:
      runInterpreter(artManager,
          new ARTLCNPLinkedAPI(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.artDirectives)));
      break;

    default:
      throw new ARTUncheckedException("no implementation for algorithm mode " + artManager.artDirectives.algorithmMode());
    }
  }

  public String processCommandLine(final String[] args) {
    // Process command line - if an argument has exactly one dot in it, treat it as a filename

    if (args.length == 0) throw new ARTUncheckedException("no arguments supplied");
    StringBuilder sb = new StringBuilder();

    for (String arg : args) {
      int dotCount = 0;
      for (int i = 0; i < arg.length(); i++)
        if (arg.charAt(i) == '.') dotCount++;

      if (dotCount == 1) {
        artManager.defaultName = arg;
        sb.append(ARTText.readFile(arg) + "\n");
      } else
        sb.append(arg + "\n");
    }

    // System.out.println("Command line - " + sb);
    return sb.toString();
  }
}
