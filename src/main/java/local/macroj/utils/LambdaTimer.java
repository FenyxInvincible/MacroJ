package local.macroj.utils;

import java.util.Timer;
import java.util.TimerTask;

public class LambdaTimer extends Timer {
    public TimerTask schedule(final Runnable r, long delay, long period) {
        final TimerTask task = new TimerTask() { public void run() { r.run(); }};
        schedule(task, delay, period);
        return task;
    }
}
