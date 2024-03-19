package uk.ac.rhul.cs.csle.art.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Relation<T1, T2> {
  private final Map<T1, Set<T2>> map;

  public Relation() {
    super();
    map = new TreeMap<>();
  }

  public Relation(Relation<T1, T2> relation) {
    this();
    for (T1 t1 : relation.getDomain())
      map.put(t1, new TreeSet<>(relation.getCodomain(t1)));
  }

  public Set<T1> getDomain() {
    return map.keySet();
  }

  public Set<T2> getCodomain(T1 t1) {
    return map.get(t1);
  }

  public boolean add(T1 src, T2 dst) {
    if (map.get(src) == null) map.put(src, new TreeSet<>());
    return map.get(src).add(dst);
  }

  public void remove(T1 src, T2 dst) {
    if (map.get(src) == null) return;
    map.get(src).remove(dst);
  }

  @SuppressWarnings("unchecked")
  public void transitiveClosure() {
    boolean changed = true;
    while (changed) {
      changed = false;
      for (T1 t1 : new HashSet<>(getDomain())) { // We may add edges from t1 (horrible)
        for (T2 t2 : new HashSet<>(getCodomain(t1))) { // Odd: even if guarded by t1 != t2 this throws concurrentModification
          Set<T2> nextEdges = getCodomain((T1) t2);
          if (nextEdges != null) for (T2 next : nextEdges)
            changed |= add(t1, next);
        }
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (T1 t1 : getDomain()) {
      sb.append(t1 + " ->");
      for (T2 t2 : getCodomain(t1))
        sb.append(" " + t2);
      sb.append("\n");
    }
    return sb.toString();
  }

  public String toDotString() {
    StringBuilder sb = new StringBuilder();
    sb.append("digraph \"GDG\" {\n"
        + "graph[ordering=out ranksep=0.1]\n node[fontname=Helvetica fontsize=9 shape=box height=0 width=0 margin=0.04 color=gray]\nedge[arrowsize=0.1 color=gray]");

    for (T1 t1 : getDomain())
      for (T2 t2 : getCodomain(t1))
        sb.append("\"" + t1 + "\" -> \"" + t2 + "\"\n");
    sb.append("}");
    return sb.toString();
  }
}
