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
@Slf4j
@Service
public class MacroKeyListener extends KeyEventReceiver {
    private final KeyManager keyManager;
    private final MacroFactory macroFactory;
    private Map<String, Key> collector = new HashMap<>();
    public MacroKeyListener(
            KeyManager keyManager,
            MacroFactory macroFactory,
            KeyHookManager keyHookManager
    ){
        super(keyHookManager);
        this.keyManager = keyManager;
        this.macroFactory = macroFactory;
    }

    public boolean onKeyUpdate(PressState pressState, int vkCode) {
        return this.onKeyUpdate(SystemState.STANDARD, pressState, null, vkCode);
    }

    @Override
    public boolean onKeyUpdate(SystemState systemState, PressState pressState, WinUser.KBDLLHOOKSTRUCT info, int vkCode) {
        log.debug("{} {}", vkCode);
        if (info!= null && info.dwExtraInfo.intValue() == Keyboard.IS_MACRO) {
            //ignore macro input
            return false;
        }

        Key key = keyManager.findKeyByKeyCode(vkCode);

        if (key == null) {
            log.error("Unknown code: {}", vkCode);
            return false;
        }

        switch (pressState){
            case DOWN:
                return keyPressed(key, pressState);
            case UP:
                return keyReleased(key, pressState);
            default:
                log.debug("Unexpected action nativeKeyTyped: {}", vkCode);
                return false;
        }
    }
    private boolean keyPressed(Key key, PressState pressState) {
        //we will ignore key event when resend appears due to key holding
        boolean ignore = key.isPressed();

        key.pressed();
        MacroKey mk = macroFactory.getMacro(key);
        if (mk != null) {
            if(ignore) {
                return true;
            }
            return macroFactory.execute(mk, pressState);
        }
        return false;
    }
    private boolean keyReleased(Key key, PressState pressState) {
        key.released();

        MacroKey mk = macroFactory.getMacro(key);
        if (mk != null) {
            return macroFactory.execute(mk, pressState);
        }

        return false;
    }
}
