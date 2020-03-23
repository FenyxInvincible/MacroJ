package local.autohotkey.utils.eso;

import local.autohotkey.utils.ScreenPicker;

public class EsoUtils {
    private EsoUtils() {
    }

    public static boolean isFirstBar(){
        return ScreenPicker.pickDwordColor(1149, 1399) != ScreenPicker.DWORD_WHITE;
    }
}
