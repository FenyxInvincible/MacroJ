package local.macroj.data.macro;

import com.google.gson.reflect.TypeToken;
import local.macroj.ApplicationConfig;
import local.macroj.data.MacroKey;
import local.macroj.data.UseKeyData;
import local.macroj.sender.Sender;
import local.macroj.utils.ScreenPicker;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
@Scope("prototype")
public class PixelColor implements Macro {

    private final Sender sender;

    private PixelColorData data;
    protected MacroKey self;

    @Override
    public void setParams(Object param, MacroKey self) {
        this.data = (PixelColorData)param;
        this.self = self;
    }

    @Override
    public Type getParamsType() {
        return TypeToken.get(PixelColorData.class).getType();
    }

    @SneakyThrows
    @Override
    public void run() {
        int attempts = data.getMaxAttempts();
        Color color = data.getDesiredColor() != null ?
                data.getDesiredColor() :
                ScreenPicker.pickRGBColor(data.getX(), data.getY());

        Thread.sleep(data.getAttemptsDelayMs());

        checking(attempts, color);
    }

    protected void checking(int attempts, Color checkingColor) throws InterruptedException {

        while (condition(attempts, checkingColor)) {
            sender.sendKeys(data.getKeys(), ApplicationConfig.DEFAULT_SEND_DELAY, self);
            Thread.sleep(data.getAttemptsDelayMs());
            attempts--;
        }
    }

    protected boolean condition(int attempts, Color checkingColor) {
        return attempts > 0
                && data.isEquality() == ScreenPicker.pickRGBColor(data.getX(), data.getY()).equals(checkingColor);
    }

    @Data
    @NoArgsConstructor
    public static class PixelColorData {
        private boolean equality = true;
        @NonNull
        private Integer x;
        @NonNull
        private Integer y;
        private Color desiredColor = null;
        private int maxAttempts = 3;
        private int attemptsDelayMs = 50;
        private List<UseKeyData> keys;
    }
}
