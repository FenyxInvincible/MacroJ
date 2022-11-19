package local.macroj.data.macro;

import com.google.gson.reflect.TypeToken;
import local.macroj.ApplicationConfig;
import local.macroj.data.MacroKey;
import local.macroj.data.ScreenPosition;
import local.macroj.data.UseKeyData;
import local.macroj.sender.Sender;
import local.macroj.utils.ScreenPicker;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Scope("prototype")
@Slf4j
public class SwitchCaseByColor implements Macro {

    private final Sender sender;
    private SwitchCases switchCases;
    private MacroKey keyInitiator;

    @Override
    public Type getParamsType() {
        return TypeToken.get(SwitchCaseByColor.SwitchCases.class).getType();
    }

    @Override
    public void setParams(Object param, MacroKey self) {
        switchCases = (SwitchCaseByColor.SwitchCases) param;
        keyInitiator = self;
    }

    @Override
    public void run() {
        for (SwitchCase switchCase : switchCases.cases) {
            if (ScreenPicker.pickRGBColor(switchCase.pixel.getX(), switchCase.pixel.getY()).equals(switchCase.color)) {
                sender.send(switchCase.key, ApplicationConfig.DEFAULT_SEND_DELAY, keyInitiator);
                return;
            }
        }

        sender.send(switchCases.defaultKey, ApplicationConfig.DEFAULT_SEND_DELAY, keyInitiator);
    }

    @Data
    public static class SwitchCases {
        private ArrayList<SwitchCase> cases;
        @NonNull
        private UseKeyData defaultKey;
    }

    @Data
    public static class SwitchCase {
        @NonNull
        private ScreenPosition pixel;

        @NonNull
        private Color color;

        @NonNull
        private UseKeyData key;
    }
}
