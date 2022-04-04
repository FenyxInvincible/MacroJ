package local.autohotkey.data.macro.eldenring.data;

import local.autohotkey.data.Key;
import lombok.Data;

@Data
public class SelectSlot {
    private final int position;
    private final Key changeKey;
    private final Key useKey;
}
