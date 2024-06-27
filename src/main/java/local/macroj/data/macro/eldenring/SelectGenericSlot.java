package local.macroj.data.macro.eldenring;

import com.google.gson.reflect.TypeToken;
import local.macroj.data.Key;
import local.macroj.data.MacroKey;
import local.macroj.data.UseKeyData;
import local.macroj.data.macro.Macro;
import local.macroj.data.macro.eldenring.data.SelectSlot;
import local.macroj.sender.Sender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public abstract class SelectGenericSlot implements Macro {

    protected final InMemoryHandler data;
    private final Sender sender;
    protected SelectSlot slotInfo;

    @Override
    public Type getParamsType() {
        return TypeToken.get(SelectSlot.class).getType();
    }

    @Override
    public void setParams(Object param, MacroKey self) {
        slotInfo = (SelectSlot)param;
    }

    public void run () {
        try {
            var changeDelay = getChangeDelay();
            var keyDelay = getKeyDelay();
            //here we can have expected exception, and if so don't want to execute code below
            int shiftAmount = selectShift(slotInfo.getPosition());

            //execute onInactive of previous slot
            var clazz = this.getClass();
            if (!data.getInactiveActions(clazz).isEmpty() && !data.getInactiveActions(clazz).equals(slotInfo.getOnActive())) {
                log.debug("execute onInactive of previous slot");
                executeActions(data.getInactiveActions(clazz));
            } else {
                log.debug(
                        "inactive action of previous slot is empty {}, inactive of previous and current are equals {}",
                        data.getInactiveActions(clazz).isEmpty(),
                        data.getInactiveActions(clazz).equals(slotInfo.getOnActive())
                );
            }

            //skip same action with inactive. We need to avoid executing same action twice, for example switch weapon twice.
            if (
                    slotInfo.getOnActive() != null && //there is an action
                            slotInfo.getPosition() != data.getInactiveActionsSlotId(this.getClass()) && //skip same slot
                            !slotInfo.getOnActive().equals(data.getInactiveActions(this.getClass())) //We need to avoid executing same action twice, for example switch weapon twice for same group of skills.
            ) {
                log.debug("executes onActive");
                executeActions(slotInfo.getOnActive());
            } else {
                log.debug(
                        "onActive is not null {}, slot is same as previous {}, onActive equals to prev invactive {}",
                        slotInfo.getOnActive() != null,
                        slotInfo.getOnActive() != null && slotInfo.getPosition() != data.getInactiveActionsSlotId(clazz),
                        slotInfo.getOnActive() != null && slotInfo.getOnActive().equals(data.getInactiveActions(clazz))
                );
            }

            //set inactive for current slot
            data.setInactiveActions(
                    this.getClass(),
                    slotInfo.getPosition(),
                    slotInfo.getOnInactive() != null ? slotInfo.getOnInactive() : Collections.emptyList()
            );

            for (int i = 0; i < shiftAmount; i++) {
                sender.sendKey(slotInfo.getChangeKey(), keyDelay);
                Thread.sleep(changeDelay);
            }

            if (slotInfo.getUseKey() != null) {
                executeActions(slotInfo.getUseKey());
            }
        } catch (IndexOutOfBoundsException e) {
            log.warn("Slot will not be changes do to out of bounds");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract int selectShift(int position);

    private void executeActions(List<UseKeyData> listOfKeys) {
        var changeDelay = getChangeDelay();
        listOfKeys.stream().forEach(
                use -> {
                    try {

                        if (use.getAction() == Key.Action.Press) {
                            sender.pressKey(use.getKey());
                            log.debug("press {}", use);
                        } else if (use.getAction() == Key.Action.Release) {
                            log.debug("release {}", use);
                            sender.releaseKey(use.getKey());
                        } else {
                            log.debug("send {}", use);
                            sender.sendKey(use.getKey(), changeDelay);
                        }

                        Thread.sleep(use.getDelay());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    protected abstract int getChangeDelay();

    protected abstract int getKeyDelay();
}
