package local.macroj.data.macro;

import local.macroj.data.MacroKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DummyMacro implements Macro {
    @Override
    public void setParams(Object param, MacroKey self) {
    }

    @Override
    public void run() {
        log.debug("DummyMacro is executed");
    }
}
