package uk.ac.rhul.cs.csle.art.cfg.chart;

import uk.ac.rhul.cs.csle.art.term.ITerms;

public class AlgX extends ChartBase {

  public AlgX(ITerms iTerms) {
    super(iTerms);

    chartInit(11);
    System.out.println(chartToString());

  }

}
