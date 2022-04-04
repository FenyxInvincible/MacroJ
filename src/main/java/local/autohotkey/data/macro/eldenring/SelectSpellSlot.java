package local.autohotkey.data.macro.eldenring;

import local.autohotkey.data.Key;
import local.autohotkey.data.macro.Macro;
import local.autohotkey.data.macro.eldenring.data.SelectSlot;
import local.autohotkey.key.MouseKey;
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
public class SelectSpellSlot implements Macro {
    private final Sender sender;
    private final InMemoryHandler data;
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
        int amount = data.selectSpell(slotInfo.getPosition());
        for (int i = 0; i < amount; i++) {
            sender.sendKey(slotInfo.getChangeKey(), 32);
            Thread.sleep(32);
        }

        if (slotInfo.getUseKey() != null) {
            if (!slotInfo.getUseKey().isMouseKey()) {
                sender.sendKey(slotInfo.getUseKey(), 64);
                log.info("keyboard");
            } else {
                log.info("mouse");
                sender.sendMouseKey(MouseKey.of(slotInfo.getUseKey().getKeyText()), 32);
            }
        }
    }
}
