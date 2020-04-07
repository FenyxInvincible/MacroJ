package local.autohotkey.data.macro;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DummyMacro implements Macro {
    @Override
    public void setParams(List<String> params) {
    }

    @Override
    public void run() {
        log.debug("DummyMacro is executed");
    }
}
