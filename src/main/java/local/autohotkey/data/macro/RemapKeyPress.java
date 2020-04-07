package local.autohotkey.data.macro;

import local.autohotkey.data.Key;
import local.autohotkey.key.MouseKey;
import local.autohotkey.key.Sender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.eso.Locks;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Scope("prototype")
public class RemapKeyPress implements Macro {
    private final Sender sender;
    private final KeyManager keys;
    private final Locks locks;
    private Key overridableKey;

    @Override
    public void setParams(List<String> params) {
        overridableKey = keys.findKeyByText(params.get(0));
    }

    @Override
    public void run() {
        sender.pressKey(overridableKey);
    }
}
