package local.autohotkey.service;
import com.sun.jna.platform.win32.WinUser;
import local.autohotkey.data.Key;
import local.autohotkey.data.MacroKey;
import local.autohotkey.jna.Keyboard;
import lombok.extern.slf4j.Slf4j;
import local.autohotkey.jna.hook.key.KeyEventReceiver;
import local.autohotkey.jna.hook.key.KeyHookManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static local.autohotkey.jna.Windows.IS_MACRO;

@Slf4j
@Service
public class KeyEventListener extends KeyEventReceiver {

    private final MacroListener macroListener;

    public KeyEventListener(
            KeyHookManager keyHookManager,
            MacroListener macroListener
    ){
        super(keyHookManager);
        this.macroListener = macroListener;
    }

    @Override
    public boolean onKeyUpdate(SystemState systemState, PressState pressState, WinUser.KBDLLHOOKSTRUCT info, int vkCode) {
        return macroListener.onUpdate(MacroListener.EventState.valueOf(pressState), info, vkCode);
    }
}
