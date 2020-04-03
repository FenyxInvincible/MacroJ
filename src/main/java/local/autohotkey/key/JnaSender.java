package local.autohotkey.key;

import local.autohotkey.data.Key;
import me.coley.simplejna.Keyboard;
import me.coley.simplejna.Mouse;
import org.springframework.stereotype.Component;

@Component
public class JnaSender implements Sender{
    @Override
    public void pressKey(Key key) {
        Keyboard.sendKeyDown(key.getKeyCode());
    }

    @Override
    public void releaseKey(Key key) {
        Keyboard.sendKeyUp(key.getKeyCode());
    }

    @Override
    public void sendKey(Key key, int delay) {

        try {
            Keyboard.sendKeyDown(key.getKeyCode());
            Thread.sleep(delay);
            Keyboard.sendKeyUp(key.getKeyCode());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseLeftClick() {
        Mouse.mouseLeftClick(-1, -1);
    }

    @Override
    public void mouseRightClick(int delay) throws InterruptedException {
        Mouse.mouseRightClick(-1, -1);
    }

    @Override
    public void mouseMiddleClick() {
        Mouse.mouseMiddleClick(-1, -1);
    }
}
