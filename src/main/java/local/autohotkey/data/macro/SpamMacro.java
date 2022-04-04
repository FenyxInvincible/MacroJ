package local.autohotkey.data.macro;

import local.autohotkey.data.Key;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.eso.Locks;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Scope("prototype")
@Slf4j
public class SpamMacro implements Macro {
    private final Sender sender;
    private final KeyManager keys;
    private final Locks locks;
    private Key overridableKey;
    private int delay;

    @Override
    public void setParams(Object param, Key self) {
        List<String> params = (List<String>) param;
        overridableKey = keys.findKeyByText(params.get(0));
        delay = Integer.parseInt(params.get(1));
    }

    @SneakyThrows
    @Override
    public void run() {
        while (overridableKey.isPressed()) {
            sender.pressKey(overridableKey);
            Thread.sleep(delay);
        }
        sender.releaseKey(overridableKey);
    }
}
