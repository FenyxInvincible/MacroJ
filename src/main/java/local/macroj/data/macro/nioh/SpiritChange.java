package local.macroj.data.macro.nioh;

import local.macroj.data.Key;
import local.macroj.data.MacroKey;
import local.macroj.data.macro.Macro;
import local.macroj.sender.Sender;
import local.macroj.service.KeyManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;

@Component
@RequiredArgsConstructor
@Scope("prototype")
@Slf4j
public class SpiritChange implements Macro {

    private final Sender sender;
    private final KeyManager keys;
    private Key skillKey;
    private Key panelKey;


    @SneakyThrows
    @Override
    public void run() {
        sender.sendKey(panelKey, 30);
        Thread.sleep(100);
        sender.sendKey(skillKey, 30);
        Thread.sleep(100);
    }

    @Override
    public void setParams(Object param, MacroKey self) {
        java.util.List<String> params = (List<String>) param;
        panelKey = keys.findKeyByText(params.get(0));
        skillKey = keys.findKeyByText(params.get(1));
    }

    @Override
    public Type getParamsType() {
        return null;
    }
}
