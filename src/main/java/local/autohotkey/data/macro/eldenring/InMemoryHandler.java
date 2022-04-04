package local.autohotkey.data.macro.eldenring;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Getter
@Setter
public class InMemoryHandler {
    MutablePair<Integer, Integer> spells = new MutablePair<>(1, 1);
    MutablePair<Integer, Integer> consumables = new MutablePair<>(1, 1);

    private long spellKeyPressed;
    private long consumableKeyPressed;

    public void resetSpells(int amount) {
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

    /**
     *
     * @param desired desired position
     * @param mem pair
     * @return necessary steps to reach desired position
     */
    private int select(int desired, MutablePair<Integer, Integer> mem) {
        if (desired <= 0 || desired > mem.right) {
            return 0;
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
}
