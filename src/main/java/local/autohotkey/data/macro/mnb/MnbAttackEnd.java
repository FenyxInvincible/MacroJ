package local.autohotkey.data.macro.mnb;

import local.autohotkey.data.MacroKey;
import local.autohotkey.data.macro.Macro;
import local.autohotkey.key.MouseKey;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.eso.Locks;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MnbAttackEnd implements Macro {
    private final Sender sender;
    private final KeyManager keys;
    private final Locks locks;
    private String direction;

    @Override
    public void setParams(Object param, MacroKey self) {
    }

    @Override
    public void run() {
        try {
            locks.getCastLock().lock();
            sender.mouseKeyRelease(MouseKey.LMB);
        } finally {
            locks.getCastLock().unlock();
        }
    }
}
