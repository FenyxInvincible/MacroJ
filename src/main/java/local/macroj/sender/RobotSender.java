package local.macroj.sender;

import local.macroj.data.Key;
import local.macroj.key.MouseKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.event.InputEvent;

/**
 * AllowRecursive doesn't work for this Sender due to absence of sending dwExtraInfo
 * Need to be careful when resending of same key happens. See parent interface for more info
 */
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
    public void pressKey(Key key, boolean allowRecursive) {
        robot.keyPress(key.getKeyCode());
        log.debug("RobotSender pressKey {} allow recursive: {}", key, allowRecursive);
    }

    @Override
    public void releaseKey(Key key, boolean allowRecursive) {
        robot.keyRelease(key.getKeyCode());
        log.debug("RobotSender releaseKey {} allow recursive: {}", key, allowRecursive);
    }

    @Override
    public void sendKey(Key key, int delay, boolean allowRecursive) {

        try {
            pressKey(key);
            Thread.sleep(delay);
            releaseKey(key);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void mouseKeyClick(MouseKey key, boolean allowRecursive) {
        switch (key){
            case LMB:
                mouseLeftClick();
                break;
            case RMB:
                mouseRightClick();
                break;
            case MMB:
                mouseMiddleClick();
            case MOUSE_SCROLL:
                throw new IllegalArgumentException("MOUSE_SCROLL doesn't have click action. Only UP or DOWN supported");
        }
    }

    @Override
    public void mouseKeyPress(MouseKey key, boolean allowRecursive) {
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
            case MOUSE_SCROLL:
                robot.mouseWheel(-1);
                break;
        }
    }

    @Override
    public void mouseKeyRelease(MouseKey key, boolean allowRecursive) {
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
            case MOUSE_SCROLL:
                robot.mouseWheel(1);
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
        robot.mouseMove(x, y);
    }

    private void mouseLeftClick() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private void mouseRightClick() {
        robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
    }

    private void mouseMiddleClick() {
        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
    }
}
