package com.sun.scenario.effect.compiler.lexer;
import com.sun.scenario.effect.compiler.JSLLexer;
import org.junit.Test;
public class TypeTest extends LexerBase {
@Test
public void floatScalar() throws Exception {
assertRecognized("float");
}
@Test
public void floatVec2() throws Exception {
assertRecognized("float2");
}
@Test
public void floatVec3() throws Exception {
assertRecognized("float3");
}
@Test
public void floatVec4() throws Exception {
assertRecognized("float4");
}
@Test
public void intScalar() throws Exception {
assertRecognized("int");
}
@Test
public void intVec2() throws Exception {
assertRecognized("int2");
}
@Test
public void intVec3() throws Exception {
assertRecognized("int3");
}
@Test
public void intVec4() throws Exception {
assertRecognized("int4");
}
@Test
public void boolScalar() throws Exception {
assertRecognized("bool");
}
@Test
public void boolVec2() throws Exception {
assertRecognized("bool2");
}
@Test
public void boolVec3() throws Exception {
assertRecognized("bool3");
}
@Test
public void boolVec4() throws Exception {
assertRecognized("bool4");
}
@Test
public void sampler() throws Exception {
assertRecognized("sampler");
}
@Test
public void notAType() throws Exception {
assertNotRecognized("double");
}
@Override
protected int expectedTokenType() {
return JSLLexer.TYPE;
}
}
