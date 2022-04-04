package local.autohotkey.key;

import local.autohotkey.data.macro.eldenring.data.KeyActions;

import java.util.Arrays;

public enum MouseKey {
    LMB, RMB, MMB, UNKNOWN;

    public static MouseKey of(String str) {
        return Arrays.stream(MouseKey.values())
                .filter(e -> e.name().equalsIgnoreCase(str))
                .findAny().orElse(UNKNOWN);
    }
}
