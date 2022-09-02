package local.macroj.data.macro;

import local.macroj.data.MacroKey;
import local.macroj.utils.Overlay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Random;

@Component
@Slf4j
public class OverlayExample implements Macro {


    private final Overlay overlay;

    public OverlayExample(Overlay overlay) {
        this.overlay = overlay;
    }

    @Override
    public void setParams(Object param, MacroKey self) {

    }

    @Override
    public void run() {
        try {
            overlay.draw(Overlay.getRandomLabel(), graphics -> {
                graphics.setColor(new Color(1f,0f,0f,.5f));
                graphics.draw3DRect(new Random().nextInt(1000), new Random().nextInt(500), 100, 100, true);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
