package local.macroj.data.macro;

import com.google.gson.reflect.TypeToken;
import local.macroj.data.MacroBaseActionData;
import local.macroj.data.MacroKey;
import local.macroj.data.ScreenPosition;
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
        var isInverted = switchCases.invert;
        for (SwitchCase switchCase : switchCases.cases) {
            var actualColor = ScreenPicker.pickRGBColor(switchCase.pixel.getX(), switchCase.pixel.getY());

            log.info("Checking {} Invert: {} Checked color: {} Actual color: {}", switchCase.pixel, isInverted, switchCase.color, actualColor);
            if (!isInverted && actualColor.equals(switchCase.color)) {
                sender.handleMacroBaseAction(switchCase.key, keyInitiator);
                return;
            } else if (isInverted && !actualColor.equals(switchCase.color)) {
                sender.handleMacroBaseAction(switchCase.key, keyInitiator);
                return;
            }
        }

        if(switchCases.defaultAction != null) {
            sender.handleMacroBaseAction(switchCases.defaultAction, keyInitiator);
        }
    }

    @Data
    public static class SwitchCases {
        private ArrayList<SwitchCase> cases;
        private MacroBaseActionData defaultAction;

        private boolean invert = false;
    }

    @Data
    public static class SwitchCase {
        @NonNull
        private ScreenPosition pixel;

        @NonNull
        private Color color;

        @NonNull
        private MacroBaseActionData key;
    }
}
