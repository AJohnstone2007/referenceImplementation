package com.sun.util.reentrant;
import java.lang.ref.Reference;
public class ReentrantContext {
byte usage = ReentrantContextProvider.USAGE_TL_INACTIVE;
Reference<? extends ReentrantContext> reference = null;
}
