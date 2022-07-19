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
    public void pressKey(Key key, boolean allowRecursive) {
        if (key.isMouseKey()) {
            mouseKeyPress(MouseKey.of(key.getKeyText()), allowRecursive);
        } else {
            Keyboard.sendKeyDown(key.getKeyCode(), key.getScanCode(), allowRecursive);
        }
    }

    @Override
    public void releaseKey(Key key, boolean allowRecursive) {
        if (key.isMouseKey()) {
            mouseKeyRelease(MouseKey.of(key.getKeyText()), allowRecursive);
        } else {
            Keyboard.sendKeyUp(key.getKeyCode(), key.getScanCode(), allowRecursive);
        }
    }

    @Override
    public void sendKey(Key key, int delay, boolean allowRecursive) {
        try {
            if (key.isMouseKey()) {
                sendMouseKey(MouseKey.of(key.getKeyText()), delay, allowRecursive);
            } else {
                Keyboard.sendKeyDown(key.getKeyCode(), key.getScanCode(), allowRecursive);
                Thread.sleep(delay);
                Keyboard.sendKeyUp(key.getKeyCode(), key.getScanCode(), allowRecursive);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseKeyClick(MouseKey key, boolean allowRecursive) {
        switch (key){
            case LMB:
                mouseLeftClick(allowRecursive);
                break;
            case RMB:
                mouseRightClick(allowRecursive);
                break;
            case MMB:
                mouseMiddleClick(allowRecursive);
                break;
            case MOUSE_SCROLL:
                throw new IllegalArgumentException("MOUSE_SCROLL doesn't have click action. Only UP or DOWN supported");
        }
    }

    @Override
    public void mouseKeyPress(MouseKey key, boolean allowRecursive) {
        switch (key){
            case LMB:
                Mouse.mouseAction(-1, -1, Mouse.MOUSEEVENTF_LEFTDOWN, allowRecursive);
                break;
            case RMB:
                Mouse.mouseAction(-1, -1, Mouse.MOUSEEVENTF_RIGHTDOWN, allowRecursive);
                break;
            case MMB:
                Mouse.mouseAction(-1, -1, Mouse.MOUSEEVENTF_MIDDLEDOWN, allowRecursive);
                break;
            case MOUSE_SCROLL:
                Mouse.mouseAction(-1, -1, Mouse.MOUSEEVENTF_WHEEL, -Mouse.DwData.WHEEL_DELTA.getValue(), allowRecursive);
                break;
        }
    }

    @Override
    public void mouseKeyRelease(MouseKey key, boolean allowRecursive) {
        switch (key){
            case LMB:
                Mouse.mouseAction(-1, -1, Mouse.MOUSEEVENTF_LEFTUP, allowRecursive);
                break;
            case RMB:
                Mouse.mouseAction(-1, -1, Mouse.MOUSEEVENTF_RIGHTUP, allowRecursive);
                break;
            case MMB:
                Mouse.mouseAction(-1, -1, Mouse.MOUSEEVENTF_MIDDLEUP, allowRecursive);
                break;
            case MOUSE_SCROLL:
                Mouse.mouseAction(-1, -1, Mouse.MOUSEEVENTF_WHEEL, Mouse.DwData.WHEEL_DELTA.getValue(), allowRecursive);
                break;
        }
    }

    @Override
    public void sendMouseKey(MouseKey key, int delay, boolean allowRecursive) throws InterruptedException {
        mouseKeyPress(key);
        Thread.sleep(delay);
        mouseKeyRelease(key);
    }

    @Override
    public void mouseMove(int x, int y) {
        Mouse.mouseMove(x, y);
    }

    private void mouseLeftClick(boolean allowRecursive) {
        Mouse.mouseLeftClick(-1, -1, allowRecursive);
    }

    private void mouseRightClick(boolean allowRecursive) {
        Mouse.mouseRightClick(-1, -1, allowRecursive);
    }

    private void mouseMiddleClick(boolean allowRecursive) {
        Mouse.mouseMiddleClick(-1, -1, allowRecursive);
    }
}
