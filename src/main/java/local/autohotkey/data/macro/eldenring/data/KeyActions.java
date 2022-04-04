package local.autohotkey.data.macro.eldenring.data;

import java.util.Arrays;

public enum KeyActions {
    Pressed,
    Released,
    Unknown;

    public static KeyActions of(String str) {
        return Arrays.stream(KeyActions.values())
                .filter(e -> e.name().equalsIgnoreCase(str))
                .findAny().orElse(Unknown);
    }
}
