package local.autohotkey.sender;

import local.autohotkey.data.Key;
import local.autohotkey.jna.Keyboard;
import local.autohotkey.jna.Mouse;
import local.autohotkey.key.MouseKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@Component
@Qualifier("Robot")
@Slf4j
public class RobotSender implements Sender{

    private Robot robot;

    @PostConstruct
    public void init() throws AWTException {
        robot = new Robot();
    }

    @Override
    public void pressKey(Key key) {
        robot.keyPress(key.getKeyCode());
    }

    @Override
    public void releaseKey(Key key) {
        robot.keyRelease(key.getKeyCode());
    }

    @Override
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
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                break;
            case RMB:
                robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
                break;
            case MMB:
                robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                break;
        }
    }

    @Override
    public void mouseKeyRelease(MouseKey key) {
        switch (key){
            case LMB:
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                break;
            case RMB:
                robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
                break;
            case MMB:
                robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
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
