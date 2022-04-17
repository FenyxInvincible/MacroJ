package local.autohotkey.data.macro;

import local.autohotkey.data.Key;
import local.autohotkey.data.macro.Macro;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.Overlay;
import local.autohotkey.utils.ScreenPicker;
import lombok.RequiredArgsConstructor;
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
public class SendSequence implements Macro {

    private final Sender sender;
    private final KeyManager keys;
    private final Overlay overlay;
    private int delay;
    private List<String> params;

    @Override
    public void setParams(List<String> params) {
        delay = Integer.parseInt(params.get(0));
        this.params = params.subList(1, params.size());
    }

    @Override
    public void run() {
        params.stream().forEach(k -> {
            sender.sendKey(keys.findKeyByText(k), 25);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
