package local.macroj.data.macro;

import local.macroj.data.MacroKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

@Component
@Slf4j
public class DeadlockDetection implements Macro {
    @Override
    public void setParams(Object param, MacroKey self) {

    }

    @Override
    public void run() {

        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long[] threadIds = bean.findDeadlockedThreads(); // Returns null if no threads are deadlocked.

        if (threadIds != null) {
            ThreadInfo[] infos = bean.getThreadInfo(threadIds);

            for (ThreadInfo info : infos) {
                StackTraceElement[] stack = info.getStackTrace();
                log.error("Deadlock is found: {}", stack);
            }
        } else {
            log.info("No deadlock found");
        }
    }
}
