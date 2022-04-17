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
public class StamRegenV2 implements Macro {
    public static final int START_X = 400;
    public static final int END_X = 916;
    public static final int LINE_Y = 138;
    public static final Color RED_LINE = new Color(240, 170, 150);
    public static final Color RED_LINE1 = new Color(233, 128, 112);
    public static final Color BLUE_LINE = new Color(234, 254, 255);
    private static final double RATIO = 3.3;
    //SPIRIT
    private static final int SPIRIT_X = 242;
    private static final int SPIRIT_Y = 89;
    private static final Color SPIRIT_COLOR = ScreenPicker.dwordToColor(6274755);


    private final Sender sender;
    private final KeyManager keys;
    private final Overlay overlay;
    private Key key5;
    private static AtomicBoolean isInterrupted = new AtomicBoolean(false);
    private static AtomicBoolean isStarted = new AtomicBoolean(false);

    @Override
    public void setParams(List<String> params) {
        key5 = keys.findKeyByText("9");
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
            int iteration = 0;

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
                        if (ScreenPicker.colorCloseTo(pixel, BLUE_LINE, 15)) {
                            found = true;
                            continue;
                        }

                        if (found && (ScreenPicker.colorCloseTo(pixel, RED_LINE, 20) || ScreenPicker.colorCloseTo(pixel, RED_LINE1, 20))) {
                            length++;
                        }
                    }

                    if (length != 0) {
                        log.info("{}", length);
                        Thread.sleep((long)(length * RATIO));
                        sender.sendKey(key5, 30);
                    }

                    //SPIRIT
                    if (iteration > 4) {
                        Color pixel = ScreenPicker.pickRGBColor(SPIRIT_X, SPIRIT_Y);
                        log.info("{} {}", pixel, SPIRIT_COLOR);
                        if (!ScreenPicker.colorCloseTo(pixel, SPIRIT_COLOR, 20)) {
                            sender.sendKey(keys.findKeyByText("P"), 30);
                        }
                        iteration = 0;
                    }


                    Thread.sleep(100);
                    iteration++;
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isInterrupted.set(false);
        }
    }
}
