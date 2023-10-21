import com.sun.scenario.effect.compiler.JSLC;
import com.sun.scenario.effect.compiler.JSLC.JSLCInfo;
import com.sun.scenario.effect.light.Light;
import java.io.File;
public class CompilePhong {
private static final String declPosConst =
"param float3 normalizedLightPosition;";
private static final String funcPosConst =
"float3 Lxyz = normalizedLightPosition;";
private static final String declPosVar =
"param float surfaceScale;\n" +
"param float3 lightPosition;";
private static final String funcPosVar =
"float bumpA = sample(bumpImg, pos0).a;\n" +
"float3 tmp = float3(pixcoord.x, pixcoord.y, surfaceScale*bumpA);\n" +
"float3 Lxyz = normalize(lightPosition - tmp);";
private static final String declRgbConst = "";
private static final String funcRgbConst =
"float3 Lrgb = lightColor;";
private static final String declRgbVar =
"param float3 normalizedLightDirection;\n" +
"param float lightSpecularExponent;";
private static final String funcRgbVar =
"float LdotS = dot(Lxyz, normalizedLightDirection);\n" +
"LdotS = min(LdotS, 0.0);\n" +
"float3 Lrgb = lightColor * pow(-LdotS, lightSpecularExponent);";
public static void main(String[] args) throws Exception {
JSLCInfo jslcinfo = new JSLCInfo("PhongLighting");
jslcinfo.shaderName = "PhongLighting";
int index = jslcinfo.parseArgs(args);
if (index != args.length - 1) {
jslcinfo.usage(System.err);
}
String arg = args[index];
if (!arg.equals(jslcinfo.shaderName)) {
jslcinfo.error("Unrecognized argument: "+arg);
}
File baseFile = jslcinfo.getJSLFile();
String base = CompileJSL.readFile(baseFile);
long basetime = baseFile.lastModified();
for (Light.Type type : Light.Type.values()) {
String posDecl, posFunc, rgbFunc, rgbDecl;
switch (type) {
case DISTANT:
posDecl = declPosConst;
posFunc = funcPosConst;
rgbDecl = declRgbConst;
rgbFunc = funcRgbConst;
break;
case POINT:
posDecl = declPosVar;
posFunc = funcPosVar;
rgbDecl = declRgbConst;
rgbFunc = funcRgbConst;
break;
case SPOT:
posDecl = declPosVar;
posFunc = funcPosVar;
rgbDecl = declRgbVar;
rgbFunc = funcRgbVar;
break;
default:
throw new InternalError();
}
String decls = posDecl + "\n" + rgbDecl;
String funcs = posFunc + "\n" + rgbFunc;
String source = String.format(base, decls, funcs);
jslcinfo.peerName = jslcinfo.shaderName + "_" + type.name();
JSLC.compile(jslcinfo, source, basetime);
}
}
}
