package local.macroj.macro;

import local.macroj.data.macro.Macro;
import local.macroj.service.MacroListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class MacroThreads {

    private MacroThread onPressThread = new MacroThread();
    private MacroThread onReleaseThread = new MacroThread();

    public void run(Macro macro, MacroListener.EventState eventType) {

        MacroThread macroThread = null;
        switch (eventType){
            case DOWN:
                macroThread = onPressThread;
                break;
            case UP:
                macroThread = onReleaseThread;
                break;
            default:
                throw new IllegalArgumentException("Unknown event type " + eventType);
        }

        if (macroThread.isFree()) {
            log.debug("Thread is free {}", macro.getClass());
            macroThread.execute(macro);
        } else {
            log.debug("Thread is not free. Skipping");
        }
    }

    private class MacroThread {
        private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        private AtomicBoolean isFree = new AtomicBoolean(true);

        public boolean isFree() {
            return isFree.get();
        }

        public void execute(final Macro macro) {
            isFree.set(false);
            executor.execute(() -> {
                log.debug("Obtaining thread for {}", macro.getClass());
                macro.run();
                isFree.set(true);
                log.debug("Releasing thread for {}", macro.getClass());
            });
        }
    }
}
