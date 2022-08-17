package local.macroj.data.macro;

import com.google.gson.reflect.TypeToken;
import local.macroj.ApplicationConfig;
import local.macroj.data.MacroKey;
import local.macroj.data.UseKeyData;
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

    private List<UseKeyData> sendKeys;
    private MacroKey self;

    @Override
    public void run() {
        try {
            sender.sendKeys(sendKeys, ApplicationConfig.DEFAULT_SEND_DELAY, self);
        } catch (Exception e) {
            log.error("Unhandled exception while sending sequence: params {}", sendKeys, e);
        }
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
