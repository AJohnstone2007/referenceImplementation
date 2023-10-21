import com.sun.scenario.effect.compiler.JSLC;
import com.sun.scenario.effect.compiler.JSLC.JSLCInfo;
import com.sun.scenario.effect.impl.state.LinearConvolveRenderState;
import java.io.File;
public class CompileLinearConvolve {
private static void compileLinearConvolve(JSLCInfo jslcinfo, String name)
throws Exception
{
int outTypes = jslcinfo.outTypes;
jslcinfo.shaderName = "Effect";
File baseFile = jslcinfo.getJSLFile(name);
String base = CompileJSL.readFile(baseFile);
long basetime = baseFile.lastModified();
jslcinfo.outTypes = (outTypes & JSLC.OUT_HW_SHADERS);
int lastpeersize = -1;
for (int i = 1; i < LinearConvolveRenderState.MAX_COMPILED_KERNEL_SIZE; i += 4) {
int peersize = LinearConvolveRenderState.getPeerSize(i);
if (peersize != lastpeersize) {
String source = String.format(base, peersize/4, peersize/4);
jslcinfo.peerName = name + "_" + peersize;
JSLC.compile(jslcinfo, source, basetime);
lastpeersize = peersize;
}
}
jslcinfo.outTypes = (outTypes & JSLC.OUT_HW_PEERS);
jslcinfo.peerName = name;
jslcinfo.genericsName = "LinearConvolveRenderState";
jslcinfo.interfaceName = null;
int peersize = LinearConvolveRenderState.MAX_COMPILED_KERNEL_SIZE / 4;
String genericbase = String.format(base, peersize, 0);
JSLC.compile(jslcinfo, genericbase, basetime);
jslcinfo.outTypes = (outTypes & JSLC.OUT_SW_PEERS);
JSLC.compile(jslcinfo, genericbase, basetime);
}
public static void main(String[] args) throws Exception {
JSLCInfo jslcinfo = new JSLCInfo("LinearConvolve[Shadow]");
int index = jslcinfo.parseArgs(args);
if (index != args.length - 1) {
jslcinfo.usage(System.err);
}
String arg = args[index];
if (arg.equals("LinearConvolve") ||
arg.equals("LinearConvolveShadow"))
{
compileLinearConvolve(jslcinfo, arg);
} else {
jslcinfo.error("Unrecognized argument: "+arg);
}
}
}
