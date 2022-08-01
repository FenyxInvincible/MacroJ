package local.macroj.data.macro.cyberpunk;

import local.macroj.data.Key;
import local.macroj.data.MacroKey;
import local.macroj.data.macro.Macro;
import local.macroj.data.macro.Memorize;
import local.macroj.sender.RobotSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Scope("prototype")
@RequiredArgsConstructor
public class Avoid implements Macro {

    private final RobotSender sender;

    private long latestPress;

    @Override
    public void setParams(Object param, MacroKey self) {

    }

    @Override
    public void run() {
        long pressMillis = System.currentTimeMillis();
        try {
        if (pressMillis - latestPress < 500) {
            Key key = Memorize.latestKey;
            sender.sendKey(key, 30);

                Thread.sleep(50);

            sender.sendKey(key, 30);
        }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        latestPress = pressMillis;
    }
}
