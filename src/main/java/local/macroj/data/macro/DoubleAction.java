package local.macroj.data.macro;

import com.google.gson.reflect.TypeToken;
import local.macroj.data.Key;
import local.macroj.data.MacroKey;
import local.macroj.sender.Sender;
import local.macroj.service.KeyManager;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.PackagePrivate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class DoubleAction implements Macro {

    private final Sender sender;

    private DoubleActionData data;
    private MacroKey initiator;

    @Override
    public Type getParamsType() {
        return TypeToken.get(DoubleActionData.class).getType();
    }

    @Override
    public void setParams(Object param, MacroKey self) {
        data = (DoubleActionData) param;
        initiator = self;
    }

    @Override
    public void run() {

        boolean initialState = initiator.getKey().isPressed();

        long start = System.currentTimeMillis();
        Key key = data.longKey;
        while (System.currentTimeMillis() - start < data.longPressMs) {
            try {
                if (initiator.getKey().isPressed() != initialState) {
                    key = data.shortKey;
                    break;
                }
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log.debug("DoubleAction: sending key {} data: {}", key, data);
        sender.sendKey(key, data.pressReleaseDelayMs, !key.equals(initiator.getKey()) && data.propagateCall);

    }

    @Data
    public static class DoubleActionData {
        Key shortKey;
        Key longKey;
        int pressReleaseDelayMs = 16;
        int longPressMs = 500;
        /**
         * By default all sends from sender are not propagated, so this sending will not trigger next macro
         * When true, if there is macro that is bound to sent key, it will be executed
         */
        boolean propagateCall = false;
    }
}
