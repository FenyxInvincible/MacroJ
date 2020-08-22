package local.autohotkey.data.macro;

import local.autohotkey.utils.Overlay;
import local.autohotkey.utils.ScreenPicker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@Slf4j
public class OverlayExample implements Macro {


    private final Overlay overlay;

    public OverlayExample(Overlay overlay) {
        this.overlay = overlay;
    }

    @Override
    public void setParams(List<String> params) {

    }

    @Override
    public void run() {
        try {
            overlay.draw(UUID.randomUUID().toString(), graphics -> {
                graphics.setColor(new Color(1f,0f,0f,.5f));
                graphics.draw3DRect(new Random().nextInt(1000), new Random().nextInt(500), 100, 100, true);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
