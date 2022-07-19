package local.autohotkey.data.macro;

import local.autohotkey.data.MacroKey;
import local.autohotkey.utils.ScreenPicker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
@Slf4j
public class DebugMacro implements Macro {

    @Override
    public void setParams(Object param, MacroKey self) {

    }

    @Override
    public void run() {
        try {
            log.info("=============DEBUG START ==============");
            log.info("Foreground application name is: {}", ScreenPicker.getForegroundWindowTitle());
            log.info("Foreground application path: {}", ScreenPicker.getForegroundWindowPath());

            PointerInfo a = MouseInfo.getPointerInfo();
            Point b = a.getLocation();
            int color = ScreenPicker.pickDwordColor((int) b.getX(), (int) b.getY());
            log.info("x: {} y: {} Dword: {} Color: {}", b.getX(), b.getY(), color, ScreenPicker.dwordToColor(color));
            log.info("=============DEBUG END ==============");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
