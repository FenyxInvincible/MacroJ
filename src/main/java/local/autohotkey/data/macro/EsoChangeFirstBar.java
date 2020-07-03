package local.autohotkey.data.macro;

import local.autohotkey.data.Key;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.ScreenPicker;
import local.autohotkey.utils.eso.EsoUtils;
import local.autohotkey.utils.eso.Locks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EsoChangeFirstBar implements Macro {

    private final Sender sender;
    private final KeyManager keys;
    private final Locks locks;

    private Key f21;

    @Override
    public void setParams(List<String> params) {
        f21 = keys.findKeyByText("F21");
    }

    @Override
    public void run() {
        try{
            locks.getSwitchBarLock().lock();
            long start = System.currentTimeMillis();
            log.debug("ScreenPicker.pickDwordColor != ScreenPicker.DWORD_WHITE {}", ScreenPicker.pickDwordColor(1149, 1399) != ScreenPicker.DWORD_WHITE);
            while (EsoUtils.isFirstBar()){
                log.debug("=========={}", f21);
                sender.sendKey(f21, 16);
                Thread.sleep(50);

                if (System.currentTimeMillis() - start > 1500) {
                    log.warn("Exiting by timeout {}", this.getClass().getName());
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            locks.getSwitchBarLock().unlock();
        }
    }
}
