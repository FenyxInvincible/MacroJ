package local.autohotkey.sender;

import local.autohotkey.data.Key;
import local.autohotkey.jna.Keyboard;
import local.autohotkey.jna.Mouse;
import local.autohotkey.key.MouseKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JnaSender implements Sender{
    @Override
    public void pressKey(Key key) {
        if (key.isMouseKey()) {
            mouseKeyPress(MouseKey.of(key.getKeyText()));
        } else {
            Keyboard.sendKeyDown(key.getKeyCode(), key.getScanCode());
        }
    }

    @Override
    public void releaseKey(Key key) {
        if (key.isMouseKey()) {
            mouseKeyRelease(MouseKey.of(key.getKeyText()));
        } else {
            Keyboard.sendKeyUp(key.getKeyCode(), key.getScanCode());
        }
    }

    @Override
    public void sendKey(Key key, int delay) {
        try {
            if (key.isMouseKey()) {
                sendMouseKey(MouseKey.of(key.getKeyText()), delay);
            } else {
                Keyboard.sendKeyDown(key.getKeyCode(), key.getScanCode());
                Thread.sleep(delay);
                Keyboard.sendKeyUp(key.getKeyCode(), key.getScanCode());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseKeyClick(MouseKey key) {
        switch (key){
            case LMB:
                mouseLeftClick();
                break;
            case RMB:
                mouseRightClick();
                break;
            case MMB:
                mouseMiddleClick();
                break;
        }
    }

    @Override
    public void mouseKeyPress(MouseKey key) {
        switch (key){
            case LMB:
                Mouse.mouseAction(-1, -1, Mouse.MOUSEEVENTF_LEFTDOWN);
                break;
            case RMB:
                Mouse.mouseAction(-1, -1, Mouse.MOUSEEVENTF_RIGHTDOWN);
                break;
            case MMB:
                Mouse.mouseAction(-1, -1, Mouse.MOUSEEVENTF_MIDDLEDOWN);
                break;
        }
    }

    @Override
    public void mouseKeyRelease(MouseKey key) {
        switch (key){
            case LMB:
                Mouse.mouseAction(-1, -1, Mouse.MOUSEEVENTF_LEFTUP);
                break;
            case RMB:
                Mouse.mouseAction(-1, -1, Mouse.MOUSEEVENTF_RIGHTUP);
                break;
            case MMB:
                Mouse.mouseAction(-1, -1, Mouse.MOUSEEVENTF_MIDDLEUP);
                break;
        }
    }

    @Override
    public void sendMouseKey(MouseKey key, int delay) throws InterruptedException {
        mouseKeyPress(key);
        Thread.sleep(delay);
        mouseKeyRelease(key);
    }

    @Override
    public void mouseMove(int x, int y) {
        Mouse.mouseMove(x, y);
    }

    private void mouseLeftClick() {
        Mouse.mouseLeftClick(-1, -1);
    }

    private void mouseRightClick() {
        Mouse.mouseRightClick(-1, -1);
    }

    private void mouseMiddleClick() {
        Mouse.mouseMiddleClick(-1, -1);
    }
}
