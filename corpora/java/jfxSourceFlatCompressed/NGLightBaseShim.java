package com.sun.javafx.sg.prism;
public class NGLightBaseShim {
public static boolean affects(NGPointLight lightPeer, NGShape3D shapePeer) {
return lightPeer.affects(shapePeer);
}
}