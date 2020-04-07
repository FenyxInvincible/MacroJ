package local.autohotkey.data.macro;

import local.autohotkey.data.Key;
import local.autohotkey.key.MouseKey;
import local.autohotkey.key.Sender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.service.MouseEventListener;
import local.autohotkey.utils.eso.Locks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Scope("prototype")
public class MnbAttackStart implements Macro {
    private static Robot robot;
    private static HashMap<String, Integer> directionKey = new HashMap<>();

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private final Sender sender;
    private final KeyManager keys;
    private final Locks locks;
    private String direction;

    @PostConstruct
    private void init() {
        if (directionKey.isEmpty()) {
            directionKey.put("up", KeyEvent.VK_W);
            directionKey.put("left", KeyEvent.VK_A);
            directionKey.put("down", KeyEvent.VK_S);
            directionKey.put("right", KeyEvent.VK_D);
        }
    }

    @Override
    public void setParams(List<String> params) {
        direction = params.get(0).toLowerCase();
    }

    @Override
    public void run() {
        try {
            int dk = directionKey.get(direction);
            locks.getCastLock().lock();
            sender.mouseKeyClick(MouseKey.RMB);
            robot.keyPress(dk);
            sender.mouseKeyPress(MouseKey.LMB);
            robot.keyRelease(dk);
            locks.getCastLock().unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int newPosX(Point point, int step) {
        int x = point.x;
        if (direction.equals("left")) {
            x -= step;
        } else if (direction.equals("right")) {
            x += step;
        }
        return x;
    }

    private int newPosY(Point point, int step) {
        int y = point.y;
        if (direction.equals("up")) {
            y -= step;
        } else if (direction.equals("down")) {
            y += step;
        }
        return y;
    }
}
