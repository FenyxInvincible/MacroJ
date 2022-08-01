package local.macroj.data.macro;

import com.google.gson.reflect.TypeToken;
import local.macroj.data.Key;
import local.macroj.data.MacroKey;
import local.macroj.data.UseKeyData;
import local.macroj.sender.Sender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

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
