package local.autohotkey.data.macro.nioh;

import local.autohotkey.data.Key;
import local.autohotkey.data.macro.Macro;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.Overlay;
import local.autohotkey.utils.ScreenPicker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Scope("prototype")
@Slf4j
public class StamRegen implements Macro {
    public static final int START_X = 322;
    public static final int END_X = 834;
    public static final int LINE_Y = 139;
    public static final Color RED_LINE = new Color(213, 115, 106);
    private static final double RATIO = 3.0;


    private final Sender sender;
    private final KeyManager keys;
    private final Overlay overlay;
    private Key key5;
    private static AtomicBoolean isInterrupted = new AtomicBoolean(false);
    private static AtomicBoolean isStarted = new AtomicBoolean(false);

    @Override
    public void setParams(List<String> params) {
        key5 = keys.findKeyByText("5");
    }

    @Override
    public void run() {
        if (!isStarted.get()) {
            Executors.newSingleThreadExecutor().execute(new RegenDaemon());
            isStarted.set(true);
            isInterrupted.set(false);
            toggleOverlay();
        } else {
            isStarted.set(false);
            isInterrupted.set(true);
            toggleOverlay();
        }
    }

    private void toggleOverlay(){
        overlay.draw(UUID.randomUUID().toString(), graphics -> {
            try {
                if(isStarted.get()) {
                    InputStream image = new ClassPathResource("poe/heal_icon.png").getInputStream();
                    graphics.setColor(new Color(1f,0f,0f,.5f));
                    graphics.drawImage(ImageIO.read(image), 0, 80, null);
                } else {
                    overlay.clean();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    class RegenDaemon implements Runnable {

        @Override
        public void run() {
            while (!isInterrupted.get()) {
                try {

                    BufferedImage img = ScreenPicker.screenshot();

                    int length = 0;
                    boolean found = false;
                    //log.info("NEW LOOP +++++++++++++++++++++++++++++++++++++ ");
                    for (int i = START_X; i < END_X; i++) {
                        Color pixel = new Color(img.getRGB(i, LINE_Y));
                        //log.info("{} {} ", pixel, COLOR);
                        //r=108,g=120,b=214
                        if (pixel.getRed() > 240 && pixel.getBlue() > 240 && pixel.getGreen() > 240) {
                            found = true;
                            continue;
                        }

                        if (found && closeTo(pixel, RED_LINE, 10)) {
                            length++;
                        }
                    }

                    if (length != 0) {
                        log.info("{}", length);
                        Thread.sleep((long)(length * RATIO));
                        sender.sendKey(key5, 30);
                    }

                    Thread.sleep(100);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isInterrupted.set(false);
        }

        private boolean closeTo(Color pixel, Color redLine, int delta) {
            return pixel.getRed() < redLine.getRed() + delta &&
                    pixel.getRed() > redLine.getRed() - delta &&
                    pixel.getGreen() < redLine.getGreen() + delta &&
                    pixel.getGreen() > redLine.getGreen() - delta &&
                    pixel.getBlue() < redLine.getBlue() + delta &&
                    pixel.getBlue() > redLine.getBlue() - delta;
        }
    }
}
