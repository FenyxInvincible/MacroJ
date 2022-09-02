package local.macroj.data.macro;

import com.google.gson.reflect.TypeToken;
import local.macroj.data.FontData;
import local.macroj.data.MacroIconData;
import local.macroj.data.MacroKey;
import local.macroj.utils.Overlay;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * For RobotSender implementation better do not use recursive call when macro is bind on key and resend it
 */
@Component
@Slf4j
@Scope("prototype")
@RequiredArgsConstructor
public class Countdown implements Macro {

    private final Overlay overlay;
    private IconTimerTask task;
    private CountdownData data;

    @Override
    public Type getParamsType() {
        return TypeToken.get(CountdownData.class).getType();
    }

    @Override
    public void setParams(Object param, MacroKey self) {
        data = (CountdownData) param;
    }

    @Override
    public void run() {
        if(task != null) {
            task.stop();
        }
        task = new IconTimerTask(data);

        Timer timer = new Timer();
        timer.schedule(task, 0, data.refresh);
    }


    @Override
    public void shutdown() {
        if(task != null) {
            task.stop();
        }
    }

    @RequiredArgsConstructor
    private class IconTimerTask extends TimerTask {
        private final CountdownData data;
        private String drawId;
        private int count = 0;
        private String textDrawId;
        private long startTime;

        @Override
        public void run() {
            if (count == 0) {
                startTime = System.currentTimeMillis();
                drawIcon();
            }

            drawText();

            if (System.currentTimeMillis() - startTime > data.duration) {
                log.info("Canceling");
                stop();
            }

            count++;
        }

        private void drawText() {

            String newDraw = UUID.randomUUID().toString();
            overlay.draw(newDraw, graphics -> {
                Color c = graphics.getColor();
                Font f = graphics.getFont();
                graphics.setColor(data.font.getColor());
                graphics.setFont(new Font(data.font.getName(), data.font.getStyle().getValue(), data.font.getSize()));

                graphics.drawString(
                        String.format("%.2f", (float)(data.duration - data.refresh * count) / 1000.f),
                        data.icon.getX(),
                        data.icon.getY() + data.icon.getHeight()
                );

                graphics.setColor(c);
                graphics.setFont(f);

            });

            if(textDrawId != null) {
                overlay.clear(textDrawId);
            }

            textDrawId = newDraw;
        }

        private void drawIcon() {
            drawId = UUID.randomUUID().toString();

            log.info("Drawing icon {}({})", data.icon.getImagePath(), drawId);
            overlay.draw(drawId, graphics -> {
                log.info("!!!!!!!!!Image");
                try {
                    InputStream image = Files.newInputStream(Paths.get(data.icon.getImagePath()));
                    log.info("!!!!!!!!!Image {}", image);
                    BufferedImage scaledImage = Overlay.scale(ImageIO.read(image), data.icon.getWidth(), data.icon.getHeight());
                    graphics.setColor(new Color(1f, 0f, 0f, 1.f));
                    graphics.drawImage(scaledImage, data.icon.getX(), data.icon.getY(), null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        public void stop() {
            if(textDrawId != null) {
                overlay.clear(textDrawId);
            }
            if(drawId != null) {
                overlay.clear(drawId);
            }

            cancel();
        }
    }

    @Data
    @NoArgsConstructor
    public static class CountdownData {
        private int duration = 1000;
        private int refresh = 100;

        private FontData font = new FontData();
        private MacroIconData icon = new MacroIconData();
    }
}