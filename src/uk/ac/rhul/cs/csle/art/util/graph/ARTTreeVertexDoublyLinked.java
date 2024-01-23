package uk.ac.rhul.cs.csle.art.util.graph;

public class ARTTreeVertexDoublyLinked extends ARTTreeVertex {
  protected ARTTreeVertexDoublyLinked leftSibling = null;

  public ARTTreeVertexDoublyLinked(Object key, Object payload, ARTTreeVertex child, ARTTreeVertexDoublyLinked rightSibling) {
    super(key, payload);
    this.child = child;
    this.sibling = rightSibling;
    this.leftSibling = rightSibling.leftSibling;
    rightSibling.leftSibling = this;
  }

  public ARTTreeVertexDoublyLinked(Object key, Object payload) {
    super(key, payload);
  }

  public ARTTreeVertexDoublyLinked addChild(ARTTreeVertexDoublyLinked newChild) {
    // System.err.printf("Adding tree vertex " + newChild + " to vertex " + this + "\n");
    if (child == null)
      child = newChild;
    else {
      ARTTreeVertex tmp = child;
      while (tmp.sibling != null)
        tmp = tmp.sibling;

      tmp.sibling = newChild;
      ((ARTTreeVertexDoublyLinked) tmp.sibling).leftSibling = (ARTTreeVertexDoublyLinked) tmp;
    }
    return newChild;
  }

  public ARTTreeVertex getLeftSibling() {
    return leftSibling;
  }

  public void setLeftSibling(ARTTreeVertexDoublyLinked leftSibling) {
    this.leftSibling = leftSibling;
  }

}
