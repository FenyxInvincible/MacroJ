package local.autohotkey.data.macro.nioh;

import com.google.gson.reflect.TypeToken;
import local.autohotkey.data.Key;
import local.autohotkey.data.MacroKey;
import local.autohotkey.data.macro.Macro;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.Overlay;
import local.autohotkey.utils.ScreenPicker;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Scope("prototype")
@Slf4j
public class StamRegenV2 implements Macro {

    public static final Color RED_LINE = new Color(240, 170, 150);
    public static final Color RED_LINE1 = new Color(233, 128, 112);
    public static final Color BLUE_LINE = new Color(234, 254, 255);

    private final Sender sender;
    private final Overlay overlay;

    private static final AtomicBoolean isInterrupted = new AtomicBoolean(false);
    private static final AtomicBoolean isStarted = new AtomicBoolean(false);
    private StagRegenData macroData;
    private Color spiritPixelColor;

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
                    InputStream image = new FileInputStream(macroData.buffActiveImagePath);
                    BufferedImage scaledImage = Overlay.scale(ImageIO.read(image), 50, 50);
                    graphics.setColor(new Color(1f,0f,0f,.5f));
                    graphics.drawImage(scaledImage, 0, 80, null);
                } else {
                    overlay.clean();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void setParams(Object param, MacroKey self) {
        StagRegenData params = (StagRegenData) param;
        macroData = params;
        spiritPixelColor = ScreenPicker.dwordToColor(macroData.spiritPixelColorDword);
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
                    //log.debug("NEW LOOP +++++++++++++++++++++++++++++++++++++ ");
                    for (int i = macroData.staminaBarStartX; i < macroData.staminaBarEndX; i++) {
                        Color pixel = new Color(img.getRGB(i, macroData.staminaBarY));
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
                        log.debug("{}", length);
                        Thread.sleep((long)(length * macroData.ratio));
                        sender.sendKey(macroData.staminaRegenKey, 30);
                    }

                    //SPIRIT
                    if(macroData.isSpiritChangeActive) {
                        if (iteration > 4) {
                            Color pixel = ScreenPicker.pickRGBColor(macroData.spiritPixelX, macroData.spiritPixelY);
                            log.debug("checkedPixel {} SPIRIT_COLOR {}", pixel, macroData.spiritPixelColorDword);

                            if (!ScreenPicker.colorCloseTo(pixel, spiritPixelColor, 20)) {
                                sender.sendKey(macroData.changeSpiritKey, 30);
                            }
                            iteration = 0;
                        }
                        iteration++;
                    }

                    Thread.sleep(100);

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isInterrupted.set(false);
        }
    }

    @Override
    public Type getParamsType() {
        return TypeToken.get(StagRegenData.class).getType();
    }

    @Data
    public static class StagRegenData {
        private final String buffActiveImagePath;

        //stam regen settings
        private final Key staminaRegenKey;
        private final int staminaBarStartX;
        private final int staminaBarEndX;
        private final int staminaBarY;
        private final double ratio;

        //spirit change settings
        private final boolean isSpiritChangeActive = false;
        private final Key changeSpiritKey;
        private final int spiritPixelX;
        private final int spiritPixelY;
        private final int spiritPixelColorDword;
    }
}
