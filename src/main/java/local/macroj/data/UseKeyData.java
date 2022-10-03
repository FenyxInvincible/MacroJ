package local.macroj.data;

import lombok.Data;
import lombok.NonNull;

@Data
public class UseKeyData {
    @NonNull
    private final Key key;
    private final int delay;
    private final Key.Action action;
}
