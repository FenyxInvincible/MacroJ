package local.macroj.data.macro.eldenring;

import local.macroj.data.Key;
import local.macroj.data.MacroKey;
import local.macroj.data.macro.Macro;
import local.macroj.data.macro.eldenring.data.KeyActions;
import local.macroj.sender.Sender;
import local.macroj.service.KeyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Scope("prototype")
public class NextSpell implements Macro {
    private final Sender sender;
    private final KeyManager keys;
    private final InMemoryHandler data;

    private KeyActions lastAction;
    private Key changeKey;
    private Integer maxSlots;

    public void setAction(KeyActions action){
        lastAction = action;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setParams(Object param, MacroKey self) {
        //todo not working due to params were refactored
        /*changeKey = keys.findKeyByText(params.get(0));
        lastAction = KeyActions.of(params.get(1));
        maxSlots = Integer.valueOf(params.get(2));*/

        data.resetSpells(maxSlots);
    }

    @Override
    public void run() {
        if(lastAction == KeyActions.Pressed) {
            sender.pressKey(changeKey);
            data.nextSpell();
            data.setSpellKeyPressed(System.currentTimeMillis());
        } else if(lastAction == KeyActions.Released) {
            //elden ring and ds has behavior:
            // if we long press change btn first items will be selected
            if(System.currentTimeMillis() - data.getSpellKeyPressed() > 500) {
                data.resetSpells(maxSlots);
            }
            sender.releaseKey(changeKey);
        }
    }
}
