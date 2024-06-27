package local.macroj.data.macro.eldenring.data;

import local.macroj.data.Key;
import lombok.Data;

@Data
public class Settings {
    private Setting spells;
    private Setting consumables;

    @Data
    public static class Setting{
        private Key key;
        private int amount;
        private int keyDelay = 32;
        private int changeDelay = 32;
    }
}
