package local.autohotkey.data.macro.nioh;

import local.autohotkey.data.Key;
import local.autohotkey.data.macro.Macro;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.Overlay;
import local.autohotkey.utils.ScreenPicker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Scope("prototype")
@Slf4j
public class SpiritChange implements Macro {

    private final Sender sender;
    private final KeyManager keys;
    private Key skillKey;
    private Key panelKey;

    @Override
    public void setParams(List<String> params) {
        panelKey = keys.findKeyByText(params.get(0));
        skillKey = keys.findKeyByText(params.get(1));
    }

    @SneakyThrows
    @Override
    public void run() {
        sender.sendKey(panelKey, 30);
        Thread.sleep(100);
        sender.sendKey(skillKey, 30);
        Thread.sleep(100);
    }
}
