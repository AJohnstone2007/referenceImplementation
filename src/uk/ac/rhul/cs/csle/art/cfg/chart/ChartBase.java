package uk.ac.rhul.cs.csle.art.cfg.chart;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.ParserBase;

public class ChartBase extends ParserBase {
  Set<Integer>[][] chart;

  void initChart(int n) {
    for (int x = 0; x < n + 1; x++)
      for (int y = 0; y <= x; y++)
        chart[x][y] = new HashSet<>();
  }
}
