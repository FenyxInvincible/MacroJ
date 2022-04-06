package local.autohotkey.data;

import local.autohotkey.data.Key;
import lombok.Data;

@Data
public class UseKeyData {
    private Key key;
    private int delay;
}
