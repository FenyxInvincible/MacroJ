package local.macroj.service;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinUser;
import local.macroj.data.Key;
import local.macroj.data.MacroKey;
import local.macroj.jna.hook.key.KeyEventReceiver;
import local.macroj.jna.hook.mouse.struct.MOUSEHOOKSTRUCT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import static local.macroj.jna.Windows.IS_MACRO;

/**
 * Aggregated listener for keyboard and mouse events
*/
@Service
@Slf4j
@RequiredArgsConstructor
@Lazy
public class MacroListener {

    private final KeyManager keyManager;
    private final MacroFactory macroFactory;

    /**
     *
     * @param state UP, DOWN or UNKNOWN
     * @param info KBDLLHOOKSTRUCT or MOUSEHOOKSTRUCT
     * @param vkCode key code
     * @return
     */
    public boolean onUpdate(EventState state, Structure info, int vkCode) {
        log.debug(
                "vkCode: {} pressState: {} ",
                vkCode,
                state
        );

        //ignore input for event sent from macro
        if (info instanceof WinUser.KBDLLHOOKSTRUCT) {
            WinUser.KBDLLHOOKSTRUCT kbdllInfo = (WinUser.KBDLLHOOKSTRUCT) info;
            if (kbdllInfo.dwExtraInfo.intValue() == IS_MACRO) {
                log.debug("dwExtraInfo IS_MACRO value is found. Skipping handling");
                return false;
            }
        } else if (info instanceof MOUSEHOOKSTRUCT) {
            MOUSEHOOKSTRUCT mouseInfo = (MOUSEHOOKSTRUCT) info;
            if (mouseInfo.dwExtraInfo.intValue() == IS_MACRO) {
                log.debug("dwExtraInfo IS_MACRO value is found. Skipping handling");
                return false;
            }
        } else {//potentially hardware input exists and may be added in future
            throw new IllegalArgumentException("Unknown structure: " + info.getClass().getSimpleName());
        }

        Key key = keyManager.findKeyByKeyCode(vkCode);

        if (key == null) {
            log.error("Unknown code: {}", vkCode);
            return false;
        }

        switch (state){
            case DOWN:
                return keyPressed(key, state);
            case UP:
                return keyReleased(key, state);
            default:
                log.debug("Unexpected action nativeKeyTyped: {}", vkCode);
                return false;
        }
    }
    private boolean keyPressed(Key key, EventState pressState) {
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
    private boolean keyReleased(Key key, EventState pressState) {
        key.released();

        MacroKey mk = macroFactory.getMacro(key);
        if (mk != null) {
            return macroFactory.execute(mk, pressState);
        }

        return false;
    }

    public static enum EventState {
        UP, DOWN, UNKNOWN;

        public static EventState valueOf(KeyEventReceiver.PressState state) {
            switch (state) {
                case UP:
                    return UP;
                case DOWN:
                    return DOWN;
                default:
                    return UNKNOWN;
            }
        }
    }
}
