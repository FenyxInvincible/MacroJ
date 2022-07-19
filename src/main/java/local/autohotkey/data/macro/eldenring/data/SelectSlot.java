package local.autohotkey.data.macro.eldenring.data;

import local.autohotkey.data.Key;
import local.autohotkey.data.UseKeyData;
import lombok.Data;

import java.util.List;

@Data
public class SelectSlot {
    private final int position;
    private final Key changeKey;
    private final List<UseKeyData> useKey;
    //executes before slot become active
    private final List<UseKeyData> onActive;
    //executes before slot become active
    private final List<UseKeyData> onInactive;
}
