package local.autohotkey.data.macro;

import local.autohotkey.data.Key;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DummyMacro implements Macro {
    @Override
    public void setParams(Object param, Key self) {
    }

    @Override
    public void run() {
        log.debug("DummyMacro is executed");
    }
}
