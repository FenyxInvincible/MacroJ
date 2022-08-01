package local.macroj.data.macro;

import com.google.gson.reflect.TypeToken;
import local.macroj.data.MacroKey;
import local.macroj.utils.Overlay;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

@Component
@Slf4j
public class Crosshair implements Macro {

    private static final String CROSSHAIR = "crosshair";
    private boolean isOn = false;

    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final Overlay overlay;
    private BufferedImage crosshair;

    public Crosshair(Overlay overlay) {
        this.overlay = overlay;
    }

    @Data
    public static class Param{
        private String imagePath;
        private int scaleX;
        private int scaleY;
    }

    @Override
    public Type getParamsType() {
        return TypeToken.get(Param.class).getType();
    }

    @Override
    public void setParams(Object param, MacroKey self) {
        try {
            Param params = (Param) param;
            InputStream inputStream = new FileInputStream(params.getImagePath());
            crosshair = Overlay.scale(
                    ImageIO.read(inputStream),
                    params.getScaleX(),
                    params.getScaleY()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (!isOn) {
            try {
                overlay.draw(CROSSHAIR, graphics -> {
                    log.info("Crosshair on");
                    graphics.drawImage(crosshair, screenSize.width / 2 - crosshair.getWidth()/2, screenSize.height / 2 - crosshair.getHeight()/2, null);
                });
                isOn = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            overlay.draw(CROSSHAIR, graphics -> {
                log.info("Crosshair off");
            });
            isOn = false;
        }

    }
}
