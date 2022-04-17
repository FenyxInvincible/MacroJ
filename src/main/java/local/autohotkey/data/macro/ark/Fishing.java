package local.autohotkey.data.macro.ark;

import local.autohotkey.data.Key;
import local.autohotkey.data.Vector2D;
import local.autohotkey.data.macro.Macro;
import local.autohotkey.data.macro.poe.Heal;
import local.autohotkey.key.MouseKey;
import local.autohotkey.sender.RobotSender;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.ScreenPicker;
import local.autohotkey.utils.eso.Locks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Scope("prototype")
@RequiredArgsConstructor
@Slf4j
public class Fishing implements Macro {
    private static final Vector2D NIBBLE1 = new Vector2D(1200, 100);
    private static final Vector2D NIBBLE2 = new Vector2D(1225, 125);
    private static final Map<String, Vector2D> FISHING_KEYS = new LinkedHashMap<>();
    static {
        FISHING_KEYS.put("W", new Vector2D(1490, 1168));
        FISHING_KEYS.put("Q", new Vector2D(1580, 1356));
        FISHING_KEYS.put("X", new Vector2D(1509, 1150));
        FISHING_KEYS.put("A", new Vector2D(1592, 1328));
        FISHING_KEYS.put("D", new Vector2D(1580, 1220));
        FISHING_KEYS.put("Z", new Vector2D(1555, 1206));
        FISHING_KEYS.put("S", new Vector2D(1575, 1264));
        FISHING_KEYS.put("C", new Vector2D(1575, 1287));
        FISHING_KEYS.put("E", new Vector2D(1533, 1235));
    }

    private final Sender sender;
    private final KeyManager keys;

    private static AtomicBoolean isInterrupted = new AtomicBoolean(false);
    private static AtomicBoolean isStarted = new AtomicBoolean(false);

    @PostConstruct
    public void init(){
    }

    @Override
    public void setParams(List<String> params) {
    }

    @Override
    public void run() {
        if (!isStarted.get()) {
            Executors.newSingleThreadExecutor().execute(new Fishing.Daemon());
            isStarted.set(true);
            isInterrupted.set(false);
        } else {
            isStarted.set(false);
            isInterrupted.set(true);
        }
    }

    private boolean isWhite(Color color) {
        return color.getBlue() > 254 &&  color.getRed() > 254 && color.getGreen() > 254;
    }

    private class Daemon implements Runnable {

        @Override
        public void run() {

            sender.mouseKeyClick(MouseKey.LMB);
            log.info("Fishing session started!");

            while (!isInterrupted.get()) {
                try {
                    BufferedImage img = ScreenPicker.screenshot();
                    if(
                        isWhite(new Color(img.getRGB((int)NIBBLE1.x, (int)NIBBLE1.y))) &&
                        isWhite(new Color(img.getRGB((int)NIBBLE2.x, (int)NIBBLE2.y)))
                    ) {
                        log.info("NIBBLE!");
                        catchFish();
                        Thread.sleep(500);

                        log.info("New try...");
                        sender.mouseKeyClick(MouseKey.LMB);
                    }

                    Thread.sleep(500);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }

            log.info("Fishing stopped!");
            isInterrupted.set(false);
        }

        private void catchFish() throws InterruptedException, IOException {

            boolean loop = true;
            while (loop) {
                BufferedImage img = ScreenPicker.screenshot();
                loop = findCatchKey(img);
                Thread.sleep(500);
            }
        }

        private boolean findCatchKey(BufferedImage img){
            for (Map.Entry<String, Vector2D> entry: FISHING_KEYS.entrySet()) {
                Vector2D v = entry.getValue();
                if (isWhite(new Color(img.getRGB((int)v.x, (int)v.y)))) {
                    log.info("Sent key: {}", entry.getKey());
                    sender.sendKey(keys.findKeyByText(entry.getKey()), 16);
                    return true;
                }
            }

            log.info("New key not found");
            return false;
        }
    }
}
