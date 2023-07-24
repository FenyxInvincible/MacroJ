package local.macroj.data.macro.poe;

import com.google.gson.reflect.TypeToken;
import local.macroj.data.Key;
import local.macroj.data.MacroKey;
import local.macroj.data.macro.Macro;
import local.macroj.sender.Sender;
import local.macroj.service.KeyManager;
import local.macroj.utils.ScreenPicker;
import local.macroj.utils.eso.Locks;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Type;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Scope("prototype")
public class FollowBot implements Macro {
    private final Sender sender;
    private final KeyManager keys;
    private final Locks locks;
    private Key key;
    private static final AtomicBoolean isInterrupted = new AtomicBoolean(false);
    private static final AtomicBoolean isStarted = new AtomicBoolean(false);
    private FollowBotData data;
    private MacroKey initiator;

    @Override
    public Type getParamsType() {
        return TypeToken.get(FollowBotData.class).getType();
    }

    @Override
    public void setParams(Object param, MacroKey self) {
        this.data = (FollowBotData)param;
        this.initiator = self;
    }

    @Override
    public void run() {
        if (!isStarted.get()) {
            Executors.newSingleThreadExecutor().execute(new BotDaemon());
            isStarted.set(true);
            isInterrupted.set(false);
        } else {
            isStarted.set(false);
            isInterrupted.set(true);
        }
    }

    class BotDaemon implements Runnable {

        @Override
        public void run() {
            BufferedImage img = ScreenPicker.captureScreen(
                    data.getFindingZoneStartX(),
                    data.getFindingZoneStartY(),
                    data.getFindingZoneWidth(),
                    data.getFindingZoneHeight()
            );
            File dir = ScreenPicker.getRootDir(this);
            File file = new File(dir, "test.jpg");
            ScreenPicker.saveImageAsJpg(img, file);
            /*while (!isInterrupted.get()) {
                try {
                    findMaster();
                    Thread.sleep(data.refreshTimeMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isInterrupted.set(false);*/
        }
    }

    private void findMaster() {
        var img = ScreenPicker.captureScreen(
                data.getFindingZoneStartX(),
                data.getFindingZoneStartY(),
                data.getFindingZoneWidth(),
                data.getFindingZoneHeight()
        );
        File dir = ScreenPicker.getRootDir(this);
        File file = new File(dir, "test.jpg");
        ScreenPicker.saveImageAsJpg(img, file);
    }

    @Data
    @NoArgsConstructor
    public static class FollowBotData {
        /**
         * current pixel color is equal to desired pixel
         */
        private int refreshTimeMs = 200;
        @NonNull
        private Integer findingZoneStartX;
        @NonNull
        private Integer findingZoneStartY;
        @NonNull
        private Integer findingZoneWidth;
        @NonNull
        private Integer findingZoneHeight;
        private Color desiredColor = null;
    }
}
