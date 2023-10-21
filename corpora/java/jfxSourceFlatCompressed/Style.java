package javafx.css;
final public class Style {
public Selector getSelector() {
return selector;
}
public Declaration getDeclaration() {
return declaration;
}
public Style(Selector selector, Declaration declaration) {
this.selector = selector;
this.declaration = declaration;
}
@Override public boolean equals(Object obj) {
if (obj == this) {
return true;
}
if (obj == null) {
return false;
}
if (getClass() != obj.getClass()) {
return false;
}
final Style other = (Style) obj;
if (this.selector != other.selector && (this.selector == null || !this.selector.equals(other.selector))) {
return false;
}
if (this.declaration != other.declaration && (this.declaration == null || !this.declaration.equals(other.declaration))) {
return false;
}
return true;
}
@Override public int hashCode() {
int hash = 3;
hash = 83 * hash + (this.selector != null ? this.selector.hashCode() : 0);
hash = 83 * hash + (this.declaration != null ? this.declaration.hashCode() : 0);
return hash;
}
@Override public String toString() {
final StringBuilder sb = new StringBuilder()
.append(String.valueOf(selector))
.append(" { ")
.append(String.valueOf(declaration))
.append( " } ");
return sb.toString();
}
private final Selector selector;
private final Declaration declaration;
}
