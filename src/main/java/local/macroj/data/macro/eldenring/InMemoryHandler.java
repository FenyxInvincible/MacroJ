package local.macroj.data.macro.eldenring;

import local.macroj.data.UseKeyData;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Getter
@Setter
public class InMemoryHandler {
    MutablePair<Integer, Integer> spells = new MutablePair<>(1, 1);
    MutablePair<Integer, Integer> consumables = new MutablePair<>(1, 1);

    int spellsKeyDelay = 32;
    int spellsChangeDelay = 32;
    int consumablesKeyDelay = 32;
    int consumablesChangeDelay = 32;

    private long spellKeyPressed;
    private long consumableKeyPressed;
    private Map<Class, InactiveAction> inactiveActionsByHandler = new HashMap<>();

    public void resetSpells(int amount) {
        inactiveActionsByHandler.entrySet().forEach(
                e -> e.getValue().inactiveActions.clear()
        );
        reset(amount, spells);
    }

    public void resetConsumables(int amount) {
        reset(amount, consumables);
    }

    public int selectSpell(int i) {
        return select(i, spells);
    }

    public int selectConsumable(int i) {
        return select(i, consumables);
    }

    public void nextSpell() {
        next(spells);
    }

    public void nextConsumable() {
        next(consumables);
    }

    public List<UseKeyData> getInactiveActions(Class clazz) {
        var ia = inactiveActionsByHandler.computeIfAbsent(clazz, aClass -> new InactiveAction());
        return ia.inactiveActions;
    }

    public void setInactiveActions(Class clazz, int slotId, List<UseKeyData> actions) {
        var ia = inactiveActionsByHandler.computeIfAbsent(clazz, aClass -> new InactiveAction());
        ia.inactiveActionsSlotId = slotId;
        ia.inactiveActions.clear();
        ia.inactiveActions.addAll(actions);
    }

    public int getInactiveActionsSlotId(Class clazz) {
        var ia = inactiveActionsByHandler.computeIfAbsent(clazz, aClass -> new InactiveAction());
        return ia.inactiveActionsSlotId;
    }
    /**
     *
     * @param desired desired position
     * @param mem pair
     * @return necessary steps to reach desired position
     */
    private int select(int desired, MutablePair<Integer, Integer> mem) {
        if (desired <= 0 || desired > mem.right) {
            log.warn("desired slot {} but only {} are available", desired, mem.right);
            throw new IndexOutOfBoundsException("desired more that available");
        }

        int current = mem.left;
        int amount = mem.right;

        mem.left = desired;
        int shift = current <= desired ? desired - current : amount - current + desired;
        log.info("amount {} current {} desired {} shift {}", amount, current, desired, shift);
        return shift;
    }

    private void next(MutablePair<Integer, Integer> mem) {
        int next = mem.left + 1 > mem.right ? 1 : mem.left + 1;
        mem.left = next;
        log.info("current {} max {}", mem.left, mem.right);
    }

    private void reset(int amount, MutablePair<Integer, Integer> mem) {
        mem.left = 1;
        mem.right = amount;
    }

    public void setDelays(int spellsKeyDelay, int spellsChangeDelay, int consumablesKeyDelay, int consumablesChangeDelay) {
        this.spellsKeyDelay = spellsKeyDelay;
        this.spellsChangeDelay = spellsChangeDelay;
        this.consumablesKeyDelay = consumablesKeyDelay;
        this.consumablesChangeDelay = consumablesChangeDelay;
    }

    public static class InactiveAction {
        private int inactiveActionsSlotId = 0;
        private final List<UseKeyData> inactiveActions = new ArrayList<>();
    }
}
