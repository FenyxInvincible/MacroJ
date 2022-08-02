package local.macroj.data.macro.eldenring;

import local.macroj.sender.Sender;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Scope("prototype")
public class SelectSpellSlot extends SelectGenericSlot {

    @Autowired
    public SelectSpellSlot(InMemoryHandler data, Sender sender) {
        super(data, sender);
    }

    @Override
    protected int selectShift(int position) {
        return data.selectSpell(position);
    }
}
