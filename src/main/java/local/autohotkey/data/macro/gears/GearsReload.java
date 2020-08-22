package local.autohotkey.data.macro.gears;

import local.autohotkey.data.Key;
import local.autohotkey.data.macro.Macro;
import local.autohotkey.sender.RobotSender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.ScreenPicker;
import local.autohotkey.utils.eso.Locks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@Component
@Scope("prototype")
@RequiredArgsConstructor
@Slf4j
public class GearsReload implements Macro {

    private final RobotSender sender;
    private final KeyManager keys;
    private final Locks locks;
    private Key rKey;
    private int y;
    private int x1;
    private int x2;
    private double speed;
    private int length;


    @PostConstruct
    public void init(){
        rKey = keys.findKeyByText("R");
    }

    @Override
    public void setParams(List<String> params) {
        y = Integer.parseInt(params.get(0));
        x1 = Integer.parseInt(params.get(1));
        x2 = Integer.parseInt(params.get(2));
        speed = Double.parseDouble(params.get(3));
        length = Integer.parseInt(params.get(4));
    }

    @Override
    public void run() {
        try {
            long time = System.currentTimeMillis();
            Thread.sleep(1000);
            BufferedImage img = ScreenPicker.screenshot();

            long delay = 0;

            Color color = new Color(img.getRGB(x1, y));
            Color color1 = new Color(img.getRGB(x2, y));
            log.info("{} {}", color, color1);
            if (isWhite(color) || isWhite(color1)) {//Longshot
                int reloadDeltaPixels = findReloadDelta(img);


                delay = 1000 + Math.round(speed * reloadDeltaPixels);
                log.info("Reload delta {} calculated delay {}" , reloadDeltaPixels, delay);
            }

            if (delay > 0) {
                delay = delay - (System.currentTimeMillis() - time);
                delay = delay > 0 ? delay : 0;
                Thread.sleep(delay);
                log.info("sent R {}", delay);
                sender.sendKey(rKey, 16);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isWhite(Color color) {
        return color.getBlue() > 247 &&  color.getRed() > 247 && color.getGreen() > 247;
    }

    private int findReloadDelta(BufferedImage img) {
        for(int i = 2100; i < 2308; i++){
            if (img.getRGB(i, 221) == -460552) {
                return  2308 - i;
            }
        }
        return 0;
    }
}
