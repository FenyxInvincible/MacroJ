package local.autohotkey;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationRunner {

    private final ExecutorService executorService;
    private final NativeKeyListener keyListener;

    public void run() throws NativeHookException {
        GlobalScreen.setEventDispatcher(executorService);
        GlobalScreen.registerNativeHook();

        GlobalScreen.addNativeKeyListener(keyListener);
        log.info("Application started");
        log.debug("Application asdasd");
    }
}
