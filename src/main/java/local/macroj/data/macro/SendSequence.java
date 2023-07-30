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
import java.util.List;

@Component
@RequiredArgsConstructor
@Scope("prototype")
@Slf4j
public class SendSequence implements Macro {

    private final Sender sender;

    private List<MacroBaseActionData> sendKeys;
    private MacroKey self;

    @Override
    public void run() {
        try {
            sender.handleMacroBaseActions(sendKeys, self);
        } catch (Exception e) {
            log.error("Unhandled exception while sending sequence: params {}", sendKeys, e);
        }
    }

    @Override
    public void setParams(Object param, MacroKey self) {
        sendKeys = (List<MacroBaseActionData>)param;
        this.self = self;
    }

    @Override
    public Type getParamsType() {
        return TypeToken.getParameterized(List.class, MacroBaseActionData.class).getType();
    }
}
