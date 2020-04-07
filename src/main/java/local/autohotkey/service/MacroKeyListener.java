package local.autohotkey.service;
import com.sun.jna.platform.unix.X11;
import local.autohotkey.data.Key;
import lombok.extern.slf4j.Slf4j;
import local.autohotkey.jna.hook.key.KeyEventReceiver;
import local.autohotkey.jna.hook.key.KeyHookManager;
import org.springframework.stereotype.Service;
import java.awt.event.KeyEvent;
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
    @Override
    public boolean onKeyUpdate(SystemState systemState, PressState pressState, int time, int vkCode) {
        Key key = keyManager.findKeyByKeyCode(vkCode);
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
