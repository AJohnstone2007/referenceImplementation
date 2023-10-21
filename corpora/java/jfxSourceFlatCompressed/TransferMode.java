package javafx.scene.input;
public enum
TransferMode {
COPY,
MOVE,
LINK;
public static final TransferMode[] ANY = { COPY, MOVE, LINK };
public static final TransferMode[] COPY_OR_MOVE = { COPY, MOVE };
public static final TransferMode[] NONE = { };
}
