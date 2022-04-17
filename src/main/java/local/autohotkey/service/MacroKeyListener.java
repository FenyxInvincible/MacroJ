package local.autohotkey.service;
import com.sun.jna.platform.win32.WinUser;
import local.autohotkey.data.Key;
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
        //log.info("{}", info);
        if (info!= null && info.dwExtraInfo.intValue() == Keyboard.IS_MACRO) {
            //ignore macro input
            return false;
        }

        Key key = keyManager.findKeyByKeyCode(vkCode);

        if (key == null) {
            log.error("Unknown code: {}", vkCode);
            return false;
        }

        //repeating key
        /*if(key.isPressed() && pressState == PressState.DOWN) {
            return true;
        }*/

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
        if (key.isPressed()) {
            return false;
        }
        key.pressed();
        if (macroFactory.hasMacro(key)) {
            return macroFactory.execute(key, pressState);
        }
        return false;
    }
    private boolean keyReleased(Key key, PressState pressState) {
        key.released();

        if (macroFactory.hasMacro(key)) {
            return macroFactory.execute(key, pressState);
        }

        return false;
    }
}
