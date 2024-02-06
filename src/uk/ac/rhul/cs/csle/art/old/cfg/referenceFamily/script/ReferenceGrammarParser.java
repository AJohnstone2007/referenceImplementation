package uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.script;

import java.io.FileNotFoundException;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.old.term.*;
import uk.ac.rhul.cs.csle.art.old.util.bitset.ARTBitSet;
import uk.ac.rhul.cs.csle.art.old.util.text.*;
import uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support.*;
import uk.ac.rhul.cs.csle.art.old.v3.lex.*;
import uk.ac.rhul.cs.csle.art.old.v3.manager.*;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.*;
import uk.ac.rhul.cs.csle.art.old.v3.manager.mode.*;
/*******************************************************************************
*
* ReferenceGrammarParser.java
*
*******************************************************************************/
@SuppressWarnings("fallthrough") public class ReferenceGrammarParser extends ARTGLLParserHashPool {
private static boolean[] ARTSet1;
private static boolean[] ARTSet2;
private static boolean[] ARTSet3;
private static boolean[] ARTSet4;
private static boolean[] ARTSet5;
private static boolean[] ARTSet6;
private static boolean[] ARTSet7;
private static boolean[] ARTSet8;
private static boolean[] ARTSet9;
private static boolean[] ARTSet10;
private static boolean[] ARTSet11;
private static boolean[] ARTSet12;
private static boolean[] ARTSet13;
private static boolean[] ARTSet14;
private static boolean[] ARTSet15;
private static boolean[] ARTSet16;
private static boolean[] ARTSet17;
private static boolean[] ARTSet18;
private static boolean[] ARTSet19;
private static boolean[] ARTSet20;
private static boolean[] ARTSet21;
private static boolean[] ARTSet22;
private static boolean[] ARTSet23;
private static boolean[] ARTSet24;
private static boolean[] ARTSet25;
private static boolean[] ARTSet26;
private static boolean[] ARTSet27;
private static boolean[] ARTSet28;
private static boolean[] ARTSet29;
private static boolean[] ARTSet30;
private static boolean[] ARTSet31;
private static boolean[] ARTSet32;
private static boolean[] ARTSet33;
private static boolean[] ARTSet34;
private static boolean[] ARTSet35;
private static boolean[] ARTSet36;
private static boolean[] ARTSet37;
private static boolean[] ARTSet38;
private static boolean[] ARTSet39;
private static boolean[] ARTSet40;
private static boolean[] ARTSet41;
private static boolean[] ARTSet42;
private static boolean[] ARTSet43;
private static boolean[] ARTSet44;
private static boolean[] ARTSet45;
private static boolean[] ARTSet46;
private static boolean[] ARTSet47;
private static boolean[] ARTSet48;
private static boolean[] ARTSet49;
private static boolean[] ARTSet50;
private static boolean[] ARTSet51;
private static boolean[] ARTSet52;
private static boolean[] ARTSet53;
private static boolean[] ARTSet54;
private static boolean[] ARTSet55;
private static boolean[] ARTSet56;
private static boolean[] ARTSet57;
private static boolean[] ARTSet58;
private static boolean[] ARTSet59;
private static boolean[] ARTSet60;
private static boolean[] ARTSet61;
private static boolean[] ARTSet62;
private static boolean[] ARTSet63;
private static boolean[] ARTSet64;
private static boolean[] ARTSet65;
private static boolean[] ARTSet66;
private static boolean[] ARTSet67;
private static boolean[] ARTSet68;
private static boolean[] ARTSet69;
private static boolean[] ARTSet70;
private static boolean[] ARTSet71;
private static boolean[] ARTSet72;
private static boolean[] ARTSet73;
private static boolean[] ARTSet74;
private static boolean[] ARTSet75;
private static boolean[] ARTSet76;
private static boolean[] ARTSet77;
private static boolean[] ARTSet78;
private static boolean[] ARTSet79;
private static boolean[] ARTSet80;
private static boolean[] ARTSet81;
private static boolean[] ARTSet82;
private static boolean[] ARTSet83;
private static boolean[] ARTSet84;
private static boolean[] ARTSet85;
private static boolean[] ARTSet86;

/* Start of artLabel enumeration */
public static final int ARTX_EOS = 0;
public static final int ARTTB_ID = 1;
public static final int ARTTB_INTEGER = 2;
public static final int ARTTB_REAL = 3;
public static final int ARTTB_STRING_DQ = 4;
public static final int ARTTB_STRING_PLAIN_SQ = 5;
public static final int ARTTS__HASH = 6;
public static final int ARTTS__AMPERSAND = 7;
public static final int ARTTS__LPAR = 8;
public static final int ARTTS__RPAR = 9;
public static final int ARTTS__STAR = 10;
public static final int ARTTS__PLUS = 11;
public static final int ARTTS__COMMA = 12;
public static final int ARTTS__PERIOD = 13;
public static final int ARTTS__COLON_COLON_EQUAL = 14;
public static final int ARTTS__COLON_EQUAL = 15;
public static final int ARTTS__EQUAL = 16;
public static final int ARTTS__QUERY = 17;
public static final int ARTTS__UPARROW = 18;
public static final int ARTTS__UPARROW_PLUS = 19;
public static final int ARTTS__UPARROW_UPARROW = 20;
public static final int ARTTS__BAR = 21;
public static final int ARTX_EPSILON = 22;
public static final int ARTL_ART_arguments = 23;
public static final int ARTL_ART_cfgAction = 24;
public static final int ARTL_ART_cfgActionSeq = 25;
public static final int ARTL_ART_cfgActions = 26;
public static final int ARTL_ART_cfgAlt = 27;
public static final int ARTL_ART_cfgAltNoAction = 28;
public static final int ARTL_ART_cfgAlts = 29;
public static final int ARTL_ART_cfgAnnotation = 30;
public static final int ARTL_ART_cfgAssignment = 31;
public static final int ARTL_ART_cfgAttribute = 32;
public static final int ARTL_ART_cfgBuiltinTerminal = 33;
public static final int ARTL_ART_cfgCaseSensitiveTerminal = 34;
public static final int ARTL_ART_cfgDoFirst = 35;
public static final int ARTL_ART_cfgElems = 36;
public static final int ARTL_ART_cfgEpsilon = 37;
public static final int ARTL_ART_cfgEpsilonCarrier = 38;
public static final int ARTL_ART_cfgEquation = 39;
public static final int ARTL_ART_cfgExtended = 40;
public static final int ARTL_ART_cfgFoldOver = 41;
public static final int ARTL_ART_cfgFoldUnder = 42;
public static final int ARTL_ART_cfgInsert = 43;
public static final int ARTL_ART_cfgKleene = 44;
public static final int ARTL_ART_cfgLHS = 45;
public static final int ARTL_ART_cfgNonterminal = 46;
public static final int ARTL_ART_cfgOptional = 47;
public static final int ARTL_ART_cfgPositive = 48;
public static final int ARTL_ART_cfgPrim = 49;
public static final int ARTL_ART_cfgRule = 50;
public static final int ARTL_ART_cfgRules = 51;
public static final int ARTL_ART_cfgSeq = 52;
public static final int ARTL_ART_cfgSlot = 53;
public static final int ARTL_ART_term = 54;
public static final int ARTL_ART_arguments_321 = 55;
public static final int ARTL_ART_arguments_322 = 56;
public static final int ARTL_ART_arguments_323 = 57;
public static final int ARTL_ART_arguments_324 = 58;
public static final int ARTL_ART_arguments_325 = 59;
public static final int ARTL_ART_arguments_326 = 60;
public static final int ARTL_ART_arguments_327 = 61;
public static final int ARTL_ART_arguments_328 = 62;
public static final int ARTL_ART_arguments_329 = 63;
public static final int ARTL_ART_arguments_330 = 64;
public static final int ARTL_ART_arguments_331 = 65;
public static final int ARTL_ART_arguments_332 = 66;
public static final int ARTL_ART_cfgAction_245 = 67;
public static final int ARTL_ART_cfgAction_246 = 68;
public static final int ARTL_ART_cfgAction_247 = 69;
public static final int ARTL_ART_cfgAction_248 = 70;
public static final int ARTL_ART_cfgAction_249 = 71;
public static final int ARTL_ART_cfgAction_250 = 72;
public static final int ARTL_ART_cfgAction_251 = 73;
public static final int ARTL_ART_cfgAction_252 = 74;
public static final int ARTL_ART_cfgAction_253 = 75;
public static final int ARTL_ART_cfgAction_254 = 76;
public static final int ARTL_ART_cfgAction_255 = 77;
public static final int ARTL_ART_cfgAction_256 = 78;
public static final int ARTL_ART_cfgActionSeq_235 = 79;
public static final int ARTL_ART_cfgActionSeq_236 = 80;
public static final int ARTL_ART_cfgActionSeq_237 = 81;
public static final int ARTL_ART_cfgActionSeq_238 = 82;
public static final int ARTL_ART_cfgActionSeq_239 = 83;
public static final int ARTL_ART_cfgActionSeq_240 = 84;
public static final int ARTL_ART_cfgActionSeq_241 = 85;
public static final int ARTL_ART_cfgActionSeq_242 = 86;
public static final int ARTL_ART_cfgActionSeq_243 = 87;
public static final int ARTL_ART_cfgActionSeq_244 = 88;
public static final int ARTL_ART_cfgActions_63 = 89;
public static final int ARTL_ART_cfgActions_64 = 90;
public static final int ARTL_ART_cfgActions_65 = 91;
public static final int ARTL_ART_cfgActions_66 = 92;
public static final int ARTL_ART_cfgActions_67 = 93;
public static final int ARTL_ART_cfgActions_68 = 94;
public static final int ARTL_ART_cfgActions_69 = 95;
public static final int ARTL_ART_cfgActions_70 = 96;
public static final int ARTL_ART_cfgActions_71 = 97;
public static final int ARTL_ART_cfgActions_72 = 98;
public static final int ARTL_ART_cfgAlt_35 = 99;
public static final int ARTL_ART_cfgAlt_36 = 100;
public static final int ARTL_ART_cfgAlt_37 = 101;
public static final int ARTL_ART_cfgAlt_38 = 102;
public static final int ARTL_ART_cfgAlt_39 = 103;
public static final int ARTL_ART_cfgAlt_40 = 104;
public static final int ARTL_ART_cfgAlt_41 = 105;
public static final int ARTL_ART_cfgAlt_42 = 106;
public static final int ARTL_ART_cfgAlt_43 = 107;
public static final int ARTL_ART_cfgAlt_44 = 108;
public static final int ARTL_ART_cfgAlt_45 = 109;
public static final int ARTL_ART_cfgAlt_46 = 110;
public static final int ARTL_ART_cfgAlt_47 = 111;
public static final int ARTL_ART_cfgAlt_48 = 112;
public static final int ARTL_ART_cfgAlt_49 = 113;
public static final int ARTL_ART_cfgAlt_50 = 114;
public static final int ARTL_ART_cfgAlt_51 = 115;
public static final int ARTL_ART_cfgAlt_52 = 116;
public static final int ARTL_ART_cfgAlt_53 = 117;
public static final int ARTL_ART_cfgAlt_54 = 118;
public static final int ARTL_ART_cfgAlt_55 = 119;
public static final int ARTL_ART_cfgAlt_56 = 120;
public static final int ARTL_ART_cfgAlt_57 = 121;
public static final int ARTL_ART_cfgAlt_58 = 122;
public static final int ARTL_ART_cfgAltNoAction_105 = 123;
public static final int ARTL_ART_cfgAltNoAction_106 = 124;
public static final int ARTL_ART_cfgAltNoAction_107 = 125;
public static final int ARTL_ART_cfgAltNoAction_108 = 126;
public static final int ARTL_ART_cfgAltNoAction_109 = 127;
public static final int ARTL_ART_cfgAltNoAction_110 = 128;
public static final int ARTL_ART_cfgAltNoAction_111 = 129;
public static final int ARTL_ART_cfgAltNoAction_112 = 130;
public static final int ARTL_ART_cfgAltNoAction_113 = 131;
public static final int ARTL_ART_cfgAltNoAction_114 = 132;
public static final int ARTL_ART_cfgAlts_23 = 133;
public static final int ARTL_ART_cfgAlts_24 = 134;
public static final int ARTL_ART_cfgAlts_25 = 135;
public static final int ARTL_ART_cfgAlts_26 = 136;
public static final int ARTL_ART_cfgAlts_27 = 137;
public static final int ARTL_ART_cfgAlts_28 = 138;
public static final int ARTL_ART_cfgAlts_29 = 139;
public static final int ARTL_ART_cfgAlts_30 = 140;
public static final int ARTL_ART_cfgAlts_31 = 141;
public static final int ARTL_ART_cfgAlts_32 = 142;
public static final int ARTL_ART_cfgAlts_33 = 143;
public static final int ARTL_ART_cfgAlts_34 = 144;
public static final int ARTL_ART_cfgAnnotation_97 = 145;
public static final int ARTL_ART_cfgAnnotation_98 = 146;
public static final int ARTL_ART_cfgAnnotation_99 = 147;
public static final int ARTL_ART_cfgAnnotation_100 = 148;
public static final int ARTL_ART_cfgAnnotation_101 = 149;
public static final int ARTL_ART_cfgAnnotation_102 = 150;
public static final int ARTL_ART_cfgAnnotation_103 = 151;
public static final int ARTL_ART_cfgAnnotation_104 = 152;
public static final int ARTL_ART_cfgAssignment_265 = 153;
public static final int ARTL_ART_cfgAssignment_266 = 154;
public static final int ARTL_ART_cfgAssignment_267 = 155;
public static final int ARTL_ART_cfgAssignment_268 = 156;
public static final int ARTL_ART_cfgAssignment_269 = 157;
public static final int ARTL_ART_cfgAssignment_270 = 158;
public static final int ARTL_ART_cfgAssignment_271 = 159;
public static final int ARTL_ART_cfgAssignment_272 = 160;
public static final int ARTL_ART_cfgAttribute_279 = 161;
public static final int ARTL_ART_cfgAttribute_280 = 162;
public static final int ARTL_ART_cfgAttribute_281 = 163;
public static final int ARTL_ART_cfgAttribute_282 = 164;
public static final int ARTL_ART_cfgAttribute_283 = 165;
public static final int ARTL_ART_cfgAttribute_284 = 166;
public static final int ARTL_ART_cfgAttribute_285 = 167;
public static final int ARTL_ART_cfgAttribute_286 = 168;
public static final int ARTL_ART_cfgBuiltinTerminal_229 = 169;
public static final int ARTL_ART_cfgBuiltinTerminal_230 = 170;
public static final int ARTL_ART_cfgBuiltinTerminal_231 = 171;
public static final int ARTL_ART_cfgBuiltinTerminal_232 = 172;
public static final int ARTL_ART_cfgBuiltinTerminal_233 = 173;
public static final int ARTL_ART_cfgBuiltinTerminal_234 = 174;
public static final int ARTL_ART_cfgCaseSensitiveTerminal_225 = 175;
public static final int ARTL_ART_cfgCaseSensitiveTerminal_226 = 176;
public static final int ARTL_ART_cfgCaseSensitiveTerminal_227 = 177;
public static final int ARTL_ART_cfgCaseSensitiveTerminal_228 = 178;
public static final int ARTL_ART_cfgDoFirst_157 = 179;
public static final int ARTL_ART_cfgDoFirst_158 = 180;
public static final int ARTL_ART_cfgDoFirst_159 = 181;
public static final int ARTL_ART_cfgDoFirst_160 = 182;
public static final int ARTL_ART_cfgDoFirst_161 = 183;
public static final int ARTL_ART_cfgDoFirst_162 = 184;
public static final int ARTL_ART_cfgDoFirst_163 = 185;
public static final int ARTL_ART_cfgDoFirst_164 = 186;
public static final int ARTL_ART_cfgElems_83 = 187;
public static final int ARTL_ART_cfgElems_84 = 188;
public static final int ARTL_ART_cfgElems_85 = 189;
public static final int ARTL_ART_cfgElems_86 = 190;
public static final int ARTL_ART_cfgElems_87 = 191;
public static final int ARTL_ART_cfgElems_88 = 192;
public static final int ARTL_ART_cfgElems_89 = 193;
public static final int ARTL_ART_cfgElems_90 = 194;
public static final int ARTL_ART_cfgElems_91 = 195;
public static final int ARTL_ART_cfgElems_92 = 196;
public static final int ARTL_ART_cfgElems_93 = 197;
public static final int ARTL_ART_cfgElems_94 = 198;
public static final int ARTL_ART_cfgElems_95 = 199;
public static final int ARTL_ART_cfgElems_96 = 200;
public static final int ARTL_ART_cfgEpsilon_73 = 201;
public static final int ARTL_ART_cfgEpsilon_74 = 202;
public static final int ARTL_ART_cfgEpsilon_75 = 203;
public static final int ARTL_ART_cfgEpsilon_76 = 204;
public static final int ARTL_ART_cfgEpsilonCarrier_77 = 205;
public static final int ARTL_ART_cfgEpsilonCarrier_78 = 206;
public static final int ARTL_ART_cfgEpsilonCarrier_79 = 207;
public static final int ARTL_ART_cfgEpsilonCarrier_80 = 208;
public static final int ARTL_ART_cfgEpsilonCarrier_81 = 209;
public static final int ARTL_ART_cfgEpsilonCarrier_82 = 210;
public static final int ARTL_ART_cfgEquation_257 = 211;
public static final int ARTL_ART_cfgEquation_258 = 212;
public static final int ARTL_ART_cfgEquation_259 = 213;
public static final int ARTL_ART_cfgEquation_260 = 214;
public static final int ARTL_ART_cfgEquation_261 = 215;
public static final int ARTL_ART_cfgEquation_262 = 216;
public static final int ARTL_ART_cfgEquation_263 = 217;
public static final int ARTL_ART_cfgEquation_264 = 218;
public static final int ARTL_ART_cfgExtended_131 = 219;
public static final int ARTL_ART_cfgExtended_132 = 220;
public static final int ARTL_ART_cfgExtended_133 = 221;
public static final int ARTL_ART_cfgExtended_134 = 222;
public static final int ARTL_ART_cfgExtended_135 = 223;
public static final int ARTL_ART_cfgExtended_136 = 224;
public static final int ARTL_ART_cfgExtended_137 = 225;
public static final int ARTL_ART_cfgExtended_138 = 226;
public static final int ARTL_ART_cfgExtended_139 = 227;
public static final int ARTL_ART_cfgExtended_140 = 228;
public static final int ARTL_ART_cfgExtended_141 = 229;
public static final int ARTL_ART_cfgExtended_142 = 230;
public static final int ARTL_ART_cfgExtended_143 = 231;
public static final int ARTL_ART_cfgExtended_144 = 232;
public static final int ARTL_ART_cfgExtended_145 = 233;
public static final int ARTL_ART_cfgExtended_146 = 234;
public static final int ARTL_ART_cfgExtended_147 = 235;
public static final int ARTL_ART_cfgExtended_148 = 236;
public static final int ARTL_ART_cfgExtended_149 = 237;
public static final int ARTL_ART_cfgExtended_150 = 238;
public static final int ARTL_ART_cfgExtended_151 = 239;
public static final int ARTL_ART_cfgExtended_152 = 240;
public static final int ARTL_ART_cfgExtended_153 = 241;
public static final int ARTL_ART_cfgExtended_154 = 242;
public static final int ARTL_ART_cfgExtended_155 = 243;
public static final int ARTL_ART_cfgExtended_156 = 244;
public static final int ARTL_ART_cfgFoldOver_217 = 245;
public static final int ARTL_ART_cfgFoldOver_218 = 246;
public static final int ARTL_ART_cfgFoldOver_219 = 247;
public static final int ARTL_ART_cfgFoldOver_220 = 248;
public static final int ARTL_ART_cfgFoldUnder_213 = 249;
public static final int ARTL_ART_cfgFoldUnder_214 = 250;
public static final int ARTL_ART_cfgFoldUnder_215 = 251;
public static final int ARTL_ART_cfgFoldUnder_216 = 252;
public static final int ARTL_ART_cfgInsert_273 = 253;
public static final int ARTL_ART_cfgInsert_274 = 254;
public static final int ARTL_ART_cfgInsert_275 = 255;
public static final int ARTL_ART_cfgInsert_276 = 256;
public static final int ARTL_ART_cfgInsert_277 = 257;
public static final int ARTL_ART_cfgInsert_278 = 258;
public static final int ARTL_ART_cfgKleene_181 = 259;
public static final int ARTL_ART_cfgKleene_182 = 260;
public static final int ARTL_ART_cfgKleene_183 = 261;
public static final int ARTL_ART_cfgKleene_184 = 262;
public static final int ARTL_ART_cfgKleene_185 = 263;
public static final int ARTL_ART_cfgKleene_186 = 264;
public static final int ARTL_ART_cfgKleene_187 = 265;
public static final int ARTL_ART_cfgKleene_188 = 266;
public static final int ARTL_ART_cfgKleene_189 = 267;
public static final int ARTL_ART_cfgKleene_190 = 268;
public static final int ARTL_ART_cfgKleene_191 = 269;
public static final int ARTL_ART_cfgKleene_192 = 270;
public static final int ARTL_ART_cfgKleene_193 = 271;
public static final int ARTL_ART_cfgKleene_194 = 272;
public static final int ARTL_ART_cfgKleene_195 = 273;
public static final int ARTL_ART_cfgKleene_196 = 274;
public static final int ARTL_ART_cfgLHS_19 = 275;
public static final int ARTL_ART_cfgLHS_20 = 276;
public static final int ARTL_ART_cfgLHS_21 = 277;
public static final int ARTL_ART_cfgLHS_22 = 278;
public static final int ARTL_ART_cfgNonterminal_221 = 279;
public static final int ARTL_ART_cfgNonterminal_222 = 280;
public static final int ARTL_ART_cfgNonterminal_223 = 281;
public static final int ARTL_ART_cfgNonterminal_224 = 282;
public static final int ARTL_ART_cfgOptional_165 = 283;
public static final int ARTL_ART_cfgOptional_166 = 284;
public static final int ARTL_ART_cfgOptional_167 = 285;
public static final int ARTL_ART_cfgOptional_168 = 286;
public static final int ARTL_ART_cfgOptional_169 = 287;
public static final int ARTL_ART_cfgOptional_170 = 288;
public static final int ARTL_ART_cfgOptional_171 = 289;
public static final int ARTL_ART_cfgOptional_172 = 290;
public static final int ARTL_ART_cfgOptional_173 = 291;
public static final int ARTL_ART_cfgOptional_174 = 292;
public static final int ARTL_ART_cfgOptional_175 = 293;
public static final int ARTL_ART_cfgOptional_176 = 294;
public static final int ARTL_ART_cfgOptional_177 = 295;
public static final int ARTL_ART_cfgOptional_178 = 296;
public static final int ARTL_ART_cfgOptional_179 = 297;
public static final int ARTL_ART_cfgOptional_180 = 298;
public static final int ARTL_ART_cfgPositive_197 = 299;
public static final int ARTL_ART_cfgPositive_198 = 300;
public static final int ARTL_ART_cfgPositive_199 = 301;
public static final int ARTL_ART_cfgPositive_200 = 302;
public static final int ARTL_ART_cfgPositive_201 = 303;
public static final int ARTL_ART_cfgPositive_202 = 304;
public static final int ARTL_ART_cfgPositive_203 = 305;
public static final int ARTL_ART_cfgPositive_204 = 306;
public static final int ARTL_ART_cfgPositive_205 = 307;
public static final int ARTL_ART_cfgPositive_206 = 308;
public static final int ARTL_ART_cfgPositive_207 = 309;
public static final int ARTL_ART_cfgPositive_208 = 310;
public static final int ARTL_ART_cfgPositive_209 = 311;
public static final int ARTL_ART_cfgPositive_210 = 312;
public static final int ARTL_ART_cfgPositive_211 = 313;
public static final int ARTL_ART_cfgPositive_212 = 314;
public static final int ARTL_ART_cfgPrim_119 = 315;
public static final int ARTL_ART_cfgPrim_120 = 316;
public static final int ARTL_ART_cfgPrim_121 = 317;
public static final int ARTL_ART_cfgPrim_122 = 318;
public static final int ARTL_ART_cfgPrim_123 = 319;
public static final int ARTL_ART_cfgPrim_124 = 320;
public static final int ARTL_ART_cfgPrim_125 = 321;
public static final int ARTL_ART_cfgPrim_126 = 322;
public static final int ARTL_ART_cfgPrim_127 = 323;
public static final int ARTL_ART_cfgPrim_128 = 324;
public static final int ARTL_ART_cfgPrim_129 = 325;
public static final int ARTL_ART_cfgPrim_130 = 326;
public static final int ARTL_ART_cfgRule_11 = 327;
public static final int ARTL_ART_cfgRule_12 = 328;
public static final int ARTL_ART_cfgRule_13 = 329;
public static final int ARTL_ART_cfgRule_14 = 330;
public static final int ARTL_ART_cfgRule_15 = 331;
public static final int ARTL_ART_cfgRule_16 = 332;
public static final int ARTL_ART_cfgRule_17 = 333;
public static final int ARTL_ART_cfgRule_18 = 334;
public static final int ARTL_ART_cfgRules_1 = 335;
public static final int ARTL_ART_cfgRules_2 = 336;
public static final int ARTL_ART_cfgRules_3 = 337;
public static final int ARTL_ART_cfgRules_4 = 338;
public static final int ARTL_ART_cfgRules_5 = 339;
public static final int ARTL_ART_cfgRules_6 = 340;
public static final int ARTL_ART_cfgRules_7 = 341;
public static final int ARTL_ART_cfgRules_8 = 342;
public static final int ARTL_ART_cfgRules_9 = 343;
public static final int ARTL_ART_cfgRules_10 = 344;
public static final int ARTL_ART_cfgSeq_59 = 345;
public static final int ARTL_ART_cfgSeq_60 = 346;
public static final int ARTL_ART_cfgSeq_61 = 347;
public static final int ARTL_ART_cfgSeq_62 = 348;
public static final int ARTL_ART_cfgSlot_115 = 349;
public static final int ARTL_ART_cfgSlot_116 = 350;
public static final int ARTL_ART_cfgSlot_117 = 351;
public static final int ARTL_ART_cfgSlot_118 = 352;
public static final int ARTL_ART_term_287 = 353;
public static final int ARTL_ART_term_288 = 354;
public static final int ARTL_ART_term_289 = 355;
public static final int ARTL_ART_term_290 = 356;
public static final int ARTL_ART_term_291 = 357;
public static final int ARTL_ART_term_292 = 358;
public static final int ARTL_ART_term_293 = 359;
public static final int ARTL_ART_term_294 = 360;
public static final int ARTL_ART_term_295 = 361;
public static final int ARTL_ART_term_296 = 362;
public static final int ARTL_ART_term_297 = 363;
public static final int ARTL_ART_term_298 = 364;
public static final int ARTL_ART_term_299 = 365;
public static final int ARTL_ART_term_300 = 366;
public static final int ARTL_ART_term_301 = 367;
public static final int ARTL_ART_term_302 = 368;
public static final int ARTL_ART_term_303 = 369;
public static final int ARTL_ART_term_304 = 370;
public static final int ARTL_ART_term_305 = 371;
public static final int ARTL_ART_term_306 = 372;
public static final int ARTL_ART_term_307 = 373;
public static final int ARTL_ART_term_308 = 374;
public static final int ARTL_ART_term_309 = 375;
public static final int ARTL_ART_term_310 = 376;
public static final int ARTL_ART_term_311 = 377;
public static final int ARTL_ART_term_312 = 378;
public static final int ARTL_ART_term_313 = 379;
public static final int ARTL_ART_term_314 = 380;
public static final int ARTL_ART_term_315 = 381;
public static final int ARTL_ART_term_316 = 382;
public static final int ARTL_ART_term_317 = 383;
public static final int ARTL_ART_term_318 = 384;
public static final int ARTL_ART_term_319 = 385;
public static final int ARTL_ART_term_320 = 386;
public static final int ARTX_DESPATCH = 387;
public static final int ARTX_DUMMY = 388;
public static final int ARTX_LABEL_EXTENT = 389;
/* End of artLabel enumeration */

/* Start of artName enumeration */
public static final int ARTNAME_NONE = 0;
public static final int ARTNAME_EXTENT = 1;
/* End of artName enumeration */
public void ARTPF_ART_arguments() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal arguments production descriptor loads*/
    case ARTL_ART_arguments: 
      if (ARTSet2[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_arguments_322, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet2[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_arguments_326, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal arguments: match production*/
    case ARTL_ART_arguments_322: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_arguments_324, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_term; return; }
    case ARTL_ART_arguments_324: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal arguments: match production*/
    case ARTL_ART_arguments_326: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_arguments_328, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_term; return; }
    case ARTL_ART_arguments_328: 
      /* Nonterminal template end */
      if (!ARTSet5[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COMMA, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_arguments_330, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_arguments_332, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_arguments; return; }
    case ARTL_ART_arguments_332: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgAction() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgAction production descriptor loads*/
    case ARTL_ART_cfgAction: 
      if (ARTSet9[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgAction_246, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet10[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgAction_250, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgAction_254, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgAction: match production*/
    case ARTL_ART_cfgAction_246: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAction_248, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgEquation; return; }
    case ARTL_ART_cfgAction_248: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet8[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgAction: match production*/
    case ARTL_ART_cfgAction_250: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAction_252, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAssignment; return; }
    case ARTL_ART_cfgAction_252: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet8[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgAction: match production*/
    case ARTL_ART_cfgAction_254: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAction_256, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgInsert; return; }
    case ARTL_ART_cfgAction_256: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet8[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgActionSeq() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgActionSeq production descriptor loads*/
    case ARTL_ART_cfgActionSeq: 
      if (ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgActionSeq_236, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgActionSeq_240, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgActionSeq: match production*/
    case ARTL_ART_cfgActionSeq_236: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgActionSeq_238, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAction; return; }
    case ARTL_ART_cfgActionSeq_238: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet13[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgActionSeq: match production*/
    case ARTL_ART_cfgActionSeq_240: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgActionSeq_242, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAction; return; }
    case ARTL_ART_cfgActionSeq_242: 
      /* Nonterminal template end */
      if (!ARTSet14[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgActionSeq_244, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgActionSeq; return; }
    case ARTL_ART_cfgActionSeq_244: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet13[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgActions() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgActions production descriptor loads*/
    case ARTL_ART_cfgActions: 
      if (ARTSet13[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgActions_64, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgActions_68, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgActions: match production*/
    case ARTL_ART_cfgActions_64: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgActions_66, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgSlot; return; }
    case ARTL_ART_cfgActions_66: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet13[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgActions: match production*/
    case ARTL_ART_cfgActions_68: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgActions_70, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgSlot; return; }
    case ARTL_ART_cfgActions_70: 
      /* Nonterminal template end */
      if (!ARTSet14[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgActions_72, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgActionSeq; return; }
    case ARTL_ART_cfgActions_72: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet13[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgAlt() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgAlt production descriptor loads*/
    case ARTL_ART_cfgAlt: 
      if (ARTSet20[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgAlt_36, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet25[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgAlt_44, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet28[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgAlt_52, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgAlt: match production*/
    case ARTL_ART_cfgAlt_36: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAlt_38, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgSeq; return; }
    case ARTL_ART_cfgAlt_38: 
      /* Nonterminal template end */
      if (!ARTSet22[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAlt_40, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgActions; return; }
    case ARTL_ART_cfgAlt_40: 
      /* Nonterminal template end */
      if (!ARTSet24[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAlt_42, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgEpsilon; return; }
    case ARTL_ART_cfgAlt_42: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet19[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgAlt: match production*/
    case ARTL_ART_cfgAlt_44: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAlt_46, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgSeq; return; }
    case ARTL_ART_cfgAlt_46: 
      /* Nonterminal template end */
      if (!ARTSet26[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAlt_48, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgActions; return; }
    case ARTL_ART_cfgAlt_48: 
      /* Nonterminal template end */
      if (!ARTSet27[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAlt_50, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgEpsilonCarrier; return; }
    case ARTL_ART_cfgAlt_50: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet19[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgAlt: match production*/
    case ARTL_ART_cfgAlt_52: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAlt_54, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgSeq; return; }
    case ARTL_ART_cfgAlt_54: 
      /* Nonterminal template end */
      if (!ARTSet28[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAlt_56, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgElems; return; }
    case ARTL_ART_cfgAlt_56: 
      /* Nonterminal template end */
      if (!ARTSet29[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAlt_58, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgActions; return; }
    case ARTL_ART_cfgAlt_58: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet19[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgAltNoAction() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgAltNoAction production descriptor loads*/
    case ARTL_ART_cfgAltNoAction: 
      if (ARTSet30[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgAltNoAction_106, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgAltNoAction: match production*/
    case ARTL_ART_cfgAltNoAction_106: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAltNoAction_108, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgSeq; return; }
    case ARTL_ART_cfgAltNoAction_108: 
      /* Nonterminal template end */
      if (!ARTSet32[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAltNoAction_110, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgSlot; return; }
    case ARTL_ART_cfgAltNoAction_110: 
      /* Nonterminal template end */
      if (!ARTSet33[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAltNoAction_112, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgPrim; return; }
    case ARTL_ART_cfgAltNoAction_112: 
      /* Nonterminal template end */
      if (!ARTSet34[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAltNoAction_114, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgSlot; return; }
    case ARTL_ART_cfgAltNoAction_114: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet31[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgAlts() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgAlts production descriptor loads*/
    case ARTL_ART_cfgAlts: 
      if (ARTSet35[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgAlts_24, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet35[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgAlts_28, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgAlts: match production*/
    case ARTL_ART_cfgAlts_24: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAlts_26, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAlt; return; }
    case ARTL_ART_cfgAlts_26: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet36[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgAlts: match production*/
    case ARTL_ART_cfgAlts_28: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAlts_30, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAlt; return; }
    case ARTL_ART_cfgAlts_30: 
      /* Nonterminal template end */
      if (!ARTSet37[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__BAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgAlts_32, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet38[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAlts_34, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAlts; return; }
    case ARTL_ART_cfgAlts_34: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet36[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgAnnotation() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgAnnotation production descriptor loads*/
    case ARTL_ART_cfgAnnotation: 
      if (ARTSet41[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgAnnotation_98, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet42[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgAnnotation_102, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgAnnotation: match production*/
    case ARTL_ART_cfgAnnotation_98: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAnnotation_100, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgFoldUnder; return; }
    case ARTL_ART_cfgAnnotation_100: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgAnnotation: match production*/
    case ARTL_ART_cfgAnnotation_102: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAnnotation_104, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgFoldOver; return; }
    case ARTL_ART_cfgAnnotation_104: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgAssignment() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgAssignment production descriptor loads*/
    case ARTL_ART_cfgAssignment: 
      if (ARTSet43[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgAssignment_266, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgAssignment: match production*/
    case ARTL_ART_cfgAssignment_266: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAssignment_268, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAttribute; return; }
    case ARTL_ART_cfgAssignment_268: 
      /* Nonterminal template end */
      if (!ARTSet44[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COLON_EQUAL, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgAssignment_270, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet2[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgAssignment_272, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_term; return; }
    case ARTL_ART_cfgAssignment_272: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet8[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgAttribute() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgAttribute production descriptor loads*/
    case ARTL_ART_cfgAttribute: 
      if (ARTSet45[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgAttribute_280, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgAttribute: match production*/
    case ARTL_ART_cfgAttribute_280: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_ID, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgAttribute_282, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet47[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__PERIOD, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgAttribute_284, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet45[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_ID, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgAttribute_286, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet46[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgBuiltinTerminal() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgBuiltinTerminal production descriptor loads*/
    case ARTL_ART_cfgBuiltinTerminal: 
      if (ARTSet48[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgBuiltinTerminal_230, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgBuiltinTerminal: match production*/
    case ARTL_ART_cfgBuiltinTerminal_230: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__AMPERSAND, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgBuiltinTerminal_232, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet45[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_ID, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgBuiltinTerminal_234, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet49[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgCaseSensitiveTerminal() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgCaseSensitiveTerminal production descriptor loads*/
    case ARTL_ART_cfgCaseSensitiveTerminal: 
      if (ARTSet50[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgCaseSensitiveTerminal_226, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgCaseSensitiveTerminal: match production*/
    case ARTL_ART_cfgCaseSensitiveTerminal_226: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_STRING_PLAIN_SQ, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgCaseSensitiveTerminal_228, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet49[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgDoFirst() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgDoFirst production descriptor loads*/
    case ARTL_ART_cfgDoFirst: 
      if (ARTSet51[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgDoFirst_158, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgDoFirst: match production*/
    case ARTL_ART_cfgDoFirst_158: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__LPAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgDoFirst_160, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet38[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgDoFirst_162, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAlts; return; }
    case ARTL_ART_cfgDoFirst_162: 
      /* Nonterminal template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__RPAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgDoFirst_164, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgElems() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgElems production descriptor loads*/
    case ARTL_ART_cfgElems: 
      if (ARTSet52[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgElems_84, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet52[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgElems_90, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgElems: match production*/
    case ARTL_ART_cfgElems_84: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgElems_86, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgActions; return; }
    case ARTL_ART_cfgElems_86: 
      /* Nonterminal template end */
      if (!ARTSet53[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgElems_88, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgExtended; return; }
    case ARTL_ART_cfgElems_88: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet29[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgElems: match production*/
    case ARTL_ART_cfgElems_90: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgElems_92, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgActions; return; }
    case ARTL_ART_cfgElems_92: 
      /* Nonterminal template end */
      if (!ARTSet53[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgElems_94, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgExtended; return; }
    case ARTL_ART_cfgElems_94: 
      /* Nonterminal template end */
      if (!ARTSet28[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgElems_96, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgElems; return; }
    case ARTL_ART_cfgElems_96: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet29[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgEpsilon() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgEpsilon production descriptor loads*/
    case ARTL_ART_cfgEpsilon: 
      if (ARTSet54[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgEpsilon_74, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgEpsilon: match production*/
    case ARTL_ART_cfgEpsilon_74: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__HASH, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgEpsilon_76, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet55[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgEpsilonCarrier() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgEpsilonCarrier production descriptor loads*/
    case ARTL_ART_cfgEpsilonCarrier: 
      if (ARTSet24[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgEpsilonCarrier_78, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgEpsilonCarrier: match production*/
    case ARTL_ART_cfgEpsilonCarrier_78: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgEpsilonCarrier_80, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgEpsilon; return; }
    case ARTL_ART_cfgEpsilonCarrier_80: 
      /* Nonterminal template end */
      if (!ARTSet56[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgEpsilonCarrier_82, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAnnotation; return; }
    case ARTL_ART_cfgEpsilonCarrier_82: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet19[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgEquation() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgEquation production descriptor loads*/
    case ARTL_ART_cfgEquation: 
      if (ARTSet43[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgEquation_258, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgEquation: match production*/
    case ARTL_ART_cfgEquation_258: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgEquation_260, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAttribute; return; }
    case ARTL_ART_cfgEquation_260: 
      /* Nonterminal template end */
      if (!ARTSet57[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__EQUAL, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgEquation_262, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet2[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgEquation_264, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_term; return; }
    case ARTL_ART_cfgEquation_264: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet8[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgExtended() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgExtended production descriptor loads*/
    case ARTL_ART_cfgExtended: 
      if (ARTSet33[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgExtended_132, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet33[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgExtended_136, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet59[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgExtended_142, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet60[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgExtended_146, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet61[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgExtended_150, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet62[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgExtended_154, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgExtended: match production*/
    case ARTL_ART_cfgExtended_132: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgExtended_134, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgPrim; return; }
    case ARTL_ART_cfgExtended_134: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgExtended: match production*/
    case ARTL_ART_cfgExtended_136: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgExtended_138, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgPrim; return; }
    case ARTL_ART_cfgExtended_138: 
      /* Nonterminal template end */
      if (!ARTSet56[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgExtended_140, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAnnotation; return; }
    case ARTL_ART_cfgExtended_140: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgExtended: match production*/
    case ARTL_ART_cfgExtended_142: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgExtended_144, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgDoFirst; return; }
    case ARTL_ART_cfgExtended_144: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgExtended: match production*/
    case ARTL_ART_cfgExtended_146: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgExtended_148, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgOptional; return; }
    case ARTL_ART_cfgExtended_148: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgExtended: match production*/
    case ARTL_ART_cfgExtended_150: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgExtended_152, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgKleene; return; }
    case ARTL_ART_cfgExtended_152: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgExtended: match production*/
    case ARTL_ART_cfgExtended_154: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgExtended_156, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgPositive; return; }
    case ARTL_ART_cfgExtended_156: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgFoldOver() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgFoldOver production descriptor loads*/
    case ARTL_ART_cfgFoldOver: 
      if (ARTSet63[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgFoldOver_218, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgFoldOver: match production*/
    case ARTL_ART_cfgFoldOver_218: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__UPARROW_UPARROW, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgFoldOver_220, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgFoldUnder() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgFoldUnder production descriptor loads*/
    case ARTL_ART_cfgFoldUnder: 
      if (ARTSet64[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgFoldUnder_214, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgFoldUnder: match production*/
    case ARTL_ART_cfgFoldUnder_214: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__UPARROW, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgFoldUnder_216, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgInsert() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgInsert production descriptor loads*/
    case ARTL_ART_cfgInsert: 
      if (ARTSet65[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgInsert_274, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgInsert: match production*/
    case ARTL_ART_cfgInsert_274: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__UPARROW_PLUS, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgInsert_276, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet45[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_ID, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgInsert_278, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet8[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgKleene() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgKleene production descriptor loads*/
    case ARTL_ART_cfgKleene: 
      if (ARTSet51[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgKleene_182, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet68[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgKleene_192, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgKleene: match production*/
    case ARTL_ART_cfgKleene_182: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__LPAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgKleene_184, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet38[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgKleene_186, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAlts; return; }
    case ARTL_ART_cfgKleene_186: 
      /* Nonterminal template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__RPAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgKleene_188, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet67[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__STAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgKleene_190, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgKleene: match production*/
    case ARTL_ART_cfgKleene_192: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgKleene_194, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAltNoAction; return; }
    case ARTL_ART_cfgKleene_194: 
      /* Nonterminal template end */
      if (!ARTSet67[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__STAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgKleene_196, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgLHS() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgLHS production descriptor loads*/
    case ARTL_ART_cfgLHS: 
      if (ARTSet45[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgLHS_20, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgLHS: match production*/
    case ARTL_ART_cfgLHS_20: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_ID, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgLHS_22, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet69[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgNonterminal() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgNonterminal production descriptor loads*/
    case ARTL_ART_cfgNonterminal: 
      if (ARTSet45[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgNonterminal_222, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgNonterminal: match production*/
    case ARTL_ART_cfgNonterminal_222: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_ID, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgNonterminal_224, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet49[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgOptional() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgOptional production descriptor loads*/
    case ARTL_ART_cfgOptional: 
      if (ARTSet51[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgOptional_166, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet68[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgOptional_176, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgOptional: match production*/
    case ARTL_ART_cfgOptional_166: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__LPAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgOptional_168, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet38[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgOptional_170, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAlts; return; }
    case ARTL_ART_cfgOptional_170: 
      /* Nonterminal template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__RPAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgOptional_172, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet70[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__QUERY, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgOptional_174, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgOptional: match production*/
    case ARTL_ART_cfgOptional_176: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgOptional_178, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAltNoAction; return; }
    case ARTL_ART_cfgOptional_178: 
      /* Nonterminal template end */
      if (!ARTSet70[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__QUERY, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgOptional_180, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgPositive() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgPositive production descriptor loads*/
    case ARTL_ART_cfgPositive: 
      if (ARTSet51[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgPositive_198, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet68[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgPositive_208, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgPositive: match production*/
    case ARTL_ART_cfgPositive_198: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__LPAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgPositive_200, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet38[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgPositive_202, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAlts; return; }
    case ARTL_ART_cfgPositive_202: 
      /* Nonterminal template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__RPAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgPositive_204, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet71[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__PLUS, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgPositive_206, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgPositive: match production*/
    case ARTL_ART_cfgPositive_208: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgPositive_210, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAltNoAction; return; }
    case ARTL_ART_cfgPositive_210: 
      /* Nonterminal template end */
      if (!ARTSet71[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__PLUS, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgPositive_212, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet40[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgPrim() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgPrim production descriptor loads*/
    case ARTL_ART_cfgPrim: 
      if (ARTSet73[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgPrim_120, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet74[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgPrim_124, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet75[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgPrim_128, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgPrim: match production*/
    case ARTL_ART_cfgPrim_120: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgPrim_122, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgNonterminal; return; }
    case ARTL_ART_cfgPrim_122: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet49[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgPrim: match production*/
    case ARTL_ART_cfgPrim_124: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgPrim_126, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgCaseSensitiveTerminal; return; }
    case ARTL_ART_cfgPrim_126: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet49[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgPrim: match production*/
    case ARTL_ART_cfgPrim_128: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgPrim_130, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgBuiltinTerminal; return; }
    case ARTL_ART_cfgPrim_130: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet49[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgRule() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgRule production descriptor loads*/
    case ARTL_ART_cfgRule: 
      if (ARTSet76[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgRule_12, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgRule: match production*/
    case ARTL_ART_cfgRule_12: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgRule_14, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgLHS; return; }
    case ARTL_ART_cfgRule_14: 
      /* Nonterminal template end */
      if (!ARTSet69[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__COLON_COLON_EQUAL, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgRule_16, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet38[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgRule_18, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAlts; return; }
    case ARTL_ART_cfgRule_18: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet77[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgRules() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgRules production descriptor loads*/
    case ARTL_ART_cfgRules: 
      if (ARTSet78[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgRules_2, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet78[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgRules_6, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgRules: match production*/
    case ARTL_ART_cfgRules_2: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgRules_4, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgRule; return; }
    case ARTL_ART_cfgRules_4: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet79[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal cfgRules: match production*/
    case ARTL_ART_cfgRules_6: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgRules_8, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgRule; return; }
    case ARTL_ART_cfgRules_8: 
      /* Nonterminal template end */
      if (!ARTSet80[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_cfgRules_10, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgRules; return; }
    case ARTL_ART_cfgRules_10: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet79[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgSeq() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgSeq production descriptor loads*/
    case ARTL_ART_cfgSeq: 
      if (ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgSeq_60, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgSeq: match production*/
    case ARTL_ART_cfgSeq_60: 
      /* Cat/unary template start */
      /* Epsilon template start */
      artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgSeq_62, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Epsilon template end */
      /* Cat/unary template end */
      if (!ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_cfgSlot() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal cfgSlot production descriptor loads*/
    case ARTL_ART_cfgSlot: 
      if (ARTSet81[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_cfgSlot_116, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal cfgSlot: match production*/
    case ARTL_ART_cfgSlot_116: 
      /* Cat/unary template start */
      /* Epsilon template start */
      artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_cfgSlot_118, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Epsilon template end */
      /* Cat/unary template end */
      if (!ARTSet81[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_term() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal term production descriptor loads*/
    case ARTL_ART_term: 
      if (ARTSet43[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_term_288, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet84[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_term_292, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet85[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_term_296, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet86[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_term_300, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet45[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_term_304, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet45[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_term_312, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal term: match production*/
    case ARTL_ART_term_288: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_term_290, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_cfgAttribute; return; }
    case ARTL_ART_term_290: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet83[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal term: match production*/
    case ARTL_ART_term_292: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_INTEGER, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_term_294, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet83[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal term: match production*/
    case ARTL_ART_term_296: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_REAL, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_term_298, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet83[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal term: match production*/
    case ARTL_ART_term_300: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_STRING_DQ, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_term_302, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet83[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal term: match production*/
    case ARTL_ART_term_304: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_ID, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_term_306, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet51[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__LPAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_term_308, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__RPAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_term_310, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet83[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal term: match production*/
    case ARTL_ART_term_312: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_ID, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_term_314, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet51[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__LPAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_term_316, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_term_318, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_arguments; return; }
    case ARTL_ART_term_318: 
      /* Nonterminal template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS__RPAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_term_320, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet83[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void artParseBody(int artStartLabel) {
  artSetupCompleteTime = artReadClock();
  artSpecificationName = "ReferenceGrammarParser.art";
  artStartSymbolLabel = artStartLabel;
  artIsInLanguage = false;
  artTokenExtent = 55;
  artLexicaliseForV3GLL(artInputString, null);
  artLexCompleteTime = artReadClock();
  artDummySPPFNode = artFindSPPFInitial(ARTL_DUMMY, 0, 0);
  artCurrentSPPFNode = artDummySPPFNode;
  artRootGSSNode = artFindGSS(ARTL_EOS, 0, 0, 0);
  artCurrentGSSNode = artRootGSSNode;
  artCurrentRestartLabel = artStartSymbolLabel;
  artCurrentInputPairIndex = 0;
  artCurrentInputPairReference = 0;
  while (true)
    switch (artlhsL[artCurrentRestartLabel]) {
      case ARTL_ART_arguments: 
        ARTPF_ART_arguments();
        break;
      case ARTL_ART_cfgAction: 
        ARTPF_ART_cfgAction();
        break;
      case ARTL_ART_cfgActionSeq: 
        ARTPF_ART_cfgActionSeq();
        break;
      case ARTL_ART_cfgActions: 
        ARTPF_ART_cfgActions();
        break;
      case ARTL_ART_cfgAlt: 
        ARTPF_ART_cfgAlt();
        break;
      case ARTL_ART_cfgAltNoAction: 
        ARTPF_ART_cfgAltNoAction();
        break;
      case ARTL_ART_cfgAlts: 
        ARTPF_ART_cfgAlts();
        break;
      case ARTL_ART_cfgAnnotation: 
        ARTPF_ART_cfgAnnotation();
        break;
      case ARTL_ART_cfgAssignment: 
        ARTPF_ART_cfgAssignment();
        break;
      case ARTL_ART_cfgAttribute: 
        ARTPF_ART_cfgAttribute();
        break;
      case ARTL_ART_cfgBuiltinTerminal: 
        ARTPF_ART_cfgBuiltinTerminal();
        break;
      case ARTL_ART_cfgCaseSensitiveTerminal: 
        ARTPF_ART_cfgCaseSensitiveTerminal();
        break;
      case ARTL_ART_cfgDoFirst: 
        ARTPF_ART_cfgDoFirst();
        break;
      case ARTL_ART_cfgElems: 
        ARTPF_ART_cfgElems();
        break;
      case ARTL_ART_cfgEpsilon: 
        ARTPF_ART_cfgEpsilon();
        break;
      case ARTL_ART_cfgEpsilonCarrier: 
        ARTPF_ART_cfgEpsilonCarrier();
        break;
      case ARTL_ART_cfgEquation: 
        ARTPF_ART_cfgEquation();
        break;
      case ARTL_ART_cfgExtended: 
        ARTPF_ART_cfgExtended();
        break;
      case ARTL_ART_cfgFoldOver: 
        ARTPF_ART_cfgFoldOver();
        break;
      case ARTL_ART_cfgFoldUnder: 
        ARTPF_ART_cfgFoldUnder();
        break;
      case ARTL_ART_cfgInsert: 
        ARTPF_ART_cfgInsert();
        break;
      case ARTL_ART_cfgKleene: 
        ARTPF_ART_cfgKleene();
        break;
      case ARTL_ART_cfgLHS: 
        ARTPF_ART_cfgLHS();
        break;
      case ARTL_ART_cfgNonterminal: 
        ARTPF_ART_cfgNonterminal();
        break;
      case ARTL_ART_cfgOptional: 
        ARTPF_ART_cfgOptional();
        break;
      case ARTL_ART_cfgPositive: 
        ARTPF_ART_cfgPositive();
        break;
      case ARTL_ART_cfgPrim: 
        ARTPF_ART_cfgPrim();
        break;
      case ARTL_ART_cfgRule: 
        ARTPF_ART_cfgRule();
        break;
      case ARTL_ART_cfgRules: 
        ARTPF_ART_cfgRules();
        break;
      case ARTL_ART_cfgSeq: 
        ARTPF_ART_cfgSeq();
        break;
      case ARTL_ART_cfgSlot: 
        ARTPF_ART_cfgSlot();
        break;
      case ARTL_ART_term: 
        ARTPF_ART_term();
        break;
      case ARTX_DESPATCH: 
        if (artNoDescriptors()) { 
          artCheckAcceptance();
          artParseCompleteTime = artReadClock();
          artParseEndMemory = artMemoryUsed();
          return;
         }
        artUnloadDescriptor();
    }
}

public void ARTSet1initialise() {
  ARTSet1 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet1, 0, artSetExtent, false);
}

public void ARTSet42initialise() {
  ARTSet42 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet42, 0, artSetExtent, false);
  ARTSet42[ARTTS__UPARROW_UPARROW] = true;
  ARTSet42[ARTL_ART_cfgFoldOver] = true;
}

public void ARTSet46initialise() {
  ARTSet46 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet46, 0, artSetExtent, false);
  ARTSet46[ARTX_EOS] = true;
  ARTSet46[ARTTB_ID] = true;
  ARTSet46[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet46[ARTTS__HASH] = true;
  ARTSet46[ARTTS__AMPERSAND] = true;
  ARTSet46[ARTTS__LPAR] = true;
  ARTSet46[ARTTS__RPAR] = true;
  ARTSet46[ARTTS__COMMA] = true;
  ARTSet46[ARTTS__COLON_EQUAL] = true;
  ARTSet46[ARTTS__EQUAL] = true;
  ARTSet46[ARTTS__UPARROW_PLUS] = true;
  ARTSet46[ARTTS__BAR] = true;
  ARTSet46[ARTL_ART_cfgAction] = true;
  ARTSet46[ARTL_ART_cfgActionSeq] = true;
  ARTSet46[ARTL_ART_cfgAltNoAction] = true;
  ARTSet46[ARTL_ART_cfgAssignment] = true;
  ARTSet46[ARTL_ART_cfgAttribute] = true;
  ARTSet46[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet46[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet46[ARTL_ART_cfgDoFirst] = true;
  ARTSet46[ARTL_ART_cfgEpsilon] = true;
  ARTSet46[ARTL_ART_cfgEpsilonCarrier] = true;
  ARTSet46[ARTL_ART_cfgEquation] = true;
  ARTSet46[ARTL_ART_cfgExtended] = true;
  ARTSet46[ARTL_ART_cfgInsert] = true;
  ARTSet46[ARTL_ART_cfgKleene] = true;
  ARTSet46[ARTL_ART_cfgLHS] = true;
  ARTSet46[ARTL_ART_cfgNonterminal] = true;
  ARTSet46[ARTL_ART_cfgOptional] = true;
  ARTSet46[ARTL_ART_cfgPositive] = true;
  ARTSet46[ARTL_ART_cfgPrim] = true;
  ARTSet46[ARTL_ART_cfgRule] = true;
  ARTSet46[ARTL_ART_cfgRules] = true;
  ARTSet46[ARTL_ART_cfgSeq] = true;
  ARTSet46[ARTL_ART_cfgSlot] = true;
}

public void ARTSet21initialise() {
  ARTSet21 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet21, 0, artSetExtent, false);
  ARTSet21[ARTL_ART_cfgSeq] = true;
}

public void ARTSet31initialise() {
  ARTSet31 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet31, 0, artSetExtent, false);
  ARTSet31[ARTTS__STAR] = true;
  ARTSet31[ARTTS__PLUS] = true;
  ARTSet31[ARTTS__QUERY] = true;
}

public void ARTSet76initialise() {
  ARTSet76 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet76, 0, artSetExtent, false);
  ARTSet76[ARTTB_ID] = true;
  ARTSet76[ARTL_ART_cfgLHS] = true;
}

public void ARTSet65initialise() {
  ARTSet65 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet65, 0, artSetExtent, false);
  ARTSet65[ARTTS__UPARROW_PLUS] = true;
}

public void ARTSet83initialise() {
  ARTSet83 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet83, 0, artSetExtent, false);
  ARTSet83[ARTX_EOS] = true;
  ARTSet83[ARTTB_ID] = true;
  ARTSet83[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet83[ARTTS__HASH] = true;
  ARTSet83[ARTTS__AMPERSAND] = true;
  ARTSet83[ARTTS__LPAR] = true;
  ARTSet83[ARTTS__RPAR] = true;
  ARTSet83[ARTTS__COMMA] = true;
  ARTSet83[ARTTS__UPARROW_PLUS] = true;
  ARTSet83[ARTTS__BAR] = true;
  ARTSet83[ARTL_ART_cfgAction] = true;
  ARTSet83[ARTL_ART_cfgActionSeq] = true;
  ARTSet83[ARTL_ART_cfgAltNoAction] = true;
  ARTSet83[ARTL_ART_cfgAssignment] = true;
  ARTSet83[ARTL_ART_cfgAttribute] = true;
  ARTSet83[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet83[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet83[ARTL_ART_cfgDoFirst] = true;
  ARTSet83[ARTL_ART_cfgEpsilon] = true;
  ARTSet83[ARTL_ART_cfgEpsilonCarrier] = true;
  ARTSet83[ARTL_ART_cfgEquation] = true;
  ARTSet83[ARTL_ART_cfgExtended] = true;
  ARTSet83[ARTL_ART_cfgInsert] = true;
  ARTSet83[ARTL_ART_cfgKleene] = true;
  ARTSet83[ARTL_ART_cfgLHS] = true;
  ARTSet83[ARTL_ART_cfgNonterminal] = true;
  ARTSet83[ARTL_ART_cfgOptional] = true;
  ARTSet83[ARTL_ART_cfgPositive] = true;
  ARTSet83[ARTL_ART_cfgPrim] = true;
  ARTSet83[ARTL_ART_cfgRule] = true;
  ARTSet83[ARTL_ART_cfgRules] = true;
  ARTSet83[ARTL_ART_cfgSeq] = true;
  ARTSet83[ARTL_ART_cfgSlot] = true;
}

public void ARTSet33initialise() {
  ARTSet33 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet33, 0, artSetExtent, false);
  ARTSet33[ARTTB_ID] = true;
  ARTSet33[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet33[ARTTS__AMPERSAND] = true;
  ARTSet33[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet33[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet33[ARTL_ART_cfgNonterminal] = true;
  ARTSet33[ARTL_ART_cfgPrim] = true;
}

public void ARTSet36initialise() {
  ARTSet36 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet36, 0, artSetExtent, false);
  ARTSet36[ARTX_EOS] = true;
  ARTSet36[ARTTB_ID] = true;
  ARTSet36[ARTTS__RPAR] = true;
  ARTSet36[ARTL_ART_cfgLHS] = true;
  ARTSet36[ARTL_ART_cfgRule] = true;
  ARTSet36[ARTL_ART_cfgRules] = true;
}

public void ARTSet25initialise() {
  ARTSet25 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet25, 0, artSetExtent, false);
  ARTSet25[ARTTB_ID] = true;
  ARTSet25[ARTTS__HASH] = true;
  ARTSet25[ARTTS__UPARROW_PLUS] = true;
  ARTSet25[ARTL_ART_cfgAction] = true;
  ARTSet25[ARTL_ART_cfgActionSeq] = true;
  ARTSet25[ARTL_ART_cfgActions] = true;
  ARTSet25[ARTL_ART_cfgAssignment] = true;
  ARTSet25[ARTL_ART_cfgAttribute] = true;
  ARTSet25[ARTL_ART_cfgEpsilon] = true;
  ARTSet25[ARTL_ART_cfgEpsilonCarrier] = true;
  ARTSet25[ARTL_ART_cfgEquation] = true;
  ARTSet25[ARTL_ART_cfgInsert] = true;
  ARTSet25[ARTL_ART_cfgSeq] = true;
  ARTSet25[ARTL_ART_cfgSlot] = true;
}

public void ARTSet32initialise() {
  ARTSet32 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet32, 0, artSetExtent, false);
  ARTSet32[ARTTB_ID] = true;
  ARTSet32[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet32[ARTTS__AMPERSAND] = true;
  ARTSet32[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet32[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet32[ARTL_ART_cfgNonterminal] = true;
  ARTSet32[ARTL_ART_cfgPrim] = true;
  ARTSet32[ARTL_ART_cfgSlot] = true;
}

public void ARTSet22initialise() {
  ARTSet22 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet22, 0, artSetExtent, false);
  ARTSet22[ARTTB_ID] = true;
  ARTSet22[ARTTS__HASH] = true;
  ARTSet22[ARTTS__UPARROW_PLUS] = true;
  ARTSet22[ARTL_ART_cfgAction] = true;
  ARTSet22[ARTL_ART_cfgActionSeq] = true;
  ARTSet22[ARTL_ART_cfgActions] = true;
  ARTSet22[ARTL_ART_cfgAssignment] = true;
  ARTSet22[ARTL_ART_cfgAttribute] = true;
  ARTSet22[ARTL_ART_cfgEpsilon] = true;
  ARTSet22[ARTL_ART_cfgEquation] = true;
  ARTSet22[ARTL_ART_cfgInsert] = true;
  ARTSet22[ARTL_ART_cfgSlot] = true;
}

public void ARTSet18initialise() {
  ARTSet18 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet18, 0, artSetExtent, false);
  ARTSet18[ARTTB_ID] = true;
  ARTSet18[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet18[ARTTS__HASH] = true;
  ARTSet18[ARTTS__AMPERSAND] = true;
  ARTSet18[ARTTS__LPAR] = true;
  ARTSet18[ARTTS__UPARROW_PLUS] = true;
  ARTSet18[ARTL_ART_cfgAction] = true;
  ARTSet18[ARTL_ART_cfgActionSeq] = true;
  ARTSet18[ARTL_ART_cfgActions] = true;
  ARTSet18[ARTL_ART_cfgAltNoAction] = true;
  ARTSet18[ARTL_ART_cfgAssignment] = true;
  ARTSet18[ARTL_ART_cfgAttribute] = true;
  ARTSet18[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet18[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet18[ARTL_ART_cfgDoFirst] = true;
  ARTSet18[ARTL_ART_cfgElems] = true;
  ARTSet18[ARTL_ART_cfgEpsilon] = true;
  ARTSet18[ARTL_ART_cfgEpsilonCarrier] = true;
  ARTSet18[ARTL_ART_cfgEquation] = true;
  ARTSet18[ARTL_ART_cfgExtended] = true;
  ARTSet18[ARTL_ART_cfgInsert] = true;
  ARTSet18[ARTL_ART_cfgKleene] = true;
  ARTSet18[ARTL_ART_cfgNonterminal] = true;
  ARTSet18[ARTL_ART_cfgOptional] = true;
  ARTSet18[ARTL_ART_cfgPositive] = true;
  ARTSet18[ARTL_ART_cfgPrim] = true;
  ARTSet18[ARTL_ART_cfgSeq] = true;
  ARTSet18[ARTL_ART_cfgSlot] = true;
}

public void ARTSet74initialise() {
  ARTSet74 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet74, 0, artSetExtent, false);
  ARTSet74[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet74[ARTL_ART_cfgCaseSensitiveTerminal] = true;
}

public void ARTSet19initialise() {
  ARTSet19 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet19, 0, artSetExtent, false);
  ARTSet19[ARTX_EOS] = true;
  ARTSet19[ARTTB_ID] = true;
  ARTSet19[ARTTS__RPAR] = true;
  ARTSet19[ARTTS__BAR] = true;
  ARTSet19[ARTL_ART_cfgLHS] = true;
  ARTSet19[ARTL_ART_cfgRule] = true;
  ARTSet19[ARTL_ART_cfgRules] = true;
}

public void ARTSet23initialise() {
  ARTSet23 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet23, 0, artSetExtent, false);
  ARTSet23[ARTTB_ID] = true;
  ARTSet23[ARTTS__UPARROW_PLUS] = true;
  ARTSet23[ARTL_ART_cfgAction] = true;
  ARTSet23[ARTL_ART_cfgActionSeq] = true;
  ARTSet23[ARTL_ART_cfgActions] = true;
  ARTSet23[ARTL_ART_cfgAssignment] = true;
  ARTSet23[ARTL_ART_cfgAttribute] = true;
  ARTSet23[ARTL_ART_cfgEquation] = true;
  ARTSet23[ARTL_ART_cfgInsert] = true;
  ARTSet23[ARTL_ART_cfgSlot] = true;
}

public void ARTSet28initialise() {
  ARTSet28 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet28, 0, artSetExtent, false);
  ARTSet28[ARTTB_ID] = true;
  ARTSet28[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet28[ARTTS__AMPERSAND] = true;
  ARTSet28[ARTTS__LPAR] = true;
  ARTSet28[ARTTS__UPARROW_PLUS] = true;
  ARTSet28[ARTL_ART_cfgAction] = true;
  ARTSet28[ARTL_ART_cfgActionSeq] = true;
  ARTSet28[ARTL_ART_cfgActions] = true;
  ARTSet28[ARTL_ART_cfgAltNoAction] = true;
  ARTSet28[ARTL_ART_cfgAssignment] = true;
  ARTSet28[ARTL_ART_cfgAttribute] = true;
  ARTSet28[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet28[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet28[ARTL_ART_cfgDoFirst] = true;
  ARTSet28[ARTL_ART_cfgElems] = true;
  ARTSet28[ARTL_ART_cfgEquation] = true;
  ARTSet28[ARTL_ART_cfgExtended] = true;
  ARTSet28[ARTL_ART_cfgInsert] = true;
  ARTSet28[ARTL_ART_cfgKleene] = true;
  ARTSet28[ARTL_ART_cfgNonterminal] = true;
  ARTSet28[ARTL_ART_cfgOptional] = true;
  ARTSet28[ARTL_ART_cfgPositive] = true;
  ARTSet28[ARTL_ART_cfgPrim] = true;
  ARTSet28[ARTL_ART_cfgSeq] = true;
  ARTSet28[ARTL_ART_cfgSlot] = true;
}

public void ARTSet68initialise() {
  ARTSet68 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet68, 0, artSetExtent, false);
  ARTSet68[ARTTB_ID] = true;
  ARTSet68[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet68[ARTTS__AMPERSAND] = true;
  ARTSet68[ARTL_ART_cfgAltNoAction] = true;
  ARTSet68[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet68[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet68[ARTL_ART_cfgNonterminal] = true;
  ARTSet68[ARTL_ART_cfgPrim] = true;
  ARTSet68[ARTL_ART_cfgSeq] = true;
  ARTSet68[ARTL_ART_cfgSlot] = true;
}

public void ARTSet7initialise() {
  ARTSet7 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet7, 0, artSetExtent, false);
  ARTSet7[ARTTB_ID] = true;
  ARTSet7[ARTTS__UPARROW_PLUS] = true;
  ARTSet7[ARTL_ART_cfgAssignment] = true;
  ARTSet7[ARTL_ART_cfgAttribute] = true;
  ARTSet7[ARTL_ART_cfgEquation] = true;
  ARTSet7[ARTL_ART_cfgInsert] = true;
}

public void ARTSet2initialise() {
  ARTSet2 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet2, 0, artSetExtent, false);
  ARTSet2[ARTTB_ID] = true;
  ARTSet2[ARTTB_INTEGER] = true;
  ARTSet2[ARTTB_REAL] = true;
  ARTSet2[ARTTB_STRING_DQ] = true;
  ARTSet2[ARTL_ART_cfgAttribute] = true;
  ARTSet2[ARTL_ART_term] = true;
}

public void ARTSet62initialise() {
  ARTSet62 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet62, 0, artSetExtent, false);
  ARTSet62[ARTTB_ID] = true;
  ARTSet62[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet62[ARTTS__AMPERSAND] = true;
  ARTSet62[ARTTS__LPAR] = true;
  ARTSet62[ARTL_ART_cfgAltNoAction] = true;
  ARTSet62[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet62[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet62[ARTL_ART_cfgNonterminal] = true;
  ARTSet62[ARTL_ART_cfgPositive] = true;
  ARTSet62[ARTL_ART_cfgPrim] = true;
  ARTSet62[ARTL_ART_cfgSeq] = true;
  ARTSet62[ARTL_ART_cfgSlot] = true;
}

public void ARTSet43initialise() {
  ARTSet43 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet43, 0, artSetExtent, false);
  ARTSet43[ARTTB_ID] = true;
  ARTSet43[ARTL_ART_cfgAttribute] = true;
}

public void ARTSet54initialise() {
  ARTSet54 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet54, 0, artSetExtent, false);
  ARTSet54[ARTTS__HASH] = true;
}

public void ARTSet8initialise() {
  ARTSet8 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet8, 0, artSetExtent, false);
  ARTSet8[ARTX_EOS] = true;
  ARTSet8[ARTTB_ID] = true;
  ARTSet8[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet8[ARTTS__HASH] = true;
  ARTSet8[ARTTS__AMPERSAND] = true;
  ARTSet8[ARTTS__LPAR] = true;
  ARTSet8[ARTTS__RPAR] = true;
  ARTSet8[ARTTS__UPARROW_PLUS] = true;
  ARTSet8[ARTTS__BAR] = true;
  ARTSet8[ARTL_ART_cfgAction] = true;
  ARTSet8[ARTL_ART_cfgActionSeq] = true;
  ARTSet8[ARTL_ART_cfgAltNoAction] = true;
  ARTSet8[ARTL_ART_cfgAssignment] = true;
  ARTSet8[ARTL_ART_cfgAttribute] = true;
  ARTSet8[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet8[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet8[ARTL_ART_cfgDoFirst] = true;
  ARTSet8[ARTL_ART_cfgEpsilon] = true;
  ARTSet8[ARTL_ART_cfgEpsilonCarrier] = true;
  ARTSet8[ARTL_ART_cfgEquation] = true;
  ARTSet8[ARTL_ART_cfgExtended] = true;
  ARTSet8[ARTL_ART_cfgInsert] = true;
  ARTSet8[ARTL_ART_cfgKleene] = true;
  ARTSet8[ARTL_ART_cfgLHS] = true;
  ARTSet8[ARTL_ART_cfgNonterminal] = true;
  ARTSet8[ARTL_ART_cfgOptional] = true;
  ARTSet8[ARTL_ART_cfgPositive] = true;
  ARTSet8[ARTL_ART_cfgPrim] = true;
  ARTSet8[ARTL_ART_cfgRule] = true;
  ARTSet8[ARTL_ART_cfgRules] = true;
  ARTSet8[ARTL_ART_cfgSeq] = true;
  ARTSet8[ARTL_ART_cfgSlot] = true;
}

public void ARTSet48initialise() {
  ARTSet48 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet48, 0, artSetExtent, false);
  ARTSet48[ARTTS__AMPERSAND] = true;
}

public void ARTSet55initialise() {
  ARTSet55 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet55, 0, artSetExtent, false);
  ARTSet55[ARTX_EOS] = true;
  ARTSet55[ARTTB_ID] = true;
  ARTSet55[ARTTS__RPAR] = true;
  ARTSet55[ARTTS__UPARROW] = true;
  ARTSet55[ARTTS__UPARROW_UPARROW] = true;
  ARTSet55[ARTTS__BAR] = true;
  ARTSet55[ARTL_ART_cfgAnnotation] = true;
  ARTSet55[ARTL_ART_cfgFoldOver] = true;
  ARTSet55[ARTL_ART_cfgFoldUnder] = true;
  ARTSet55[ARTL_ART_cfgLHS] = true;
  ARTSet55[ARTL_ART_cfgRule] = true;
  ARTSet55[ARTL_ART_cfgRules] = true;
}

public void ARTSet51initialise() {
  ARTSet51 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet51, 0, artSetExtent, false);
  ARTSet51[ARTTS__LPAR] = true;
}

public void ARTSet3initialise() {
  ARTSet3 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet3, 0, artSetExtent, false);
  ARTSet3[ARTTS__RPAR] = true;
}

public void ARTSet40initialise() {
  ARTSet40 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet40, 0, artSetExtent, false);
  ARTSet40[ARTX_EOS] = true;
  ARTSet40[ARTTB_ID] = true;
  ARTSet40[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet40[ARTTS__AMPERSAND] = true;
  ARTSet40[ARTTS__LPAR] = true;
  ARTSet40[ARTTS__RPAR] = true;
  ARTSet40[ARTTS__UPARROW_PLUS] = true;
  ARTSet40[ARTTS__BAR] = true;
  ARTSet40[ARTL_ART_cfgAction] = true;
  ARTSet40[ARTL_ART_cfgActionSeq] = true;
  ARTSet40[ARTL_ART_cfgActions] = true;
  ARTSet40[ARTL_ART_cfgAltNoAction] = true;
  ARTSet40[ARTL_ART_cfgAssignment] = true;
  ARTSet40[ARTL_ART_cfgAttribute] = true;
  ARTSet40[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet40[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet40[ARTL_ART_cfgDoFirst] = true;
  ARTSet40[ARTL_ART_cfgElems] = true;
  ARTSet40[ARTL_ART_cfgEquation] = true;
  ARTSet40[ARTL_ART_cfgExtended] = true;
  ARTSet40[ARTL_ART_cfgInsert] = true;
  ARTSet40[ARTL_ART_cfgKleene] = true;
  ARTSet40[ARTL_ART_cfgLHS] = true;
  ARTSet40[ARTL_ART_cfgNonterminal] = true;
  ARTSet40[ARTL_ART_cfgOptional] = true;
  ARTSet40[ARTL_ART_cfgPositive] = true;
  ARTSet40[ARTL_ART_cfgPrim] = true;
  ARTSet40[ARTL_ART_cfgRule] = true;
  ARTSet40[ARTL_ART_cfgRules] = true;
  ARTSet40[ARTL_ART_cfgSeq] = true;
  ARTSet40[ARTL_ART_cfgSlot] = true;
}

public void ARTSet67initialise() {
  ARTSet67 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet67, 0, artSetExtent, false);
  ARTSet67[ARTTS__STAR] = true;
}

public void ARTSet71initialise() {
  ARTSet71 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet71, 0, artSetExtent, false);
  ARTSet71[ARTTS__PLUS] = true;
}

public void ARTSet5initialise() {
  ARTSet5 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet5, 0, artSetExtent, false);
  ARTSet5[ARTTS__COMMA] = true;
}

public void ARTSet47initialise() {
  ARTSet47 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet47, 0, artSetExtent, false);
  ARTSet47[ARTTS__PERIOD] = true;
}

public void ARTSet81initialise() {
  ARTSet81 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet81, 0, artSetExtent, false);
  ARTSet81[ARTX_EOS] = true;
  ARTSet81[ARTTB_ID] = true;
  ARTSet81[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet81[ARTTS__HASH] = true;
  ARTSet81[ARTTS__AMPERSAND] = true;
  ARTSet81[ARTTS__LPAR] = true;
  ARTSet81[ARTTS__RPAR] = true;
  ARTSet81[ARTTS__STAR] = true;
  ARTSet81[ARTTS__PLUS] = true;
  ARTSet81[ARTTS__QUERY] = true;
  ARTSet81[ARTTS__UPARROW_PLUS] = true;
  ARTSet81[ARTTS__BAR] = true;
  ARTSet81[ARTL_ART_cfgAction] = true;
  ARTSet81[ARTL_ART_cfgActionSeq] = true;
  ARTSet81[ARTL_ART_cfgAltNoAction] = true;
  ARTSet81[ARTL_ART_cfgAssignment] = true;
  ARTSet81[ARTL_ART_cfgAttribute] = true;
  ARTSet81[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet81[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet81[ARTL_ART_cfgDoFirst] = true;
  ARTSet81[ARTL_ART_cfgEpsilon] = true;
  ARTSet81[ARTL_ART_cfgEpsilonCarrier] = true;
  ARTSet81[ARTL_ART_cfgEquation] = true;
  ARTSet81[ARTL_ART_cfgExtended] = true;
  ARTSet81[ARTL_ART_cfgInsert] = true;
  ARTSet81[ARTL_ART_cfgKleene] = true;
  ARTSet81[ARTL_ART_cfgLHS] = true;
  ARTSet81[ARTL_ART_cfgNonterminal] = true;
  ARTSet81[ARTL_ART_cfgOptional] = true;
  ARTSet81[ARTL_ART_cfgPositive] = true;
  ARTSet81[ARTL_ART_cfgPrim] = true;
  ARTSet81[ARTL_ART_cfgRule] = true;
  ARTSet81[ARTL_ART_cfgRules] = true;
  ARTSet81[ARTL_ART_cfgSeq] = true;
  ARTSet81[ARTL_ART_cfgSlot] = true;
}

public void ARTSet49initialise() {
  ARTSet49 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet49, 0, artSetExtent, false);
  ARTSet49[ARTX_EOS] = true;
  ARTSet49[ARTTB_ID] = true;
  ARTSet49[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet49[ARTTS__AMPERSAND] = true;
  ARTSet49[ARTTS__LPAR] = true;
  ARTSet49[ARTTS__RPAR] = true;
  ARTSet49[ARTTS__STAR] = true;
  ARTSet49[ARTTS__PLUS] = true;
  ARTSet49[ARTTS__QUERY] = true;
  ARTSet49[ARTTS__UPARROW] = true;
  ARTSet49[ARTTS__UPARROW_PLUS] = true;
  ARTSet49[ARTTS__UPARROW_UPARROW] = true;
  ARTSet49[ARTTS__BAR] = true;
  ARTSet49[ARTL_ART_cfgAction] = true;
  ARTSet49[ARTL_ART_cfgActionSeq] = true;
  ARTSet49[ARTL_ART_cfgActions] = true;
  ARTSet49[ARTL_ART_cfgAltNoAction] = true;
  ARTSet49[ARTL_ART_cfgAnnotation] = true;
  ARTSet49[ARTL_ART_cfgAssignment] = true;
  ARTSet49[ARTL_ART_cfgAttribute] = true;
  ARTSet49[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet49[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet49[ARTL_ART_cfgDoFirst] = true;
  ARTSet49[ARTL_ART_cfgElems] = true;
  ARTSet49[ARTL_ART_cfgEquation] = true;
  ARTSet49[ARTL_ART_cfgExtended] = true;
  ARTSet49[ARTL_ART_cfgFoldOver] = true;
  ARTSet49[ARTL_ART_cfgFoldUnder] = true;
  ARTSet49[ARTL_ART_cfgInsert] = true;
  ARTSet49[ARTL_ART_cfgKleene] = true;
  ARTSet49[ARTL_ART_cfgLHS] = true;
  ARTSet49[ARTL_ART_cfgNonterminal] = true;
  ARTSet49[ARTL_ART_cfgOptional] = true;
  ARTSet49[ARTL_ART_cfgPositive] = true;
  ARTSet49[ARTL_ART_cfgPrim] = true;
  ARTSet49[ARTL_ART_cfgRule] = true;
  ARTSet49[ARTL_ART_cfgRules] = true;
  ARTSet49[ARTL_ART_cfgSeq] = true;
  ARTSet49[ARTL_ART_cfgSlot] = true;
}

public void ARTSet13initialise() {
  ARTSet13 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet13, 0, artSetExtent, false);
  ARTSet13[ARTX_EOS] = true;
  ARTSet13[ARTTB_ID] = true;
  ARTSet13[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet13[ARTTS__HASH] = true;
  ARTSet13[ARTTS__AMPERSAND] = true;
  ARTSet13[ARTTS__LPAR] = true;
  ARTSet13[ARTTS__RPAR] = true;
  ARTSet13[ARTTS__BAR] = true;
  ARTSet13[ARTL_ART_cfgAltNoAction] = true;
  ARTSet13[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet13[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet13[ARTL_ART_cfgDoFirst] = true;
  ARTSet13[ARTL_ART_cfgEpsilon] = true;
  ARTSet13[ARTL_ART_cfgEpsilonCarrier] = true;
  ARTSet13[ARTL_ART_cfgExtended] = true;
  ARTSet13[ARTL_ART_cfgKleene] = true;
  ARTSet13[ARTL_ART_cfgLHS] = true;
  ARTSet13[ARTL_ART_cfgNonterminal] = true;
  ARTSet13[ARTL_ART_cfgOptional] = true;
  ARTSet13[ARTL_ART_cfgPositive] = true;
  ARTSet13[ARTL_ART_cfgPrim] = true;
  ARTSet13[ARTL_ART_cfgRule] = true;
  ARTSet13[ARTL_ART_cfgRules] = true;
  ARTSet13[ARTL_ART_cfgSeq] = true;
  ARTSet13[ARTL_ART_cfgSlot] = true;
}

public void ARTSet53initialise() {
  ARTSet53 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet53, 0, artSetExtent, false);
  ARTSet53[ARTTB_ID] = true;
  ARTSet53[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet53[ARTTS__AMPERSAND] = true;
  ARTSet53[ARTTS__LPAR] = true;
  ARTSet53[ARTL_ART_cfgAltNoAction] = true;
  ARTSet53[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet53[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet53[ARTL_ART_cfgDoFirst] = true;
  ARTSet53[ARTL_ART_cfgExtended] = true;
  ARTSet53[ARTL_ART_cfgKleene] = true;
  ARTSet53[ARTL_ART_cfgNonterminal] = true;
  ARTSet53[ARTL_ART_cfgOptional] = true;
  ARTSet53[ARTL_ART_cfgPositive] = true;
  ARTSet53[ARTL_ART_cfgPrim] = true;
  ARTSet53[ARTL_ART_cfgSeq] = true;
  ARTSet53[ARTL_ART_cfgSlot] = true;
}

public void ARTSet61initialise() {
  ARTSet61 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet61, 0, artSetExtent, false);
  ARTSet61[ARTTB_ID] = true;
  ARTSet61[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet61[ARTTS__AMPERSAND] = true;
  ARTSet61[ARTTS__LPAR] = true;
  ARTSet61[ARTL_ART_cfgAltNoAction] = true;
  ARTSet61[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet61[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet61[ARTL_ART_cfgKleene] = true;
  ARTSet61[ARTL_ART_cfgNonterminal] = true;
  ARTSet61[ARTL_ART_cfgPrim] = true;
  ARTSet61[ARTL_ART_cfgSeq] = true;
  ARTSet61[ARTL_ART_cfgSlot] = true;
}

public void ARTSet78initialise() {
  ARTSet78 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet78, 0, artSetExtent, false);
  ARTSet78[ARTTB_ID] = true;
  ARTSet78[ARTL_ART_cfgLHS] = true;
  ARTSet78[ARTL_ART_cfgRule] = true;
}

public void ARTSet15initialise() {
  ARTSet15 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet15, 0, artSetExtent, false);
  ARTSet15[ARTTB_ID] = true;
  ARTSet15[ARTTS__UPARROW_PLUS] = true;
  ARTSet15[ARTL_ART_cfgAction] = true;
  ARTSet15[ARTL_ART_cfgActionSeq] = true;
  ARTSet15[ARTL_ART_cfgAssignment] = true;
  ARTSet15[ARTL_ART_cfgAttribute] = true;
  ARTSet15[ARTL_ART_cfgEquation] = true;
  ARTSet15[ARTL_ART_cfgInsert] = true;
  ARTSet15[ARTL_ART_cfgSlot] = true;
}

public void ARTSet27initialise() {
  ARTSet27 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet27, 0, artSetExtent, false);
  ARTSet27[ARTTS__HASH] = true;
  ARTSet27[ARTL_ART_cfgEpsilon] = true;
  ARTSet27[ARTL_ART_cfgEpsilonCarrier] = true;
}

public void ARTSet35initialise() {
  ARTSet35 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet35, 0, artSetExtent, false);
  ARTSet35[ARTTB_ID] = true;
  ARTSet35[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet35[ARTTS__HASH] = true;
  ARTSet35[ARTTS__AMPERSAND] = true;
  ARTSet35[ARTTS__LPAR] = true;
  ARTSet35[ARTTS__UPARROW_PLUS] = true;
  ARTSet35[ARTL_ART_cfgAction] = true;
  ARTSet35[ARTL_ART_cfgActionSeq] = true;
  ARTSet35[ARTL_ART_cfgActions] = true;
  ARTSet35[ARTL_ART_cfgAlt] = true;
  ARTSet35[ARTL_ART_cfgAltNoAction] = true;
  ARTSet35[ARTL_ART_cfgAssignment] = true;
  ARTSet35[ARTL_ART_cfgAttribute] = true;
  ARTSet35[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet35[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet35[ARTL_ART_cfgDoFirst] = true;
  ARTSet35[ARTL_ART_cfgElems] = true;
  ARTSet35[ARTL_ART_cfgEpsilon] = true;
  ARTSet35[ARTL_ART_cfgEpsilonCarrier] = true;
  ARTSet35[ARTL_ART_cfgEquation] = true;
  ARTSet35[ARTL_ART_cfgExtended] = true;
  ARTSet35[ARTL_ART_cfgInsert] = true;
  ARTSet35[ARTL_ART_cfgKleene] = true;
  ARTSet35[ARTL_ART_cfgNonterminal] = true;
  ARTSet35[ARTL_ART_cfgOptional] = true;
  ARTSet35[ARTL_ART_cfgPositive] = true;
  ARTSet35[ARTL_ART_cfgPrim] = true;
  ARTSet35[ARTL_ART_cfgSeq] = true;
  ARTSet35[ARTL_ART_cfgSlot] = true;
}

public void ARTSet11initialise() {
  ARTSet11 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet11, 0, artSetExtent, false);
  ARTSet11[ARTTS__UPARROW_PLUS] = true;
  ARTSet11[ARTL_ART_cfgInsert] = true;
}

public void ARTSet14initialise() {
  ARTSet14 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet14, 0, artSetExtent, false);
  ARTSet14[ARTTB_ID] = true;
  ARTSet14[ARTTS__UPARROW_PLUS] = true;
  ARTSet14[ARTL_ART_cfgAction] = true;
  ARTSet14[ARTL_ART_cfgActionSeq] = true;
  ARTSet14[ARTL_ART_cfgAssignment] = true;
  ARTSet14[ARTL_ART_cfgAttribute] = true;
  ARTSet14[ARTL_ART_cfgEquation] = true;
  ARTSet14[ARTL_ART_cfgInsert] = true;
}

public void ARTSet20initialise() {
  ARTSet20 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet20, 0, artSetExtent, false);
  ARTSet20[ARTTB_ID] = true;
  ARTSet20[ARTTS__HASH] = true;
  ARTSet20[ARTTS__UPARROW_PLUS] = true;
  ARTSet20[ARTL_ART_cfgAction] = true;
  ARTSet20[ARTL_ART_cfgActionSeq] = true;
  ARTSet20[ARTL_ART_cfgActions] = true;
  ARTSet20[ARTL_ART_cfgAssignment] = true;
  ARTSet20[ARTL_ART_cfgAttribute] = true;
  ARTSet20[ARTL_ART_cfgEpsilon] = true;
  ARTSet20[ARTL_ART_cfgEquation] = true;
  ARTSet20[ARTL_ART_cfgInsert] = true;
  ARTSet20[ARTL_ART_cfgSeq] = true;
  ARTSet20[ARTL_ART_cfgSlot] = true;
}

public void ARTSet82initialise() {
  ARTSet82 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet82, 0, artSetExtent, false);
  ARTSet82[ARTTB_ID] = true;
  ARTSet82[ARTTB_INTEGER] = true;
  ARTSet82[ARTTB_REAL] = true;
  ARTSet82[ARTTB_STRING_DQ] = true;
  ARTSet82[ARTL_ART_cfgAttribute] = true;
}

public void ARTSet80initialise() {
  ARTSet80 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet80, 0, artSetExtent, false);
  ARTSet80[ARTTB_ID] = true;
  ARTSet80[ARTL_ART_cfgLHS] = true;
  ARTSet80[ARTL_ART_cfgRule] = true;
  ARTSet80[ARTL_ART_cfgRules] = true;
}

public void ARTSet34initialise() {
  ARTSet34 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet34, 0, artSetExtent, false);
  ARTSet34[ARTTS__STAR] = true;
  ARTSet34[ARTTS__PLUS] = true;
  ARTSet34[ARTTS__QUERY] = true;
  ARTSet34[ARTL_ART_cfgSlot] = true;
}

public void ARTSet57initialise() {
  ARTSet57 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet57, 0, artSetExtent, false);
  ARTSet57[ARTTS__EQUAL] = true;
}

public void ARTSet4initialise() {
  ARTSet4 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet4, 0, artSetExtent, false);
}

public void ARTSet70initialise() {
  ARTSet70 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet70, 0, artSetExtent, false);
  ARTSet70[ARTTS__QUERY] = true;
}

public void ARTSet63initialise() {
  ARTSet63 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet63, 0, artSetExtent, false);
  ARTSet63[ARTTS__UPARROW_UPARROW] = true;
}

public void ARTSet52initialise() {
  ARTSet52 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet52, 0, artSetExtent, false);
  ARTSet52[ARTTB_ID] = true;
  ARTSet52[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet52[ARTTS__AMPERSAND] = true;
  ARTSet52[ARTTS__LPAR] = true;
  ARTSet52[ARTTS__UPARROW_PLUS] = true;
  ARTSet52[ARTL_ART_cfgAction] = true;
  ARTSet52[ARTL_ART_cfgActionSeq] = true;
  ARTSet52[ARTL_ART_cfgActions] = true;
  ARTSet52[ARTL_ART_cfgAltNoAction] = true;
  ARTSet52[ARTL_ART_cfgAssignment] = true;
  ARTSet52[ARTL_ART_cfgAttribute] = true;
  ARTSet52[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet52[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet52[ARTL_ART_cfgDoFirst] = true;
  ARTSet52[ARTL_ART_cfgEquation] = true;
  ARTSet52[ARTL_ART_cfgExtended] = true;
  ARTSet52[ARTL_ART_cfgInsert] = true;
  ARTSet52[ARTL_ART_cfgKleene] = true;
  ARTSet52[ARTL_ART_cfgNonterminal] = true;
  ARTSet52[ARTL_ART_cfgOptional] = true;
  ARTSet52[ARTL_ART_cfgPositive] = true;
  ARTSet52[ARTL_ART_cfgPrim] = true;
  ARTSet52[ARTL_ART_cfgSeq] = true;
  ARTSet52[ARTL_ART_cfgSlot] = true;
}

public void ARTSet72initialise() {
  ARTSet72 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet72, 0, artSetExtent, false);
  ARTSet72[ARTTB_ID] = true;
  ARTSet72[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet72[ARTTS__AMPERSAND] = true;
  ARTSet72[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet72[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet72[ARTL_ART_cfgNonterminal] = true;
}

public void ARTSet44initialise() {
  ARTSet44 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet44, 0, artSetExtent, false);
  ARTSet44[ARTTS__COLON_EQUAL] = true;
}

public void ARTSet9initialise() {
  ARTSet9 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet9, 0, artSetExtent, false);
  ARTSet9[ARTTB_ID] = true;
  ARTSet9[ARTL_ART_cfgAttribute] = true;
  ARTSet9[ARTL_ART_cfgEquation] = true;
}

public void ARTSet24initialise() {
  ARTSet24 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet24, 0, artSetExtent, false);
  ARTSet24[ARTTS__HASH] = true;
  ARTSet24[ARTL_ART_cfgEpsilon] = true;
}

public void ARTSet79initialise() {
  ARTSet79 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet79, 0, artSetExtent, false);
  ARTSet79[ARTX_EOS] = true;
}

public void ARTSet6initialise() {
  ARTSet6 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet6, 0, artSetExtent, false);
  ARTSet6[ARTTB_ID] = true;
  ARTSet6[ARTTB_INTEGER] = true;
  ARTSet6[ARTTB_REAL] = true;
  ARTSet6[ARTTB_STRING_DQ] = true;
  ARTSet6[ARTL_ART_arguments] = true;
  ARTSet6[ARTL_ART_cfgAttribute] = true;
  ARTSet6[ARTL_ART_term] = true;
}

public void ARTSet60initialise() {
  ARTSet60 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet60, 0, artSetExtent, false);
  ARTSet60[ARTTB_ID] = true;
  ARTSet60[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet60[ARTTS__AMPERSAND] = true;
  ARTSet60[ARTTS__LPAR] = true;
  ARTSet60[ARTL_ART_cfgAltNoAction] = true;
  ARTSet60[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet60[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet60[ARTL_ART_cfgNonterminal] = true;
  ARTSet60[ARTL_ART_cfgOptional] = true;
  ARTSet60[ARTL_ART_cfgPrim] = true;
  ARTSet60[ARTL_ART_cfgSeq] = true;
  ARTSet60[ARTL_ART_cfgSlot] = true;
}

public void ARTSet75initialise() {
  ARTSet75 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet75, 0, artSetExtent, false);
  ARTSet75[ARTTS__AMPERSAND] = true;
  ARTSet75[ARTL_ART_cfgBuiltinTerminal] = true;
}

public void ARTSet58initialise() {
  ARTSet58 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet58, 0, artSetExtent, false);
  ARTSet58[ARTTB_ID] = true;
  ARTSet58[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet58[ARTTS__AMPERSAND] = true;
  ARTSet58[ARTTS__LPAR] = true;
  ARTSet58[ARTL_ART_cfgAltNoAction] = true;
  ARTSet58[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet58[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet58[ARTL_ART_cfgDoFirst] = true;
  ARTSet58[ARTL_ART_cfgKleene] = true;
  ARTSet58[ARTL_ART_cfgNonterminal] = true;
  ARTSet58[ARTL_ART_cfgOptional] = true;
  ARTSet58[ARTL_ART_cfgPositive] = true;
  ARTSet58[ARTL_ART_cfgPrim] = true;
  ARTSet58[ARTL_ART_cfgSeq] = true;
  ARTSet58[ARTL_ART_cfgSlot] = true;
}

public void ARTSet45initialise() {
  ARTSet45 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet45, 0, artSetExtent, false);
  ARTSet45[ARTTB_ID] = true;
}

public void ARTSet64initialise() {
  ARTSet64 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet64, 0, artSetExtent, false);
  ARTSet64[ARTTS__UPARROW] = true;
}

public void ARTSet84initialise() {
  ARTSet84 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet84, 0, artSetExtent, false);
  ARTSet84[ARTTB_INTEGER] = true;
}

public void ARTSet59initialise() {
  ARTSet59 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet59, 0, artSetExtent, false);
  ARTSet59[ARTTS__LPAR] = true;
  ARTSet59[ARTL_ART_cfgDoFirst] = true;
}

public void ARTSet41initialise() {
  ARTSet41 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet41, 0, artSetExtent, false);
  ARTSet41[ARTTS__UPARROW] = true;
  ARTSet41[ARTL_ART_cfgFoldUnder] = true;
}

public void ARTSet17initialise() {
  ARTSet17 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet17, 0, artSetExtent, false);
  ARTSet17[ARTTB_ID] = true;
  ARTSet17[ARTTS__UPARROW_PLUS] = true;
  ARTSet17[ARTL_ART_cfgAction] = true;
  ARTSet17[ARTL_ART_cfgActionSeq] = true;
  ARTSet17[ARTL_ART_cfgAssignment] = true;
  ARTSet17[ARTL_ART_cfgAttribute] = true;
  ARTSet17[ARTL_ART_cfgEquation] = true;
  ARTSet17[ARTL_ART_cfgInsert] = true;
  ARTSet17[ARTL_ART_cfgSlot] = true;
}

public void ARTSet39initialise() {
  ARTSet39 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet39, 0, artSetExtent, false);
  ARTSet39[ARTTS__UPARROW] = true;
  ARTSet39[ARTTS__UPARROW_UPARROW] = true;
  ARTSet39[ARTL_ART_cfgFoldOver] = true;
  ARTSet39[ARTL_ART_cfgFoldUnder] = true;
}

public void ARTSet77initialise() {
  ARTSet77 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet77, 0, artSetExtent, false);
  ARTSet77[ARTX_EOS] = true;
  ARTSet77[ARTTB_ID] = true;
  ARTSet77[ARTL_ART_cfgLHS] = true;
  ARTSet77[ARTL_ART_cfgRule] = true;
  ARTSet77[ARTL_ART_cfgRules] = true;
}

public void ARTSet86initialise() {
  ARTSet86 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet86, 0, artSetExtent, false);
  ARTSet86[ARTTB_STRING_DQ] = true;
}

public void ARTSet29initialise() {
  ARTSet29 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet29, 0, artSetExtent, false);
  ARTSet29[ARTX_EOS] = true;
  ARTSet29[ARTTB_ID] = true;
  ARTSet29[ARTTS__RPAR] = true;
  ARTSet29[ARTTS__UPARROW_PLUS] = true;
  ARTSet29[ARTTS__BAR] = true;
  ARTSet29[ARTL_ART_cfgAction] = true;
  ARTSet29[ARTL_ART_cfgActionSeq] = true;
  ARTSet29[ARTL_ART_cfgActions] = true;
  ARTSet29[ARTL_ART_cfgAssignment] = true;
  ARTSet29[ARTL_ART_cfgAttribute] = true;
  ARTSet29[ARTL_ART_cfgEquation] = true;
  ARTSet29[ARTL_ART_cfgInsert] = true;
  ARTSet29[ARTL_ART_cfgLHS] = true;
  ARTSet29[ARTL_ART_cfgRule] = true;
  ARTSet29[ARTL_ART_cfgRules] = true;
  ARTSet29[ARTL_ART_cfgSlot] = true;
}

public void ARTSet26initialise() {
  ARTSet26 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet26, 0, artSetExtent, false);
  ARTSet26[ARTTB_ID] = true;
  ARTSet26[ARTTS__HASH] = true;
  ARTSet26[ARTTS__UPARROW_PLUS] = true;
  ARTSet26[ARTL_ART_cfgAction] = true;
  ARTSet26[ARTL_ART_cfgActionSeq] = true;
  ARTSet26[ARTL_ART_cfgActions] = true;
  ARTSet26[ARTL_ART_cfgAssignment] = true;
  ARTSet26[ARTL_ART_cfgAttribute] = true;
  ARTSet26[ARTL_ART_cfgEpsilon] = true;
  ARTSet26[ARTL_ART_cfgEpsilonCarrier] = true;
  ARTSet26[ARTL_ART_cfgEquation] = true;
  ARTSet26[ARTL_ART_cfgInsert] = true;
  ARTSet26[ARTL_ART_cfgSlot] = true;
}

public void ARTSet38initialise() {
  ARTSet38 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet38, 0, artSetExtent, false);
  ARTSet38[ARTTB_ID] = true;
  ARTSet38[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet38[ARTTS__HASH] = true;
  ARTSet38[ARTTS__AMPERSAND] = true;
  ARTSet38[ARTTS__LPAR] = true;
  ARTSet38[ARTTS__UPARROW_PLUS] = true;
  ARTSet38[ARTL_ART_cfgAction] = true;
  ARTSet38[ARTL_ART_cfgActionSeq] = true;
  ARTSet38[ARTL_ART_cfgActions] = true;
  ARTSet38[ARTL_ART_cfgAlt] = true;
  ARTSet38[ARTL_ART_cfgAltNoAction] = true;
  ARTSet38[ARTL_ART_cfgAlts] = true;
  ARTSet38[ARTL_ART_cfgAssignment] = true;
  ARTSet38[ARTL_ART_cfgAttribute] = true;
  ARTSet38[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet38[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet38[ARTL_ART_cfgDoFirst] = true;
  ARTSet38[ARTL_ART_cfgElems] = true;
  ARTSet38[ARTL_ART_cfgEpsilon] = true;
  ARTSet38[ARTL_ART_cfgEpsilonCarrier] = true;
  ARTSet38[ARTL_ART_cfgEquation] = true;
  ARTSet38[ARTL_ART_cfgExtended] = true;
  ARTSet38[ARTL_ART_cfgInsert] = true;
  ARTSet38[ARTL_ART_cfgKleene] = true;
  ARTSet38[ARTL_ART_cfgNonterminal] = true;
  ARTSet38[ARTL_ART_cfgOptional] = true;
  ARTSet38[ARTL_ART_cfgPositive] = true;
  ARTSet38[ARTL_ART_cfgPrim] = true;
  ARTSet38[ARTL_ART_cfgSeq] = true;
  ARTSet38[ARTL_ART_cfgSlot] = true;
}

public void ARTSet30initialise() {
  ARTSet30 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet30, 0, artSetExtent, false);
  ARTSet30[ARTTB_ID] = true;
  ARTSet30[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet30[ARTTS__AMPERSAND] = true;
  ARTSet30[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet30[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet30[ARTL_ART_cfgNonterminal] = true;
  ARTSet30[ARTL_ART_cfgPrim] = true;
  ARTSet30[ARTL_ART_cfgSeq] = true;
  ARTSet30[ARTL_ART_cfgSlot] = true;
}

public void ARTSet12initialise() {
  ARTSet12 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet12, 0, artSetExtent, false);
  ARTSet12[ARTTB_ID] = true;
  ARTSet12[ARTTS__UPARROW_PLUS] = true;
  ARTSet12[ARTL_ART_cfgAction] = true;
  ARTSet12[ARTL_ART_cfgAssignment] = true;
  ARTSet12[ARTL_ART_cfgAttribute] = true;
  ARTSet12[ARTL_ART_cfgEquation] = true;
  ARTSet12[ARTL_ART_cfgInsert] = true;
}

public void ARTSet50initialise() {
  ARTSet50 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet50, 0, artSetExtent, false);
  ARTSet50[ARTTB_STRING_PLAIN_SQ] = true;
}

public void ARTSet37initialise() {
  ARTSet37 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet37, 0, artSetExtent, false);
  ARTSet37[ARTTS__BAR] = true;
}

public void ARTSet56initialise() {
  ARTSet56 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet56, 0, artSetExtent, false);
  ARTSet56[ARTTS__UPARROW] = true;
  ARTSet56[ARTTS__UPARROW_UPARROW] = true;
  ARTSet56[ARTL_ART_cfgAnnotation] = true;
  ARTSet56[ARTL_ART_cfgFoldOver] = true;
  ARTSet56[ARTL_ART_cfgFoldUnder] = true;
}

public void ARTSet10initialise() {
  ARTSet10 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet10, 0, artSetExtent, false);
  ARTSet10[ARTTB_ID] = true;
  ARTSet10[ARTL_ART_cfgAssignment] = true;
  ARTSet10[ARTL_ART_cfgAttribute] = true;
}

public void ARTSet16initialise() {
  ARTSet16 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet16, 0, artSetExtent, false);
  ARTSet16[ARTL_ART_cfgSlot] = true;
}

public void ARTSet69initialise() {
  ARTSet69 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet69, 0, artSetExtent, false);
  ARTSet69[ARTTS__COLON_COLON_EQUAL] = true;
}

public void ARTSet73initialise() {
  ARTSet73 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet73, 0, artSetExtent, false);
  ARTSet73[ARTTB_ID] = true;
  ARTSet73[ARTL_ART_cfgNonterminal] = true;
}

public void ARTSet85initialise() {
  ARTSet85 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet85, 0, artSetExtent, false);
  ARTSet85[ARTTB_REAL] = true;
}

public void ARTSet66initialise() {
  ARTSet66 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet66, 0, artSetExtent, false);
  ARTSet66[ARTTB_ID] = true;
  ARTSet66[ARTTB_STRING_PLAIN_SQ] = true;
  ARTSet66[ARTTS__AMPERSAND] = true;
  ARTSet66[ARTTS__LPAR] = true;
  ARTSet66[ARTL_ART_cfgAltNoAction] = true;
  ARTSet66[ARTL_ART_cfgBuiltinTerminal] = true;
  ARTSet66[ARTL_ART_cfgCaseSensitiveTerminal] = true;
  ARTSet66[ARTL_ART_cfgNonterminal] = true;
  ARTSet66[ARTL_ART_cfgPrim] = true;
  ARTSet66[ARTL_ART_cfgSeq] = true;
  ARTSet66[ARTL_ART_cfgSlot] = true;
}

public void artSetInitialise() {
  ARTSet1initialise();
  ARTSet42initialise();
  ARTSet46initialise();
  ARTSet21initialise();
  ARTSet31initialise();
  ARTSet76initialise();
  ARTSet65initialise();
  ARTSet83initialise();
  ARTSet33initialise();
  ARTSet36initialise();
  ARTSet25initialise();
  ARTSet32initialise();
  ARTSet22initialise();
  ARTSet18initialise();
  ARTSet74initialise();
  ARTSet19initialise();
  ARTSet23initialise();
  ARTSet28initialise();
  ARTSet68initialise();
  ARTSet7initialise();
  ARTSet2initialise();
  ARTSet62initialise();
  ARTSet43initialise();
  ARTSet54initialise();
  ARTSet8initialise();
  ARTSet48initialise();
  ARTSet55initialise();
  ARTSet51initialise();
  ARTSet3initialise();
  ARTSet40initialise();
  ARTSet67initialise();
  ARTSet71initialise();
  ARTSet5initialise();
  ARTSet47initialise();
  ARTSet81initialise();
  ARTSet49initialise();
  ARTSet13initialise();
  ARTSet53initialise();
  ARTSet61initialise();
  ARTSet78initialise();
  ARTSet15initialise();
  ARTSet27initialise();
  ARTSet35initialise();
  ARTSet11initialise();
  ARTSet14initialise();
  ARTSet20initialise();
  ARTSet82initialise();
  ARTSet80initialise();
  ARTSet34initialise();
  ARTSet57initialise();
  ARTSet4initialise();
  ARTSet70initialise();
  ARTSet63initialise();
  ARTSet52initialise();
  ARTSet72initialise();
  ARTSet44initialise();
  ARTSet9initialise();
  ARTSet24initialise();
  ARTSet79initialise();
  ARTSet6initialise();
  ARTSet60initialise();
  ARTSet75initialise();
  ARTSet58initialise();
  ARTSet45initialise();
  ARTSet64initialise();
  ARTSet84initialise();
  ARTSet59initialise();
  ARTSet41initialise();
  ARTSet17initialise();
  ARTSet39initialise();
  ARTSet77initialise();
  ARTSet86initialise();
  ARTSet29initialise();
  ARTSet26initialise();
  ARTSet38initialise();
  ARTSet30initialise();
  ARTSet12initialise();
  ARTSet50initialise();
  ARTSet37initialise();
  ARTSet56initialise();
  ARTSet10initialise();
  ARTSet16initialise();
  ARTSet69initialise();
  ARTSet73initialise();
  ARTSet85initialise();
  ARTSet66initialise();
}

public void artTableInitialiser_ART_arguments() {
  artLabelInternalStrings[ARTL_ART_arguments] = "arguments";
  artLabelStrings[ARTL_ART_arguments] = "arguments";
  artKindOfs[ARTL_ART_arguments] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_arguments_322] = "arguments ::= . term ";
  artLabelStrings[ARTL_ART_arguments_322] = "";
  artlhsL[ARTL_ART_arguments_322] = ARTL_ART_arguments;
  artKindOfs[ARTL_ART_arguments_322] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_arguments_324] = "arguments ::= term .";
  artLabelStrings[ARTL_ART_arguments_324] = "";
  artlhsL[ARTL_ART_arguments_324] = ARTL_ART_arguments;
  artSlotInstanceOfs[ARTL_ART_arguments_324] = ARTL_ART_term;
  artKindOfs[ARTL_ART_arguments_324] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_arguments_324] = true;
  arteoR_pL[ARTL_ART_arguments_324] = true;
  artPopD[ARTL_ART_arguments_324] = true;
  artLabelInternalStrings[ARTL_ART_arguments_326] = "arguments ::= . term ','  arguments ";
  artLabelStrings[ARTL_ART_arguments_326] = "";
  artlhsL[ARTL_ART_arguments_326] = ARTL_ART_arguments;
  artKindOfs[ARTL_ART_arguments_326] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_arguments_328] = "arguments ::= term . ','  arguments ";
  artLabelStrings[ARTL_ART_arguments_328] = "";
  artlhsL[ARTL_ART_arguments_328] = ARTL_ART_arguments;
  artSlotInstanceOfs[ARTL_ART_arguments_328] = ARTL_ART_term;
  artKindOfs[ARTL_ART_arguments_328] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_arguments_328] = true;
  artFolds[ARTL_ART_arguments_330] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_arguments_329] = "arguments ::= term ','  arguments ";
  artLabelStrings[ARTL_ART_arguments_329] = "";
  artlhsL[ARTL_ART_arguments_329] = ARTL_ART_arguments;
  artLabelInternalStrings[ARTL_ART_arguments_330] = "arguments ::= term ','  . arguments ";
  artLabelStrings[ARTL_ART_arguments_330] = "";
  artlhsL[ARTL_ART_arguments_330] = ARTL_ART_arguments;
  artKindOfs[ARTL_ART_arguments_330] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_arguments_332] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_arguments_332] = "arguments ::= term ','  arguments .";
  artLabelStrings[ARTL_ART_arguments_332] = "";
  artlhsL[ARTL_ART_arguments_332] = ARTL_ART_arguments;
  artSlotInstanceOfs[ARTL_ART_arguments_332] = ARTL_ART_arguments;
  artKindOfs[ARTL_ART_arguments_332] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_arguments_332] = true;
  arteoR_pL[ARTL_ART_arguments_332] = true;
  artPopD[ARTL_ART_arguments_332] = true;
}

public void artTableInitialiser_ART_cfgAction() {
  artLabelInternalStrings[ARTL_ART_cfgAction] = "cfgAction";
  artLabelStrings[ARTL_ART_cfgAction] = "cfgAction";
  artKindOfs[ARTL_ART_cfgAction] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgAction_246] = "cfgAction ::= . cfgEquation ";
  artLabelStrings[ARTL_ART_cfgAction_246] = "";
  artlhsL[ARTL_ART_cfgAction_246] = ARTL_ART_cfgAction;
  artKindOfs[ARTL_ART_cfgAction_246] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgAction_248] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgAction_248] = "cfgAction ::= cfgEquation .";
  artLabelStrings[ARTL_ART_cfgAction_248] = "";
  artlhsL[ARTL_ART_cfgAction_248] = ARTL_ART_cfgAction;
  artSlotInstanceOfs[ARTL_ART_cfgAction_248] = ARTL_ART_cfgEquation;
  artKindOfs[ARTL_ART_cfgAction_248] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgAction_248] = true;
  arteoR_pL[ARTL_ART_cfgAction_248] = true;
  artPopD[ARTL_ART_cfgAction_248] = true;
  artLabelInternalStrings[ARTL_ART_cfgAction_250] = "cfgAction ::= . cfgAssignment ";
  artLabelStrings[ARTL_ART_cfgAction_250] = "";
  artlhsL[ARTL_ART_cfgAction_250] = ARTL_ART_cfgAction;
  artKindOfs[ARTL_ART_cfgAction_250] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgAction_252] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgAction_252] = "cfgAction ::= cfgAssignment .";
  artLabelStrings[ARTL_ART_cfgAction_252] = "";
  artlhsL[ARTL_ART_cfgAction_252] = ARTL_ART_cfgAction;
  artSlotInstanceOfs[ARTL_ART_cfgAction_252] = ARTL_ART_cfgAssignment;
  artKindOfs[ARTL_ART_cfgAction_252] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgAction_252] = true;
  arteoR_pL[ARTL_ART_cfgAction_252] = true;
  artPopD[ARTL_ART_cfgAction_252] = true;
  artLabelInternalStrings[ARTL_ART_cfgAction_254] = "cfgAction ::= . cfgInsert ";
  artLabelStrings[ARTL_ART_cfgAction_254] = "";
  artlhsL[ARTL_ART_cfgAction_254] = ARTL_ART_cfgAction;
  artKindOfs[ARTL_ART_cfgAction_254] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgAction_256] = "cfgAction ::= cfgInsert .";
  artLabelStrings[ARTL_ART_cfgAction_256] = "";
  artlhsL[ARTL_ART_cfgAction_256] = ARTL_ART_cfgAction;
  artSlotInstanceOfs[ARTL_ART_cfgAction_256] = ARTL_ART_cfgInsert;
  artKindOfs[ARTL_ART_cfgAction_256] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgAction_256] = true;
  arteoR_pL[ARTL_ART_cfgAction_256] = true;
  artPopD[ARTL_ART_cfgAction_256] = true;
}

public void artTableInitialiser_ART_cfgActionSeq() {
  artLabelInternalStrings[ARTL_ART_cfgActionSeq] = "cfgActionSeq";
  artLabelStrings[ARTL_ART_cfgActionSeq] = "cfgActionSeq";
  artKindOfs[ARTL_ART_cfgActionSeq] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgActionSeq_236] = "cfgActionSeq ::= . cfgAction ";
  artLabelStrings[ARTL_ART_cfgActionSeq_236] = "";
  artlhsL[ARTL_ART_cfgActionSeq_236] = ARTL_ART_cfgActionSeq;
  artKindOfs[ARTL_ART_cfgActionSeq_236] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgActionSeq_238] = "cfgActionSeq ::= cfgAction .";
  artLabelStrings[ARTL_ART_cfgActionSeq_238] = "";
  artlhsL[ARTL_ART_cfgActionSeq_238] = ARTL_ART_cfgActionSeq;
  artSlotInstanceOfs[ARTL_ART_cfgActionSeq_238] = ARTL_ART_cfgAction;
  artKindOfs[ARTL_ART_cfgActionSeq_238] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgActionSeq_238] = true;
  arteoR_pL[ARTL_ART_cfgActionSeq_238] = true;
  artPopD[ARTL_ART_cfgActionSeq_238] = true;
  artLabelInternalStrings[ARTL_ART_cfgActionSeq_240] = "cfgActionSeq ::= . cfgAction cfgActionSeq ";
  artLabelStrings[ARTL_ART_cfgActionSeq_240] = "";
  artlhsL[ARTL_ART_cfgActionSeq_240] = ARTL_ART_cfgActionSeq;
  artKindOfs[ARTL_ART_cfgActionSeq_240] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgActionSeq_242] = "cfgActionSeq ::= cfgAction . cfgActionSeq ";
  artLabelStrings[ARTL_ART_cfgActionSeq_242] = "";
  artlhsL[ARTL_ART_cfgActionSeq_242] = ARTL_ART_cfgActionSeq;
  artSlotInstanceOfs[ARTL_ART_cfgActionSeq_242] = ARTL_ART_cfgAction;
  artKindOfs[ARTL_ART_cfgActionSeq_242] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgActionSeq_242] = true;
  artFolds[ARTL_ART_cfgActionSeq_244] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgActionSeq_244] = "cfgActionSeq ::= cfgAction cfgActionSeq .";
  artLabelStrings[ARTL_ART_cfgActionSeq_244] = "";
  artlhsL[ARTL_ART_cfgActionSeq_244] = ARTL_ART_cfgActionSeq;
  artSlotInstanceOfs[ARTL_ART_cfgActionSeq_244] = ARTL_ART_cfgActionSeq;
  artKindOfs[ARTL_ART_cfgActionSeq_244] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgActionSeq_244] = true;
  arteoR_pL[ARTL_ART_cfgActionSeq_244] = true;
  artPopD[ARTL_ART_cfgActionSeq_244] = true;
}

public void artTableInitialiser_ART_cfgActions() {
  artLabelInternalStrings[ARTL_ART_cfgActions] = "cfgActions";
  artLabelStrings[ARTL_ART_cfgActions] = "cfgActions";
  artKindOfs[ARTL_ART_cfgActions] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgActions_64] = "cfgActions ::= . cfgSlot ";
  artLabelStrings[ARTL_ART_cfgActions_64] = "";
  artlhsL[ARTL_ART_cfgActions_64] = ARTL_ART_cfgActions;
  artKindOfs[ARTL_ART_cfgActions_64] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgActions_66] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgActions_66] = "cfgActions ::= cfgSlot .";
  artLabelStrings[ARTL_ART_cfgActions_66] = "";
  artlhsL[ARTL_ART_cfgActions_66] = ARTL_ART_cfgActions;
  artSlotInstanceOfs[ARTL_ART_cfgActions_66] = ARTL_ART_cfgSlot;
  artKindOfs[ARTL_ART_cfgActions_66] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgActions_66] = true;
  arteoR_pL[ARTL_ART_cfgActions_66] = true;
  artPopD[ARTL_ART_cfgActions_66] = true;
  artLabelInternalStrings[ARTL_ART_cfgActions_68] = "cfgActions ::= . cfgSlot cfgActionSeq ";
  artLabelStrings[ARTL_ART_cfgActions_68] = "";
  artlhsL[ARTL_ART_cfgActions_68] = ARTL_ART_cfgActions;
  artKindOfs[ARTL_ART_cfgActions_68] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgActions_70] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgActions_70] = "cfgActions ::= cfgSlot . cfgActionSeq ";
  artLabelStrings[ARTL_ART_cfgActions_70] = "";
  artlhsL[ARTL_ART_cfgActions_70] = ARTL_ART_cfgActions;
  artSlotInstanceOfs[ARTL_ART_cfgActions_70] = ARTL_ART_cfgSlot;
  artKindOfs[ARTL_ART_cfgActions_70] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgActions_70] = true;
  artFolds[ARTL_ART_cfgActions_72] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgActions_72] = "cfgActions ::= cfgSlot cfgActionSeq .";
  artLabelStrings[ARTL_ART_cfgActions_72] = "";
  artlhsL[ARTL_ART_cfgActions_72] = ARTL_ART_cfgActions;
  artSlotInstanceOfs[ARTL_ART_cfgActions_72] = ARTL_ART_cfgActionSeq;
  artKindOfs[ARTL_ART_cfgActions_72] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgActions_72] = true;
  arteoR_pL[ARTL_ART_cfgActions_72] = true;
  artPopD[ARTL_ART_cfgActions_72] = true;
}

public void artTableInitialiser_ART_cfgAlt() {
  artLabelInternalStrings[ARTL_ART_cfgAlt] = "cfgAlt";
  artLabelStrings[ARTL_ART_cfgAlt] = "cfgAlt";
  artKindOfs[ARTL_ART_cfgAlt] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgAlt_36] = "cfgAlt ::= . cfgSeq cfgActions cfgEpsilon ";
  artLabelStrings[ARTL_ART_cfgAlt_36] = "";
  artlhsL[ARTL_ART_cfgAlt_36] = ARTL_ART_cfgAlt;
  artKindOfs[ARTL_ART_cfgAlt_36] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgAlt_38] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgAlt_38] = "cfgAlt ::= cfgSeq . cfgActions cfgEpsilon ";
  artLabelStrings[ARTL_ART_cfgAlt_38] = "";
  artlhsL[ARTL_ART_cfgAlt_38] = ARTL_ART_cfgAlt;
  artSlotInstanceOfs[ARTL_ART_cfgAlt_38] = ARTL_ART_cfgSeq;
  artKindOfs[ARTL_ART_cfgAlt_38] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgAlt_38] = true;
  artLabelInternalStrings[ARTL_ART_cfgAlt_40] = "cfgAlt ::= cfgSeq cfgActions . cfgEpsilon ";
  artLabelStrings[ARTL_ART_cfgAlt_40] = "";
  artlhsL[ARTL_ART_cfgAlt_40] = ARTL_ART_cfgAlt;
  artSlotInstanceOfs[ARTL_ART_cfgAlt_40] = ARTL_ART_cfgActions;
  artKindOfs[ARTL_ART_cfgAlt_40] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgAlt_42] = "cfgAlt ::= cfgSeq cfgActions cfgEpsilon .";
  artLabelStrings[ARTL_ART_cfgAlt_42] = "";
  artlhsL[ARTL_ART_cfgAlt_42] = ARTL_ART_cfgAlt;
  artSlotInstanceOfs[ARTL_ART_cfgAlt_42] = ARTL_ART_cfgEpsilon;
  artKindOfs[ARTL_ART_cfgAlt_42] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgAlt_42] = true;
  arteoR_pL[ARTL_ART_cfgAlt_42] = true;
  artPopD[ARTL_ART_cfgAlt_42] = true;
  artLabelInternalStrings[ARTL_ART_cfgAlt_44] = "cfgAlt ::= . cfgSeq cfgActions cfgEpsilonCarrier ";
  artLabelStrings[ARTL_ART_cfgAlt_44] = "";
  artlhsL[ARTL_ART_cfgAlt_44] = ARTL_ART_cfgAlt;
  artKindOfs[ARTL_ART_cfgAlt_44] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgAlt_46] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgAlt_46] = "cfgAlt ::= cfgSeq . cfgActions cfgEpsilonCarrier ";
  artLabelStrings[ARTL_ART_cfgAlt_46] = "";
  artlhsL[ARTL_ART_cfgAlt_46] = ARTL_ART_cfgAlt;
  artSlotInstanceOfs[ARTL_ART_cfgAlt_46] = ARTL_ART_cfgSeq;
  artKindOfs[ARTL_ART_cfgAlt_46] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgAlt_46] = true;
  artLabelInternalStrings[ARTL_ART_cfgAlt_48] = "cfgAlt ::= cfgSeq cfgActions . cfgEpsilonCarrier ";
  artLabelStrings[ARTL_ART_cfgAlt_48] = "";
  artlhsL[ARTL_ART_cfgAlt_48] = ARTL_ART_cfgAlt;
  artSlotInstanceOfs[ARTL_ART_cfgAlt_48] = ARTL_ART_cfgActions;
  artKindOfs[ARTL_ART_cfgAlt_48] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgAlt_50] = "cfgAlt ::= cfgSeq cfgActions cfgEpsilonCarrier .";
  artLabelStrings[ARTL_ART_cfgAlt_50] = "";
  artlhsL[ARTL_ART_cfgAlt_50] = ARTL_ART_cfgAlt;
  artSlotInstanceOfs[ARTL_ART_cfgAlt_50] = ARTL_ART_cfgEpsilonCarrier;
  artKindOfs[ARTL_ART_cfgAlt_50] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgAlt_50] = true;
  arteoR_pL[ARTL_ART_cfgAlt_50] = true;
  artPopD[ARTL_ART_cfgAlt_50] = true;
  artLabelInternalStrings[ARTL_ART_cfgAlt_52] = "cfgAlt ::= . cfgSeq cfgElems cfgActions ";
  artLabelStrings[ARTL_ART_cfgAlt_52] = "";
  artlhsL[ARTL_ART_cfgAlt_52] = ARTL_ART_cfgAlt;
  artKindOfs[ARTL_ART_cfgAlt_52] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgAlt_54] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgAlt_54] = "cfgAlt ::= cfgSeq . cfgElems cfgActions ";
  artLabelStrings[ARTL_ART_cfgAlt_54] = "";
  artlhsL[ARTL_ART_cfgAlt_54] = ARTL_ART_cfgAlt;
  artSlotInstanceOfs[ARTL_ART_cfgAlt_54] = ARTL_ART_cfgSeq;
  artKindOfs[ARTL_ART_cfgAlt_54] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgAlt_54] = true;
  artFolds[ARTL_ART_cfgAlt_56] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgAlt_56] = "cfgAlt ::= cfgSeq cfgElems . cfgActions ";
  artLabelStrings[ARTL_ART_cfgAlt_56] = "";
  artlhsL[ARTL_ART_cfgAlt_56] = ARTL_ART_cfgAlt;
  artSlotInstanceOfs[ARTL_ART_cfgAlt_56] = ARTL_ART_cfgElems;
  artKindOfs[ARTL_ART_cfgAlt_56] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgAlt_58] = "cfgAlt ::= cfgSeq cfgElems cfgActions .";
  artLabelStrings[ARTL_ART_cfgAlt_58] = "";
  artlhsL[ARTL_ART_cfgAlt_58] = ARTL_ART_cfgAlt;
  artSlotInstanceOfs[ARTL_ART_cfgAlt_58] = ARTL_ART_cfgActions;
  artKindOfs[ARTL_ART_cfgAlt_58] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgAlt_58] = true;
  arteoR_pL[ARTL_ART_cfgAlt_58] = true;
  artPopD[ARTL_ART_cfgAlt_58] = true;
}

public void artTableInitialiser_ART_cfgAltNoAction() {
  artLabelInternalStrings[ARTL_ART_cfgAltNoAction] = "cfgAltNoAction";
  artLabelStrings[ARTL_ART_cfgAltNoAction] = "cfgAltNoAction";
  artKindOfs[ARTL_ART_cfgAltNoAction] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgAltNoAction_106] = "cfgAltNoAction ::= . cfgSeq cfgSlot cfgPrim cfgSlot ";
  artLabelStrings[ARTL_ART_cfgAltNoAction_106] = "";
  artlhsL[ARTL_ART_cfgAltNoAction_106] = ARTL_ART_cfgAltNoAction;
  artKindOfs[ARTL_ART_cfgAltNoAction_106] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgAltNoAction_108] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgAltNoAction_108] = "cfgAltNoAction ::= cfgSeq . cfgSlot cfgPrim cfgSlot ";
  artLabelStrings[ARTL_ART_cfgAltNoAction_108] = "";
  artlhsL[ARTL_ART_cfgAltNoAction_108] = ARTL_ART_cfgAltNoAction;
  artSlotInstanceOfs[ARTL_ART_cfgAltNoAction_108] = ARTL_ART_cfgSeq;
  artKindOfs[ARTL_ART_cfgAltNoAction_108] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgAltNoAction_108] = true;
  artLabelInternalStrings[ARTL_ART_cfgAltNoAction_110] = "cfgAltNoAction ::= cfgSeq cfgSlot . cfgPrim cfgSlot ";
  artLabelStrings[ARTL_ART_cfgAltNoAction_110] = "";
  artlhsL[ARTL_ART_cfgAltNoAction_110] = ARTL_ART_cfgAltNoAction;
  artSlotInstanceOfs[ARTL_ART_cfgAltNoAction_110] = ARTL_ART_cfgSlot;
  artKindOfs[ARTL_ART_cfgAltNoAction_110] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgAltNoAction_112] = "cfgAltNoAction ::= cfgSeq cfgSlot cfgPrim . cfgSlot ";
  artLabelStrings[ARTL_ART_cfgAltNoAction_112] = "";
  artlhsL[ARTL_ART_cfgAltNoAction_112] = ARTL_ART_cfgAltNoAction;
  artSlotInstanceOfs[ARTL_ART_cfgAltNoAction_112] = ARTL_ART_cfgPrim;
  artKindOfs[ARTL_ART_cfgAltNoAction_112] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgAltNoAction_114] = "cfgAltNoAction ::= cfgSeq cfgSlot cfgPrim cfgSlot .";
  artLabelStrings[ARTL_ART_cfgAltNoAction_114] = "";
  artlhsL[ARTL_ART_cfgAltNoAction_114] = ARTL_ART_cfgAltNoAction;
  artSlotInstanceOfs[ARTL_ART_cfgAltNoAction_114] = ARTL_ART_cfgSlot;
  artKindOfs[ARTL_ART_cfgAltNoAction_114] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgAltNoAction_114] = true;
  arteoR_pL[ARTL_ART_cfgAltNoAction_114] = true;
  artPopD[ARTL_ART_cfgAltNoAction_114] = true;
}

public void artTableInitialiser_ART_cfgAlts() {
  artLabelInternalStrings[ARTL_ART_cfgAlts] = "cfgAlts";
  artLabelStrings[ARTL_ART_cfgAlts] = "cfgAlts";
  artKindOfs[ARTL_ART_cfgAlts] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgAlts_24] = "cfgAlts ::= . cfgAlt ";
  artLabelStrings[ARTL_ART_cfgAlts_24] = "";
  artlhsL[ARTL_ART_cfgAlts_24] = ARTL_ART_cfgAlts;
  artKindOfs[ARTL_ART_cfgAlts_24] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgAlts_26] = "cfgAlts ::= cfgAlt .";
  artLabelStrings[ARTL_ART_cfgAlts_26] = "";
  artlhsL[ARTL_ART_cfgAlts_26] = ARTL_ART_cfgAlts;
  artSlotInstanceOfs[ARTL_ART_cfgAlts_26] = ARTL_ART_cfgAlt;
  artKindOfs[ARTL_ART_cfgAlts_26] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgAlts_26] = true;
  arteoR_pL[ARTL_ART_cfgAlts_26] = true;
  artPopD[ARTL_ART_cfgAlts_26] = true;
  artLabelInternalStrings[ARTL_ART_cfgAlts_28] = "cfgAlts ::= . cfgAlt '|'  cfgAlts ";
  artLabelStrings[ARTL_ART_cfgAlts_28] = "";
  artlhsL[ARTL_ART_cfgAlts_28] = ARTL_ART_cfgAlts;
  artKindOfs[ARTL_ART_cfgAlts_28] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgAlts_30] = "cfgAlts ::= cfgAlt . '|'  cfgAlts ";
  artLabelStrings[ARTL_ART_cfgAlts_30] = "";
  artlhsL[ARTL_ART_cfgAlts_30] = ARTL_ART_cfgAlts;
  artSlotInstanceOfs[ARTL_ART_cfgAlts_30] = ARTL_ART_cfgAlt;
  artKindOfs[ARTL_ART_cfgAlts_30] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgAlts_30] = true;
  artFolds[ARTL_ART_cfgAlts_32] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgAlts_31] = "cfgAlts ::= cfgAlt '|'  cfgAlts ";
  artLabelStrings[ARTL_ART_cfgAlts_31] = "";
  artlhsL[ARTL_ART_cfgAlts_31] = ARTL_ART_cfgAlts;
  artLabelInternalStrings[ARTL_ART_cfgAlts_32] = "cfgAlts ::= cfgAlt '|'  . cfgAlts ";
  artLabelStrings[ARTL_ART_cfgAlts_32] = "";
  artlhsL[ARTL_ART_cfgAlts_32] = ARTL_ART_cfgAlts;
  artKindOfs[ARTL_ART_cfgAlts_32] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgAlts_34] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgAlts_34] = "cfgAlts ::= cfgAlt '|'  cfgAlts .";
  artLabelStrings[ARTL_ART_cfgAlts_34] = "";
  artlhsL[ARTL_ART_cfgAlts_34] = ARTL_ART_cfgAlts;
  artSlotInstanceOfs[ARTL_ART_cfgAlts_34] = ARTL_ART_cfgAlts;
  artKindOfs[ARTL_ART_cfgAlts_34] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgAlts_34] = true;
  arteoR_pL[ARTL_ART_cfgAlts_34] = true;
  artPopD[ARTL_ART_cfgAlts_34] = true;
}

public void artTableInitialiser_ART_cfgAnnotation() {
  artLabelInternalStrings[ARTL_ART_cfgAnnotation] = "cfgAnnotation";
  artLabelStrings[ARTL_ART_cfgAnnotation] = "cfgAnnotation";
  artKindOfs[ARTL_ART_cfgAnnotation] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgAnnotation_98] = "cfgAnnotation ::= . cfgFoldUnder ";
  artLabelStrings[ARTL_ART_cfgAnnotation_98] = "";
  artlhsL[ARTL_ART_cfgAnnotation_98] = ARTL_ART_cfgAnnotation;
  artKindOfs[ARTL_ART_cfgAnnotation_98] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgAnnotation_100] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgAnnotation_100] = "cfgAnnotation ::= cfgFoldUnder .";
  artLabelStrings[ARTL_ART_cfgAnnotation_100] = "";
  artlhsL[ARTL_ART_cfgAnnotation_100] = ARTL_ART_cfgAnnotation;
  artSlotInstanceOfs[ARTL_ART_cfgAnnotation_100] = ARTL_ART_cfgFoldUnder;
  artKindOfs[ARTL_ART_cfgAnnotation_100] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgAnnotation_100] = true;
  arteoR_pL[ARTL_ART_cfgAnnotation_100] = true;
  artPopD[ARTL_ART_cfgAnnotation_100] = true;
  artLabelInternalStrings[ARTL_ART_cfgAnnotation_102] = "cfgAnnotation ::= . cfgFoldOver ";
  artLabelStrings[ARTL_ART_cfgAnnotation_102] = "";
  artlhsL[ARTL_ART_cfgAnnotation_102] = ARTL_ART_cfgAnnotation;
  artKindOfs[ARTL_ART_cfgAnnotation_102] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgAnnotation_104] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgAnnotation_104] = "cfgAnnotation ::= cfgFoldOver .";
  artLabelStrings[ARTL_ART_cfgAnnotation_104] = "";
  artlhsL[ARTL_ART_cfgAnnotation_104] = ARTL_ART_cfgAnnotation;
  artSlotInstanceOfs[ARTL_ART_cfgAnnotation_104] = ARTL_ART_cfgFoldOver;
  artKindOfs[ARTL_ART_cfgAnnotation_104] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgAnnotation_104] = true;
  arteoR_pL[ARTL_ART_cfgAnnotation_104] = true;
  artPopD[ARTL_ART_cfgAnnotation_104] = true;
}

public void artTableInitialiser_ART_cfgAssignment() {
  artLabelInternalStrings[ARTL_ART_cfgAssignment] = "cfgAssignment";
  artLabelStrings[ARTL_ART_cfgAssignment] = "cfgAssignment";
  artKindOfs[ARTL_ART_cfgAssignment] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgAssignment_266] = "cfgAssignment ::= . cfgAttribute ':='  term ";
  artLabelStrings[ARTL_ART_cfgAssignment_266] = "";
  artlhsL[ARTL_ART_cfgAssignment_266] = ARTL_ART_cfgAssignment;
  artKindOfs[ARTL_ART_cfgAssignment_266] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgAssignment_268] = "cfgAssignment ::= cfgAttribute . ':='  term ";
  artLabelStrings[ARTL_ART_cfgAssignment_268] = "";
  artlhsL[ARTL_ART_cfgAssignment_268] = ARTL_ART_cfgAssignment;
  artSlotInstanceOfs[ARTL_ART_cfgAssignment_268] = ARTL_ART_cfgAttribute;
  artKindOfs[ARTL_ART_cfgAssignment_268] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgAssignment_268] = true;
  artFolds[ARTL_ART_cfgAssignment_270] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgAssignment_269] = "cfgAssignment ::= cfgAttribute ':='  term ";
  artLabelStrings[ARTL_ART_cfgAssignment_269] = "";
  artlhsL[ARTL_ART_cfgAssignment_269] = ARTL_ART_cfgAssignment;
  artLabelInternalStrings[ARTL_ART_cfgAssignment_270] = "cfgAssignment ::= cfgAttribute ':='  . term ";
  artLabelStrings[ARTL_ART_cfgAssignment_270] = "";
  artlhsL[ARTL_ART_cfgAssignment_270] = ARTL_ART_cfgAssignment;
  artKindOfs[ARTL_ART_cfgAssignment_270] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgAssignment_272] = "cfgAssignment ::= cfgAttribute ':='  term .";
  artLabelStrings[ARTL_ART_cfgAssignment_272] = "";
  artlhsL[ARTL_ART_cfgAssignment_272] = ARTL_ART_cfgAssignment;
  artSlotInstanceOfs[ARTL_ART_cfgAssignment_272] = ARTL_ART_term;
  artKindOfs[ARTL_ART_cfgAssignment_272] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgAssignment_272] = true;
  arteoR_pL[ARTL_ART_cfgAssignment_272] = true;
  artPopD[ARTL_ART_cfgAssignment_272] = true;
}

public void artTableInitialiser_ART_cfgAttribute() {
  artLabelInternalStrings[ARTL_ART_cfgAttribute] = "cfgAttribute";
  artLabelStrings[ARTL_ART_cfgAttribute] = "cfgAttribute";
  artKindOfs[ARTL_ART_cfgAttribute] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgAttribute_280] = "cfgAttribute ::= . &ID  '.'  &ID  ";
  artLabelStrings[ARTL_ART_cfgAttribute_280] = "";
  artlhsL[ARTL_ART_cfgAttribute_280] = ARTL_ART_cfgAttribute;
  artKindOfs[ARTL_ART_cfgAttribute_280] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgAttribute_280] = true;
  artLabelInternalStrings[ARTL_ART_cfgAttribute_281] = "cfgAttribute ::= &ID  '.'  &ID  ";
  artLabelStrings[ARTL_ART_cfgAttribute_281] = "";
  artlhsL[ARTL_ART_cfgAttribute_281] = ARTL_ART_cfgAttribute;
  artPopD[ARTL_ART_cfgAttribute_281] = true;
  artLabelInternalStrings[ARTL_ART_cfgAttribute_282] = "cfgAttribute ::= &ID  . '.'  &ID  ";
  artLabelStrings[ARTL_ART_cfgAttribute_282] = "";
  artlhsL[ARTL_ART_cfgAttribute_282] = ARTL_ART_cfgAttribute;
  artKindOfs[ARTL_ART_cfgAttribute_282] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgAttribute_282] = true;
  artPopD[ARTL_ART_cfgAttribute_282] = true;
  artFolds[ARTL_ART_cfgAttribute_284] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgAttribute_283] = "cfgAttribute ::= &ID  '.'  &ID  ";
  artLabelStrings[ARTL_ART_cfgAttribute_283] = "";
  artlhsL[ARTL_ART_cfgAttribute_283] = ARTL_ART_cfgAttribute;
  artPopD[ARTL_ART_cfgAttribute_283] = true;
  artLabelInternalStrings[ARTL_ART_cfgAttribute_284] = "cfgAttribute ::= &ID  '.'  . &ID  ";
  artLabelStrings[ARTL_ART_cfgAttribute_284] = "";
  artlhsL[ARTL_ART_cfgAttribute_284] = ARTL_ART_cfgAttribute;
  artKindOfs[ARTL_ART_cfgAttribute_284] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgAttribute_284] = true;
  artLabelInternalStrings[ARTL_ART_cfgAttribute_285] = "cfgAttribute ::= &ID  '.'  &ID  ";
  artLabelStrings[ARTL_ART_cfgAttribute_285] = "";
  artlhsL[ARTL_ART_cfgAttribute_285] = ARTL_ART_cfgAttribute;
  artPopD[ARTL_ART_cfgAttribute_285] = true;
  artLabelInternalStrings[ARTL_ART_cfgAttribute_286] = "cfgAttribute ::= &ID  '.'  &ID  .";
  artLabelStrings[ARTL_ART_cfgAttribute_286] = "";
  artlhsL[ARTL_ART_cfgAttribute_286] = ARTL_ART_cfgAttribute;
  artKindOfs[ARTL_ART_cfgAttribute_286] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgAttribute_286] = true;
  arteoR_pL[ARTL_ART_cfgAttribute_286] = true;
  artPopD[ARTL_ART_cfgAttribute_286] = true;
}

public void artTableInitialiser_ART_cfgBuiltinTerminal() {
  artLabelInternalStrings[ARTL_ART_cfgBuiltinTerminal] = "cfgBuiltinTerminal";
  artLabelStrings[ARTL_ART_cfgBuiltinTerminal] = "cfgBuiltinTerminal";
  artKindOfs[ARTL_ART_cfgBuiltinTerminal] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgBuiltinTerminal_230] = "cfgBuiltinTerminal ::= . '&'  &ID  ";
  artLabelStrings[ARTL_ART_cfgBuiltinTerminal_230] = "";
  artlhsL[ARTL_ART_cfgBuiltinTerminal_230] = ARTL_ART_cfgBuiltinTerminal;
  artKindOfs[ARTL_ART_cfgBuiltinTerminal_230] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgBuiltinTerminal_230] = true;
  artFolds[ARTL_ART_cfgBuiltinTerminal_232] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgBuiltinTerminal_231] = "cfgBuiltinTerminal ::= '&'  &ID  ";
  artLabelStrings[ARTL_ART_cfgBuiltinTerminal_231] = "";
  artlhsL[ARTL_ART_cfgBuiltinTerminal_231] = ARTL_ART_cfgBuiltinTerminal;
  artPopD[ARTL_ART_cfgBuiltinTerminal_231] = true;
  artLabelInternalStrings[ARTL_ART_cfgBuiltinTerminal_232] = "cfgBuiltinTerminal ::= '&'  . &ID  ";
  artLabelStrings[ARTL_ART_cfgBuiltinTerminal_232] = "";
  artlhsL[ARTL_ART_cfgBuiltinTerminal_232] = ARTL_ART_cfgBuiltinTerminal;
  artKindOfs[ARTL_ART_cfgBuiltinTerminal_232] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgBuiltinTerminal_232] = true;
  artPopD[ARTL_ART_cfgBuiltinTerminal_232] = true;
  artLabelInternalStrings[ARTL_ART_cfgBuiltinTerminal_233] = "cfgBuiltinTerminal ::= '&'  &ID  ";
  artLabelStrings[ARTL_ART_cfgBuiltinTerminal_233] = "";
  artlhsL[ARTL_ART_cfgBuiltinTerminal_233] = ARTL_ART_cfgBuiltinTerminal;
  artPopD[ARTL_ART_cfgBuiltinTerminal_233] = true;
  artLabelInternalStrings[ARTL_ART_cfgBuiltinTerminal_234] = "cfgBuiltinTerminal ::= '&'  &ID  .";
  artLabelStrings[ARTL_ART_cfgBuiltinTerminal_234] = "";
  artlhsL[ARTL_ART_cfgBuiltinTerminal_234] = ARTL_ART_cfgBuiltinTerminal;
  artKindOfs[ARTL_ART_cfgBuiltinTerminal_234] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgBuiltinTerminal_234] = true;
  arteoR_pL[ARTL_ART_cfgBuiltinTerminal_234] = true;
  artPopD[ARTL_ART_cfgBuiltinTerminal_234] = true;
}

public void artTableInitialiser_ART_cfgCaseSensitiveTerminal() {
  artLabelInternalStrings[ARTL_ART_cfgCaseSensitiveTerminal] = "cfgCaseSensitiveTerminal";
  artLabelStrings[ARTL_ART_cfgCaseSensitiveTerminal] = "cfgCaseSensitiveTerminal";
  artKindOfs[ARTL_ART_cfgCaseSensitiveTerminal] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgCaseSensitiveTerminal_226] = "cfgCaseSensitiveTerminal ::= . &STRING_PLAIN_SQ  ";
  artLabelStrings[ARTL_ART_cfgCaseSensitiveTerminal_226] = "";
  artlhsL[ARTL_ART_cfgCaseSensitiveTerminal_226] = ARTL_ART_cfgCaseSensitiveTerminal;
  artKindOfs[ARTL_ART_cfgCaseSensitiveTerminal_226] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgCaseSensitiveTerminal_226] = true;
  artLabelInternalStrings[ARTL_ART_cfgCaseSensitiveTerminal_227] = "cfgCaseSensitiveTerminal ::= &STRING_PLAIN_SQ  ";
  artLabelStrings[ARTL_ART_cfgCaseSensitiveTerminal_227] = "";
  artlhsL[ARTL_ART_cfgCaseSensitiveTerminal_227] = ARTL_ART_cfgCaseSensitiveTerminal;
  artPopD[ARTL_ART_cfgCaseSensitiveTerminal_227] = true;
  artLabelInternalStrings[ARTL_ART_cfgCaseSensitiveTerminal_228] = "cfgCaseSensitiveTerminal ::= &STRING_PLAIN_SQ  .";
  artLabelStrings[ARTL_ART_cfgCaseSensitiveTerminal_228] = "";
  artlhsL[ARTL_ART_cfgCaseSensitiveTerminal_228] = ARTL_ART_cfgCaseSensitiveTerminal;
  artKindOfs[ARTL_ART_cfgCaseSensitiveTerminal_228] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgCaseSensitiveTerminal_228] = true;
  arteoR_pL[ARTL_ART_cfgCaseSensitiveTerminal_228] = true;
  artPopD[ARTL_ART_cfgCaseSensitiveTerminal_228] = true;
}

public void artTableInitialiser_ART_cfgDoFirst() {
  artLabelInternalStrings[ARTL_ART_cfgDoFirst] = "cfgDoFirst";
  artLabelStrings[ARTL_ART_cfgDoFirst] = "cfgDoFirst";
  artKindOfs[ARTL_ART_cfgDoFirst] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgDoFirst_158] = "cfgDoFirst ::= . '('  cfgAlts ')'  ";
  artLabelStrings[ARTL_ART_cfgDoFirst_158] = "";
  artlhsL[ARTL_ART_cfgDoFirst_158] = ARTL_ART_cfgDoFirst;
  artKindOfs[ARTL_ART_cfgDoFirst_158] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgDoFirst_160] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgDoFirst_159] = "cfgDoFirst ::= '('  cfgAlts ')'  ";
  artLabelStrings[ARTL_ART_cfgDoFirst_159] = "";
  artlhsL[ARTL_ART_cfgDoFirst_159] = ARTL_ART_cfgDoFirst;
  artLabelInternalStrings[ARTL_ART_cfgDoFirst_160] = "cfgDoFirst ::= '('  . cfgAlts ')'  ";
  artLabelStrings[ARTL_ART_cfgDoFirst_160] = "";
  artlhsL[ARTL_ART_cfgDoFirst_160] = ARTL_ART_cfgDoFirst;
  artKindOfs[ARTL_ART_cfgDoFirst_160] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgDoFirst_160] = true;
  artFolds[ARTL_ART_cfgDoFirst_162] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgDoFirst_162] = "cfgDoFirst ::= '('  cfgAlts . ')'  ";
  artLabelStrings[ARTL_ART_cfgDoFirst_162] = "";
  artlhsL[ARTL_ART_cfgDoFirst_162] = ARTL_ART_cfgDoFirst;
  artSlotInstanceOfs[ARTL_ART_cfgDoFirst_162] = ARTL_ART_cfgAlts;
  artKindOfs[ARTL_ART_cfgDoFirst_162] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgDoFirst_162] = true;
  artFolds[ARTL_ART_cfgDoFirst_164] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgDoFirst_163] = "cfgDoFirst ::= '('  cfgAlts ')'  ";
  artLabelStrings[ARTL_ART_cfgDoFirst_163] = "";
  artlhsL[ARTL_ART_cfgDoFirst_163] = ARTL_ART_cfgDoFirst;
  artPopD[ARTL_ART_cfgDoFirst_163] = true;
  artLabelInternalStrings[ARTL_ART_cfgDoFirst_164] = "cfgDoFirst ::= '('  cfgAlts ')'  .";
  artLabelStrings[ARTL_ART_cfgDoFirst_164] = "";
  artlhsL[ARTL_ART_cfgDoFirst_164] = ARTL_ART_cfgDoFirst;
  artKindOfs[ARTL_ART_cfgDoFirst_164] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgDoFirst_164] = true;
  arteoR_pL[ARTL_ART_cfgDoFirst_164] = true;
  artPopD[ARTL_ART_cfgDoFirst_164] = true;
}

public void artTableInitialiser_ART_cfgElems() {
  artLabelInternalStrings[ARTL_ART_cfgElems] = "cfgElems";
  artLabelStrings[ARTL_ART_cfgElems] = "cfgElems";
  artKindOfs[ARTL_ART_cfgElems] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgElems_84] = "cfgElems ::= . cfgActions cfgExtended ";
  artLabelStrings[ARTL_ART_cfgElems_84] = "";
  artlhsL[ARTL_ART_cfgElems_84] = ARTL_ART_cfgElems;
  artKindOfs[ARTL_ART_cfgElems_84] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgElems_86] = "cfgElems ::= cfgActions . cfgExtended ";
  artLabelStrings[ARTL_ART_cfgElems_86] = "";
  artlhsL[ARTL_ART_cfgElems_86] = ARTL_ART_cfgElems;
  artSlotInstanceOfs[ARTL_ART_cfgElems_86] = ARTL_ART_cfgActions;
  artKindOfs[ARTL_ART_cfgElems_86] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgElems_86] = true;
  artLabelInternalStrings[ARTL_ART_cfgElems_88] = "cfgElems ::= cfgActions cfgExtended .";
  artLabelStrings[ARTL_ART_cfgElems_88] = "";
  artlhsL[ARTL_ART_cfgElems_88] = ARTL_ART_cfgElems;
  artSlotInstanceOfs[ARTL_ART_cfgElems_88] = ARTL_ART_cfgExtended;
  artKindOfs[ARTL_ART_cfgElems_88] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgElems_88] = true;
  arteoR_pL[ARTL_ART_cfgElems_88] = true;
  artPopD[ARTL_ART_cfgElems_88] = true;
  artLabelInternalStrings[ARTL_ART_cfgElems_90] = "cfgElems ::= . cfgActions cfgExtended cfgElems ";
  artLabelStrings[ARTL_ART_cfgElems_90] = "";
  artlhsL[ARTL_ART_cfgElems_90] = ARTL_ART_cfgElems;
  artKindOfs[ARTL_ART_cfgElems_90] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgElems_92] = "cfgElems ::= cfgActions . cfgExtended cfgElems ";
  artLabelStrings[ARTL_ART_cfgElems_92] = "";
  artlhsL[ARTL_ART_cfgElems_92] = ARTL_ART_cfgElems;
  artSlotInstanceOfs[ARTL_ART_cfgElems_92] = ARTL_ART_cfgActions;
  artKindOfs[ARTL_ART_cfgElems_92] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgElems_92] = true;
  artLabelInternalStrings[ARTL_ART_cfgElems_94] = "cfgElems ::= cfgActions cfgExtended . cfgElems ";
  artLabelStrings[ARTL_ART_cfgElems_94] = "";
  artlhsL[ARTL_ART_cfgElems_94] = ARTL_ART_cfgElems;
  artSlotInstanceOfs[ARTL_ART_cfgElems_94] = ARTL_ART_cfgExtended;
  artKindOfs[ARTL_ART_cfgElems_94] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgElems_96] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgElems_96] = "cfgElems ::= cfgActions cfgExtended cfgElems .";
  artLabelStrings[ARTL_ART_cfgElems_96] = "";
  artlhsL[ARTL_ART_cfgElems_96] = ARTL_ART_cfgElems;
  artSlotInstanceOfs[ARTL_ART_cfgElems_96] = ARTL_ART_cfgElems;
  artKindOfs[ARTL_ART_cfgElems_96] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgElems_96] = true;
  arteoR_pL[ARTL_ART_cfgElems_96] = true;
  artPopD[ARTL_ART_cfgElems_96] = true;
}

public void artTableInitialiser_ART_cfgEpsilon() {
  artLabelInternalStrings[ARTL_ART_cfgEpsilon] = "cfgEpsilon";
  artLabelStrings[ARTL_ART_cfgEpsilon] = "cfgEpsilon";
  artKindOfs[ARTL_ART_cfgEpsilon] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgEpsilon_74] = "cfgEpsilon ::= . '#'  ";
  artLabelStrings[ARTL_ART_cfgEpsilon_74] = "";
  artlhsL[ARTL_ART_cfgEpsilon_74] = ARTL_ART_cfgEpsilon;
  artKindOfs[ARTL_ART_cfgEpsilon_74] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgEpsilon_74] = true;
  artFolds[ARTL_ART_cfgEpsilon_76] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgEpsilon_75] = "cfgEpsilon ::= '#'  ";
  artLabelStrings[ARTL_ART_cfgEpsilon_75] = "";
  artlhsL[ARTL_ART_cfgEpsilon_75] = ARTL_ART_cfgEpsilon;
  artPopD[ARTL_ART_cfgEpsilon_75] = true;
  artLabelInternalStrings[ARTL_ART_cfgEpsilon_76] = "cfgEpsilon ::= '#'  .";
  artLabelStrings[ARTL_ART_cfgEpsilon_76] = "";
  artlhsL[ARTL_ART_cfgEpsilon_76] = ARTL_ART_cfgEpsilon;
  artKindOfs[ARTL_ART_cfgEpsilon_76] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgEpsilon_76] = true;
  arteoR_pL[ARTL_ART_cfgEpsilon_76] = true;
  artPopD[ARTL_ART_cfgEpsilon_76] = true;
}

public void artTableInitialiser_ART_cfgEpsilonCarrier() {
  artLabelInternalStrings[ARTL_ART_cfgEpsilonCarrier] = "cfgEpsilonCarrier";
  artLabelStrings[ARTL_ART_cfgEpsilonCarrier] = "cfgEpsilonCarrier";
  artKindOfs[ARTL_ART_cfgEpsilonCarrier] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgEpsilonCarrier_78] = "cfgEpsilonCarrier ::= . cfgEpsilon cfgAnnotation ";
  artLabelStrings[ARTL_ART_cfgEpsilonCarrier_78] = "";
  artlhsL[ARTL_ART_cfgEpsilonCarrier_78] = ARTL_ART_cfgEpsilonCarrier;
  artKindOfs[ARTL_ART_cfgEpsilonCarrier_78] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgEpsilonCarrier_80] = "cfgEpsilonCarrier ::= cfgEpsilon . cfgAnnotation ";
  artLabelStrings[ARTL_ART_cfgEpsilonCarrier_80] = "";
  artlhsL[ARTL_ART_cfgEpsilonCarrier_80] = ARTL_ART_cfgEpsilonCarrier;
  artSlotInstanceOfs[ARTL_ART_cfgEpsilonCarrier_80] = ARTL_ART_cfgEpsilon;
  artKindOfs[ARTL_ART_cfgEpsilonCarrier_80] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgEpsilonCarrier_80] = true;
  artFolds[ARTL_ART_cfgEpsilonCarrier_82] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgEpsilonCarrier_82] = "cfgEpsilonCarrier ::= cfgEpsilon cfgAnnotation .";
  artLabelStrings[ARTL_ART_cfgEpsilonCarrier_82] = "";
  artlhsL[ARTL_ART_cfgEpsilonCarrier_82] = ARTL_ART_cfgEpsilonCarrier;
  artSlotInstanceOfs[ARTL_ART_cfgEpsilonCarrier_82] = ARTL_ART_cfgAnnotation;
  artKindOfs[ARTL_ART_cfgEpsilonCarrier_82] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgEpsilonCarrier_82] = true;
  arteoR_pL[ARTL_ART_cfgEpsilonCarrier_82] = true;
  artPopD[ARTL_ART_cfgEpsilonCarrier_82] = true;
}

public void artTableInitialiser_ART_cfgEquation() {
  artLabelInternalStrings[ARTL_ART_cfgEquation] = "cfgEquation";
  artLabelStrings[ARTL_ART_cfgEquation] = "cfgEquation";
  artKindOfs[ARTL_ART_cfgEquation] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgEquation_258] = "cfgEquation ::= . cfgAttribute '='  term ";
  artLabelStrings[ARTL_ART_cfgEquation_258] = "";
  artlhsL[ARTL_ART_cfgEquation_258] = ARTL_ART_cfgEquation;
  artKindOfs[ARTL_ART_cfgEquation_258] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgEquation_260] = "cfgEquation ::= cfgAttribute . '='  term ";
  artLabelStrings[ARTL_ART_cfgEquation_260] = "";
  artlhsL[ARTL_ART_cfgEquation_260] = ARTL_ART_cfgEquation;
  artSlotInstanceOfs[ARTL_ART_cfgEquation_260] = ARTL_ART_cfgAttribute;
  artKindOfs[ARTL_ART_cfgEquation_260] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgEquation_260] = true;
  artFolds[ARTL_ART_cfgEquation_262] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgEquation_261] = "cfgEquation ::= cfgAttribute '='  term ";
  artLabelStrings[ARTL_ART_cfgEquation_261] = "";
  artlhsL[ARTL_ART_cfgEquation_261] = ARTL_ART_cfgEquation;
  artLabelInternalStrings[ARTL_ART_cfgEquation_262] = "cfgEquation ::= cfgAttribute '='  . term ";
  artLabelStrings[ARTL_ART_cfgEquation_262] = "";
  artlhsL[ARTL_ART_cfgEquation_262] = ARTL_ART_cfgEquation;
  artKindOfs[ARTL_ART_cfgEquation_262] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgEquation_264] = "cfgEquation ::= cfgAttribute '='  term .";
  artLabelStrings[ARTL_ART_cfgEquation_264] = "";
  artlhsL[ARTL_ART_cfgEquation_264] = ARTL_ART_cfgEquation;
  artSlotInstanceOfs[ARTL_ART_cfgEquation_264] = ARTL_ART_term;
  artKindOfs[ARTL_ART_cfgEquation_264] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgEquation_264] = true;
  arteoR_pL[ARTL_ART_cfgEquation_264] = true;
  artPopD[ARTL_ART_cfgEquation_264] = true;
}

public void artTableInitialiser_ART_cfgExtended() {
  artLabelInternalStrings[ARTL_ART_cfgExtended] = "cfgExtended";
  artLabelStrings[ARTL_ART_cfgExtended] = "cfgExtended";
  artKindOfs[ARTL_ART_cfgExtended] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgExtended_132] = "cfgExtended ::= . cfgPrim ";
  artLabelStrings[ARTL_ART_cfgExtended_132] = "";
  artlhsL[ARTL_ART_cfgExtended_132] = ARTL_ART_cfgExtended;
  artKindOfs[ARTL_ART_cfgExtended_132] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgExtended_134] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgExtended_134] = "cfgExtended ::= cfgPrim .";
  artLabelStrings[ARTL_ART_cfgExtended_134] = "";
  artlhsL[ARTL_ART_cfgExtended_134] = ARTL_ART_cfgExtended;
  artSlotInstanceOfs[ARTL_ART_cfgExtended_134] = ARTL_ART_cfgPrim;
  artKindOfs[ARTL_ART_cfgExtended_134] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgExtended_134] = true;
  arteoR_pL[ARTL_ART_cfgExtended_134] = true;
  artPopD[ARTL_ART_cfgExtended_134] = true;
  artLabelInternalStrings[ARTL_ART_cfgExtended_136] = "cfgExtended ::= . cfgPrim cfgAnnotation ";
  artLabelStrings[ARTL_ART_cfgExtended_136] = "";
  artlhsL[ARTL_ART_cfgExtended_136] = ARTL_ART_cfgExtended;
  artKindOfs[ARTL_ART_cfgExtended_136] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgExtended_138] = "cfgExtended ::= cfgPrim . cfgAnnotation ";
  artLabelStrings[ARTL_ART_cfgExtended_138] = "";
  artlhsL[ARTL_ART_cfgExtended_138] = ARTL_ART_cfgExtended;
  artSlotInstanceOfs[ARTL_ART_cfgExtended_138] = ARTL_ART_cfgPrim;
  artKindOfs[ARTL_ART_cfgExtended_138] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgExtended_138] = true;
  artFolds[ARTL_ART_cfgExtended_140] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgExtended_140] = "cfgExtended ::= cfgPrim cfgAnnotation .";
  artLabelStrings[ARTL_ART_cfgExtended_140] = "";
  artlhsL[ARTL_ART_cfgExtended_140] = ARTL_ART_cfgExtended;
  artSlotInstanceOfs[ARTL_ART_cfgExtended_140] = ARTL_ART_cfgAnnotation;
  artKindOfs[ARTL_ART_cfgExtended_140] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgExtended_140] = true;
  arteoR_pL[ARTL_ART_cfgExtended_140] = true;
  artPopD[ARTL_ART_cfgExtended_140] = true;
  artLabelInternalStrings[ARTL_ART_cfgExtended_142] = "cfgExtended ::= . cfgDoFirst ";
  artLabelStrings[ARTL_ART_cfgExtended_142] = "";
  artlhsL[ARTL_ART_cfgExtended_142] = ARTL_ART_cfgExtended;
  artKindOfs[ARTL_ART_cfgExtended_142] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgExtended_144] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgExtended_144] = "cfgExtended ::= cfgDoFirst .";
  artLabelStrings[ARTL_ART_cfgExtended_144] = "";
  artlhsL[ARTL_ART_cfgExtended_144] = ARTL_ART_cfgExtended;
  artSlotInstanceOfs[ARTL_ART_cfgExtended_144] = ARTL_ART_cfgDoFirst;
  artKindOfs[ARTL_ART_cfgExtended_144] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgExtended_144] = true;
  arteoR_pL[ARTL_ART_cfgExtended_144] = true;
  artPopD[ARTL_ART_cfgExtended_144] = true;
  artLabelInternalStrings[ARTL_ART_cfgExtended_146] = "cfgExtended ::= . cfgOptional ";
  artLabelStrings[ARTL_ART_cfgExtended_146] = "";
  artlhsL[ARTL_ART_cfgExtended_146] = ARTL_ART_cfgExtended;
  artKindOfs[ARTL_ART_cfgExtended_146] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgExtended_148] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgExtended_148] = "cfgExtended ::= cfgOptional .";
  artLabelStrings[ARTL_ART_cfgExtended_148] = "";
  artlhsL[ARTL_ART_cfgExtended_148] = ARTL_ART_cfgExtended;
  artSlotInstanceOfs[ARTL_ART_cfgExtended_148] = ARTL_ART_cfgOptional;
  artKindOfs[ARTL_ART_cfgExtended_148] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgExtended_148] = true;
  arteoR_pL[ARTL_ART_cfgExtended_148] = true;
  artPopD[ARTL_ART_cfgExtended_148] = true;
  artLabelInternalStrings[ARTL_ART_cfgExtended_150] = "cfgExtended ::= . cfgKleene ";
  artLabelStrings[ARTL_ART_cfgExtended_150] = "";
  artlhsL[ARTL_ART_cfgExtended_150] = ARTL_ART_cfgExtended;
  artKindOfs[ARTL_ART_cfgExtended_150] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgExtended_152] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgExtended_152] = "cfgExtended ::= cfgKleene .";
  artLabelStrings[ARTL_ART_cfgExtended_152] = "";
  artlhsL[ARTL_ART_cfgExtended_152] = ARTL_ART_cfgExtended;
  artSlotInstanceOfs[ARTL_ART_cfgExtended_152] = ARTL_ART_cfgKleene;
  artKindOfs[ARTL_ART_cfgExtended_152] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgExtended_152] = true;
  arteoR_pL[ARTL_ART_cfgExtended_152] = true;
  artPopD[ARTL_ART_cfgExtended_152] = true;
  artLabelInternalStrings[ARTL_ART_cfgExtended_154] = "cfgExtended ::= . cfgPositive ";
  artLabelStrings[ARTL_ART_cfgExtended_154] = "";
  artlhsL[ARTL_ART_cfgExtended_154] = ARTL_ART_cfgExtended;
  artKindOfs[ARTL_ART_cfgExtended_154] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgExtended_156] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgExtended_156] = "cfgExtended ::= cfgPositive .";
  artLabelStrings[ARTL_ART_cfgExtended_156] = "";
  artlhsL[ARTL_ART_cfgExtended_156] = ARTL_ART_cfgExtended;
  artSlotInstanceOfs[ARTL_ART_cfgExtended_156] = ARTL_ART_cfgPositive;
  artKindOfs[ARTL_ART_cfgExtended_156] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgExtended_156] = true;
  arteoR_pL[ARTL_ART_cfgExtended_156] = true;
  artPopD[ARTL_ART_cfgExtended_156] = true;
}

public void artTableInitialiser_ART_cfgFoldOver() {
  artLabelInternalStrings[ARTL_ART_cfgFoldOver] = "cfgFoldOver";
  artLabelStrings[ARTL_ART_cfgFoldOver] = "cfgFoldOver";
  artKindOfs[ARTL_ART_cfgFoldOver] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgFoldOver_218] = "cfgFoldOver ::= . '^^'  ";
  artLabelStrings[ARTL_ART_cfgFoldOver_218] = "";
  artlhsL[ARTL_ART_cfgFoldOver_218] = ARTL_ART_cfgFoldOver;
  artKindOfs[ARTL_ART_cfgFoldOver_218] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgFoldOver_218] = true;
  artFolds[ARTL_ART_cfgFoldOver_220] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgFoldOver_219] = "cfgFoldOver ::= '^^'  ";
  artLabelStrings[ARTL_ART_cfgFoldOver_219] = "";
  artlhsL[ARTL_ART_cfgFoldOver_219] = ARTL_ART_cfgFoldOver;
  artPopD[ARTL_ART_cfgFoldOver_219] = true;
  artLabelInternalStrings[ARTL_ART_cfgFoldOver_220] = "cfgFoldOver ::= '^^'  .";
  artLabelStrings[ARTL_ART_cfgFoldOver_220] = "";
  artlhsL[ARTL_ART_cfgFoldOver_220] = ARTL_ART_cfgFoldOver;
  artKindOfs[ARTL_ART_cfgFoldOver_220] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgFoldOver_220] = true;
  arteoR_pL[ARTL_ART_cfgFoldOver_220] = true;
  artPopD[ARTL_ART_cfgFoldOver_220] = true;
}

public void artTableInitialiser_ART_cfgFoldUnder() {
  artLabelInternalStrings[ARTL_ART_cfgFoldUnder] = "cfgFoldUnder";
  artLabelStrings[ARTL_ART_cfgFoldUnder] = "cfgFoldUnder";
  artKindOfs[ARTL_ART_cfgFoldUnder] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgFoldUnder_214] = "cfgFoldUnder ::= . '^'  ";
  artLabelStrings[ARTL_ART_cfgFoldUnder_214] = "";
  artlhsL[ARTL_ART_cfgFoldUnder_214] = ARTL_ART_cfgFoldUnder;
  artKindOfs[ARTL_ART_cfgFoldUnder_214] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgFoldUnder_214] = true;
  artFolds[ARTL_ART_cfgFoldUnder_216] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgFoldUnder_215] = "cfgFoldUnder ::= '^'  ";
  artLabelStrings[ARTL_ART_cfgFoldUnder_215] = "";
  artlhsL[ARTL_ART_cfgFoldUnder_215] = ARTL_ART_cfgFoldUnder;
  artPopD[ARTL_ART_cfgFoldUnder_215] = true;
  artLabelInternalStrings[ARTL_ART_cfgFoldUnder_216] = "cfgFoldUnder ::= '^'  .";
  artLabelStrings[ARTL_ART_cfgFoldUnder_216] = "";
  artlhsL[ARTL_ART_cfgFoldUnder_216] = ARTL_ART_cfgFoldUnder;
  artKindOfs[ARTL_ART_cfgFoldUnder_216] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgFoldUnder_216] = true;
  arteoR_pL[ARTL_ART_cfgFoldUnder_216] = true;
  artPopD[ARTL_ART_cfgFoldUnder_216] = true;
}

public void artTableInitialiser_ART_cfgInsert() {
  artLabelInternalStrings[ARTL_ART_cfgInsert] = "cfgInsert";
  artLabelStrings[ARTL_ART_cfgInsert] = "cfgInsert";
  artKindOfs[ARTL_ART_cfgInsert] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgInsert_274] = "cfgInsert ::= . '^+'  &ID  ";
  artLabelStrings[ARTL_ART_cfgInsert_274] = "";
  artlhsL[ARTL_ART_cfgInsert_274] = ARTL_ART_cfgInsert;
  artKindOfs[ARTL_ART_cfgInsert_274] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgInsert_274] = true;
  artFolds[ARTL_ART_cfgInsert_276] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgInsert_275] = "cfgInsert ::= '^+'  &ID  ";
  artLabelStrings[ARTL_ART_cfgInsert_275] = "";
  artlhsL[ARTL_ART_cfgInsert_275] = ARTL_ART_cfgInsert;
  artPopD[ARTL_ART_cfgInsert_275] = true;
  artLabelInternalStrings[ARTL_ART_cfgInsert_276] = "cfgInsert ::= '^+'  . &ID  ";
  artLabelStrings[ARTL_ART_cfgInsert_276] = "";
  artlhsL[ARTL_ART_cfgInsert_276] = ARTL_ART_cfgInsert;
  artKindOfs[ARTL_ART_cfgInsert_276] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgInsert_276] = true;
  artPopD[ARTL_ART_cfgInsert_276] = true;
  artLabelInternalStrings[ARTL_ART_cfgInsert_277] = "cfgInsert ::= '^+'  &ID  ";
  artLabelStrings[ARTL_ART_cfgInsert_277] = "";
  artlhsL[ARTL_ART_cfgInsert_277] = ARTL_ART_cfgInsert;
  artPopD[ARTL_ART_cfgInsert_277] = true;
  artLabelInternalStrings[ARTL_ART_cfgInsert_278] = "cfgInsert ::= '^+'  &ID  .";
  artLabelStrings[ARTL_ART_cfgInsert_278] = "";
  artlhsL[ARTL_ART_cfgInsert_278] = ARTL_ART_cfgInsert;
  artKindOfs[ARTL_ART_cfgInsert_278] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgInsert_278] = true;
  arteoR_pL[ARTL_ART_cfgInsert_278] = true;
  artPopD[ARTL_ART_cfgInsert_278] = true;
}

public void artTableInitialiser_ART_cfgKleene() {
  artLabelInternalStrings[ARTL_ART_cfgKleene] = "cfgKleene";
  artLabelStrings[ARTL_ART_cfgKleene] = "cfgKleene";
  artKindOfs[ARTL_ART_cfgKleene] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgKleene_182] = "cfgKleene ::= . '('  cfgAlts ')'  '*'  ";
  artLabelStrings[ARTL_ART_cfgKleene_182] = "";
  artlhsL[ARTL_ART_cfgKleene_182] = ARTL_ART_cfgKleene;
  artKindOfs[ARTL_ART_cfgKleene_182] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgKleene_184] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgKleene_183] = "cfgKleene ::= '('  cfgAlts ')'  '*'  ";
  artLabelStrings[ARTL_ART_cfgKleene_183] = "";
  artlhsL[ARTL_ART_cfgKleene_183] = ARTL_ART_cfgKleene;
  artLabelInternalStrings[ARTL_ART_cfgKleene_184] = "cfgKleene ::= '('  . cfgAlts ')'  '*'  ";
  artLabelStrings[ARTL_ART_cfgKleene_184] = "";
  artlhsL[ARTL_ART_cfgKleene_184] = ARTL_ART_cfgKleene;
  artKindOfs[ARTL_ART_cfgKleene_184] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgKleene_184] = true;
  artFolds[ARTL_ART_cfgKleene_186] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgKleene_186] = "cfgKleene ::= '('  cfgAlts . ')'  '*'  ";
  artLabelStrings[ARTL_ART_cfgKleene_186] = "";
  artlhsL[ARTL_ART_cfgKleene_186] = ARTL_ART_cfgKleene;
  artSlotInstanceOfs[ARTL_ART_cfgKleene_186] = ARTL_ART_cfgAlts;
  artKindOfs[ARTL_ART_cfgKleene_186] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgKleene_186] = true;
  artFolds[ARTL_ART_cfgKleene_188] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgKleene_187] = "cfgKleene ::= '('  cfgAlts ')'  '*'  ";
  artLabelStrings[ARTL_ART_cfgKleene_187] = "";
  artlhsL[ARTL_ART_cfgKleene_187] = ARTL_ART_cfgKleene;
  artPopD[ARTL_ART_cfgKleene_187] = true;
  artLabelInternalStrings[ARTL_ART_cfgKleene_188] = "cfgKleene ::= '('  cfgAlts ')'  . '*'  ";
  artLabelStrings[ARTL_ART_cfgKleene_188] = "";
  artlhsL[ARTL_ART_cfgKleene_188] = ARTL_ART_cfgKleene;
  artKindOfs[ARTL_ART_cfgKleene_188] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgKleene_188] = true;
  artFolds[ARTL_ART_cfgKleene_190] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgKleene_189] = "cfgKleene ::= '('  cfgAlts ')'  '*'  ";
  artLabelStrings[ARTL_ART_cfgKleene_189] = "";
  artlhsL[ARTL_ART_cfgKleene_189] = ARTL_ART_cfgKleene;
  artPopD[ARTL_ART_cfgKleene_189] = true;
  artLabelInternalStrings[ARTL_ART_cfgKleene_190] = "cfgKleene ::= '('  cfgAlts ')'  '*'  .";
  artLabelStrings[ARTL_ART_cfgKleene_190] = "";
  artlhsL[ARTL_ART_cfgKleene_190] = ARTL_ART_cfgKleene;
  artKindOfs[ARTL_ART_cfgKleene_190] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgKleene_190] = true;
  arteoR_pL[ARTL_ART_cfgKleene_190] = true;
  artPopD[ARTL_ART_cfgKleene_190] = true;
  artLabelInternalStrings[ARTL_ART_cfgKleene_192] = "cfgKleene ::= . cfgAltNoAction '*'  ";
  artLabelStrings[ARTL_ART_cfgKleene_192] = "";
  artlhsL[ARTL_ART_cfgKleene_192] = ARTL_ART_cfgKleene;
  artKindOfs[ARTL_ART_cfgKleene_192] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgKleene_194] = "cfgKleene ::= cfgAltNoAction . '*'  ";
  artLabelStrings[ARTL_ART_cfgKleene_194] = "";
  artlhsL[ARTL_ART_cfgKleene_194] = ARTL_ART_cfgKleene;
  artSlotInstanceOfs[ARTL_ART_cfgKleene_194] = ARTL_ART_cfgAltNoAction;
  artKindOfs[ARTL_ART_cfgKleene_194] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgKleene_194] = true;
  artPopD[ARTL_ART_cfgKleene_194] = true;
  artFolds[ARTL_ART_cfgKleene_196] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgKleene_195] = "cfgKleene ::= cfgAltNoAction '*'  ";
  artLabelStrings[ARTL_ART_cfgKleene_195] = "";
  artlhsL[ARTL_ART_cfgKleene_195] = ARTL_ART_cfgKleene;
  artPopD[ARTL_ART_cfgKleene_195] = true;
  artLabelInternalStrings[ARTL_ART_cfgKleene_196] = "cfgKleene ::= cfgAltNoAction '*'  .";
  artLabelStrings[ARTL_ART_cfgKleene_196] = "";
  artlhsL[ARTL_ART_cfgKleene_196] = ARTL_ART_cfgKleene;
  artKindOfs[ARTL_ART_cfgKleene_196] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgKleene_196] = true;
  arteoR_pL[ARTL_ART_cfgKleene_196] = true;
  artPopD[ARTL_ART_cfgKleene_196] = true;
}

public void artTableInitialiser_ART_cfgLHS() {
  artLabelInternalStrings[ARTL_ART_cfgLHS] = "cfgLHS";
  artLabelStrings[ARTL_ART_cfgLHS] = "cfgLHS";
  artKindOfs[ARTL_ART_cfgLHS] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgLHS_20] = "cfgLHS ::= . &ID  ";
  artLabelStrings[ARTL_ART_cfgLHS_20] = "";
  artlhsL[ARTL_ART_cfgLHS_20] = ARTL_ART_cfgLHS;
  artKindOfs[ARTL_ART_cfgLHS_20] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgLHS_20] = true;
  artLabelInternalStrings[ARTL_ART_cfgLHS_21] = "cfgLHS ::= &ID  ";
  artLabelStrings[ARTL_ART_cfgLHS_21] = "";
  artlhsL[ARTL_ART_cfgLHS_21] = ARTL_ART_cfgLHS;
  artPopD[ARTL_ART_cfgLHS_21] = true;
  artLabelInternalStrings[ARTL_ART_cfgLHS_22] = "cfgLHS ::= &ID  .";
  artLabelStrings[ARTL_ART_cfgLHS_22] = "";
  artlhsL[ARTL_ART_cfgLHS_22] = ARTL_ART_cfgLHS;
  artKindOfs[ARTL_ART_cfgLHS_22] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgLHS_22] = true;
  arteoR_pL[ARTL_ART_cfgLHS_22] = true;
  artPopD[ARTL_ART_cfgLHS_22] = true;
}

public void artTableInitialiser_ART_cfgNonterminal() {
  artLabelInternalStrings[ARTL_ART_cfgNonterminal] = "cfgNonterminal";
  artLabelStrings[ARTL_ART_cfgNonterminal] = "cfgNonterminal";
  artKindOfs[ARTL_ART_cfgNonterminal] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgNonterminal_222] = "cfgNonterminal ::= . &ID  ";
  artLabelStrings[ARTL_ART_cfgNonterminal_222] = "";
  artlhsL[ARTL_ART_cfgNonterminal_222] = ARTL_ART_cfgNonterminal;
  artKindOfs[ARTL_ART_cfgNonterminal_222] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgNonterminal_222] = true;
  artLabelInternalStrings[ARTL_ART_cfgNonterminal_223] = "cfgNonterminal ::= &ID  ";
  artLabelStrings[ARTL_ART_cfgNonterminal_223] = "";
  artlhsL[ARTL_ART_cfgNonterminal_223] = ARTL_ART_cfgNonterminal;
  artPopD[ARTL_ART_cfgNonterminal_223] = true;
  artLabelInternalStrings[ARTL_ART_cfgNonterminal_224] = "cfgNonterminal ::= &ID  .";
  artLabelStrings[ARTL_ART_cfgNonterminal_224] = "";
  artlhsL[ARTL_ART_cfgNonterminal_224] = ARTL_ART_cfgNonterminal;
  artKindOfs[ARTL_ART_cfgNonterminal_224] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgNonterminal_224] = true;
  arteoR_pL[ARTL_ART_cfgNonterminal_224] = true;
  artPopD[ARTL_ART_cfgNonterminal_224] = true;
}

public void artTableInitialiser_ART_cfgOptional() {
  artLabelInternalStrings[ARTL_ART_cfgOptional] = "cfgOptional";
  artLabelStrings[ARTL_ART_cfgOptional] = "cfgOptional";
  artKindOfs[ARTL_ART_cfgOptional] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgOptional_166] = "cfgOptional ::= . '('  cfgAlts ')'  '?'  ";
  artLabelStrings[ARTL_ART_cfgOptional_166] = "";
  artlhsL[ARTL_ART_cfgOptional_166] = ARTL_ART_cfgOptional;
  artKindOfs[ARTL_ART_cfgOptional_166] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgOptional_168] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgOptional_167] = "cfgOptional ::= '('  cfgAlts ')'  '?'  ";
  artLabelStrings[ARTL_ART_cfgOptional_167] = "";
  artlhsL[ARTL_ART_cfgOptional_167] = ARTL_ART_cfgOptional;
  artLabelInternalStrings[ARTL_ART_cfgOptional_168] = "cfgOptional ::= '('  . cfgAlts ')'  '?'  ";
  artLabelStrings[ARTL_ART_cfgOptional_168] = "";
  artlhsL[ARTL_ART_cfgOptional_168] = ARTL_ART_cfgOptional;
  artKindOfs[ARTL_ART_cfgOptional_168] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgOptional_168] = true;
  artFolds[ARTL_ART_cfgOptional_170] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgOptional_170] = "cfgOptional ::= '('  cfgAlts . ')'  '?'  ";
  artLabelStrings[ARTL_ART_cfgOptional_170] = "";
  artlhsL[ARTL_ART_cfgOptional_170] = ARTL_ART_cfgOptional;
  artSlotInstanceOfs[ARTL_ART_cfgOptional_170] = ARTL_ART_cfgAlts;
  artKindOfs[ARTL_ART_cfgOptional_170] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgOptional_170] = true;
  artFolds[ARTL_ART_cfgOptional_172] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgOptional_171] = "cfgOptional ::= '('  cfgAlts ')'  '?'  ";
  artLabelStrings[ARTL_ART_cfgOptional_171] = "";
  artlhsL[ARTL_ART_cfgOptional_171] = ARTL_ART_cfgOptional;
  artPopD[ARTL_ART_cfgOptional_171] = true;
  artLabelInternalStrings[ARTL_ART_cfgOptional_172] = "cfgOptional ::= '('  cfgAlts ')'  . '?'  ";
  artLabelStrings[ARTL_ART_cfgOptional_172] = "";
  artlhsL[ARTL_ART_cfgOptional_172] = ARTL_ART_cfgOptional;
  artKindOfs[ARTL_ART_cfgOptional_172] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgOptional_172] = true;
  artFolds[ARTL_ART_cfgOptional_174] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgOptional_173] = "cfgOptional ::= '('  cfgAlts ')'  '?'  ";
  artLabelStrings[ARTL_ART_cfgOptional_173] = "";
  artlhsL[ARTL_ART_cfgOptional_173] = ARTL_ART_cfgOptional;
  artPopD[ARTL_ART_cfgOptional_173] = true;
  artLabelInternalStrings[ARTL_ART_cfgOptional_174] = "cfgOptional ::= '('  cfgAlts ')'  '?'  .";
  artLabelStrings[ARTL_ART_cfgOptional_174] = "";
  artlhsL[ARTL_ART_cfgOptional_174] = ARTL_ART_cfgOptional;
  artKindOfs[ARTL_ART_cfgOptional_174] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgOptional_174] = true;
  arteoR_pL[ARTL_ART_cfgOptional_174] = true;
  artPopD[ARTL_ART_cfgOptional_174] = true;
  artLabelInternalStrings[ARTL_ART_cfgOptional_176] = "cfgOptional ::= . cfgAltNoAction '?'  ";
  artLabelStrings[ARTL_ART_cfgOptional_176] = "";
  artlhsL[ARTL_ART_cfgOptional_176] = ARTL_ART_cfgOptional;
  artKindOfs[ARTL_ART_cfgOptional_176] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgOptional_178] = "cfgOptional ::= cfgAltNoAction . '?'  ";
  artLabelStrings[ARTL_ART_cfgOptional_178] = "";
  artlhsL[ARTL_ART_cfgOptional_178] = ARTL_ART_cfgOptional;
  artSlotInstanceOfs[ARTL_ART_cfgOptional_178] = ARTL_ART_cfgAltNoAction;
  artKindOfs[ARTL_ART_cfgOptional_178] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgOptional_178] = true;
  artPopD[ARTL_ART_cfgOptional_178] = true;
  artFolds[ARTL_ART_cfgOptional_180] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgOptional_179] = "cfgOptional ::= cfgAltNoAction '?'  ";
  artLabelStrings[ARTL_ART_cfgOptional_179] = "";
  artlhsL[ARTL_ART_cfgOptional_179] = ARTL_ART_cfgOptional;
  artPopD[ARTL_ART_cfgOptional_179] = true;
  artLabelInternalStrings[ARTL_ART_cfgOptional_180] = "cfgOptional ::= cfgAltNoAction '?'  .";
  artLabelStrings[ARTL_ART_cfgOptional_180] = "";
  artlhsL[ARTL_ART_cfgOptional_180] = ARTL_ART_cfgOptional;
  artKindOfs[ARTL_ART_cfgOptional_180] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgOptional_180] = true;
  arteoR_pL[ARTL_ART_cfgOptional_180] = true;
  artPopD[ARTL_ART_cfgOptional_180] = true;
}

public void artTableInitialiser_ART_cfgPositive() {
  artLabelInternalStrings[ARTL_ART_cfgPositive] = "cfgPositive";
  artLabelStrings[ARTL_ART_cfgPositive] = "cfgPositive";
  artKindOfs[ARTL_ART_cfgPositive] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgPositive_198] = "cfgPositive ::= . '('  cfgAlts ')'  '+'  ";
  artLabelStrings[ARTL_ART_cfgPositive_198] = "";
  artlhsL[ARTL_ART_cfgPositive_198] = ARTL_ART_cfgPositive;
  artKindOfs[ARTL_ART_cfgPositive_198] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgPositive_200] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgPositive_199] = "cfgPositive ::= '('  cfgAlts ')'  '+'  ";
  artLabelStrings[ARTL_ART_cfgPositive_199] = "";
  artlhsL[ARTL_ART_cfgPositive_199] = ARTL_ART_cfgPositive;
  artLabelInternalStrings[ARTL_ART_cfgPositive_200] = "cfgPositive ::= '('  . cfgAlts ')'  '+'  ";
  artLabelStrings[ARTL_ART_cfgPositive_200] = "";
  artlhsL[ARTL_ART_cfgPositive_200] = ARTL_ART_cfgPositive;
  artKindOfs[ARTL_ART_cfgPositive_200] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgPositive_200] = true;
  artFolds[ARTL_ART_cfgPositive_202] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgPositive_202] = "cfgPositive ::= '('  cfgAlts . ')'  '+'  ";
  artLabelStrings[ARTL_ART_cfgPositive_202] = "";
  artlhsL[ARTL_ART_cfgPositive_202] = ARTL_ART_cfgPositive;
  artSlotInstanceOfs[ARTL_ART_cfgPositive_202] = ARTL_ART_cfgAlts;
  artKindOfs[ARTL_ART_cfgPositive_202] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgPositive_202] = true;
  artFolds[ARTL_ART_cfgPositive_204] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgPositive_203] = "cfgPositive ::= '('  cfgAlts ')'  '+'  ";
  artLabelStrings[ARTL_ART_cfgPositive_203] = "";
  artlhsL[ARTL_ART_cfgPositive_203] = ARTL_ART_cfgPositive;
  artPopD[ARTL_ART_cfgPositive_203] = true;
  artLabelInternalStrings[ARTL_ART_cfgPositive_204] = "cfgPositive ::= '('  cfgAlts ')'  . '+'  ";
  artLabelStrings[ARTL_ART_cfgPositive_204] = "";
  artlhsL[ARTL_ART_cfgPositive_204] = ARTL_ART_cfgPositive;
  artKindOfs[ARTL_ART_cfgPositive_204] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgPositive_204] = true;
  artFolds[ARTL_ART_cfgPositive_206] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgPositive_205] = "cfgPositive ::= '('  cfgAlts ')'  '+'  ";
  artLabelStrings[ARTL_ART_cfgPositive_205] = "";
  artlhsL[ARTL_ART_cfgPositive_205] = ARTL_ART_cfgPositive;
  artPopD[ARTL_ART_cfgPositive_205] = true;
  artLabelInternalStrings[ARTL_ART_cfgPositive_206] = "cfgPositive ::= '('  cfgAlts ')'  '+'  .";
  artLabelStrings[ARTL_ART_cfgPositive_206] = "";
  artlhsL[ARTL_ART_cfgPositive_206] = ARTL_ART_cfgPositive;
  artKindOfs[ARTL_ART_cfgPositive_206] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgPositive_206] = true;
  arteoR_pL[ARTL_ART_cfgPositive_206] = true;
  artPopD[ARTL_ART_cfgPositive_206] = true;
  artLabelInternalStrings[ARTL_ART_cfgPositive_208] = "cfgPositive ::= . cfgAltNoAction '+'  ";
  artLabelStrings[ARTL_ART_cfgPositive_208] = "";
  artlhsL[ARTL_ART_cfgPositive_208] = ARTL_ART_cfgPositive;
  artKindOfs[ARTL_ART_cfgPositive_208] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgPositive_210] = "cfgPositive ::= cfgAltNoAction . '+'  ";
  artLabelStrings[ARTL_ART_cfgPositive_210] = "";
  artlhsL[ARTL_ART_cfgPositive_210] = ARTL_ART_cfgPositive;
  artSlotInstanceOfs[ARTL_ART_cfgPositive_210] = ARTL_ART_cfgAltNoAction;
  artKindOfs[ARTL_ART_cfgPositive_210] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgPositive_210] = true;
  artPopD[ARTL_ART_cfgPositive_210] = true;
  artFolds[ARTL_ART_cfgPositive_212] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgPositive_211] = "cfgPositive ::= cfgAltNoAction '+'  ";
  artLabelStrings[ARTL_ART_cfgPositive_211] = "";
  artlhsL[ARTL_ART_cfgPositive_211] = ARTL_ART_cfgPositive;
  artPopD[ARTL_ART_cfgPositive_211] = true;
  artLabelInternalStrings[ARTL_ART_cfgPositive_212] = "cfgPositive ::= cfgAltNoAction '+'  .";
  artLabelStrings[ARTL_ART_cfgPositive_212] = "";
  artlhsL[ARTL_ART_cfgPositive_212] = ARTL_ART_cfgPositive;
  artKindOfs[ARTL_ART_cfgPositive_212] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgPositive_212] = true;
  arteoR_pL[ARTL_ART_cfgPositive_212] = true;
  artPopD[ARTL_ART_cfgPositive_212] = true;
}

public void artTableInitialiser_ART_cfgPrim() {
  artLabelInternalStrings[ARTL_ART_cfgPrim] = "cfgPrim";
  artLabelStrings[ARTL_ART_cfgPrim] = "cfgPrim";
  artKindOfs[ARTL_ART_cfgPrim] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgPrim_120] = "cfgPrim ::= . cfgNonterminal ";
  artLabelStrings[ARTL_ART_cfgPrim_120] = "";
  artlhsL[ARTL_ART_cfgPrim_120] = ARTL_ART_cfgPrim;
  artKindOfs[ARTL_ART_cfgPrim_120] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgPrim_122] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgPrim_122] = "cfgPrim ::= cfgNonterminal .";
  artLabelStrings[ARTL_ART_cfgPrim_122] = "";
  artlhsL[ARTL_ART_cfgPrim_122] = ARTL_ART_cfgPrim;
  artSlotInstanceOfs[ARTL_ART_cfgPrim_122] = ARTL_ART_cfgNonterminal;
  artKindOfs[ARTL_ART_cfgPrim_122] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgPrim_122] = true;
  arteoR_pL[ARTL_ART_cfgPrim_122] = true;
  artPopD[ARTL_ART_cfgPrim_122] = true;
  artLabelInternalStrings[ARTL_ART_cfgPrim_124] = "cfgPrim ::= . cfgCaseSensitiveTerminal ";
  artLabelStrings[ARTL_ART_cfgPrim_124] = "";
  artlhsL[ARTL_ART_cfgPrim_124] = ARTL_ART_cfgPrim;
  artKindOfs[ARTL_ART_cfgPrim_124] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgPrim_126] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgPrim_126] = "cfgPrim ::= cfgCaseSensitiveTerminal .";
  artLabelStrings[ARTL_ART_cfgPrim_126] = "";
  artlhsL[ARTL_ART_cfgPrim_126] = ARTL_ART_cfgPrim;
  artSlotInstanceOfs[ARTL_ART_cfgPrim_126] = ARTL_ART_cfgCaseSensitiveTerminal;
  artKindOfs[ARTL_ART_cfgPrim_126] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgPrim_126] = true;
  arteoR_pL[ARTL_ART_cfgPrim_126] = true;
  artPopD[ARTL_ART_cfgPrim_126] = true;
  artLabelInternalStrings[ARTL_ART_cfgPrim_128] = "cfgPrim ::= . cfgBuiltinTerminal ";
  artLabelStrings[ARTL_ART_cfgPrim_128] = "";
  artlhsL[ARTL_ART_cfgPrim_128] = ARTL_ART_cfgPrim;
  artKindOfs[ARTL_ART_cfgPrim_128] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgPrim_130] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgPrim_130] = "cfgPrim ::= cfgBuiltinTerminal .";
  artLabelStrings[ARTL_ART_cfgPrim_130] = "";
  artlhsL[ARTL_ART_cfgPrim_130] = ARTL_ART_cfgPrim;
  artSlotInstanceOfs[ARTL_ART_cfgPrim_130] = ARTL_ART_cfgBuiltinTerminal;
  artKindOfs[ARTL_ART_cfgPrim_130] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgPrim_130] = true;
  arteoR_pL[ARTL_ART_cfgPrim_130] = true;
  artPopD[ARTL_ART_cfgPrim_130] = true;
}

public void artTableInitialiser_ART_cfgRule() {
  artLabelInternalStrings[ARTL_ART_cfgRule] = "cfgRule";
  artLabelStrings[ARTL_ART_cfgRule] = "cfgRule";
  artKindOfs[ARTL_ART_cfgRule] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgRule_12] = "cfgRule ::= . cfgLHS '::='  cfgAlts ";
  artLabelStrings[ARTL_ART_cfgRule_12] = "";
  artlhsL[ARTL_ART_cfgRule_12] = ARTL_ART_cfgRule;
  artKindOfs[ARTL_ART_cfgRule_12] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgRule_14] = "cfgRule ::= cfgLHS . '::='  cfgAlts ";
  artLabelStrings[ARTL_ART_cfgRule_14] = "";
  artlhsL[ARTL_ART_cfgRule_14] = ARTL_ART_cfgRule;
  artSlotInstanceOfs[ARTL_ART_cfgRule_14] = ARTL_ART_cfgLHS;
  artKindOfs[ARTL_ART_cfgRule_14] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgRule_14] = true;
  artFolds[ARTL_ART_cfgRule_16] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgRule_15] = "cfgRule ::= cfgLHS '::='  cfgAlts ";
  artLabelStrings[ARTL_ART_cfgRule_15] = "";
  artlhsL[ARTL_ART_cfgRule_15] = ARTL_ART_cfgRule;
  artLabelInternalStrings[ARTL_ART_cfgRule_16] = "cfgRule ::= cfgLHS '::='  . cfgAlts ";
  artLabelStrings[ARTL_ART_cfgRule_16] = "";
  artlhsL[ARTL_ART_cfgRule_16] = ARTL_ART_cfgRule;
  artKindOfs[ARTL_ART_cfgRule_16] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgRule_18] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgRule_18] = "cfgRule ::= cfgLHS '::='  cfgAlts .";
  artLabelStrings[ARTL_ART_cfgRule_18] = "";
  artlhsL[ARTL_ART_cfgRule_18] = ARTL_ART_cfgRule;
  artSlotInstanceOfs[ARTL_ART_cfgRule_18] = ARTL_ART_cfgAlts;
  artKindOfs[ARTL_ART_cfgRule_18] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgRule_18] = true;
  arteoR_pL[ARTL_ART_cfgRule_18] = true;
  artPopD[ARTL_ART_cfgRule_18] = true;
}

public void artTableInitialiser_ART_cfgRules() {
  artLabelInternalStrings[ARTL_ART_cfgRules] = "cfgRules";
  artLabelStrings[ARTL_ART_cfgRules] = "cfgRules";
  artKindOfs[ARTL_ART_cfgRules] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgRules_2] = "cfgRules ::= . cfgRule ";
  artLabelStrings[ARTL_ART_cfgRules_2] = "";
  artlhsL[ARTL_ART_cfgRules_2] = ARTL_ART_cfgRules;
  artKindOfs[ARTL_ART_cfgRules_2] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_cfgRules_4] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_cfgRules_4] = "cfgRules ::= cfgRule .";
  artLabelStrings[ARTL_ART_cfgRules_4] = "";
  artlhsL[ARTL_ART_cfgRules_4] = ARTL_ART_cfgRules;
  artSlotInstanceOfs[ARTL_ART_cfgRules_4] = ARTL_ART_cfgRule;
  artKindOfs[ARTL_ART_cfgRules_4] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgRules_4] = true;
  arteoR_pL[ARTL_ART_cfgRules_4] = true;
  artPopD[ARTL_ART_cfgRules_4] = true;
  artLabelInternalStrings[ARTL_ART_cfgRules_6] = "cfgRules ::= . cfgRule cfgRules ";
  artLabelStrings[ARTL_ART_cfgRules_6] = "";
  artlhsL[ARTL_ART_cfgRules_6] = ARTL_ART_cfgRules;
  artKindOfs[ARTL_ART_cfgRules_6] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_cfgRules_8] = "cfgRules ::= cfgRule . cfgRules ";
  artLabelStrings[ARTL_ART_cfgRules_8] = "";
  artlhsL[ARTL_ART_cfgRules_8] = ARTL_ART_cfgRules;
  artSlotInstanceOfs[ARTL_ART_cfgRules_8] = ARTL_ART_cfgRule;
  artKindOfs[ARTL_ART_cfgRules_8] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_cfgRules_8] = true;
  artFolds[ARTL_ART_cfgRules_10] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgRules_10] = "cfgRules ::= cfgRule cfgRules .";
  artLabelStrings[ARTL_ART_cfgRules_10] = "";
  artlhsL[ARTL_ART_cfgRules_10] = ARTL_ART_cfgRules;
  artSlotInstanceOfs[ARTL_ART_cfgRules_10] = ARTL_ART_cfgRules;
  artKindOfs[ARTL_ART_cfgRules_10] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgRules_10] = true;
  arteoR_pL[ARTL_ART_cfgRules_10] = true;
  artPopD[ARTL_ART_cfgRules_10] = true;
}

public void artTableInitialiser_ART_cfgSeq() {
  artLabelInternalStrings[ARTL_ART_cfgSeq] = "cfgSeq";
  artLabelStrings[ARTL_ART_cfgSeq] = "cfgSeq";
  artKindOfs[ARTL_ART_cfgSeq] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgSeq_60] = "cfgSeq ::= . # ";
  artLabelStrings[ARTL_ART_cfgSeq_60] = "";
  artlhsL[ARTL_ART_cfgSeq_60] = ARTL_ART_cfgSeq;
  artKindOfs[ARTL_ART_cfgSeq_60] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgSeq_60] = true;
  artFolds[ARTL_ART_cfgSeq_62] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgSeq_62] = "cfgSeq ::= # .";
  artLabelStrings[ARTL_ART_cfgSeq_62] = "";
  artlhsL[ARTL_ART_cfgSeq_62] = ARTL_ART_cfgSeq;
  artKindOfs[ARTL_ART_cfgSeq_62] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgSeq_62] = true;
  arteoR_pL[ARTL_ART_cfgSeq_62] = true;
  artPopD[ARTL_ART_cfgSeq_62] = true;
}

public void artTableInitialiser_ART_cfgSlot() {
  artLabelInternalStrings[ARTL_ART_cfgSlot] = "cfgSlot";
  artLabelStrings[ARTL_ART_cfgSlot] = "cfgSlot";
  artKindOfs[ARTL_ART_cfgSlot] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_cfgSlot_116] = "cfgSlot ::= . # ";
  artLabelStrings[ARTL_ART_cfgSlot_116] = "";
  artlhsL[ARTL_ART_cfgSlot_116] = ARTL_ART_cfgSlot;
  artKindOfs[ARTL_ART_cfgSlot_116] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_cfgSlot_116] = true;
  artFolds[ARTL_ART_cfgSlot_118] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_cfgSlot_118] = "cfgSlot ::= # .";
  artLabelStrings[ARTL_ART_cfgSlot_118] = "";
  artlhsL[ARTL_ART_cfgSlot_118] = ARTL_ART_cfgSlot;
  artKindOfs[ARTL_ART_cfgSlot_118] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_cfgSlot_118] = true;
  arteoR_pL[ARTL_ART_cfgSlot_118] = true;
  artPopD[ARTL_ART_cfgSlot_118] = true;
}

public void artTableInitialiser_ART_term() {
  artLabelInternalStrings[ARTL_ART_term] = "term";
  artLabelStrings[ARTL_ART_term] = "term";
  artKindOfs[ARTL_ART_term] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_term_288] = "term ::= . cfgAttribute ";
  artLabelStrings[ARTL_ART_term_288] = "";
  artlhsL[ARTL_ART_term_288] = ARTL_ART_term;
  artKindOfs[ARTL_ART_term_288] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_term_290] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_term_290] = "term ::= cfgAttribute .";
  artLabelStrings[ARTL_ART_term_290] = "";
  artlhsL[ARTL_ART_term_290] = ARTL_ART_term;
  artSlotInstanceOfs[ARTL_ART_term_290] = ARTL_ART_cfgAttribute;
  artKindOfs[ARTL_ART_term_290] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_term_290] = true;
  arteoR_pL[ARTL_ART_term_290] = true;
  artPopD[ARTL_ART_term_290] = true;
  artLabelInternalStrings[ARTL_ART_term_292] = "term ::= . &INTEGER  ";
  artLabelStrings[ARTL_ART_term_292] = "";
  artlhsL[ARTL_ART_term_292] = ARTL_ART_term;
  artKindOfs[ARTL_ART_term_292] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_term_292] = true;
  artFolds[ARTL_ART_term_294] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_term_293] = "term ::= &INTEGER  ";
  artLabelStrings[ARTL_ART_term_293] = "";
  artlhsL[ARTL_ART_term_293] = ARTL_ART_term;
  artPopD[ARTL_ART_term_293] = true;
  artLabelInternalStrings[ARTL_ART_term_294] = "term ::= &INTEGER  .";
  artLabelStrings[ARTL_ART_term_294] = "";
  artlhsL[ARTL_ART_term_294] = ARTL_ART_term;
  artKindOfs[ARTL_ART_term_294] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_term_294] = true;
  arteoR_pL[ARTL_ART_term_294] = true;
  artPopD[ARTL_ART_term_294] = true;
  artLabelInternalStrings[ARTL_ART_term_296] = "term ::= . &REAL  ";
  artLabelStrings[ARTL_ART_term_296] = "";
  artlhsL[ARTL_ART_term_296] = ARTL_ART_term;
  artKindOfs[ARTL_ART_term_296] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_term_296] = true;
  artFolds[ARTL_ART_term_298] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_term_297] = "term ::= &REAL  ";
  artLabelStrings[ARTL_ART_term_297] = "";
  artlhsL[ARTL_ART_term_297] = ARTL_ART_term;
  artPopD[ARTL_ART_term_297] = true;
  artLabelInternalStrings[ARTL_ART_term_298] = "term ::= &REAL  .";
  artLabelStrings[ARTL_ART_term_298] = "";
  artlhsL[ARTL_ART_term_298] = ARTL_ART_term;
  artKindOfs[ARTL_ART_term_298] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_term_298] = true;
  arteoR_pL[ARTL_ART_term_298] = true;
  artPopD[ARTL_ART_term_298] = true;
  artLabelInternalStrings[ARTL_ART_term_300] = "term ::= . &STRING_DQ  ";
  artLabelStrings[ARTL_ART_term_300] = "";
  artlhsL[ARTL_ART_term_300] = ARTL_ART_term;
  artKindOfs[ARTL_ART_term_300] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_term_300] = true;
  artFolds[ARTL_ART_term_302] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_term_301] = "term ::= &STRING_DQ  ";
  artLabelStrings[ARTL_ART_term_301] = "";
  artlhsL[ARTL_ART_term_301] = ARTL_ART_term;
  artPopD[ARTL_ART_term_301] = true;
  artLabelInternalStrings[ARTL_ART_term_302] = "term ::= &STRING_DQ  .";
  artLabelStrings[ARTL_ART_term_302] = "";
  artlhsL[ARTL_ART_term_302] = ARTL_ART_term;
  artKindOfs[ARTL_ART_term_302] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_term_302] = true;
  arteoR_pL[ARTL_ART_term_302] = true;
  artPopD[ARTL_ART_term_302] = true;
  artLabelInternalStrings[ARTL_ART_term_304] = "term ::= . &ID  '('  ')'  ";
  artLabelStrings[ARTL_ART_term_304] = "";
  artlhsL[ARTL_ART_term_304] = ARTL_ART_term;
  artKindOfs[ARTL_ART_term_304] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_term_304] = true;
  artFolds[ARTL_ART_term_306] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_term_305] = "term ::= &ID  '('  ')'  ";
  artLabelStrings[ARTL_ART_term_305] = "";
  artlhsL[ARTL_ART_term_305] = ARTL_ART_term;
  artPopD[ARTL_ART_term_305] = true;
  artLabelInternalStrings[ARTL_ART_term_306] = "term ::= &ID  . '('  ')'  ";
  artLabelStrings[ARTL_ART_term_306] = "";
  artlhsL[ARTL_ART_term_306] = ARTL_ART_term;
  artKindOfs[ARTL_ART_term_306] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_term_306] = true;
  artPopD[ARTL_ART_term_306] = true;
  artFolds[ARTL_ART_term_308] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_term_307] = "term ::= &ID  '('  ')'  ";
  artLabelStrings[ARTL_ART_term_307] = "";
  artlhsL[ARTL_ART_term_307] = ARTL_ART_term;
  artPopD[ARTL_ART_term_307] = true;
  artLabelInternalStrings[ARTL_ART_term_308] = "term ::= &ID  '('  . ')'  ";
  artLabelStrings[ARTL_ART_term_308] = "";
  artlhsL[ARTL_ART_term_308] = ARTL_ART_term;
  artKindOfs[ARTL_ART_term_308] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_term_308] = true;
  artFolds[ARTL_ART_term_310] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_term_309] = "term ::= &ID  '('  ')'  ";
  artLabelStrings[ARTL_ART_term_309] = "";
  artlhsL[ARTL_ART_term_309] = ARTL_ART_term;
  artPopD[ARTL_ART_term_309] = true;
  artLabelInternalStrings[ARTL_ART_term_310] = "term ::= &ID  '('  ')'  .";
  artLabelStrings[ARTL_ART_term_310] = "";
  artlhsL[ARTL_ART_term_310] = ARTL_ART_term;
  artKindOfs[ARTL_ART_term_310] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_term_310] = true;
  arteoR_pL[ARTL_ART_term_310] = true;
  artPopD[ARTL_ART_term_310] = true;
  artLabelInternalStrings[ARTL_ART_term_312] = "term ::= . &ID  '('  arguments ')'  ";
  artLabelStrings[ARTL_ART_term_312] = "";
  artlhsL[ARTL_ART_term_312] = ARTL_ART_term;
  artKindOfs[ARTL_ART_term_312] = ARTK_INTERMEDIATE;
  artFolds[ARTL_ART_term_314] = ARTFOLD_OVER;
  artLabelInternalStrings[ARTL_ART_term_313] = "term ::= &ID  '('  arguments ')'  ";
  artLabelStrings[ARTL_ART_term_313] = "";
  artlhsL[ARTL_ART_term_313] = ARTL_ART_term;
  artLabelInternalStrings[ARTL_ART_term_314] = "term ::= &ID  . '('  arguments ')'  ";
  artLabelStrings[ARTL_ART_term_314] = "";
  artlhsL[ARTL_ART_term_314] = ARTL_ART_term;
  artKindOfs[ARTL_ART_term_314] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_term_314] = true;
  artFolds[ARTL_ART_term_316] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_term_315] = "term ::= &ID  '('  arguments ')'  ";
  artLabelStrings[ARTL_ART_term_315] = "";
  artlhsL[ARTL_ART_term_315] = ARTL_ART_term;
  artLabelInternalStrings[ARTL_ART_term_316] = "term ::= &ID  '('  . arguments ')'  ";
  artLabelStrings[ARTL_ART_term_316] = "";
  artlhsL[ARTL_ART_term_316] = ARTL_ART_term;
  artKindOfs[ARTL_ART_term_316] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_term_318] = "term ::= &ID  '('  arguments . ')'  ";
  artLabelStrings[ARTL_ART_term_318] = "";
  artlhsL[ARTL_ART_term_318] = ARTL_ART_term;
  artSlotInstanceOfs[ARTL_ART_term_318] = ARTL_ART_arguments;
  artKindOfs[ARTL_ART_term_318] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_term_318] = true;
  artFolds[ARTL_ART_term_320] = ARTFOLD_UNDER;
  artLabelInternalStrings[ARTL_ART_term_319] = "term ::= &ID  '('  arguments ')'  ";
  artLabelStrings[ARTL_ART_term_319] = "";
  artlhsL[ARTL_ART_term_319] = ARTL_ART_term;
  artPopD[ARTL_ART_term_319] = true;
  artLabelInternalStrings[ARTL_ART_term_320] = "term ::= &ID  '('  arguments ')'  .";
  artLabelStrings[ARTL_ART_term_320] = "";
  artlhsL[ARTL_ART_term_320] = ARTL_ART_term;
  artKindOfs[ARTL_ART_term_320] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_term_320] = true;
  arteoR_pL[ARTL_ART_term_320] = true;
  artPopD[ARTL_ART_term_320] = true;
}

public void artTableInitialise() {
  artLabelInternalStrings = new String[ARTX_LABEL_EXTENT + 1];
  artLabelStrings = new String[ARTX_LABEL_EXTENT + 1];
  artLabelInternalStrings[ARTL_EOS] = "ART$";
  artLabelStrings[ARTL_EOS] = " EOS $";
  artLabelInternalStrings[ARTX_DESPATCH] = "ARTX_DESPATCH";
  artLabelStrings[ARTX_DESPATCH] = " DESPATCH";
  artLabelInternalStrings[ARTL_DUMMY] = "ARTL_DUMMY";
  artLabelStrings[ARTL_DUMMY] = " DUMMY";
  artLabelInternalStrings[ARTX_LABEL_EXTENT] = "!!ILLEGAL!!";
  artLabelStrings[ARTX_LABEL_EXTENT] = " ILLEGAL";
  artLabelStrings[ARTL_EPSILON] = "#";
  artLabelInternalStrings[ARTL_EPSILON] = "#";

  artTerminalRequiresWhiteSpace = new boolean[ARTL_EPSILON];
  artInitialiseBooleanArray(artTerminalRequiresWhiteSpace, 0, ARTL_EPSILON, false);

  artTerminalCaseInsensitive = new boolean[ARTL_EPSILON];
  artInitialiseBooleanArray(artTerminalCaseInsensitive, 0, ARTL_EPSILON, false);

  artlhsL = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artlhsL, 0, ARTX_LABEL_EXTENT);
  artlhsL[ARTX_DESPATCH] = ARTX_DESPATCH;

  artKindOfs = new int[ARTX_LABEL_EXTENT + 1];
  artKindOfs[ARTL_EOS] = ARTK_EOS;
  artKindOfs[ARTL_EPSILON] = ARTK_EPSILON;

  artHigher = new ARTBitSet[ARTX_LABEL_EXTENT + 1];

  artLonger = new ARTBitSet[ARTX_LABEL_EXTENT + 1];

  artShorter = new ARTBitSet[ARTX_LABEL_EXTENT + 1];

  artPreSlots = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artPreSlots, 0, ARTX_LABEL_EXTENT);

  artPostSlots = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artPostSlots, 0, ARTX_LABEL_EXTENT);

  artInstanceOfs = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artInstanceOfs, 0, ARTX_LABEL_EXTENT);

  artSlotInstanceOfs = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artSlotInstanceOfs, 0, ARTX_LABEL_EXTENT);

  artUserNameOfs = new int[ARTX_LABEL_EXTENT + 1];

  artGathers = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artGathers, 0, ARTX_LABEL_EXTENT);

  artFolds = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artFolds, 0, ARTX_LABEL_EXTENT, 0);

  artpL = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artpL, 0, ARTX_LABEL_EXTENT);

  artaL = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artaL, 0, ARTX_LABEL_EXTENT);

  artcolonL = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artcolonL, 0, ARTX_LABEL_EXTENT);

  arteoOPL = new boolean[ARTX_LABEL_EXTENT];
  artInitialiseBooleanArray(arteoOPL, 0, ARTX_LABEL_EXTENT, false);

  artfiRL = new boolean[ARTX_LABEL_EXTENT];
  artInitialiseBooleanArray(artfiRL, 0, ARTX_LABEL_EXTENT, false);

  artfiPCL = new boolean[ARTX_LABEL_EXTENT];
  artInitialiseBooleanArray(artfiPCL, 0, ARTX_LABEL_EXTENT, false);

  arteoRL = new boolean[ARTX_LABEL_EXTENT];
  artInitialiseBooleanArray(arteoRL, 0, ARTX_LABEL_EXTENT, false);

  arteoR_pL = new boolean[ARTX_LABEL_EXTENT];
  artInitialiseBooleanArray(arteoR_pL, 0, ARTX_LABEL_EXTENT, false);

  artPopD = new boolean[ARTX_LABEL_EXTENT];
  artInitialiseBooleanArray(artPopD, 0, ARTX_LABEL_EXTENT, false);

  artLabelStrings[ARTTB_ID] = "ID";
  artLabelInternalStrings[ARTTB_ID] = "&ID";
  artKindOfs[ARTTB_ID] = ARTK_BUILTIN_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTB_ID] = true;
  artLabelStrings[ARTTB_INTEGER] = "INTEGER";
  artLabelInternalStrings[ARTTB_INTEGER] = "&INTEGER";
  artKindOfs[ARTTB_INTEGER] = ARTK_BUILTIN_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTB_INTEGER] = true;
  artLabelStrings[ARTTB_REAL] = "REAL";
  artLabelInternalStrings[ARTTB_REAL] = "&REAL";
  artKindOfs[ARTTB_REAL] = ARTK_BUILTIN_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTB_REAL] = true;
  artLabelStrings[ARTTB_STRING_DQ] = "STRING_DQ";
  artLabelInternalStrings[ARTTB_STRING_DQ] = "&STRING_DQ";
  artKindOfs[ARTTB_STRING_DQ] = ARTK_BUILTIN_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTB_STRING_DQ] = true;
  artLabelStrings[ARTTB_STRING_PLAIN_SQ] = "STRING_PLAIN_SQ";
  artLabelInternalStrings[ARTTB_STRING_PLAIN_SQ] = "&STRING_PLAIN_SQ";
  artKindOfs[ARTTB_STRING_PLAIN_SQ] = ARTK_BUILTIN_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTB_STRING_PLAIN_SQ] = true;
  artLabelStrings[ARTTS__HASH] = "#";
  artLabelInternalStrings[ARTTS__HASH] = "'#'";
  artKindOfs[ARTTS__HASH] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS__HASH] = true;
  artLabelStrings[ARTTS__AMPERSAND] = "&";
  artLabelInternalStrings[ARTTS__AMPERSAND] = "'&'";
  artKindOfs[ARTTS__AMPERSAND] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS__AMPERSAND] = true;
  artLabelStrings[ARTTS__LPAR] = "(";
  artLabelInternalStrings[ARTTS__LPAR] = "'('";
  artKindOfs[ARTTS__LPAR] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS__LPAR] = true;
  artLabelStrings[ARTTS__RPAR] = ")";
  artLabelInternalStrings[ARTTS__RPAR] = "')'";
  artKindOfs[ARTTS__RPAR] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS__RPAR] = true;
  artLabelStrings[ARTTS__STAR] = "*";
  artLabelInternalStrings[ARTTS__STAR] = "'*'";
  artKindOfs[ARTTS__STAR] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS__STAR] = true;
  artLabelStrings[ARTTS__PLUS] = "+";
  artLabelInternalStrings[ARTTS__PLUS] = "'+'";
  artKindOfs[ARTTS__PLUS] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS__PLUS] = true;
  artLabelStrings[ARTTS__COMMA] = ",";
  artLabelInternalStrings[ARTTS__COMMA] = "','";
  artKindOfs[ARTTS__COMMA] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS__COMMA] = true;
  artLabelStrings[ARTTS__PERIOD] = ".";
  artLabelInternalStrings[ARTTS__PERIOD] = "'.'";
  artKindOfs[ARTTS__PERIOD] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS__PERIOD] = true;
  artLabelStrings[ARTTS__COLON_COLON_EQUAL] = "::=";
  artLabelInternalStrings[ARTTS__COLON_COLON_EQUAL] = "'::='";
  artKindOfs[ARTTS__COLON_COLON_EQUAL] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS__COLON_COLON_EQUAL] = true;
  artLabelStrings[ARTTS__COLON_EQUAL] = ":=";
  artLabelInternalStrings[ARTTS__COLON_EQUAL] = "':='";
  artKindOfs[ARTTS__COLON_EQUAL] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS__COLON_EQUAL] = true;
  artLabelStrings[ARTTS__EQUAL] = "=";
  artLabelInternalStrings[ARTTS__EQUAL] = "'='";
  artKindOfs[ARTTS__EQUAL] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS__EQUAL] = true;
  artLabelStrings[ARTTS__QUERY] = "?";
  artLabelInternalStrings[ARTTS__QUERY] = "'?'";
  artKindOfs[ARTTS__QUERY] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS__QUERY] = true;
  artLabelStrings[ARTTS__UPARROW] = "^";
  artLabelInternalStrings[ARTTS__UPARROW] = "'^'";
  artKindOfs[ARTTS__UPARROW] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS__UPARROW] = true;
  artLabelStrings[ARTTS__UPARROW_PLUS] = "^+";
  artLabelInternalStrings[ARTTS__UPARROW_PLUS] = "'^+'";
  artKindOfs[ARTTS__UPARROW_PLUS] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS__UPARROW_PLUS] = true;
  artLabelStrings[ARTTS__UPARROW_UPARROW] = "^^";
  artLabelInternalStrings[ARTTS__UPARROW_UPARROW] = "'^^'";
  artKindOfs[ARTTS__UPARROW_UPARROW] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS__UPARROW_UPARROW] = true;
  artLabelStrings[ARTTS__BAR] = "|";
  artLabelInternalStrings[ARTTS__BAR] = "'|'";
  artKindOfs[ARTTS__BAR] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS__BAR] = true;
  artTableInitialiser_ART_arguments();
  artTableInitialiser_ART_cfgAction();
  artTableInitialiser_ART_cfgActionSeq();
  artTableInitialiser_ART_cfgActions();
  artTableInitialiser_ART_cfgAlt();
  artTableInitialiser_ART_cfgAltNoAction();
  artTableInitialiser_ART_cfgAlts();
  artTableInitialiser_ART_cfgAnnotation();
  artTableInitialiser_ART_cfgAssignment();
  artTableInitialiser_ART_cfgAttribute();
  artTableInitialiser_ART_cfgBuiltinTerminal();
  artTableInitialiser_ART_cfgCaseSensitiveTerminal();
  artTableInitialiser_ART_cfgDoFirst();
  artTableInitialiser_ART_cfgElems();
  artTableInitialiser_ART_cfgEpsilon();
  artTableInitialiser_ART_cfgEpsilonCarrier();
  artTableInitialiser_ART_cfgEquation();
  artTableInitialiser_ART_cfgExtended();
  artTableInitialiser_ART_cfgFoldOver();
  artTableInitialiser_ART_cfgFoldUnder();
  artTableInitialiser_ART_cfgInsert();
  artTableInitialiser_ART_cfgKleene();
  artTableInitialiser_ART_cfgLHS();
  artTableInitialiser_ART_cfgNonterminal();
  artTableInitialiser_ART_cfgOptional();
  artTableInitialiser_ART_cfgPositive();
  artTableInitialiser_ART_cfgPrim();
  artTableInitialiser_ART_cfgRule();
  artTableInitialiser_ART_cfgRules();
  artTableInitialiser_ART_cfgSeq();
  artTableInitialiser_ART_cfgSlot();
  artTableInitialiser_ART_term();
}

public ReferenceGrammarParser(ARTLexerV3 artLexer) {
  this(null, artLexer);
}

public ReferenceGrammarParser(ARTGrammar artGrammar, ARTLexerV3 artLexer) {
  super(artGrammar, artLexer);
  artParserKind = "GLL Gen";
  artFirstTerminalLabel = ARTTS__HASH;
  artFirstUnusedLabel = ARTX_LABEL_EXTENT + 1;
  artSetExtent = 55;
  ARTL_EOS = ARTX_EOS;
  ARTL_EPSILON = ARTX_EPSILON;
  ARTL_DUMMY = ARTX_DUMMY;
  artGrammarKind = ARTModeGrammarKind.BNF;
  artDefaultStartSymbolLabel = ARTL_ART_cfgRules;
  artBuildDirectives = "ARTDirectives [inputs=[], inputFilenames=[], directives={suppressPopGuard=false, tweLexicalisations=false, algorithmMode=gllGeneratorPool, tweLongest=false, tweSegments=false, sppfShortest=false, termWrite=false, tweCounts=false, clusteredGSS=false, twePrint=false, rewriteDisable=false, tweAmbiguityClasses=false, sppfAmbiguityAnalysis=false, rewriteConfiguration=false, outputDirectory=., inputCounts=false, twePriority=false, treeShow=false, tweRecursive=false, rewritePostorder=false, rewriteContractum=true, parseCounts=false, predictivePops=false, suppressProductionGuard=false, sppfDead=false, twePrintFull=false, input=0, tweExtents=false, suppressSemantics=false, despatchMode=fragment, treePrintLevel=3, sppfShowFull=false, treePrint=false, sppfChooseCounts=false, log=0, tweDump=false, sppfCycleDetect=false, sppfCountSentences=false, parserName=ReferenceGrammarParser, rewriteResume=true, inputPrint=false, lexerName=ReferenceGrammarLexer, trace=false, tweTokenWrite=false, tweDead=false, tweShortest=false, rewritePure=true, tweSelectOne=false, smlCycleBreak=false, termPrint=false, suppressTestRepeat=false, rewritePreorder=false, sppfAmbiguityAnalysisFull=false, tweFromSPPF=false, actionSuppress=false, tweLexicalisationsQuick=false, sppfPriority=false, sppfShow=false, rewriteOneStep=false, namespace=uk.ac.rhul.cs.csle.art.v4.cfg.referenceFamily.script, sppfSelectOne=false, FIFODescriptors=false, sppfOrderedLongest=false, verbosity=0, sppfLongest=false, gssShow=false}]";
  artFIFODescriptors = false;
  artSetInitialise();
  artTableInitialise();
}

public void ARTRD_arguments(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*arguments ::= term .*/
    case ARTL_ART_arguments_324: 
            ARTRD_arguments(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_term(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*arguments ::= term ','  . arguments */
    case ARTL_ART_arguments_330: 
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_term(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
                        break;
    /*arguments ::= term ','  arguments .*/
    case ARTL_ART_arguments_332: 
            ARTRD_arguments(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = false; artNewParent = artParent;
      ARTRD_arguments(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void ARTRD_cfgAction(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgAction ::= cfgEquation .*/
    case ARTL_ART_cfgAction_248: 
            ARTRD_cfgAction(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgEquation(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgAction ::= cfgAssignment .*/
    case ARTL_ART_cfgAction_252: 
            ARTRD_cfgAction(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgAssignment(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgAction ::= cfgInsert .*/
    case ARTL_ART_cfgAction_256: 
            ARTRD_cfgAction(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgInsert(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void ARTRD_cfgActionSeq(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgActionSeq ::= cfgAction .*/
    case ARTL_ART_cfgActionSeq_238: 
            ARTRD_cfgActionSeq(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgAction(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgActionSeq ::= cfgAction cfgActionSeq .*/
    case ARTL_ART_cfgActionSeq_244: 
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_cfgAction(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
            artNewWriteable = false; artNewParent = artParent;
      ARTRD_cfgActionSeq(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void ARTRD_cfgActions(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgActions ::= cfgSlot .*/
    case ARTL_ART_cfgActions_66: 
            ARTRD_cfgActions(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgSlot(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgActions ::= cfgSlot cfgActionSeq .*/
    case ARTL_ART_cfgActions_72: 
            artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_cfgSlot(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
            artNewWriteable = false; artNewParent = artParent;
      ARTRD_cfgActionSeq(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void ARTRD_cfgAlt(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgAlt ::= cfgSeq cfgActions . cfgEpsilon */
    case ARTL_ART_cfgAlt_40: 
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_cfgSeq(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgActions(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgAlt ::= cfgSeq cfgActions cfgEpsilon .*/
    case ARTL_ART_cfgAlt_42: 
            ARTRD_cfgAlt(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgEpsilon(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgAlt ::= cfgSeq cfgActions . cfgEpsilonCarrier */
    case ARTL_ART_cfgAlt_48: 
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_cfgSeq(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgActions(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgAlt ::= cfgSeq cfgActions cfgEpsilonCarrier .*/
    case ARTL_ART_cfgAlt_50: 
            ARTRD_cfgAlt(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgEpsilonCarrier(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgAlt ::= cfgSeq cfgElems . cfgActions */
    case ARTL_ART_cfgAlt_56: 
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_cfgSeq(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
            artNewWriteable = false; artNewParent = artParent;
      ARTRD_cfgElems(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgAlt ::= cfgSeq cfgElems cfgActions .*/
    case ARTL_ART_cfgAlt_58: 
            ARTRD_cfgAlt(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgActions(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void ARTRD_cfgAltNoAction(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgAltNoAction ::= cfgSeq cfgSlot . cfgPrim cfgSlot */
    case ARTL_ART_cfgAltNoAction_110: 
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_cfgSeq(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgSlot(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgAltNoAction ::= cfgSeq cfgSlot cfgPrim . cfgSlot */
    case ARTL_ART_cfgAltNoAction_112: 
      ARTRD_cfgAltNoAction(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgPrim(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgAltNoAction ::= cfgSeq cfgSlot cfgPrim cfgSlot .*/
    case ARTL_ART_cfgAltNoAction_114: 
            ARTRD_cfgAltNoAction(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgSlot(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void ARTRD_cfgAlts(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgAlts ::= cfgAlt .*/
    case ARTL_ART_cfgAlts_26: 
            ARTRD_cfgAlts(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgAlt(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgAlts ::= cfgAlt '|'  . cfgAlts */
    case ARTL_ART_cfgAlts_32: 
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_cfgAlt(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
                        break;
    /*cfgAlts ::= cfgAlt '|'  cfgAlts .*/
    case ARTL_ART_cfgAlts_34: 
            ARTRD_cfgAlts(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = false; artNewParent = artParent;
      ARTRD_cfgAlts(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void ARTRD_cfgAnnotation(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgAnnotation ::= cfgFoldUnder .*/
    case ARTL_ART_cfgAnnotation_100: 
            ARTRD_cfgAnnotation(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgFoldUnder(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgAnnotation ::= cfgFoldOver .*/
    case ARTL_ART_cfgAnnotation_104: 
            ARTRD_cfgAnnotation(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgFoldOver(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void ARTRD_cfgAssignment(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgAssignment ::= cfgAttribute ':='  . term */
    case ARTL_ART_cfgAssignment_270: 
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_cfgAttribute(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
                        break;
    /*cfgAssignment ::= cfgAttribute ':='  term .*/
    case ARTL_ART_cfgAssignment_272: 
            ARTRD_cfgAssignment(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_term(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void ARTRD_cfgAttribute(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgAttribute ::= &ID  '.'  . &ID  */
    case ARTL_ART_cfgAttribute_284: 
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
                        break;
    /*cfgAttribute ::= &ID  '.'  &ID  .*/
    case ARTL_ART_cfgAttribute_286: 
            ARTRD_cfgAttribute(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default: ; }}}
}

public void ARTRD_cfgBuiltinTerminal(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgBuiltinTerminal ::= '&'  &ID  .*/
    case ARTL_ART_cfgBuiltinTerminal_234: 
                        artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default: ; }}}
}

public void ARTRD_cfgCaseSensitiveTerminal(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgCaseSensitiveTerminal ::= &STRING_PLAIN_SQ  .*/
    case ARTL_ART_cfgCaseSensitiveTerminal_228: 
            ARTRD_cfgCaseSensitiveTerminal(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default: ; }}}
}

public void ARTRD_cfgDoFirst(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgDoFirst ::= '('  cfgAlts . ')'  */
    case ARTL_ART_cfgDoFirst_162: 
                  artNewWriteable = false; artNewParent = artParent;
      ARTRD_cfgAlts(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgDoFirst ::= '('  cfgAlts ')'  .*/
    case ARTL_ART_cfgDoFirst_164: 
            ARTRD_cfgDoFirst(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
                  break;
        default: ; }}}
}

public void ARTRD_cfgElems(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgElems ::= cfgActions cfgExtended .*/
    case ARTL_ART_cfgElems_88: 
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_cfgActions(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgExtended(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgElems ::= cfgActions cfgExtended . cfgElems */
    case ARTL_ART_cfgElems_94: 
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_cfgActions(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgExtended(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgElems ::= cfgActions cfgExtended cfgElems .*/
    case ARTL_ART_cfgElems_96: 
            ARTRD_cfgElems(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = false; artNewParent = artParent;
      ARTRD_cfgElems(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void ARTRD_cfgEpsilon(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgEpsilon ::= '#'  .*/
    case ARTL_ART_cfgEpsilon_76: 
            ARTRD_cfgEpsilon(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
                  break;
        default: ; }}}
}

public void ARTRD_cfgEpsilonCarrier(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgEpsilonCarrier ::= cfgEpsilon cfgAnnotation .*/
    case ARTL_ART_cfgEpsilonCarrier_82: 
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_cfgEpsilon(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
            artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgAnnotation(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void ARTRD_cfgEquation(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgEquation ::= cfgAttribute '='  . term */
    case ARTL_ART_cfgEquation_262: 
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_cfgAttribute(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
                        break;
    /*cfgEquation ::= cfgAttribute '='  term .*/
    case ARTL_ART_cfgEquation_264: 
            ARTRD_cfgEquation(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_term(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void ARTRD_cfgExtended(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgExtended ::= cfgPrim .*/
    case ARTL_ART_cfgExtended_134: 
            ARTRD_cfgExtended(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgPrim(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgExtended ::= cfgPrim cfgAnnotation .*/
    case ARTL_ART_cfgExtended_140: 
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_cfgPrim(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
            artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgAnnotation(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgExtended ::= cfgDoFirst .*/
    case ARTL_ART_cfgExtended_144: 
            ARTRD_cfgExtended(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgDoFirst(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgExtended ::= cfgOptional .*/
    case ARTL_ART_cfgExtended_148: 
            ARTRD_cfgExtended(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgOptional(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgExtended ::= cfgKleene .*/
    case ARTL_ART_cfgExtended_152: 
            ARTRD_cfgExtended(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgKleene(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgExtended ::= cfgPositive .*/
    case ARTL_ART_cfgExtended_156: 
            ARTRD_cfgExtended(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgPositive(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void ARTRD_cfgFoldOver(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgFoldOver ::= '^^'  .*/
    case ARTL_ART_cfgFoldOver_220: 
            ARTRD_cfgFoldOver(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
                  break;
        default: ; }}}
}

public void ARTRD_cfgFoldUnder(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgFoldUnder ::= '^'  .*/
    case ARTL_ART_cfgFoldUnder_216: 
            ARTRD_cfgFoldUnder(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
                  break;
        default: ; }}}
}

public void ARTRD_cfgInsert(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgInsert ::= '^+'  &ID  .*/
    case ARTL_ART_cfgInsert_278: 
                        artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default: ; }}}
}

public void ARTRD_cfgKleene(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgKleene ::= '('  cfgAlts . ')'  '*'  */
    case ARTL_ART_cfgKleene_186: 
                  artNewWriteable = false; artNewParent = artParent;
      ARTRD_cfgAlts(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgKleene ::= '('  cfgAlts ')'  . '*'  */
    case ARTL_ART_cfgKleene_188: 
      ARTRD_cfgKleene(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
                  break;
    /*cfgKleene ::= '('  cfgAlts ')'  '*'  .*/
    case ARTL_ART_cfgKleene_190: 
            ARTRD_cfgKleene(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
                  break;
    /*cfgKleene ::= cfgAltNoAction '*'  .*/
    case ARTL_ART_cfgKleene_196: 
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_cfgAltNoAction(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
                        break;
        default: ; }}}
}

public void ARTRD_cfgLHS(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgLHS ::= &ID  .*/
    case ARTL_ART_cfgLHS_22: 
            ARTRD_cfgLHS(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default: ; }}}
}

public void ARTRD_cfgNonterminal(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgNonterminal ::= &ID  .*/
    case ARTL_ART_cfgNonterminal_224: 
            ARTRD_cfgNonterminal(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default: ; }}}
}

public void ARTRD_cfgOptional(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgOptional ::= '('  cfgAlts . ')'  '?'  */
    case ARTL_ART_cfgOptional_170: 
                  artNewWriteable = false; artNewParent = artParent;
      ARTRD_cfgAlts(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgOptional ::= '('  cfgAlts ')'  . '?'  */
    case ARTL_ART_cfgOptional_172: 
      ARTRD_cfgOptional(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
                  break;
    /*cfgOptional ::= '('  cfgAlts ')'  '?'  .*/
    case ARTL_ART_cfgOptional_174: 
            ARTRD_cfgOptional(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
                  break;
    /*cfgOptional ::= cfgAltNoAction '?'  .*/
    case ARTL_ART_cfgOptional_180: 
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_cfgAltNoAction(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
                        break;
        default: ; }}}
}

public void ARTRD_cfgPositive(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgPositive ::= '('  cfgAlts . ')'  '+'  */
    case ARTL_ART_cfgPositive_202: 
                  artNewWriteable = false; artNewParent = artParent;
      ARTRD_cfgAlts(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgPositive ::= '('  cfgAlts ')'  . '+'  */
    case ARTL_ART_cfgPositive_204: 
      ARTRD_cfgPositive(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
                  break;
    /*cfgPositive ::= '('  cfgAlts ')'  '+'  .*/
    case ARTL_ART_cfgPositive_206: 
            ARTRD_cfgPositive(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
                  break;
    /*cfgPositive ::= cfgAltNoAction '+'  .*/
    case ARTL_ART_cfgPositive_212: 
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_cfgAltNoAction(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
                        break;
        default: ; }}}
}

public void ARTRD_cfgPrim(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgPrim ::= cfgNonterminal .*/
    case ARTL_ART_cfgPrim_122: 
            ARTRD_cfgPrim(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgNonterminal(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgPrim ::= cfgCaseSensitiveTerminal .*/
    case ARTL_ART_cfgPrim_126: 
            ARTRD_cfgPrim(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgCaseSensitiveTerminal(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgPrim ::= cfgBuiltinTerminal .*/
    case ARTL_ART_cfgPrim_130: 
            ARTRD_cfgPrim(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgBuiltinTerminal(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void ARTRD_cfgRule(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgRule ::= cfgLHS '::='  . cfgAlts */
    case ARTL_ART_cfgRule_16: 
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_cfgLHS(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
                        break;
    /*cfgRule ::= cfgLHS '::='  cfgAlts .*/
    case ARTL_ART_cfgRule_18: 
            ARTRD_cfgRule(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = false; artNewParent = artParent;
      ARTRD_cfgAlts(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void ARTRD_cfgRules(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgRules ::= cfgRule .*/
    case ARTL_ART_cfgRules_4: 
            ARTRD_cfgRules(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgRule(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*cfgRules ::= cfgRule cfgRules .*/
    case ARTL_ART_cfgRules_10: 
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_cfgRule(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable);
            artNewWriteable = false; artNewParent = artParent;
      ARTRD_cfgRules(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void ARTRD_cfgSeq(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgSeq ::= # .*/
    case ARTL_ART_cfgSeq_62: 
            ARTRD_cfgSeq(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
                  break;
        default: ; }}}
}

public void ARTRD_cfgSlot(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*cfgSlot ::= # .*/
    case ARTL_ART_cfgSlot_118: 
            ARTRD_cfgSlot(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
                  break;
        default: ; }}}
}

public void ARTRD_term(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*term ::= cfgAttribute .*/
    case ARTL_ART_term_290: 
            ARTRD_term(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = artWriteable; artNewParent = artParent;      if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_cfgAttribute(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*term ::= &INTEGER  .*/
    case ARTL_ART_term_294: 
            ARTRD_term(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
            if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*term ::= &REAL  .*/
    case ARTL_ART_term_298: 
            ARTRD_term(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
            if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*term ::= &STRING_DQ  .*/
    case ARTL_ART_term_302: 
            ARTRD_term(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
            if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*term ::= &ID  '('  . ')'  */
    case ARTL_ART_term_308: 
            if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
                        break;
    /*term ::= &ID  '('  ')'  .*/
    case ARTL_ART_term_310: 
            ARTRD_term(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
                  break;
    /*term ::= &ID  '('  . arguments ')'  */
    case ARTL_ART_term_316: 
            if (artWriteable) artParent.setPayload(new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
                        break;
    /*term ::= &ID  '('  arguments . ')'  */
    case ARTL_ART_term_318: 
      ARTRD_term(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_arguments(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*term ::= &ID  '('  arguments ')'  .*/
    case ARTL_ART_term_320: 
            ARTRD_term(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
                  break;
        default: ; }}}
}

public void artEvaluate(ARTGLLRDTHandle artElement, ARTGLLAttributeBlock artAttributes, ARTGLLRDTVertex artParent, Boolean artWriteable)  {
  switch (artSPPFNodeLabel(artElement.element)) {
    case ARTL_ART_arguments: ARTRD_arguments(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgAction: ARTRD_cfgAction(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgActionSeq: ARTRD_cfgActionSeq(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgActions: ARTRD_cfgActions(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgAlt: ARTRD_cfgAlt(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgAltNoAction: ARTRD_cfgAltNoAction(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgAlts: ARTRD_cfgAlts(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgAnnotation: ARTRD_cfgAnnotation(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgAssignment: ARTRD_cfgAssignment(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgAttribute: ARTRD_cfgAttribute(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgBuiltinTerminal: ARTRD_cfgBuiltinTerminal(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgCaseSensitiveTerminal: ARTRD_cfgCaseSensitiveTerminal(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgDoFirst: ARTRD_cfgDoFirst(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgElems: ARTRD_cfgElems(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgEpsilon: ARTRD_cfgEpsilon(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgEpsilonCarrier: ARTRD_cfgEpsilonCarrier(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgEquation: ARTRD_cfgEquation(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgExtended: ARTRD_cfgExtended(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgFoldOver: ARTRD_cfgFoldOver(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgFoldUnder: ARTRD_cfgFoldUnder(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgInsert: ARTRD_cfgInsert(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgKleene: ARTRD_cfgKleene(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgLHS: ARTRD_cfgLHS(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgNonterminal: ARTRD_cfgNonterminal(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgOptional: ARTRD_cfgOptional(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgPositive: ARTRD_cfgPositive(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgPrim: ARTRD_cfgPrim(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgRule: ARTRD_cfgRule(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgRules: ARTRD_cfgRules(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgSeq: ARTRD_cfgSeq(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_cfgSlot: ARTRD_cfgSlot(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_term: ARTRD_term(artElement.element, artParent, artWriteable); break;
  }
}

};
