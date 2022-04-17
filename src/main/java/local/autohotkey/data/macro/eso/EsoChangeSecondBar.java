package local.autohotkey.data.macro.eso;

import local.autohotkey.data.Key;
import local.autohotkey.data.MacroKey;
import local.autohotkey.data.macro.Macro;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.eso.EsoUtils;
import local.autohotkey.utils.eso.Locks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EsoChangeSecondBar implements Macro {

    private final Sender sender;
    private final KeyManager keys;
    private final Locks locks;

    private Key f22;
    private Key f23;

    @Override
    public void setParams(Object param, MacroKey self) {
        f22 = keys.findKeyByText("F22");
        f23 = keys.findKeyByText("F23");
    }

    @Override
    public void run() {
        try{
            locks.getSwitchBarLock().lock();
            long start = System.currentTimeMillis();
            while (!EsoUtils.isFirstBar()){
                sender.sendKey(f22, 16);
                Thread.sleep(50);

                if (System.currentTimeMillis() - start > 1500) {
                    log.warn("Exiting by timeout {}", this.getClass().getName());
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            locks.getSwitchBarLock().unlock();
        }
    }
}
