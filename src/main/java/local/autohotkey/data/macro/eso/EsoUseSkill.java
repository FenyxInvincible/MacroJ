package local.autohotkey.data.macro.eso;

import local.autohotkey.data.Key;
import local.autohotkey.data.macro.Macro;
import local.autohotkey.key.MouseKey;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.ScreenPicker;
import local.autohotkey.utils.eso.EsoUtils;
import local.autohotkey.utils.eso.Locks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
@RequiredArgsConstructor
@Slf4j
public class EsoUseSkill implements Macro {

    private static final int POTION_COLOR = 7165462;
    private static final int SYNERGY_COLOR = 16777215;
    private static final long SWITCH_DELAY = 200;
    private final Sender sender;
    private final KeyManager keys;
    private final Locks locks;

    private Key overridableKey;
    private Key triggerKey;
    private Key procKey;
    private Integer delay;
    private Integer totalExecution;
    private boolean useBlock;

    @Override
    public void setParams(Object param, Key self) {
        List<String> params = (List<String>) param;
        overridableKey = keys.findKeyByText(params.get(0));
        triggerKey = keys.findKeyByText(params.get(1));
        delay = Integer.valueOf(params.get(2));
        totalExecution = Integer.valueOf(params.get(3));
        procKey = (!params.get(4).equals("")) ?keys.findKeyByText(params.get(4)) : null;
        useBlock = Boolean.valueOf(params.get(5));
    }

    @Override
    public void run() {
        boolean usePotion = locks.getSettings().getOrDefault("potion", "off").equals("on");
        boolean useSynergy = locks.getSettings().getOrDefault("synergy", "on").equals("on");

        try{
            if(locks.getNumberOfCasting().get() > 2) {
                return;
            }
            locks.getNumberOfCasting().incrementAndGet();
            locks.getSwitchBarLock().lock();
            locks.getCastLock().lock();

            do {
                long start = System.currentTimeMillis();
                sender.mouseKeyClick(MouseKey.LMB);
                Thread.sleep(delay/2);
                sender.mouseKeyClick(MouseKey.LMB);
                Thread.sleep(delay/2);


                boolean isProcTriggered = false;
                if (procKey != null
                        && ScreenPicker.pickDwordColor(848, 576) == ScreenPicker.DWORD_WHITE
                        && EsoUtils.isFirstBar()
                ) {
                    isProcTriggered = true;
                }

                if (useSynergy && ScreenPicker.pickDwordColor( 1278, 1114) == SYNERGY_COLOR){
                    sender.mouseKeyClick(MouseKey.MMB);
                }

                sender.sendKey(isProcTriggered ? procKey: overridableKey, 30);

                if (locks.getSwitchBarLock().isHeldByCurrentThread()) {
                    locks.getSwitchBarLock().unlock();
                }

                //use potion
                Thread.sleep(50);

                if(usePotion && ScreenPicker.pickDwordColor( 974 ,1107) == POTION_COLOR) {
                    sender.sendKey(keys.findKeyByText("1"), 30);
                }

                long execution = System.currentTimeMillis() - start;

                if (execution < totalExecution) {
                    Thread.sleep(totalExecution - execution);
                    if (useSynergy && ScreenPicker.pickDwordColor( 1278, 1114) == SYNERGY_COLOR){
                        sender.mouseKeyClick(MouseKey.MMB);
                    }
                }

                log.debug("Trigger key {} is pressed : {}", triggerKey.getKeyText(), triggerKey.isPressed());
            } while (triggerKey.isPressed());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            locks.getNumberOfCasting().decrementAndGet();
            locks.getCastLock().unlock();
            if (locks.getSwitchBarLock().isHeldByCurrentThread()) {
                locks.getSwitchBarLock().unlock();
            }
        }
    }
}
