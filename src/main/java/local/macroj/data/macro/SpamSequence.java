package local.macroj.data.macro;

import com.google.gson.reflect.TypeToken;
import local.macroj.data.MacroBaseActionData;
import local.macroj.data.MacroKey;
import local.macroj.sender.Sender;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;

@Component
@RequiredArgsConstructor
@Scope("prototype")
@Slf4j
public class SpamSequence implements Macro {
    private final Sender sender;
    private List<MacroBaseActionData> keys;
    private MacroKey selfKey;

    @Override
    public Type getParamsType() {
        return TypeToken.getParameterized(List.class, MacroBaseActionData.class).getType();
    }

    @Override
    public void setParams(Object param, MacroKey self) {
        keys = (List<MacroBaseActionData>) param;
        this.selfKey = self;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (selfKey.getKey().isPressed()) {
            keys.stream()
                    .takeWhile(n -> selfKey.getKey().isPressed())
                    .forEach(k -> {
                        sender.handleMacroBaseAction(k, selfKey);
                    });
        }
    }
}
