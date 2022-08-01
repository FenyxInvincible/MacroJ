package local.macroj.service;
import com.sun.jna.platform.win32.WinUser;
import lombok.extern.slf4j.Slf4j;
import local.macroj.jna.hook.key.KeyEventReceiver;
import local.macroj.jna.hook.key.KeyHookManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Lazy
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
