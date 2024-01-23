package uk.ac.rhul.cs.csle.art.v3;

import java.io.FileNotFoundException;

import uk.ac.rhul.cs.csle.art.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.core.OLDDirectives;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.util.text.ARTTextHandlerString;
import uk.ac.rhul.cs.csle.art.v3.alg.gll.ARTGLLGenerator;
import uk.ac.rhul.cs.csle.art.v3.lex.ARTLexerV3;
import uk.ac.rhul.cs.csle.art.v3.manager.ARTManager;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.ARTGrammar;

public class ARTV3 {
  public ARTManager artManager;

  public ARTV3() {
    artManager = new ARTManager();
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
    case grammarWrite:
      ARTGrammar printGrammar = artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), false, artManager.artDirectives);
      printGrammar.prettyPrint("ARTCharacterGrammar.art", true, false, false, false, false);
      printGrammar.prettyPrint("ARTLexerGrammar.art", false, true, false, false, false);
      printGrammar.prettyPrint("ARTParserGrammar.art", false, false, true, false, false);
      printGrammar.prettyPrint("ARTPrettyGrammar.art", false, false, false, true, false);
      printGrammar.prettyPrint("ARTTokenGrammar.art", false, false, false, false, true);
      printGrammar.prettyPrintAllChoosers();

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
      OLDDirectives ob = artManager.artDirectives;
      if (artManager.artDirectives.inputs.isEmpty()) throw new ARTUncheckedException("No input specified");
      String input = artManager.artDirectives.inputs.get(0);

      ARTGrammar grammar = artManager.addGrammar("Lexer data grammar", artManager.getDefaultMainModule(), true, ob);

      ARTLexerV3 lexerV3 = new ARTLexerV3(grammar);

      lexerV3.lexicaliseToLinkedTWESet(input);
      lexerV3.postProcess();
    }
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
