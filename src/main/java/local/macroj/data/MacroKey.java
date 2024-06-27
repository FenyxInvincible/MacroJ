package local.macroj.data;

import lombok.Data;

import java.util.Objects;

@Data
public class MacroKey {
    private final Key key;
    private final Key modifier;

    @Override
    public String toString() {
        return (!Objects.isNull(modifier) ? modifier.getKeyText() + "+" : "")
            + key.getKeyText();
    }
}
