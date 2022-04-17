package local.autohotkey.data.macro;

import com.google.gson.reflect.TypeToken;
import local.autohotkey.data.Key;
import local.autohotkey.data.MacroKey;
import local.autohotkey.data.UseKeyData;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.eso.Locks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;

@Component
@RequiredArgsConstructor
@Scope("prototype")
@Slf4j
public class RemapKey implements Macro {
    private final Sender sender;
    private UseKeyData keyAction;

    @Override
    public Type getParamsType() {
        return TypeToken.get(UseKeyData.class).getType();
    }

    @Override
    public void setParams(Object param, MacroKey self) {
        keyAction = (UseKeyData) param;
    }

    @Override
    public void run() {
        if (keyAction.getAction() == Key.Action.Press) {
            sender.pressKey(keyAction.getKey());
        } else if (keyAction.getAction() == Key.Action.Release) {
            sender.releaseKey(keyAction.getKey());
        } else {
            sender.sendKey(keyAction.getKey(), keyAction.getDelay());
        }
    }
}
