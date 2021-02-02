package local.autohotkey.data.macro.cyberpunk;

import local.autohotkey.data.Key;
import local.autohotkey.data.macro.Macro;
import local.autohotkey.data.macro.Memorize;
import local.autohotkey.sender.RobotSender;
import local.autohotkey.sender.Sender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@Scope("prototype")
@RequiredArgsConstructor
public class Avoid implements Macro {

    private final RobotSender sender;

    private long latestPress;

    @Override
    public void setParams(List<String> params) {

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
