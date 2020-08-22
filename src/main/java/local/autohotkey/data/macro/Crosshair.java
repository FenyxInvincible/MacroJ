package local.autohotkey.data.macro;

import local.autohotkey.utils.Files;
import local.autohotkey.utils.Overlay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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

    @Override
    public void setParams(List<String> params) {
        try {
            InputStream inputStream = Files.loadResource(params.get(2));
            crosshair = scale(
                    ImageIO.read(inputStream),
                    Integer.parseInt(params.get(0)),
                    Integer.parseInt(params.get(1))
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

    public static BufferedImage scale(BufferedImage img, int dWidth, int dHeight) {
        Image tmp = img.getScaledInstance(dWidth, dHeight, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(dWidth, dHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return dimg;
    }
}
