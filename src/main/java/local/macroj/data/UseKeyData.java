package local.macroj.data;

import lombok.Data;

@Data
public class UseKeyData {
    private Key key;
    private int delay;
    private Key.Action action;
}
