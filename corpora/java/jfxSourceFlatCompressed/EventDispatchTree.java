package com.sun.javafx.event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
public interface EventDispatchTree extends EventDispatchChain {
EventDispatchTree createTree();
EventDispatchTree mergeTree(EventDispatchTree tree);
@Override
EventDispatchTree append(EventDispatcher eventDispatcher);
@Override
EventDispatchTree prepend(EventDispatcher eventDispatcher);
}
