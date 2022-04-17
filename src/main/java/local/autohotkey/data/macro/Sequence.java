package local.autohotkey.data.macro;

import com.google.gson.reflect.TypeToken;
import local.autohotkey.data.MacroKey;
import local.autohotkey.data.UseKeyData;
import local.autohotkey.sender.Sender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Sequence implements Macro {
    private final Sender sender;
    private List<UseKeyData> params;

    @Override
    public Type getParamsType() {
        return TypeToken.getParameterized(List.class, UseKeyData.class).getType();
    }

    @Override
    public void setParams(Object param, MacroKey self) {
        this.params = (List<UseKeyData>)param;
    }

    @Override
    public void run() {
        params.forEach(
                useKeyData -> {
                    try {
                        sender.sendKey(useKeyData.getKey(), 64);
                        Thread.sleep(useKeyData.getDelay());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );
    }
}
