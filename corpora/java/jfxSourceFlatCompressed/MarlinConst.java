package com.sun.marlin;
public interface MarlinConst {
static final boolean ENABLE_LOGS = MarlinProperties.isLoggingEnabled();
static final boolean USE_LOGGER = ENABLE_LOGS && MarlinProperties.isUseLogger();
static final boolean LOG_CREATE_CONTEXT = ENABLE_LOGS
&& MarlinProperties.isLogCreateContext();
static final boolean LOG_UNSAFE_MALLOC = ENABLE_LOGS
&& MarlinProperties.isLogUnsafeMalloc();
static final boolean DO_CHECK_UNSAFE = false;
static final boolean DO_STATS = ENABLE_LOGS && MarlinProperties.isDoStats();
static final boolean DO_MONITORS = false;
static final boolean DO_CHECKS = ENABLE_LOGS && MarlinProperties.isDoChecks();
static final boolean DO_AA_RANGE_CHECK = false;
static final boolean DO_LOG_WIDEN_ARRAY = ENABLE_LOGS && false;
static final boolean DO_LOG_OVERSIZE = ENABLE_LOGS && false;
static final boolean DO_TRACE = ENABLE_LOGS && false;
static final boolean DO_FLUSH_STATS = true;
static final boolean DO_FLUSH_MONITORS = true;
static final boolean USE_DUMP_THREAD = false;
static final long DUMP_INTERVAL = 5000L;
static final boolean DO_CLEAN_DIRTY = false;
static final boolean USE_SIMPLIFIER = MarlinProperties.isUseSimplifier();
static final boolean USE_PATH_SIMPLIFIER = MarlinProperties.isUsePathSimplifier();
static final boolean DO_CLIP_SUBDIVIDER = MarlinProperties.isDoClipSubdivider();
static final boolean DO_LOG_BOUNDS = ENABLE_LOGS && false;
static final boolean DO_LOG_CLIP = ENABLE_LOGS && false;
static final int INITIAL_PIXEL_WIDTH
= MarlinProperties.getInitialPixelWidth();
static final int INITIAL_PIXEL_HEIGHT
= MarlinProperties.getInitialPixelHeight();
static final int INITIAL_ARRAY = 256;
static final int INITIAL_AA_ARRAY = INITIAL_PIXEL_WIDTH;
static final int INITIAL_EDGES_COUNT = MarlinProperties.getInitialEdges();
static final int INITIAL_EDGES_CAPACITY = INITIAL_EDGES_COUNT * 24;
static final int INITIAL_CROSSING_COUNT = INITIAL_EDGES_COUNT >> 2;
static final byte BYTE_0 = (byte) 0;
public static final int SUBPIXEL_LG_POSITIONS_X
= MarlinProperties.getSubPixel_Log2_X();
public static final int SUBPIXEL_LG_POSITIONS_Y
= MarlinProperties.getSubPixel_Log2_Y();
public static final int MIN_SUBPIXEL_LG_POSITIONS
= Math.min(SUBPIXEL_LG_POSITIONS_X, SUBPIXEL_LG_POSITIONS_Y);
public static final int SUBPIXEL_POSITIONS_X = 1 << (SUBPIXEL_LG_POSITIONS_X);
public static final int SUBPIXEL_POSITIONS_Y = 1 << (SUBPIXEL_LG_POSITIONS_Y);
public static final float MIN_SUBPIXELS = 1 << MIN_SUBPIXEL_LG_POSITIONS;
static final int INITIAL_BUCKET_ARRAY
= INITIAL_PIXEL_HEIGHT * SUBPIXEL_POSITIONS_Y;
public static final int MAX_AA_ALPHA
= (SUBPIXEL_POSITIONS_X * SUBPIXEL_POSITIONS_Y);
public static final int BLOCK_SIZE_LG = MarlinProperties.getBlockSize_Log2();
public static final int BLOCK_SIZE = 1 << BLOCK_SIZE_LG;
static final boolean ENABLE_BLOCK_FLAGS = MarlinProperties.isUseTileFlags();
static final boolean ENABLE_BLOCK_FLAGS_HEURISTICS = MarlinProperties.isUseTileFlagsWithHeuristics();
static final boolean FORCE_RLE = MarlinProperties.isForceRLE();
static final boolean FORCE_NO_RLE = MarlinProperties.isForceNoRLE();
static final int RLE_MIN_WIDTH
= Math.max(BLOCK_SIZE, MarlinProperties.getRLEMinWidth());
public static final int WIND_EVEN_ODD = 0;
public static final int WIND_NON_ZERO = 1;
public static final int JOIN_MITER = 0;
public static final int JOIN_ROUND = 1;
public static final int JOIN_BEVEL = 2;
public static final int CAP_BUTT = 0;
public static final int CAP_ROUND = 1;
public static final int CAP_SQUARE = 2;
static final int OUTCODE_TOP = 1;
static final int OUTCODE_BOTTOM = 2;
static final int OUTCODE_LEFT = 4;
static final int OUTCODE_RIGHT = 8;
static final int OUTCODE_MASK_T_B = OUTCODE_TOP | OUTCODE_BOTTOM;
static final int OUTCODE_MASK_L_R = OUTCODE_LEFT | OUTCODE_RIGHT;
static final int OUTCODE_MASK_T_B_L_R = OUTCODE_MASK_T_B | OUTCODE_MASK_L_R;
}
