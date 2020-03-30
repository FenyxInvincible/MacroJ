package local.autohotkey;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.coley.simplejna.hook.key.KeyEventReceiver;
import me.coley.simplejna.hook.key.KeyHookManager;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationRunner {

    private final ExecutorService executorService;
    private final KeyEventReceiver keyListener;
    private final KeyHookManager keyHookManager;

    public void run() {
        keyHookManager.hook(keyListener);
        log.info("Application started");
    }
}
