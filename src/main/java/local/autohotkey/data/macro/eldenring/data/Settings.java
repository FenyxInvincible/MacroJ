package local.autohotkey.data.macro.eldenring.data;

import local.autohotkey.data.Key;
import lombok.Data;

@Data
public class Settings {
    private Setting spells;
    private Setting consumables;

    @Data
    public static class Setting{
        private Key key;
        private int amount;
    }
}
