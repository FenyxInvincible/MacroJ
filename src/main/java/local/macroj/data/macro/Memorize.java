package local.macroj.data.macro;

import local.macroj.data.Key;
import local.macroj.data.MacroKey;
import local.macroj.sender.Sender;
import local.macroj.service.KeyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@Scope("prototype")
@RequiredArgsConstructor
public class Memorize implements Macro{
    public static volatile Key latestKey;

    private final KeyManager keys;
    private final Sender sender;

    private Key key;

    @Override
    public void setParams(Object param, MacroKey self) {
        List<String> params = (List<String>) param;
        key = keys.findKeyByText(params.get(0));
    }

    @Override
    public void run() {
        latestKey = key;
        sender.pressKey(key);
    }
}