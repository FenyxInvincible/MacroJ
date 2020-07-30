package local.autohotkey;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import local.autohotkey.jna.hook.key.KeyEventReceiver;
import local.autohotkey.jna.hook.key.KeyHookManager;
import local.autohotkey.jna.hook.mouse.MouseEventReceiver;
import local.autohotkey.jna.hook.mouse.MouseHookManager;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationRunner {

    private final ExecutorService executorService;
    private final KeyEventReceiver keyListener;
    private final MouseEventReceiver mouseEventReceiver;
    private final KeyHookManager keyHookManager;
    private final MouseHookManager mouseHookManager;

    public void run() {
        keyHookManager.hook(keyListener);
        mouseHookManager.hook(mouseEventReceiver);
        log.info("Application started");
    }
}
