package uk.ac.rhul.cs.csle.art.old.core;

import uk.ac.rhul.cs.csle.art.old.cfg.CYKFamily.TwoFormMemo;
import uk.ac.rhul.cs.csle.art.old.cfg.extract.ARTCompressWhiteSpaceJava;
import uk.ac.rhul.cs.csle.art.old.cfg.extract.ARTCompressWhiteSpaceSML;
import uk.ac.rhul.cs.csle.art.old.cfg.extract.ExtractJLS;
import uk.ac.rhul.cs.csle.art.old.term.TermTool;
import uk.ac.rhul.cs.csle.art.old.term.__int32;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;

public class OLDPipeline {
  private final OLDPipelineParamaters pp;

  public OLDPipeline(OLDPipelineParamaters pp) {
    this.pp = pp;
  }

  public void interpretDynamicDirectives(OLDModule module) {
    pp.startNonterminal = module.defaultStartNonterminal;
    pp.startRelation = module.defaultStartRelation;

    for (Integer directiveTerm : pp.dynamicDirectives) {
      // System.out.println("Interpreting " + pp.iTerms.toString(directiveTerm));
      Integer directiveElementTerm = pp.iTerms.getSubterm(directiveTerm, 0);
      String directiveName = pp.iTerms.getTermSymbolString(directiveElementTerm);
      directiveName = directiveName.substring(1, directiveName.length() - 1);

      int firstArgument = 0;
      if (pp.iTerms.getTermArity(directiveTerm) > 1) firstArgument = pp.iTerms.getSubterm(directiveTerm, 1);
      int secondArgument = 0;
      if (pp.iTerms.getTermArity(directiveTerm) > 2) secondArgument = pp.iTerms.getSubterm(directiveTerm, 2);

      switch (directiveName) {
      case "main": {
        OLDModule newMain = pp.modules.get(firstArgument);

        if (newMain == null) throw new ARTUncheckedException("unknown module in directive !main " + pp.tt.toString(firstArgument));

        pp.mainModule = newMain;
        break;
      }
      case "trace":
        pp.traceLevel = new __int32(firstArgument).javaValue;
        break;

      case "start": {
        // if (directiveName.equals("cfgNonterminal":
        // startNonterminal = firstArgument;
        // case "TRRELATION": startRelation = firstArgument;
        //
        break;
      }
      case "input":
        pp.inputTerm = firstArgument;
        break;

      case "parseTwoFormMemo":
        pp.parser = new TwoFormMemo(pp);
        break;

      case "lexDFA":
        break;

      case "result":
        pp.resultTerm = firstArgument;
        break;

      case "rewritePure":
        pp.rewritePure = trueFalse(firstArgument);
        break;

      case "rewritePreorder":
        pp.rewritePreorder = trueFalse(firstArgument);
        break;

      case "rewritePostOrder":
        pp.rewritePostorder = trueFalse(firstArgument);
        break;

      case "rewriteOneStep": {
        pp.rewriteOneStep = trueFalse(firstArgument);
        pp.rewriteContractum = trueFalse(firstArgument);
        break;
      }

      case "rewriteContractum":
        pp.rewriteContractum = trueFalse(firstArgument);
        break;

      case "rewriteResume":
        pp.rewriteResume = trueFalse(firstArgument);
        break;

      case "try": {
        if (firstArgument != 0) {
          // System.out.println("Processing try first argument " + pp.iTerms.toString(firstArgument));
          String rootSymbol = pp.iTerms.getTermSymbolString(firstArgument);
          if (rootSymbol.charAt(0) == '\'')
            pp.inputString = ARTText.readFile(rootSymbol.substring(1, rootSymbol.length() - 1));
          else if (rootSymbol.charAt(0) == '"')
            pp.inputString = rootSymbol.substring(1, rootSymbol.length() - 1);
          else
            pp.inputTerm = firstArgument;
        }

        if (secondArgument != 0) pp.resultTerm = secondArgument;

        if (pp.inputTerm == 0 && pp.inputString == null) throw new ARTUncheckedException("!try directive has no input specified");

        if (pp.inputString != null) {
          System.out.print("*** !try \"" + (pp.inputString.length() <= 20 ? (pp.inputString + "\"") : (pp.inputString.substring(0, 20) + "...")));
        } else
          System.out.print("*** !try " + pp.render(pp.inputTerm));

        if (pp.resultTerm != 0) System.out.print(" resulting in " + pp.iTerms.toString(pp.resultTerm));
        System.out.println();

        tryPipeline();
        break;
      }

      case "termTool":
        new TermTool(pp.iTerms);
        break;

      case "extractJLS":
        new ExtractJLS(pp.iTerms.getTermSymbolString(pp.iTerms.getSubterm(directiveTerm, 1, 0)),
            pp.iTerms.getTermSymbolString(pp.iTerms.getSubterm(directiveTerm, 2, 0)), pp.iTerms.getTermSymbolString(pp.iTerms.getSubterm(directiveTerm, 3, 0)),
            pp.iTerms.getTermSymbolString(pp.iTerms.getSubterm(directiveTerm, 4, 0)), pp.iTerms.getTermSymbolString(pp.iTerms.getSubterm(directiveTerm, 5)));
        break;

      case "compressWhitespaceJava": {
        new ARTCompressWhiteSpaceJava(pp.iTerms.getTermSymbolString(pp.iTerms.getSubterm(directiveTerm, 1, 0)),
            pp.iTerms.getTermSymbolString(pp.iTerms.getSubterm(directiveTerm, 2, 0)));
        break;
      }

      case "compressWhitespaceSML": {
        new ARTCompressWhiteSpaceSML(pp.iTerms.getTermSymbolString(pp.iTerms.getSubterm(directiveTerm, 1, 0)),
            pp.iTerms.getTermSymbolString(pp.iTerms.getSubterm(directiveTerm, 2, 0)));
        break;
      }

      case "v3", "v4", "v4old":
        break; // Just ignore these

      default:
        throw new ARTUncheckedException("unknown directive !" + directiveName);
      }
    }

    if (pp.goodTest != 0 || pp.badTest != 0) System.out.println("try result summary: " + pp.goodTest + " good, " + pp.badTest + " bad");
  }

  private boolean trueFalse(int term) {
    // System.out.println("trueFalse() on " + iTerms.toString(term));
    if (pp.iTerms.getTermArity(term) == 0) return true;
    return pp.iTerms.hasSymbol(pp.iTerms.getSubterm(term, 0), "True");
  }

  void parse(int parser) {
    System.out.println("Parsing using process " + pp.iTerms.getString(parser));
  }

  private void tryPipeline() {
    if (pp.inputString != null) {
      pipelineLex();
      pipelineParse();
    }

    pp.eSOS.stepper();

    if (pp.inputTerm != 0) pipelineEvaluate();
  }

  private void pipelineLex() {
    // new ARTLexDFA(pp.mainModule, pp.iTerms).recogniseViaMap("lexTest.txt");
  }

  private void pipelineParse() {
    pp.parser.parse();
  }

  private void pipelineEvaluate() {
    // TODO Auto-generated method stub
  }

}
