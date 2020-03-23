package local.autohotkey.data.macro;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class DummyMacro implements Macro {
    @Override
    public void setParams(List<String> params) {
    }

    @Override
    public void run() {
        log.warn("DummyMacro is executed");
    }
}
