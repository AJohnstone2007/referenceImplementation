package uk.ac.rhul.cs.csle.art.old.v3.tg;

/******************************************************************************
 * GLLtest.java
 *
 * This is a test harness for ART generated Java parsers
 *
 * (c) Adrian Johnstone 2013
 *****************************************************************************/
import java.io.FileNotFoundException;

import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTTextHandlerConsole;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTTextLevel;

public class ARTTG {

  static ARTText text;

  public static void main(String[] args) throws FileNotFoundException {

    // process options
    String inputFilename = null;
    boolean vv3 = true;

    text = new ARTText(new ARTTextHandlerConsole());

    for (int i = 0; i < args.length; i++) {
      // System.out.printf("processing argument %d '%s'\n", i, args[i]);
      if (args[i].equals("-v3"))
        vv3 = true;
      else if (args[i].equals("-v2")) {
        vv3 = false;
      } else if (args[i].charAt(0) != '-')
        inputFilename = args[i];
      else {
        System.out.printf("Illegal command line option %s\n", args[i]);
        System.exit(0);
      }
    }

    if (inputFilename == null) text.printf(ARTTextLevel.FATAL, "Fatal error: no input file specified\n");

    ARTTGParser parser = new ARTTGParser(new ARTTGLexer());
    String input = ARTText.readFile(inputFilename);
    ARTTGParser.ARTAT_ART_text attributes = new ARTTGParser.ARTAT_ART_text();
    attributes.vv3 = vv3;
    parser.artParse(input, attributes);

    parser.artDisambiguateOrderedLongest();
    parser.artSPPFSelectOne();
    parser.artEvaluator(attributes);
  }
}
