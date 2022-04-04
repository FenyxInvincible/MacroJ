package local.autohotkey.data.macro.eldenring;

import local.autohotkey.data.Key;
import local.autohotkey.data.macro.Macro;
import local.autohotkey.data.macro.eldenring.data.SelectSlot;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Scope("prototype")
public class SelectConsumableSlot implements Macro {

    private final InMemoryHandler data;
    private final Sender sender;
    private SelectSlot slotInfo;

    @Override
    public Class<?> getParamsType() {
        return SelectSlot.class;
    }

    @Override
    public void setParams(Object param, Key self) {
        slotInfo = (SelectSlot)param;
    }

    @SneakyThrows
    @Override
    public void run() {
        int amount = data.selectConsumable(slotInfo.getPosition());
        for (int i = 0; i < amount; i++) {
            sender.sendKey(slotInfo.getChangeKey(), 32);
            Thread.sleep(32);
        }

        if (slotInfo.getUseKey() != null) {
            sender.sendKey(slotInfo.getUseKey(), 64);
        }
    }
}
