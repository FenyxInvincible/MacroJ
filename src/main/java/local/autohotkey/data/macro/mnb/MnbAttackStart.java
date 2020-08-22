package local.autohotkey.data.macro.mnb;

import local.autohotkey.data.Key;
import local.autohotkey.data.macro.Macro;
import local.autohotkey.key.MouseKey;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.eso.Locks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Scope("prototype")
public class MnbAttackStart implements Macro {
    private static HashMap<String, Key> directionKey = new HashMap<>();

    private final Sender sender;
    private final KeyManager keys;
    private final Locks locks;
    private String direction;

    @PostConstruct
    private void init() {
        if (directionKey.isEmpty()) {
            directionKey.put("up", keys.findKeyByText("w"));
            directionKey.put("left", keys.findKeyByText("a"));
            directionKey.put("down", keys.findKeyByText("s"));
            directionKey.put("right", keys.findKeyByText("d"));
        }
    }

    @Override
    public void setParams(List<String> params) {
        direction = params.get(0).toLowerCase();
    }

    @Override
    public void run() {
        try {
            Key dk = directionKey.get(direction);
            locks.getCastLock().lock();
            sender.mouseKeyPress(MouseKey.RMB);
            Thread.sleep(16);
            sender.mouseKeyRelease(MouseKey.RMB);

            sender.pressKey(dk);
            Thread.sleep(16);
            sender.pressKey(dk);
            Thread.sleep(16);
            sender.pressKey(dk);

            sender.mouseKeyPress(MouseKey.LMB);

            if(!dk.isPressed()) {
                sender.releaseKey(dk);
            }

            Thread.sleep(16);

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
