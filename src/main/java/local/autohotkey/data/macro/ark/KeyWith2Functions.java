package local.autohotkey.data.macro.ark;

import local.autohotkey.data.Key;
import local.autohotkey.data.macro.Macro;
import local.autohotkey.key.MouseKey;
import local.autohotkey.sender.JnaSender;
import local.autohotkey.sender.RobotSender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.ScreenPicker;
import local.autohotkey.utils.eso.Locks;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@Scope("prototype")
@RequiredArgsConstructor
@Slf4j
public class KeyWith2Functions implements Macro {

    private final JnaSender sender;
    private final KeyManager keys;
    private Key tKey;
    private Key key;
    private String action;
    private Key lKey;


    @PostConstruct
    public void init(){
        tKey = keys.findKeyByText("T");
        lKey = keys.findKeyByText("LMB");
    }

    @Override
    public void setParams(List<String> params) {
        key = keys.findKeyByText(params.get(0));
        action = params.get(1);
    }

    @SneakyThrows
    @Override
    public void run() {
        Key k;
        int color = ScreenPicker.pickDwordColor(2420, 44);
        if(color == 11110912) {
            sender.mouseKeyClick(MouseKey.LMB);
            Thread.sleep(32);

            k = tKey;
        } else {
            k = key;
        }

        if (action.equals("press")) {
            sender.pressKey(k);

/*
            BufferedImage bi = ScreenPicker.getImageByCoords(966, 33, 30);//1149, 1399
            File file = new File("D:\\tmp\\ark.png");
            ImageIO.write(bi, "png", file);*/
            log.info("Pressed {}", k.getKeyText());
        } else {
            sender.releaseKey(k);
            log.info("Released {}", k.getKeyText());
        }
    }
}
