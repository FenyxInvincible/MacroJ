package local.macroj;

import local.macroj.data.RuntimeConfig;
import local.macroj.service.KeyManager;
import local.macroj.service.MacroFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import local.macroj.jna.hook.key.KeyEventReceiver;
import local.macroj.jna.hook.key.KeyHookManager;
import local.macroj.jna.hook.mouse.MouseEventReceiver;
import local.macroj.jna.hook.mouse.MouseHookManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
@Lazy
public class ProfileRunner {

    private final KeyEventReceiver keyListener;
    private final MouseEventReceiver mouseEventReceiver;
    private final KeyHookManager keyHookManager;
    private final MouseHookManager mouseHookManager;

    private final KeyManager keyManager;

    private final MacroFactory macroFactory;

    private final RuntimeConfig config;

    @PostConstruct
    public void init() {
        keyHookManager.hook(keyListener);
        mouseHookManager.hook(mouseEventReceiver);
    }

    public boolean run() {
        try {
            macroFactory.init();
            log.info("Profile is running: {}", config.getCurrentProfile());
            return true;
        } catch (Exception e) {
            log.error("Could not run profile: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean stop() {
        try {
            String pf = config.getCurrentProfile();
            macroFactory.stop();
            keyManager.cleanVirtualKeys();
            log.info("Profile is stopped: {}", pf);
            return true;
        } catch (Exception e) {
            log.error("Could not stop profile: {}", e.getMessage(), e);
            return false;
        }
    }
}
