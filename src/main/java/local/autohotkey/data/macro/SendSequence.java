package local.autohotkey.data.macro;

import com.google.gson.reflect.TypeToken;
import local.autohotkey.data.Key;
import local.autohotkey.data.MacroKey;
import local.autohotkey.data.UseKeyData;
import local.autohotkey.sender.Sender;
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
public class SendSequence implements Macro {

    private final Sender sender;

    private List<UseKeyData> sendKeys;
    private MacroKey self;

    @Override
    public void run() {
        sendKeys.stream().forEach(k -> {
            try {
                if (k.getAction() == Key.Action.Press) {
                    sender.pressKey(k.getKey());
                } else if (k.getAction() == Key.Action.Release) {
                    sender.releaseKey(k.getKey());
                } else {
                    sender.sendKey(k.getKey(), 64);
                }

                Thread.sleep(k.getDelay());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void setParams(Object param, MacroKey self) {
        sendKeys = (List<UseKeyData>)param;
        this.self = self;
    }

    @Override
    public Type getParamsType() {
        return TypeToken.getParameterized(List.class, UseKeyData.class).getType();
    }
}
