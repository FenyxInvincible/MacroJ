package local.macroj.data.macro;

import local.macroj.data.MacroKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StopApplication implements Macro {

    private final ApplicationContext context;

    @Override
    public void setParams(Object param, MacroKey self) {

    }

    @Override
    public void run() {
        System.exit(0);
    }
}
