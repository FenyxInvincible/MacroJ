package local.autohotkey.data.macro;

import local.autohotkey.utils.ScreenPicker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class DebugMacro implements Macro {
    @Override
    public void setParams(List<String> params) {

    }

    @Override
    public void run() {
        try {
            PointerInfo a = MouseInfo.getPointerInfo();
            Point b = a.getLocation();
            int color = ScreenPicker.pickDwordColor((int) b.getX(), (int) b.getY());
            log.debug("x: {} y: {} Dword: {} Color: {}", b.getX(), b.getY(), color, ScreenPicker.dwordToColor(color));

            //BufferedImage bi = ScreenPicker.getImageByCoords((int)b.getX(), (int) b.getY(), 5);//1149, 1399
            BufferedImage bi = ScreenPicker.getImageByCoords(848, 576, 5);//1149, 1399
            File file = new File("E:\\tmp\\image.png");
            ImageIO.write(bi, "png", file);
            log.debug("File saved: {}",  file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
