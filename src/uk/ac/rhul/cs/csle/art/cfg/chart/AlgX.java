package uk.ac.rhul.cs.csle.art.cfg.chart;

import uk.ac.rhul.cs.csle.art.term.ITerms;

public class AlgX extends ChartBase {

  public AlgX(ITerms iTerms) {
    super(iTerms);
    int n = 10;
    chartInit(n + 1);
    System.out.println(chartToString());
    int sequenceNumber = 1;
    chartUnion(0, 0, sequenceNumber++); // initialise from start sybol
    for (int j = 1; j <= n; j++) { // Scan j from left to right
      for (int k = j - 1; k >= 0; k--)
        chartUnion(j, k, sequenceNumber++);
      chartUnion(j, j, sequenceNumber++);
    }
    System.out.println(chartToString());
  }

}
