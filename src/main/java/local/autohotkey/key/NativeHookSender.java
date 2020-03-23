package local.autohotkey.key;

import local.autohotkey.data.Key;
import local.autohotkey.service.MacroFactory;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

public class NativeHookSender implements Sender{
    private ConcurrentLinkedQueue<NativeKeyEvent> pressEventQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<NativeKeyEvent> releaseEventQueue = new ConcurrentLinkedQueue<>();

    @PostConstruct
    public void init() {
        IntStream.range(0, 50).forEach(value ->
                pressEventQueue.offer(
                        new NativeKeyEvent(
                                MacroFactory.KEY_PRESS,
                                0,
                                0,
                                0, 0, '\0'
                        )
                )
        );

        IntStream.range(0, 50).forEach(value ->
                releaseEventQueue.offer(
                        new NativeKeyEvent(
                                MacroFactory.KEY_RELEASE,
                                0,
                                0,
                                0, 0, '\0'
                        )
                )
        );
    }

    public void pressKey(Key key) {
        processKey(pressEventQueue, key);
    }

    public void releaseKey(Key key) {
        processKey(releaseEventQueue, key);
    }

    private void processKey(ConcurrentLinkedQueue<NativeKeyEvent> queue, Key key) {
        try {
            NativeKeyEvent event = queue.poll();
            event.setKeyCode(key.getKeyCode());
            event.setRawCode(key.getRawCode());
            GlobalScreen.postNativeEvent(event);

            queue.offer(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendKey(Key key, int delay) {
        try {

            pressKey(key);
            Thread.sleep(delay);
            releaseKey(key);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseLeftClick() {

    }
}
