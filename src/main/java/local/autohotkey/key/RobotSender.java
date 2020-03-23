package local.autohotkey.key;

import local.autohotkey.data.Key;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.InputEvent;

@Slf4j
public class RobotSender implements Sender {

    private Robot robot;

    public RobotSender(Robot robot) {
        this.robot = robot;
    }

    @Override
    public void pressKey(Key key) {

        robot.keyPress(key.getRawCode());
        log.debug("!!!Key sent{}",key.getKeyText());
    }

    @Override
    public void releaseKey(Key key) {
        robot.keyRelease(key.getRawCode());
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
    public void mouseLeftClick() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
}
