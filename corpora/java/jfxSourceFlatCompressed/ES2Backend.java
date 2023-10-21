package com.sun.scenario.effect.compiler.backend.hw;
import java.util.Map;
import com.sun.scenario.effect.compiler.JSLParser;
import com.sun.scenario.effect.compiler.model.Precision;
import com.sun.scenario.effect.compiler.tree.FuncDef;
import com.sun.scenario.effect.compiler.tree.JSLVisitor;
public class ES2Backend extends GLSLBackend {
public ES2Backend(JSLParser parser, JSLVisitor visitor) {
super(parser, visitor);
}
private static final Map<String, String> VAR_MAP = Map.of(
"pos0", "texCoord0",
"pos1", "texCoord1",
"color", "gl_FragColor",
"jsl_vertexColor", "perVertexColor");
private static final Map<String, String> FUNC_MAP = Map.of(
"sample", "texture2D",
"ddx", "dFdx",
"ddy", "dFdy",
"intcast", "int");
@Override
protected String getVar(String v) {
String s = VAR_MAP.get(v);
return (s != null) ? s : v;
}
@Override
protected String getFuncName(String f) {
String s = FUNC_MAP.get(f);
return (s != null) ? s : f;
}
@Override
protected String getPrecision(Precision p) {
return p.name();
}
@Override
public void visitFuncDef(FuncDef d) {
String name = d.getFunction().getName();
if ("mask".equals(name) || "paint".equals(name)) {
output("lowp ");
}
super.visitFuncDef(d);
}
@Override
protected String getHeader() {
StringBuilder sb = new StringBuilder();
sb.append("#ifdef GL_ES\n");
sb.append("#extension GL_OES_standard_derivatives : enable\n");
sb.append("#ifdef GL_FRAGMENT_PRECISION_HIGH\n");
sb.append("precision highp float;\n");
sb.append("precision highp int;\n");
sb.append("#else\n");
sb.append("precision mediump float;\n");
sb.append("precision mediump int;\n");
sb.append("#endif\n");
sb.append("#else\n");
sb.append("#define highp\n");
sb.append("#define mediump\n");
sb.append("#define lowp\n");
sb.append("#endif\n");
if (maxTexCoordIndex >= 0) {
sb.append("varying vec2 texCoord0;\n");
}
if (maxTexCoordIndex >= 1) {
sb.append("varying vec2 texCoord1;\n");
}
if (isVertexColorReferenced) {
sb.append("varying lowp vec4 perVertexColor;\n");
}
if (isPixcoordReferenced) {
sb.append("uniform vec4 jsl_pixCoordOffset;\n");
}
return sb.toString();
}
}
