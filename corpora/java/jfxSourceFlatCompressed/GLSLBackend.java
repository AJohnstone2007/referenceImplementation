package com.sun.scenario.effect.compiler.backend.hw;
import java.util.Map;
import com.sun.scenario.effect.compiler.JSLParser;
import com.sun.scenario.effect.compiler.model.Qualifier;
import com.sun.scenario.effect.compiler.model.Type;
import com.sun.scenario.effect.compiler.tree.JSLVisitor;
public class GLSLBackend extends SLBackend {
public GLSLBackend(JSLParser parser, JSLVisitor visitor) {
super(parser, visitor);
}
private static final Map<String, String> QUAL_MAP = Map.of(
"const", "const",
"param", "uniform");
private static final Map<String, String> TYPE_MAP = Map.ofEntries(
Map.entry("void", "void"),
Map.entry("float", "float"),
Map.entry("float2", "vec2"),
Map.entry("float3", "vec3"),
Map.entry("float4", "vec4"),
Map.entry("int", "int"),
Map.entry("int2", "ivec2"),
Map.entry("int3", "ivec3"),
Map.entry("int4", "ivec4"),
Map.entry("bool", "bool"),
Map.entry("bool2", "bvec2"),
Map.entry("bool3", "bvec3"),
Map.entry("bool4", "bvec4"),
Map.entry("sampler", "sampler2D"),
Map.entry("lsampler", "sampler2D"),
Map.entry("fsampler", "sampler2D"));
private static final Map<String, String> VAR_MAP = Map.of(
"pos0", "gl_TexCoord[0].st",
"pos1", "gl_TexCoord[1].st",
"color", "gl_FragColor",
"jsl_vertexColor", "gl_Color");
private static final Map<String, String> FUNC_MAP = Map.of(
"sample", "jsl_sample",
"ddx", "dFdx",
"ddy", "dFdy",
"intcast", "int",
"any", "any",
"length", "length");
static String PIXCOORD = "vec2 pixcoord = vec2(\n"+
"    gl_FragCoord.x-jsl_pixCoordOffset.x,\n" +
"    ((jsl_pixCoordOffset.z-gl_FragCoord.y)*jsl_pixCoordOffset.w)-jsl_pixCoordOffset.y);\n";
static String MAIN = "void main() {\n";
@Override
protected String getType(Type t) {
return TYPE_MAP.get(t.toString());
}
@Override
protected String getQualifier(Qualifier q) {
return QUAL_MAP.get(q.toString());
}
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
protected String getHeader() {
StringBuilder sb = new StringBuilder();
sb.append("uniform float jsl_pixCoordYOffset;\n");
sb.append("vec2 pixcoord = vec2(gl_FragCoord.x, jsl_pixCoordYOffset-gl_FragCoord.y);\n");
sb.append("uniform vec2 jsl_posValueYFlip;\n");
sb.append("vec4 jsl_sample(sampler2D img, vec2 pos) {\n");
sb.append("    pos.y = (jsl_posValueYFlip.x - pos.y) * jsl_posValueYFlip.y;\n");
sb.append("    return texture2D(img, pos);\n");
sb.append("}\n");
return sb.toString();
}
@Override
public String getShader() {
String answer = super.getShader();
if (isPixcoordReferenced) {
answer = answer.replace(MAIN, MAIN + PIXCOORD);
}
return answer;
}
}
