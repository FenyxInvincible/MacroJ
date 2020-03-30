package local.autohotkey.service;

import local.autohotkey.data.Key;
import lombok.extern.slf4j.Slf4j;
import me.coley.simplejna.hook.key.KeyEventReceiver;
import me.coley.simplejna.hook.key.KeyHookManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MacroKeyListener extends KeyEventReceiver {

    private final KeyManager keyManager;
    private final MacroFactory macroFactory;

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
        log.debug("{} {}", vkCode, key);
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
            macroFactory.execute(key, pressState);
            return true;
        }
        return false;
    }

    private boolean keyReleased(Key key, PressState pressState) {
        if (macroFactory.hasMacro(key)) {

            macroFactory.execute(key, pressState);
            return true;
        }
        key.released();
        return false;
    }
}
