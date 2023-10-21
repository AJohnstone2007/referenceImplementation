package test.com.sun.javafx.pgstub;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.PathConsumer2D;
public class SVGPathImpl extends Path2D implements PathConsumer2D {
String content;
int windingRule;
public SVGPathImpl(String content, int windingRule) {
this.content = content;
this.windingRule = windingRule;
}
public String getContent() {
return content;
}
public void setContent(String content) {
this.content = content;
}
@Override
public String toString() {
return " content=" + content + ", windingRule=" + windingRule;
}
}
