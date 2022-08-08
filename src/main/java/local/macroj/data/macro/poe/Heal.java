package local.macroj.data.macro.poe;

import local.macroj.data.Key;
import local.macroj.data.MacroKey;
import local.macroj.data.macro.Macro;
import local.macroj.sender.Sender;
import local.macroj.service.KeyManager;
import local.macroj.utils.ScreenPicker;
import local.macroj.utils.eso.Locks;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Scope("prototype")
public class Heal implements Macro {
    private final Sender sender;
    private final KeyManager keys;
    private final Locks locks;
    private Key key;
    private static AtomicBoolean isInterrupted = new AtomicBoolean(false);
    private static AtomicBoolean isStarted = new AtomicBoolean(false);

    @Override
    public void setParams(Object param, MacroKey self) {
        key = keys.findKeyByText("1");
    }

    @Override
    public void run() {
        if (!isStarted.get()) {
            Executors.newSingleThreadExecutor().execute(new HealDaemon());
            isStarted.set(true);
            isInterrupted.set(false);
        } else {
            isStarted.set(false);
            isInterrupted.set(true);
        }
    }

    class HealDaemon implements Runnable {

        @Override
        public void run() {
            while (!isInterrupted.get()) {
                try {
                    if (ScreenPicker.pickDwordColor(140, 1247) != 2693048) {
                        sender.sendKey(key, 30);
                    }
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isInterrupted.set(false);
        }
    }
}
