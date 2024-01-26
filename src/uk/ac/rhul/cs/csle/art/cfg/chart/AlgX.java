package uk.ac.rhul.cs.csle.art.cfg.chart;

import uk.ac.rhul.cs.csle.art.term.ITerms;

public class AlgX extends ChartBase {

  public AlgX(ITerms iTerms) {
    super(iTerms);
    int n = 10;
    chartInit(n + 1);
    System.out.println("Operation key: -1:initFromStart  -2:initFromPreviousColumb  -3:crossProduct  -4:completeColumn");
    int sequenceNumber = 1;
    chartUnion(0, 0, sequenceNumber++); // initialise from start sybol
    initialiseFromStartSymbol();
    for (int j = 1; j <= n; j++) { // Scan j from left to right
      chartUnion(j, j - 1, sequenceNumber++);
      initialiseFromPreviousColumn(i, j);
      for (int k = j - 2; k >= 0; k--) {
        chartUnion(j, k, sequenceNumber++);
        crossProduct(j, k);
      }
      chartUnion(j, j, sequenceNumber++);
      completeColumn(j);
    }
    System.out.println(chartToString());
  }

  private void initialiseFromStartSymbol() {
    chartUnion(0, 0, -1);
  }

  private void initialiseFromPreviousColumn(int i, int j) {
    chartUnion(j, j - 1, -2);
  }

  private void crossProduct(int j, int k) {
    chartUnion(j, k, -3);
  }

  private void completeColumn(int j) {
    chartUnion(j, j, -4);
  }

}
