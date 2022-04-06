package local.autohotkey.data.macro.eldenring;

import com.google.gson.reflect.TypeToken;
import local.autohotkey.data.Key;
import local.autohotkey.data.macro.Macro;
import local.autohotkey.data.macro.eldenring.data.Settings;
import local.autohotkey.sender.Sender;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
@RequiredArgsConstructor
@Slf4j
public class Reset  implements Macro {
    private final Sender sender;
    private final InMemoryHandler data;
    private Settings settings;

    @Override
    public Type getParamsType() {
        return TypeToken.get(Settings.class).getType();
    }

    @Override
    public void setParams(Object param, Key self) {
        settings = (Settings) param;
    }

    @SneakyThrows
    @Override
    public void run() {
        sender.pressKey(settings.getSpells().getKey());
        sender.pressKey(settings.getConsumables().getKey());
        Thread.sleep(600);
        sender.releaseKey(settings.getSpells().getKey());
        sender.releaseKey(settings.getConsumables().getKey());
        data.resetSpells(settings.getSpells().getAmount());
        data.resetConsumables(settings.getConsumables().getAmount());
    }
}
