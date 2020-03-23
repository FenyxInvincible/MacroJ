package local.autohotkey.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnativehook.NativeInputEvent;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Slf4j
@Service
@RequiredArgsConstructor
public class MacroKeyListener implements NativeKeyListener {

    private final KeyManager keyManager;
    private final MacroFactory macroFactory;

    public void nativeKeyPressed(NativeKeyEvent e) {

        if (keyManager.findKeyByKeyCode(e.getKeyCode()).isPressed()) {
            return;
        }

        keyManager.findKeyByKeyCode(e.getKeyCode()).pressed();

        if (macroFactory.hasMacro(e.getKeyCode())) {
            suppressEvent(e);
            macroFactory.execute(e);
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
        if (!keyManager.findKeyByKeyCode(e.getKeyCode()).isPressed()) {
            return;
        }

        if (macroFactory.hasMacro(e.getKeyCode())) {
            suppressEvent(e);
            macroFactory.execute(e);
        }
        keyManager.findKeyByKeyCode(e.getKeyCode()).released();
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
        log.debug("Unexpected action nativeKeyTyped: " + nativeKeyEvent.paramString());
    }

    private void suppressEvent(NativeKeyEvent e) {
        try {
            Field f = NativeInputEvent.class.getDeclaredField("reserved");
            f.setAccessible(true);
            f.setShort(e, (short) 0x01);
            //log.debug("Key suppressed {}", NativeKeyEvent.getKeyText(e.getKeyCode()));
        } catch (Exception ex) {
            log.error("Unable to suppress key: ", ex);
        }
    }
}
