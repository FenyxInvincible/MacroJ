package local.macroj.key;

import java.util.Arrays;

public enum MouseKey {
    LMB, RMB, MMB, MOUSE_SCROLL, UNKNOWN;

    public static MouseKey of(String str) {
        return Arrays.stream(MouseKey.values())
                .filter(e -> e.name().equalsIgnoreCase(str))
                .findAny().orElse(UNKNOWN);
    }
}
