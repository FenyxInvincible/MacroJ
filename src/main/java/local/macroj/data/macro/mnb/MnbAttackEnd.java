package local.macroj.data.macro.mnb;

import local.macroj.data.MacroKey;
import local.macroj.data.macro.Macro;
import local.macroj.key.MouseKey;
import local.macroj.sender.Sender;
import local.macroj.service.KeyManager;
import local.macroj.utils.eso.Locks;
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
