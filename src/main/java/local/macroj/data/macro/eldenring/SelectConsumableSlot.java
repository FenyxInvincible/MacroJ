package local.macroj.data.macro.eldenring;

import local.macroj.sender.Sender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@Scope("prototype")
public class SelectConsumableSlot extends SelectGenericSlot {

    @Autowired
    public SelectConsumableSlot(InMemoryHandler data, Sender sender) {
        super(data, sender);
    }

    @Override
    protected int selectShift(int position) {
        return data.selectConsumable(position);
    }

    @Override
    protected int getChangeDelay() {
        return data.consumablesChangeDelay;
    }

    @Override
    protected int getKeyDelay() {
        return data.consumablesKeyDelay;
    }
}
