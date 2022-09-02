package local.macroj.data.macro;

import com.google.gson.reflect.TypeToken;
import local.macroj.ApplicationConfig;
import local.macroj.data.MacroIconData;
import local.macroj.data.MacroKey;
import local.macroj.data.UseKeyData;
import local.macroj.sender.Sender;
import local.macroj.utils.Overlay;
import local.macroj.utils.ScreenPicker;
import lombok.*;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Creates daemon thread to check pixel by equality. Executes actions
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Scope("prototype")
public class PixelChecker implements Macro {

    private final Sender sender;
    private final Overlay overlay;

    private PixelCheckerData data;
    private Future<?> daemon;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(
            r -> {
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                return t;
            });
    private MacroKey initiator;

    @Override
    public void setParams(Object param, MacroKey self) {
        this.data = (PixelCheckerData)param;
        this.initiator = self;
    }

    @Override
    public Type getParamsType() {
        return TypeToken.get(PixelCheckerData.class).getType();
    }

    @SneakyThrows
    @Override
    public void run() {
        if(daemon == null) {
            daemon = executor.submit(new PixelHunterDaemon(data));

            log.info("Daemon is created");
        } else {
            log.info("Stopping the daemon...");
            daemon.cancel(true);
            daemon = null;
            log.info("Success");
        }
    }

    @Override
    public void shutdown() {
        executor.shutdownNow();
        log.info("Macro PixelChecker has been terminated by shutdown");
    }

    @RequiredArgsConstructor
    private class PixelHunterDaemon implements Runnable {

        private final PixelCheckerData data;
        private InputStream image;
        private Overlay.OverlayLabel drawId;

        @SneakyThrows
        @Override
        public void run() {
            //if color is not specified, we check current color by coords
            Color color = data.desiredColor != null ?
                    data.desiredColor :
                    ScreenPicker.pickRGBColor(data.getX(), data.getY());
            createIcon();
            checking(color);
        }

        protected void checking(Color checkingColor) throws InterruptedException {
            try {
                log.info("Daemon has started checking: color {}", checkingColor);
                while (!Thread.interrupted()) {
                    Color currentColor = ScreenPicker.pickRGBColor(data.getX(), data.getY());

                    log.debug(
                        "Current color: {} Desired color: {} Logic: {}",
                        currentColor,
                        checkingColor,
                        data.isEquality() ? "equality" : "non equality"
                    );

                    if(data.isEquality() == currentColor.equals(checkingColor)) {
                        sender.sendKeys(data.getKeys(), ApplicationConfig.DEFAULT_SEND_DELAY, initiator);
                        log.info("Keys was sent: {} for colors: Desired: {}, Actual {}", data.getKeys(), checkingColor, currentColor);
                    } else {
                        Thread.sleep(data.getRefreshTimeMs());
                    }
                }
            } catch (InterruptedException e){
                log.debug("Thread was interrupted by InterruptedException");
                Thread.currentThread().interrupt();
            } finally {
                this.shutdown();
            }
        }

        private void createIcon() {
            try {

                drawId = Overlay.getRandomLabel();

                log.info("Drawing icon {}({})", data.icon.getImagePath(), drawId);

                overlay.draw(drawId, graphics -> {
                    try {
                        image = Files.newInputStream(Paths.get(data.icon.getImagePath()));
                        BufferedImage scaledImage = Overlay.scale(ImageIO.read(image), data.icon.getWidth(), data.icon.getHeight());
                        graphics.setColor(new Color(1f, 0f, 0f, 1.f));
                        graphics.drawImage(scaledImage, data.icon.getX(), data.icon.getY(), null);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void shutdown() {
            if (image != null) {
                overlay.clear(drawId);
                log.info("Clearing icon ({})", drawId);
            }
        }
    }

    @Data
    @NoArgsConstructor
    public static class PixelCheckerData {
        /**
         * current pixel color is equal to desired pixel
         */
        private boolean equality = true;
        private int refreshTimeMs = 200;
        @NonNull
        private Integer x;
        @NonNull
        private Integer y;
        private Color desiredColor = null;
        private List<UseKeyData> keys;
        private MacroIconData icon = new MacroIconData();
    }
}
