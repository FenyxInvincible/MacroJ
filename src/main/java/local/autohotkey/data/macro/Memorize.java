package local.autohotkey.data.macro;

import local.autohotkey.data.Key;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
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
    public void setParams(Object param, Key self) {
        List<String> params = (List<String>) param;
        key = keys.findKeyByText(params.get(0));
    }

    @Override
    public void run() {
        latestKey = key;
        sender.pressKey(key);
    }
}