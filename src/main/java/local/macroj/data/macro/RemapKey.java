package local.macroj.data.macro;

import com.google.gson.reflect.TypeToken;
import local.macroj.data.MacroBaseActionData;
import local.macroj.data.MacroKey;
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
    private MacroBaseActionData keyAction;

    @Override
    public Type getParamsType() {
        return TypeToken.get(MacroBaseActionData.class).getType();
    }

    @Override
    public void setParams(Object param, MacroKey self) {
        keyAction = (MacroBaseActionData) param;
    }

    @Override
    public void run() {
        sender.handleMacroBaseAction(keyAction);
    }
}
